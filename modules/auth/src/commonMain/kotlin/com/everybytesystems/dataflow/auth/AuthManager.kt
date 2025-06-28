package com.everybytesystems.dataflow.auth

import com.everybytesystems.dataflow.core.config.DHIS2Config
import com.everybytesystems.dataflow.core.network.ApiResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json

/**
 * Manages authentication for DHIS2 API
 */
class AuthManager(
    private val config: DHIS2Config,
    private val httpClient: HttpClient,
    private val secureStorage: SecureStorage,
    private val json: Json = Json { ignoreUnknownKeys = true }
) {
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    private var currentCredentials: StoredCredentials? = null
    
    /**
     * Initialize authentication manager and restore saved credentials
     */
    suspend fun initialize() {
        try {
            val savedCredentials = secureStorage.getCredentials()
            if (savedCredentials != null && !isTokenExpired(savedCredentials)) {
                currentCredentials = savedCredentials
                savedCredentials.userInfo?.let { userInfo ->
                    _authState.value = AuthState.Authenticated(userInfo)
                }
            } else {
                // Clear expired credentials
                secureStorage.clearCredentials()
            }
        } catch (e: Exception) {
            _authState.value = AuthState.Error("Failed to initialize auth", e)
        }
    }
    
    /**
     * Authenticate with provided configuration
     */
    suspend fun authenticate(authConfig: AuthConfig): ApiResponse<UserInfo> {
        _authState.value = AuthState.Authenticating
        
        return try {
            authConfig.validate().getOrThrow()
            
            val userInfo = when (authConfig) {
                is AuthConfig.Basic -> authenticateBasic(authConfig)
                is AuthConfig.PersonalAccessToken -> authenticatePAT(authConfig)
                is AuthConfig.OAuth2 -> authenticateOAuth2(authConfig)
                is AuthConfig.Cookie -> authenticateCookie(authConfig)
            }
            
            // Store credentials securely
            val credentials = StoredCredentials(
                authConfig = authConfig,
                userInfo = userInfo
            )
            secureStorage.storeCredentials(credentials)
            currentCredentials = credentials
            
            _authState.value = AuthState.Authenticated(userInfo)
            ApiResponse.success(userInfo)
            
        } catch (e: Exception) {
            val errorMessage = "Authentication failed: ${e.message}"
            _authState.value = AuthState.Error(errorMessage, e)
            ApiResponse.error(e, errorMessage)
        }
    }
    
    /**
     * Logout and clear stored credentials
     */
    suspend fun logout() {
        try {
            secureStorage.clearCredentials()
            currentCredentials = null
            _authState.value = AuthState.Unauthenticated
        } catch (e: Exception) {
            _authState.value = AuthState.Error("Logout failed", e)
        }
    }
    
    /**
     * Check if user is currently authenticated
     */
    fun isAuthenticated(): Boolean {
        return _authState.value is AuthState.Authenticated && 
               currentCredentials != null && 
               !isTokenExpired(currentCredentials!!)
    }
    
    /**
     * Get current user info
     */
    fun getCurrentUser(): UserInfo? {
        return (_authState.value as? AuthState.Authenticated)?.user
    }
    
    /**
     * Refresh authentication token if possible
     */
    suspend fun refreshToken(): ApiResponse<Unit> {
        val credentials = currentCredentials ?: return ApiResponse.error(
            IllegalStateException("No credentials available for refresh")
        )
        
        return when (val authConfig = credentials.authConfig) {
            is AuthConfig.OAuth2 -> {
                credentials.refreshToken?.let { refreshToken ->
                    refreshOAuth2Token(authConfig, refreshToken)
                } ?: ApiResponse.error(IllegalStateException("No refresh token available"))
            }
            else -> {
                // Re-authenticate with stored credentials
                authenticate(authConfig).map { }
            }
        }
    }
    
    // TODO: Fix Ktor Auth configuration
    /*
    fun configure(authConfig: AuthConfig) {
        val credentials = currentCredentials ?: return
        
        when (val config = credentials.authConfig) {
            is AuthConfig.Basic -> {
                authConfig.basic {
                    credentials {
                        BasicAuthCredentials(username = config.username, password = config.password)
                    }
                    realm = "DHIS2"
                }
            }
            is AuthConfig.PersonalAccessToken -> {
                authConfig.bearer {
                    loadTokens {
                        BearerTokens(config.token, null)
                    }
                }
            }
            is AuthConfig.OAuth2 -> {
                credentials.accessToken?.let { token ->
                    authConfig.bearer {
                        loadTokens {
                            BearerTokens(token, credentials.refreshToken)
                        }
                        refreshTokens { _ ->
                            // Refresh token logic would go here
                            // Return null if refresh fails
                            null
                        }
                    }
                }
            }
            is AuthConfig.Cookie -> {
                // Cookie authentication would be handled differently
            }
        }
    }
    */
    
    private suspend fun authenticateBasic(config: AuthConfig.Basic): UserInfo {
        val response = httpClient.get("${this.config.apiBaseUrl}/me") {
            basicAuth(config.username, config.password)
        }
        
        if (response.status.isSuccess()) {
            return response.body<UserInfo>()
        } else {
            throw Exception("Basic authentication failed: ${response.status}")
        }
    }
    
    private suspend fun authenticatePAT(config: AuthConfig.PersonalAccessToken): UserInfo {
        val response = httpClient.get("${this.config.apiBaseUrl}/me") {
            bearerAuth(config.token)
        }
        
        if (response.status.isSuccess()) {
            return response.body<UserInfo>()
        } else {
            throw Exception("PAT authentication failed: ${response.status}")
        }
    }
    
    private suspend fun authenticateOAuth2(config: AuthConfig.OAuth2): UserInfo {
        // OAuth2 flow would typically involve redirecting to authorization server
        // This is a simplified implementation
        throw NotImplementedError("OAuth2 authentication requires platform-specific implementation")
    }
    
    private suspend fun authenticateCookie(config: AuthConfig.Cookie): UserInfo {
        val response = httpClient.get("${this.config.apiBaseUrl}/me") {
            cookie("JSESSIONID", config.sessionCookie)
        }
        
        if (response.status.isSuccess()) {
            return response.body<UserInfo>()
        } else {
            throw Exception("Cookie authentication failed: ${response.status}")
        }
    }
    
    private suspend fun refreshOAuth2Token(
        config: AuthConfig.OAuth2, 
        refreshToken: String
    ): ApiResponse<Unit> {
        return try {
            val response = httpClient.post(config.tokenUrl) {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(
                    listOf(
                        "grant_type" to "refresh_token",
                        "refresh_token" to refreshToken,
                        "client_id" to config.clientId
                    ).let { params ->
                        if (config.clientSecret != null) {
                            params + ("client_secret" to config.clientSecret)
                        } else params
                    }.formUrlEncode()
                )
            }
            
            if (response.status.isSuccess()) {
                val tokenResponse = response.body<OAuth2TokenResponse>()
                
                // Update stored credentials
                val updatedCredentials = currentCredentials?.copy(
                    accessToken = tokenResponse.access_token,
                    refreshToken = tokenResponse.refresh_token ?: refreshToken,
                    expiresAt = tokenResponse.expires_in?.let { 
                        Clock.System.now().toEpochMilliseconds() + (it * 1000)
                    }
                )
                
                if (updatedCredentials != null) {
                    secureStorage.storeCredentials(updatedCredentials)
                    currentCredentials = updatedCredentials
                }
                
                ApiResponse.success(Unit)
            } else {
                ApiResponse.error(Exception("Token refresh failed: ${response.status}"))
            }
        } catch (e: Exception) {
            ApiResponse.error(e, "Token refresh failed")
        }
    }
    
    private fun isTokenExpired(credentials: StoredCredentials): Boolean {
        val expiresAt = credentials.expiresAt ?: return false
        return Clock.System.now().toEpochMilliseconds() >= expiresAt
    }
}

/**
 * Interface for secure storage of authentication credentials
 */
interface SecureStorage {
    suspend fun storeCredentials(credentials: StoredCredentials)
    suspend fun getCredentials(): StoredCredentials?
    suspend fun clearCredentials()
}
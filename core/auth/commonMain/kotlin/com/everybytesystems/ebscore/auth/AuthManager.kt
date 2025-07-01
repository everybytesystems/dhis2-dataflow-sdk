package com.everybytesystems.ebscore.auth

import com.everybytesystems.ebscore.auth.utils.Base64Utils
import com.everybytesystems.ebscore.core.config.DHIS2Config
import com.everybytesystems.ebscore.core.network.ApiResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
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
     * Authenticate with username and password
     */
    suspend fun authenticate(username: String, password: String): ApiResponse<UserInfo> {
        _authState.value = AuthState.Loading
        
        return try {
            // Test authentication by making a request to /me endpoint
            val response = httpClient.get {
                url("${config.baseUrl}/api/me")
                headers {
                    append("Authorization", "Basic ${Base64Utils.encode("$username:$password")}")
                    append("Accept", "application/json")
                }
            }
            
            when (response.status) {
                HttpStatusCode.OK -> {
                    val userInfo = response.body<UserInfo>()
                    
                    // Store credentials
                    val credentials = StoredCredentials(
                        username = username,
                        password = password,
                        userInfo = userInfo
                    )
                    
                    secureStorage.storeCredentials(credentials)
                    currentCredentials = credentials
                    _authState.value = AuthState.Authenticated(userInfo)
                    
                    ApiResponse.Success(userInfo)
                }
                HttpStatusCode.Unauthorized -> {
                    _authState.value = AuthState.Unauthenticated
                    ApiResponse.Error(RuntimeException("Invalid credentials"))
                }
                else -> {
                    _authState.value = AuthState.Error("Authentication failed", RuntimeException("HTTP ${response.status}"))
                    ApiResponse.Error(RuntimeException("Authentication failed: ${response.status}"))
                }
            }
        } catch (e: Exception) {
            _authState.value = AuthState.Error("Authentication failed", e)
            ApiResponse.Error(e, "Authentication failed: ${e.message}")
        }
    }
    
    /**
     * Authenticate with bearer token
     */
    suspend fun authenticateWithToken(bearerToken: String): ApiResponse<UserInfo> {
        _authState.value = AuthState.Loading
        
        return try {
            // Test authentication by making a request to /me endpoint
            val response = httpClient.get {
                url("${config.baseUrl}/api/me")
                headers {
                    append("Authorization", "Bearer $bearerToken")
                    append("Accept", "application/json")
                }
            }
            
            when (response.status) {
                HttpStatusCode.OK -> {
                    val userInfo = response.body<UserInfo>()
                    
                    // Store credentials
                    val credentials = StoredCredentials(
                        username = userInfo.username,
                        bearerToken = bearerToken,
                        userInfo = userInfo
                    )
                    
                    secureStorage.storeCredentials(credentials)
                    currentCredentials = credentials
                    _authState.value = AuthState.Authenticated(userInfo)
                    
                    ApiResponse.Success(userInfo)
                }
                HttpStatusCode.Unauthorized -> {
                    _authState.value = AuthState.Unauthenticated
                    ApiResponse.Error(RuntimeException("Invalid token"))
                }
                else -> {
                    _authState.value = AuthState.Error("Authentication failed", RuntimeException("HTTP ${response.status}"))
                    ApiResponse.Error(RuntimeException("Authentication failed: ${response.status}"))
                }
            }
        } catch (e: Exception) {
            _authState.value = AuthState.Error("Authentication failed", e)
            ApiResponse.Error(e, "Authentication failed: ${e.message}")
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
     * Get current user info
     */
    fun getCurrentUser(): UserInfo? {
        return currentCredentials?.userInfo
    }
    
    /**
     * Check if user is authenticated
     */
    fun isAuthenticated(): Boolean {
        return currentCredentials != null && !isTokenExpired(currentCredentials!!)
    }
    
    /**
     * Get current credentials
     */
    fun getCredentials(): StoredCredentials? {
        return currentCredentials
    }
    
    /**
     * Refresh authentication if needed
     */
    suspend fun refreshIfNeeded(): ApiResponse<Unit> {
        val credentials = currentCredentials ?: return ApiResponse.Error(RuntimeException("No credentials available"))
        
        if (isTokenExpired(credentials)) {
            // Try to re-authenticate
            return when {
                credentials.password != null -> {
                    when (val result = authenticate(credentials.username, credentials.password)) {
                        is ApiResponse.Success -> ApiResponse.Success(Unit)
                        is ApiResponse.Error -> ApiResponse.Error(result.exception, result.message)
                        is ApiResponse.Loading -> ApiResponse.Loading
                    }
                }
                credentials.bearerToken != null -> {
                    when (val result = authenticateWithToken(credentials.bearerToken)) {
                        is ApiResponse.Success -> ApiResponse.Success(Unit)
                        is ApiResponse.Error -> ApiResponse.Error(result.exception, result.message)
                        is ApiResponse.Loading -> ApiResponse.Loading
                    }
                }
                else -> ApiResponse.Error(RuntimeException("No authentication method available"))
            }
        }
        
        return ApiResponse.Success(Unit)
    }
    
    private fun isTokenExpired(credentials: StoredCredentials): Boolean {
        val expiresAt = credentials.expiresAt ?: return false
        return Clock.System.now().toEpochMilliseconds() >= expiresAt
    }
    

}

/**
 * Authentication state
 */
sealed class AuthState {
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Authenticated(val userInfo: UserInfo) : AuthState()
    data class Error(val message: String, val exception: Throwable? = null) : AuthState()
}

/**
 * Authentication configuration
 */
@Serializable
data class AuthConfig(
    val authType: AuthType = AuthType.BASIC,
    val username: String? = null,
    val password: String? = null,
    val bearerToken: String? = null,
    val refreshToken: String? = null,
    val clientId: String? = null,
    val clientSecret: String? = null,
    val scope: String? = null,
    val tokenEndpoint: String? = null,
    val autoRefresh: Boolean = true,
    val tokenExpiryBuffer: Long = 300_000L // 5 minutes
)

/**
 * Authentication type
 */
enum class AuthType {
    BASIC,
    BEARER,
    OAUTH2,
    API_KEY
}
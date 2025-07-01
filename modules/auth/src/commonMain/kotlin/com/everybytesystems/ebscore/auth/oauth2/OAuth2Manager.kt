package com.everybytesystems.ebscore.auth.oauth2

import com.everybytesystems.ebscore.auth.SecureStorage
import com.everybytesystems.ebscore.auth.StoredCredentials
import com.everybytesystems.ebscore.auth.UserInfo
import com.everybytesystems.ebscore.auth.jwt.JwtValidator
import com.everybytesystems.ebscore.core.network.ApiResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Production-ready OAuth2 Manager for DHIS2 and other OAuth2 providers
 */
class OAuth2Manager(
    private val config: OAuth2Config,
    private val httpClient: HttpClient,
    private val secureStorage: SecureStorage,
    private val jwtValidator: JwtValidator = JwtValidator(),
    private val json: Json = Json { ignoreUnknownKeys = true }
) {
    
    private val _authState = MutableStateFlow<OAuth2State>(OAuth2State.Unauthenticated)
    val authState: StateFlow<OAuth2State> = _authState.asStateFlow()
    
    private var currentTokens: OAuth2Tokens? = null
    
    /**
     * Initialize OAuth2 manager and restore saved tokens
     */
    suspend fun initialize() {
        try {
            val savedCredentials = secureStorage.getCredentials()
            savedCredentials?.let { credentials ->
                if (credentials.bearerToken != null && credentials.refreshToken != null) {
                    val tokens = OAuth2Tokens(
                        accessToken = credentials.bearerToken,
                        refreshToken = credentials.refreshToken,
                        tokenType = "Bearer",
                        expiresIn = credentials.expiresAt?.let { 
                            maxOf(0, (it - Clock.System.now().toEpochMilliseconds()) / 1000)
                        } ?: 3600,
                        scope = config.scope
                    )
                    
                    if (!jwtValidator.isTokenExpired(tokens.accessToken)) {
                        currentTokens = tokens
                        credentials.userInfo?.let { userInfo ->
                            _authState.value = OAuth2State.Authenticated(tokens, userInfo)
                        }
                    } else if (tokens.refreshToken.isNotEmpty()) {
                        // Try to refresh the token
                        refreshToken()
                    }
                }
            }
        } catch (e: Exception) {
            _authState.value = OAuth2State.Error("Failed to initialize OAuth2", e)
        }
    }
    
    /**
     * Start OAuth2 authorization code flow
     */
    fun getAuthorizationUrl(state: String? = null): String {
        val params = mutableMapOf<String, String>().apply {
            put("response_type", "code")
            put("client_id", config.clientId)
            put("redirect_uri", config.redirectUri)
            config.scope?.let { put("scope", it) }
            state?.let { put("state", it) }
            config.codeChallenge?.let { put("code_challenge", it) }
            config.codeChallengeMethod?.let { put("code_challenge_method", it) }
        }
        
        val queryString = params.map { "${it.key}=${it.value.encodeURLParameter()}" }
            .joinToString("&")
        
        return "${config.authorizationEndpoint}?$queryString"
    }
    
    /**
     * Exchange authorization code for tokens
     */
    suspend fun exchangeCodeForTokens(
        authorizationCode: String,
        codeVerifier: String? = null
    ): ApiResponse<OAuth2Tokens> {
        _authState.value = OAuth2State.Loading
        
        return try {
            val response = httpClient.submitForm(
                url = config.tokenEndpoint,
                formParameters = parameters {
                    append("grant_type", "authorization_code")
                    append("code", authorizationCode)
                    append("client_id", config.clientId)
                    config.clientSecret?.let { append("client_secret", it) }
                    append("redirect_uri", config.redirectUri)
                    codeVerifier?.let { append("code_verifier", it) }
                }
            ) {
                headers {
                    append("Accept", "application/json")
                    append("Content-Type", "application/x-www-form-urlencoded")
                }
            }
            
            when (response.status) {
                HttpStatusCode.OK -> {
                    val tokenResponse = response.body<OAuth2TokenResponse>()
                    val tokens = OAuth2Tokens(
                        accessToken = tokenResponse.accessToken,
                        refreshToken = tokenResponse.refreshToken,
                        tokenType = tokenResponse.tokenType ?: "Bearer",
                        expiresIn = tokenResponse.expiresIn ?: 3600,
                        scope = tokenResponse.scope ?: config.scope
                    )
                    
                    // Get user info
                    val userInfo = getUserInfo(tokens.accessToken)
                    
                    // Store tokens
                    storeTokens(tokens, userInfo)
                    currentTokens = tokens
                    
                    _authState.value = OAuth2State.Authenticated(tokens, userInfo)
                    ApiResponse.Success(tokens)
                }
                else -> {
                    val error = try {
                        response.body<OAuth2ErrorResponse>()
                    } catch (e: Exception) {
                        OAuth2ErrorResponse("unknown_error", "Failed to parse error response")
                    }
                    
                    _authState.value = OAuth2State.Error("Token exchange failed: ${error.error}", 
                        RuntimeException(error.errorDescription ?: error.error))
                    ApiResponse.Error(RuntimeException("${error.error}: ${error.errorDescription}"))
                }
            }
        } catch (e: Exception) {
            _authState.value = OAuth2State.Error("Token exchange failed", e)
            ApiResponse.Error(e, "Token exchange failed: ${e.message}")
        }
    }
    
    /**
     * Refresh access token using refresh token
     */
    suspend fun refreshToken(): ApiResponse<OAuth2Tokens> {
        val refreshToken = currentTokens?.refreshToken 
            ?: return ApiResponse.Error(RuntimeException("No refresh token available"))
        
        _authState.value = OAuth2State.Loading
        
        return try {
            val response = httpClient.submitForm(
                url = config.tokenEndpoint,
                formParameters = parameters {
                    append("grant_type", "refresh_token")
                    append("refresh_token", refreshToken)
                    append("client_id", config.clientId)
                    config.clientSecret?.let { append("client_secret", it) }
                    config.scope?.let { append("scope", it) }
                }
            ) {
                headers {
                    append("Accept", "application/json")
                    append("Content-Type", "application/x-www-form-urlencoded")
                }
            }
            
            when (response.status) {
                HttpStatusCode.OK -> {
                    val tokenResponse = response.body<OAuth2TokenResponse>()
                    val tokens = OAuth2Tokens(
                        accessToken = tokenResponse.accessToken,
                        refreshToken = tokenResponse.refreshToken ?: refreshToken, // Keep old refresh token if not provided
                        tokenType = tokenResponse.tokenType ?: "Bearer",
                        expiresIn = tokenResponse.expiresIn ?: 3600,
                        scope = tokenResponse.scope ?: config.scope
                    )
                    
                    // Get updated user info
                    val userInfo = getUserInfo(tokens.accessToken)
                    
                    // Store updated tokens
                    storeTokens(tokens, userInfo)
                    currentTokens = tokens
                    
                    _authState.value = OAuth2State.Authenticated(tokens, userInfo)
                    ApiResponse.Success(tokens)
                }
                else -> {
                    val error = try {
                        response.body<OAuth2ErrorResponse>()
                    } catch (e: Exception) {
                        OAuth2ErrorResponse("unknown_error", "Failed to parse error response")
                    }
                    
                    // If refresh fails, clear tokens and require re-authentication
                    logout()
                    
                    _authState.value = OAuth2State.Error("Token refresh failed: ${error.error}", 
                        RuntimeException(error.errorDescription ?: error.error))
                    ApiResponse.Error(RuntimeException("${error.error}: ${error.errorDescription}"))
                }
            }
        } catch (e: Exception) {
            // If refresh fails, clear tokens
            logout()
            
            _authState.value = OAuth2State.Error("Token refresh failed", e)
            ApiResponse.Error(e, "Token refresh failed: ${e.message}")
        }
    }
    
    /**
     * Automatically refresh token if needed
     */
    suspend fun refreshIfNeeded(): ApiResponse<Unit> {
        val tokens = currentTokens ?: return ApiResponse.Error(RuntimeException("No tokens available"))
        
        if (jwtValidator.needsRefresh(tokens.accessToken, config.refreshBufferSeconds)) {
            return when (val result = refreshToken()) {
                is ApiResponse.Success -> ApiResponse.Success(Unit)
                is ApiResponse.Error -> result
                is ApiResponse.Loading -> ApiResponse.Loading
            }
        }
        
        return ApiResponse.Success(Unit)
    }
    
    /**
     * Get user information from userinfo endpoint
     */
    private suspend fun getUserInfo(accessToken: String): UserInfo? {
        return try {
            config.userinfoEndpoint?.let { endpoint ->
                val response = httpClient.get(endpoint) {
                    headers {
                        append("Authorization", "Bearer $accessToken")
                        append("Accept", "application/json")
                    }
                }
                
                if (response.status == HttpStatusCode.OK) {
                    response.body<UserInfo>()
                } else null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Store tokens securely
     */
    private suspend fun storeTokens(tokens: OAuth2Tokens, userInfo: UserInfo?) {
        val expiresAt = Clock.System.now().toEpochMilliseconds() + (tokens.expiresIn * 1000)
        
        val credentials = StoredCredentials(
            username = userInfo?.username ?: "oauth2_user",
            bearerToken = tokens.accessToken,
            refreshToken = tokens.refreshToken,
            expiresAt = expiresAt,
            userInfo = userInfo
        )
        
        secureStorage.storeCredentials(credentials)
    }
    
    /**
     * Logout and clear tokens
     */
    suspend fun logout() {
        try {
            // Revoke tokens if revocation endpoint is available
            currentTokens?.let { tokens ->
                config.revocationEndpoint?.let { endpoint ->
                    try {
                        httpClient.submitForm(
                            url = endpoint,
                            formParameters = parameters {
                                append("token", tokens.accessToken)
                                append("client_id", config.clientId)
                                config.clientSecret?.let { append("client_secret", it) }
                            }
                        )
                    } catch (e: Exception) {
                        // Ignore revocation errors
                    }
                }
            }
            
            secureStorage.clearCredentials()
            currentTokens = null
            _authState.value = OAuth2State.Unauthenticated
        } catch (e: Exception) {
            _authState.value = OAuth2State.Error("Logout failed", e)
        }
    }
    
    /**
     * Get current access token
     */
    fun getAccessToken(): String? {
        return currentTokens?.accessToken
    }
    
    /**
     * Check if user is authenticated
     */
    fun isAuthenticated(): Boolean {
        val tokens = currentTokens ?: return false
        return !jwtValidator.isTokenExpired(tokens.accessToken)
    }
    
    /**
     * Get current tokens
     */
    fun getCurrentTokens(): OAuth2Tokens? {
        return currentTokens
    }
}

/**
 * OAuth2 Configuration
 */
@Serializable
data class OAuth2Config(
    val clientId: String,
    val clientSecret: String? = null,
    val authorizationEndpoint: String,
    val tokenEndpoint: String,
    val userinfoEndpoint: String? = null,
    val revocationEndpoint: String? = null,
    val redirectUri: String,
    val scope: String? = null,
    val codeChallenge: String? = null,
    val codeChallengeMethod: String? = null,
    val refreshBufferSeconds: Long = 300 // 5 minutes
)

/**
 * OAuth2 Tokens
 */
@Serializable
data class OAuth2Tokens(
    val accessToken: String,
    val refreshToken: String? = null,
    val tokenType: String = "Bearer",
    val expiresIn: Long = 3600,
    val scope: String? = null
)

/**
 * OAuth2 State
 */
sealed class OAuth2State {
    object Unauthenticated : OAuth2State()
    object Loading : OAuth2State()
    data class Authenticated(val tokens: OAuth2Tokens, val userInfo: UserInfo?) : OAuth2State()
    data class Error(val message: String, val exception: Throwable? = null) : OAuth2State()
}

/**
 * OAuth2 Token Response
 */
@Serializable
private data class OAuth2TokenResponse(
    val accessToken: String,
    val tokenType: String? = null,
    val expiresIn: Long? = null,
    val refreshToken: String? = null,
    val scope: String? = null
)

/**
 * OAuth2 Error Response
 */
@Serializable
private data class OAuth2ErrorResponse(
    val error: String,
    val errorDescription: String? = null,
    val errorUri: String? = null
)
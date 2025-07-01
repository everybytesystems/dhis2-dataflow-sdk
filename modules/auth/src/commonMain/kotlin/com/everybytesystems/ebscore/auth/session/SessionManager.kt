package com.everybytesystems.ebscore.auth.session

import com.everybytesystems.ebscore.auth.AuthManager
import com.everybytesystems.ebscore.auth.SecureStorage
import com.everybytesystems.ebscore.auth.StoredCredentials
import com.everybytesystems.ebscore.auth.jwt.JwtValidator
import com.everybytesystems.ebscore.auth.oauth2.OAuth2Manager
import com.everybytesystems.ebscore.core.network.ApiResponse
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

/**
 * Production-ready Session Manager with automatic refresh capabilities
 */
class SessionManager(
    private val authManager: AuthManager? = null,
    private val oauth2Manager: OAuth2Manager? = null,
    private val secureStorage: SecureStorage,
    private val jwtValidator: JwtValidator = JwtValidator(),
    private val config: SessionConfig = SessionConfig()
) {
    
    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Inactive)
    val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()
    
    private var refreshJob: Job? = null
    private var sessionScope: CoroutineScope? = null
    
    /**
     * Start session management
     */
    fun startSession(scope: CoroutineScope) {
        sessionScope = scope
        _sessionState.value = SessionState.Active
        
        // Start automatic refresh monitoring
        startRefreshMonitoring(scope)
    }
    
    /**
     * Stop session management
     */
    fun stopSession() {
        refreshJob?.cancel()
        refreshJob = null
        sessionScope = null
        _sessionState.value = SessionState.Inactive
    }
    
    /**
     * Check if session is valid
     */
    suspend fun isSessionValid(): Boolean {
        return try {
            val credentials = secureStorage.getCredentials() ?: return false
            
            // Check if we have valid tokens
            when {
                credentials.bearerToken != null -> {
                    !jwtValidator.isTokenExpired(credentials.bearerToken, config.refreshBufferSeconds)
                }
                credentials.password != null -> {
                    // For basic auth, check if credentials are not expired
                    credentials.expiresAt?.let { expiresAt ->
                        Clock.System.now().toEpochMilliseconds() < expiresAt
                    } ?: true
                }
                else -> false
            }
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Refresh session if needed
     */
    suspend fun refreshIfNeeded(): ApiResponse<Unit> {
        return try {
            val credentials = secureStorage.getCredentials()
                ?: return ApiResponse.Error(RuntimeException("No credentials available"))
            
            when {
                // OAuth2 token refresh
                oauth2Manager != null && credentials.bearerToken != null -> {
                    if (jwtValidator.needsRefresh(credentials.bearerToken, config.refreshBufferSeconds)) {
                        when (val result = oauth2Manager.refreshToken()) {
                            is ApiResponse.Success -> {
                                _sessionState.value = SessionState.Refreshed
                                ApiResponse.Success(Unit)
                            }
                            is ApiResponse.Error -> {
                                _sessionState.value = SessionState.Expired
                                result
                            }
                            is ApiResponse.Loading -> ApiResponse.Loading
                        }
                    } else {
                        ApiResponse.Success(Unit)
                    }
                }
                
                // Basic auth refresh
                authManager != null -> {
                    when (val result = authManager.refreshIfNeeded()) {
                        is ApiResponse.Success -> {
                            _sessionState.value = SessionState.Refreshed
                            ApiResponse.Success(Unit)
                        }
                        is ApiResponse.Error -> {
                            _sessionState.value = SessionState.Expired
                            result
                        }
                        is ApiResponse.Loading -> ApiResponse.Loading
                    }
                }
                
                else -> ApiResponse.Error(RuntimeException("No authentication manager available"))
            }
        } catch (e: Exception) {
            _sessionState.value = SessionState.Error("Session refresh failed", e)
            ApiResponse.Error(e, "Session refresh failed: ${e.message}")
        }
    }
    
    /**
     * Force refresh session
     */
    suspend fun forceRefresh(): ApiResponse<Unit> {
        return try {
            when {
                oauth2Manager != null -> {
                    when (val result = oauth2Manager.refreshToken()) {
                        is ApiResponse.Success -> {
                            _sessionState.value = SessionState.Refreshed
                            ApiResponse.Success(Unit)
                        }
                        is ApiResponse.Error -> {
                            _sessionState.value = SessionState.Expired
                            result
                        }
                        is ApiResponse.Loading -> ApiResponse.Loading
                    }
                }
                
                authManager != null -> {
                    when (val result = authManager.refreshIfNeeded()) {
                        is ApiResponse.Success -> {
                            _sessionState.value = SessionState.Refreshed
                            ApiResponse.Success(Unit)
                        }
                        is ApiResponse.Error -> {
                            _sessionState.value = SessionState.Expired
                            result
                        }
                        is ApiResponse.Loading -> ApiResponse.Loading
                    }
                }
                
                else -> ApiResponse.Error(RuntimeException("No authentication manager available"))
            }
        } catch (e: Exception) {
            _sessionState.value = SessionState.Error("Force refresh failed", e)
            ApiResponse.Error(e, "Force refresh failed: ${e.message}")
        }
    }
    
    /**
     * Get session info
     */
    suspend fun getSessionInfo(): SessionInfo? {
        return try {
            val credentials = secureStorage.getCredentials() ?: return null
            
            val expiresAt = when {
                credentials.bearerToken != null -> {
                    jwtValidator.getTokenExpiration(credentials.bearerToken)?.toEpochMilliseconds()
                }
                else -> credentials.expiresAt
            }
            
            val timeUntilExpiry = expiresAt?.let { exp ->
                maxOf(0, exp - Clock.System.now().toEpochMilliseconds())
            }
            
            SessionInfo(
                isActive = isSessionValid(),
                expiresAt = expiresAt,
                timeUntilExpiry = timeUntilExpiry,
                needsRefresh = credentials.bearerToken?.let { token ->
                    jwtValidator.needsRefresh(token, config.refreshBufferSeconds)
                } ?: false,
                userInfo = credentials.userInfo
            )
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Invalidate session
     */
    suspend fun invalidateSession() {
        try {
            stopSession()
            
            // Logout from auth managers
            authManager?.logout()
            oauth2Manager?.logout()
            
            _sessionState.value = SessionState.Expired
        } catch (e: Exception) {
            _sessionState.value = SessionState.Error("Session invalidation failed", e)
        }
    }
    
    /**
     * Start automatic refresh monitoring
     */
    private fun startRefreshMonitoring(scope: CoroutineScope) {
        refreshJob?.cancel()
        
        refreshJob = scope.launch {
            while (isActive) {
                try {
                    delay(config.refreshCheckInterval * 1000)
                    
                    if (isSessionValid()) {
                        val sessionInfo = getSessionInfo()
                        sessionInfo?.let { info ->
                            if (info.needsRefresh) {
                                refreshIfNeeded()
                            }
                        }
                    } else {
                        _sessionState.value = SessionState.Expired
                        break
                    }
                } catch (e: CancellationException) {
                    break
                } catch (e: Exception) {
                    _sessionState.value = SessionState.Error("Refresh monitoring failed", e)
                    delay(config.retryInterval * 1000)
                }
            }
        }
    }
}

/**
 * Session configuration
 */
@Serializable
data class SessionConfig(
    val refreshBufferSeconds: Long = 300, // 5 minutes
    val refreshCheckInterval: Long = 60,   // Check every minute
    val retryInterval: Long = 30,          // Retry every 30 seconds on error
    val maxRetryAttempts: Int = 3,
    val autoRefreshEnabled: Boolean = true
)

/**
 * Session state
 */
sealed class SessionState {
    object Inactive : SessionState()
    object Active : SessionState()
    object Refreshed : SessionState()
    object Expired : SessionState()
    data class Error(val message: String, val exception: Throwable? = null) : SessionState()
}

/**
 * Session information
 */
@Serializable
data class SessionInfo(
    val isActive: Boolean,
    val expiresAt: Long? = null,
    val timeUntilExpiry: Long? = null,
    val needsRefresh: Boolean = false,
    val userInfo: com.everybytesystems.ebscore.auth.UserInfo? = null
) {
    /**
     * Get time until expiry in minutes
     */
    fun getTimeUntilExpiryMinutes(): Long? {
        return timeUntilExpiry?.let { it / (60 * 1000) }
    }
    
    /**
     * Check if session expires soon
     */
    fun expiresSoon(bufferMinutes: Long = 5): Boolean {
        return getTimeUntilExpiryMinutes()?.let { it <= bufferMinutes } ?: false
    }
}
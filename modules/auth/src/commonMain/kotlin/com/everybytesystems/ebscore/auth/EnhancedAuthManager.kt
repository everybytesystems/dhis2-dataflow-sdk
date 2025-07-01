package com.everybytesystems.ebscore.auth

import com.everybytesystems.ebscore.auth.biometric.BiometricAuthConfig
import com.everybytesystems.ebscore.auth.biometric.BiometricAuthManager
import com.everybytesystems.ebscore.auth.biometric.BiometricAuthResult
import com.everybytesystems.ebscore.auth.jwt.JwtValidator
import com.everybytesystems.ebscore.auth.oauth2.OAuth2Config
import com.everybytesystems.ebscore.auth.oauth2.OAuth2Manager
import com.everybytesystems.ebscore.auth.session.SessionManager
import com.everybytesystems.ebscore.core.config.DHIS2Config
import com.everybytesystems.ebscore.core.network.ApiResponse
import io.ktor.client.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.serialization.Serializable

/**
 * Enhanced Authentication Manager with OAuth2, JWT, Biometric, and Session Management
 */
class EnhancedAuthManager(
    private val config: DHIS2Config,
    private val httpClient: HttpClient,
    private val secureStorage: SecureStorage,
    private val biometricAuthManager: BiometricAuthManager? = null,
    private val jwtValidator: JwtValidator = JwtValidator()
) {
    
    // Core authentication managers
    private val basicAuthManager = AuthManager(config, httpClient, secureStorage)
    private var oauth2Manager: OAuth2Manager? = null
    private lateinit var sessionManager: SessionManager
    
    // State flows
    private val _authState = MutableStateFlow<EnhancedAuthState>(EnhancedAuthState.Unauthenticated)
    val authState: StateFlow<EnhancedAuthState> = _authState.asStateFlow()
    
    private val _biometricState = MutableStateFlow<BiometricAuthState>(BiometricAuthState.Idle)
    val biometricState: StateFlow<BiometricAuthState> = _biometricState.asStateFlow()
    
    /**
     * Initialize the enhanced auth manager
     */
    suspend fun initialize(sessionScope: CoroutineScope) {
        try {
            // Initialize basic auth manager
            basicAuthManager.initialize()
            
            // Initialize session manager
            sessionManager = SessionManager(
                authManager = basicAuthManager,
                oauth2Manager = oauth2Manager,
                secureStorage = secureStorage,
                jwtValidator = jwtValidator
            )
            sessionManager.startSession(sessionScope)
            
            // Observe basic auth state
            basicAuthManager.authState.collect { state ->
                when (state) {
                    is AuthState.Authenticated -> _authState.value = EnhancedAuthState.Authenticated(state.userInfo, AuthMethod.BASIC)
                    is AuthState.Unauthenticated -> _authState.value = EnhancedAuthState.Unauthenticated
                    is AuthState.Loading -> _authState.value = EnhancedAuthState.Loading
                    is AuthState.Error -> _authState.value = EnhancedAuthState.Error(state.message, state.exception)
                }
            }
            
        } catch (e: Exception) {
            _authState.value = EnhancedAuthState.Error("Initialization failed", e)
        }
    }
    
    /**
     * Configure OAuth2 authentication
     */
    fun configureOAuth2(oauth2Config: OAuth2Config) {
        oauth2Manager = OAuth2Manager(oauth2Config, httpClient, secureStorage, jwtValidator)
    }
    
    // MARK: - Basic Authentication
    
    /**
     * Authenticate with username and password
     */
    suspend fun authenticateBasic(username: String, password: String): ApiResponse<UserInfo> {
        _authState.value = EnhancedAuthState.Loading
        
        return when (val result = basicAuthManager.authenticate(username, password)) {
            is ApiResponse.Success -> {
                _authState.value = EnhancedAuthState.Authenticated(result.data, AuthMethod.BASIC)
                result
            }
            is ApiResponse.Error -> {
                _authState.value = EnhancedAuthState.Error("Basic authentication failed", result.exception)
                result
            }
            is ApiResponse.Loading -> result
        }
    }
    
    // MARK: - OAuth2 Authentication
    
    /**
     * Get OAuth2 authorization URL
     */
    fun getOAuth2AuthorizationUrl(state: String? = null): String? {
        return oauth2Manager?.getAuthorizationUrl(state)?.authorizationUrl
    }
    
    /**
     * Exchange OAuth2 authorization code for tokens
     */
    suspend fun authenticateOAuth2(authorizationCode: String, codeVerifier: String? = null): ApiResponse<UserInfo> {
        val manager = oauth2Manager ?: return ApiResponse.Error(RuntimeException("OAuth2 not configured"))
        
        _authState.value = EnhancedAuthState.Loading
        
        return when (val result = manager.exchangeCodeForTokens(authorizationCode, codeVerifier)) {
            is ApiResponse.Success -> {
                val userInfo = manager.getCurrentTokens()?.let { 
                    // Get user info from stored credentials
                    secureStorage.getCredentials()?.userInfo
                }
                
                if (userInfo != null) {
                    _authState.value = EnhancedAuthState.Authenticated(userInfo, AuthMethod.OAUTH2)
                    ApiResponse.Success(userInfo)
                } else {
                    _authState.value = EnhancedAuthState.Error("Failed to get user info", RuntimeException("No user info"))
                    ApiResponse.Error(RuntimeException("Failed to get user info"))
                }
            }
            is ApiResponse.Error -> {
                _authState.value = EnhancedAuthState.Error("OAuth2 authentication failed", result.exception)
                ApiResponse.Error(result.exception, result.message)
            }
            is ApiResponse.Loading -> ApiResponse.Loading
        }
    }
    
    // MARK: - Biometric Authentication
    
    /**
     * Check if biometric authentication is available
     */
    suspend fun isBiometricAvailable(): Boolean {
        return biometricAuthManager?.isBiometricAvailable()?.let { availability ->
            availability == com.everybytesystems.ebscore.auth.biometric.BiometricAvailability.AVAILABLE
        } ?: false
    }
    
    /**
     * Enable biometric authentication
     */
    suspend fun enableBiometricAuth(): ApiResponse<Unit> {
        val manager = biometricAuthManager ?: return ApiResponse.Error(RuntimeException("Biometric auth not available"))
        
        // Get current credentials to store for biometric auth
        val credentials = secureStorage.getCredentials() 
            ?: return ApiResponse.Error(RuntimeException("No credentials to secure with biometrics"))
        
        val credentialsString = "${credentials.username}:${credentials.password ?: credentials.bearerToken ?: ""}"
        
        return when (val result = manager.enableBiometricAuth(credentialsString)) {
            is BiometricAuthResult.Success -> ApiResponse.Success(Unit)
            is BiometricAuthResult.Error -> ApiResponse.Error(RuntimeException("Biometric setup failed: ${result.message}"))
            is BiometricAuthResult.UserCancelled -> ApiResponse.Error(RuntimeException("User cancelled biometric setup"))
            is BiometricAuthResult.AuthenticationFailed -> ApiResponse.Error(RuntimeException("Biometric authentication failed"))
            is BiometricAuthResult.HardwareUnavailable -> ApiResponse.Error(RuntimeException("Biometric hardware unavailable"))
            is BiometricAuthResult.NotEnrolled -> ApiResponse.Error(RuntimeException("No biometrics enrolled"))
        }
    }
    
    /**
     * Authenticate using biometrics
     */
    suspend fun authenticateBiometric(config: BiometricAuthConfig = BiometricAuthConfig()): ApiResponse<UserInfo> {
        val manager = biometricAuthManager ?: return ApiResponse.Error(RuntimeException("Biometric auth not available"))
        
        if (!manager.isBiometricAuthEnabled()) {
            return ApiResponse.Error(RuntimeException("Biometric auth not enabled"))
        }
        
        _authState.value = EnhancedAuthState.Loading
        _biometricState.value = BiometricAuthState.Authenticating
        
        return when (val result = manager.authenticate(config)) {
            is BiometricAuthResult.Success -> {
                _biometricState.value = BiometricAuthState.Authenticated
                
                // Get stored credentials and authenticate
                val credentials = secureStorage.getCredentials()
                if (credentials != null && credentials.userInfo != null) {
                    _authState.value = EnhancedAuthState.Authenticated(credentials.userInfo, AuthMethod.BIOMETRIC)
                    ApiResponse.Success(credentials.userInfo)
                } else {
                    _authState.value = EnhancedAuthState.Error("No stored credentials", RuntimeException("No credentials"))
                    ApiResponse.Error(RuntimeException("No stored credentials for biometric auth"))
                }
            }
            is BiometricAuthResult.Error -> {
                _biometricState.value = BiometricAuthState.Error(result.message)
                _authState.value = EnhancedAuthState.Error("Biometric authentication failed", RuntimeException(result.message))
                ApiResponse.Error(RuntimeException("Biometric auth failed: ${result.message}"))
            }
            is BiometricAuthResult.UserCancelled -> {
                _biometricState.value = BiometricAuthState.Idle
                _authState.value = EnhancedAuthState.Unauthenticated
                ApiResponse.Error(RuntimeException("User cancelled biometric authentication"))
            }
            is BiometricAuthResult.AuthenticationFailed -> {
                _biometricState.value = BiometricAuthState.Error("Authentication failed")
                _authState.value = EnhancedAuthState.Error("Biometric authentication failed", RuntimeException("Authentication failed"))
                ApiResponse.Error(RuntimeException("Biometric authentication failed"))
            }
            is BiometricAuthResult.HardwareUnavailable -> {
                _biometricState.value = BiometricAuthState.Error("Hardware unavailable")
                _authState.value = EnhancedAuthState.Error("Biometric hardware unavailable", RuntimeException("Hardware unavailable"))
                ApiResponse.Error(RuntimeException("Biometric hardware unavailable"))
            }
            is BiometricAuthResult.NotEnrolled -> {
                _biometricState.value = BiometricAuthState.Error("Not enrolled")
                _authState.value = EnhancedAuthState.Error("No biometrics enrolled", RuntimeException("Not enrolled"))
                ApiResponse.Error(RuntimeException("No biometrics enrolled"))
            }
        }
    }
    
    /**
     * Disable biometric authentication
     */
    suspend fun disableBiometricAuth(): Boolean {
        return biometricAuthManager?.disableBiometricAuth() ?: false
    }
    
    // MARK: - Session Management
    
    /**
     * Refresh session if needed
     */
    suspend fun refreshSession(): ApiResponse<Unit> {
        return sessionManager.refreshIfNeeded()
    }
    
    /**
     * Get session information
     */
    suspend fun getSessionInfo(): com.everybytesystems.ebscore.auth.session.SessionInfo? {
        return sessionManager.getSessionInfo()
    }
    
    /**
     * Check if session is valid
     */
    suspend fun isSessionValid(): Boolean {
        return sessionManager.isSessionValid()
    }
    
    // MARK: - General Methods
    
    /**
     * Logout from all authentication methods
     */
    suspend fun logout() {
        try {
            basicAuthManager.logout()
            oauth2Manager?.logout()
            biometricAuthManager?.disableBiometricAuth()
            sessionManager.invalidateSession()
            
            _authState.value = EnhancedAuthState.Unauthenticated
            _biometricState.value = BiometricAuthState.Idle
        } catch (e: Exception) {
            _authState.value = EnhancedAuthState.Error("Logout failed", e)
        }
    }
    
    /**
     * Get current user info
     */
    fun getCurrentUser(): UserInfo? {
        return when (val state = _authState.value) {
            is EnhancedAuthState.Authenticated -> state.userInfo
            else -> null
        }
    }
    
    /**
     * Check if user is authenticated
     */
    fun isAuthenticated(): Boolean {
        return _authState.value is EnhancedAuthState.Authenticated
    }
    
    /**
     * Get current authentication method
     */
    fun getCurrentAuthMethod(): AuthMethod? {
        return when (val state = _authState.value) {
            is EnhancedAuthState.Authenticated -> state.method
            else -> null
        }
    }
}

/**
 * Enhanced authentication state
 */
sealed class EnhancedAuthState {
    object Unauthenticated : EnhancedAuthState()
    object Loading : EnhancedAuthState()
    data class Authenticated(val userInfo: UserInfo, val method: AuthMethod) : EnhancedAuthState()
    data class Error(val message: String, val exception: Throwable? = null) : EnhancedAuthState()
}

/**
 * Authentication method
 */
enum class AuthMethod {
    BASIC,
    OAUTH2,
    BIOMETRIC,
    JWT
}

/**
 * Biometric authentication state
 */
sealed class BiometricAuthState {
    object Idle : BiometricAuthState()
    object Authenticating : BiometricAuthState()
    object Authenticated : BiometricAuthState()
    data class Error(val message: String, val exception: Throwable? = null) : BiometricAuthState()
}
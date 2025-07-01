package com.everybytesystems.ebscore.auth.biometric

import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable

/**
 * Biometric authentication manager interface
 */
interface BiometricAuthManager {
    
    /**
     * Check if biometric authentication is available on the device
     */
    suspend fun isBiometricAvailable(): BiometricAvailability
    
    /**
     * Check if biometric authentication is enrolled (user has set up biometrics)
     */
    suspend fun isBiometricEnrolled(): Boolean
    
    /**
     * Authenticate using biometrics
     */
    suspend fun authenticate(config: BiometricAuthConfig): BiometricAuthResult
    
    /**
     * Enable biometric authentication for the app
     */
    suspend fun enableBiometricAuth(credentials: String): BiometricAuthResult
    
    /**
     * Disable biometric authentication
     */
    suspend fun disableBiometricAuth(): Boolean
    
    /**
     * Check if biometric auth is enabled for the app
     */
    fun isBiometricAuthEnabled(): Boolean
    
    /**
     * Get biometric authentication state
     */
    val authState: StateFlow<BiometricAuthState>
}

/**
 * Factory for creating platform-specific biometric auth managers
 */
expect class BiometricAuthManagerFactory {
    fun create(): BiometricAuthManager
}

/**
 * Biometric authentication configuration
 */
@Serializable
data class BiometricAuthConfig(
    val title: String = "Biometric Authentication",
    val subtitle: String = "Use your biometric to authenticate",
    val description: String = "Place your finger on the sensor or look at the camera",
    val negativeButtonText: String = "Cancel",
    val allowDeviceCredential: Boolean = true,
    val confirmationRequired: Boolean = true
)

/**
 * Biometric availability status
 */
enum class BiometricAvailability {
    AVAILABLE,
    NOT_AVAILABLE,
    NOT_ENROLLED,
    HARDWARE_NOT_PRESENT,
    HARDWARE_UNAVAILABLE,
    SECURITY_UPDATE_REQUIRED,
    UNKNOWN
}

/**
 * Biometric authentication result
 */
sealed class BiometricAuthResult {
    object Success : BiometricAuthResult()
    data class Error(val errorCode: Int, val message: String) : BiometricAuthResult()
    object UserCancelled : BiometricAuthResult()
    object AuthenticationFailed : BiometricAuthResult()
    object HardwareUnavailable : BiometricAuthResult()
    object NotEnrolled : BiometricAuthResult()
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

/**
 * Biometric authentication constants
 */
object BiometricConstants {
    // Error codes
    const val ERROR_HARDWARE_UNAVAILABLE = 1
    const val ERROR_UNABLE_TO_PROCESS = 2
    const val ERROR_TIMEOUT = 3
    const val ERROR_NO_SPACE = 4
    const val ERROR_CANCELED = 5
    const val ERROR_LOCKOUT = 7
    const val ERROR_VENDOR = 8
    const val ERROR_LOCKOUT_PERMANENT = 9
    const val ERROR_USER_CANCELED = 10
    const val ERROR_NO_BIOMETRICS = 11
    const val ERROR_HW_NOT_PRESENT = 12
    const val ERROR_NEGATIVE_BUTTON = 13
    const val ERROR_NO_DEVICE_CREDENTIAL = 14
    const val ERROR_SECURITY_UPDATE_REQUIRED = 15
}
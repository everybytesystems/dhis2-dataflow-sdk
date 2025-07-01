package com.everybytesystems.ebscore.auth.biometric

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.Foundation.NSUserDefaults
import platform.LocalAuthentication.LAContext
import platform.LocalAuthentication.LAError
import platform.LocalAuthentication.LAPolicyDeviceOwnerAuthenticationWithBiometrics
import platform.LocalAuthentication.kLAErrorAuthenticationFailed
import platform.LocalAuthentication.kLAErrorBiometryNotAvailable
import platform.LocalAuthentication.kLAErrorBiometryNotEnrolled
import platform.LocalAuthentication.kLAErrorSystemCancel
import platform.LocalAuthentication.kLAErrorUserCancel
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * iOS implementation of BiometricAuthManager using LocalAuthentication framework
 */
class IOSBiometricAuthManager : BiometricAuthManager {
    
    private val _authState = MutableStateFlow<BiometricAuthState>(BiometricAuthState.Idle)
    override val authState: StateFlow<BiometricAuthState> = _authState.asStateFlow()
    
    private val context = LAContext()
    private val userDefaults = NSUserDefaults.standardUserDefaults
    
    override suspend fun isBiometricAvailable(): BiometricAvailability {
        return suspendCoroutine { continuation ->
            val error = kotlin.native.ref.createCleaner(null) { }
            
            val canEvaluate = context.canEvaluatePolicy(
                LAPolicyDeviceOwnerAuthenticationWithBiometrics,
                error = null
            )
            
            if (canEvaluate) {
                continuation.resume(BiometricAvailability.AVAILABLE)
            } else {
                // Check specific error
                continuation.resume(BiometricAvailability.NOT_AVAILABLE)
            }
        }
    }
    
    override suspend fun isBiometricEnrolled(): Boolean {
        return suspendCoroutine { continuation ->
            val canEvaluate = context.canEvaluatePolicy(
                LAPolicyDeviceOwnerAuthenticationWithBiometrics,
                error = null
            )
            continuation.resume(canEvaluate)
        }
    }
    
    override suspend fun authenticate(config: BiometricAuthConfig): BiometricAuthResult {
        _authState.value = BiometricAuthState.Authenticating
        
        return suspendCoroutine { continuation ->
            context.evaluatePolicy(
                policy = LAPolicyDeviceOwnerAuthenticationWithBiometrics,
                localizedReason = config.description
            ) { success, error ->
                if (success) {
                    _authState.value = BiometricAuthState.Authenticated
                    continuation.resume(BiometricAuthResult.Success)
                } else {
                    val errorCode = (error as? LAError)?.code?.toInt() ?: -1
                    val errorMessage = error?.localizedDescription ?: "Unknown error"
                    
                    _authState.value = BiometricAuthState.Error(errorMessage)
                    
                    val result = when (errorCode) {
                        kLAErrorUserCancel.toInt(),
                        kLAErrorSystemCancel.toInt() -> BiometricAuthResult.UserCancelled
                        kLAErrorBiometryNotAvailable.toInt() -> BiometricAuthResult.HardwareUnavailable
                        kLAErrorBiometryNotEnrolled.toInt() -> BiometricAuthResult.NotEnrolled
                        kLAErrorAuthenticationFailed.toInt() -> BiometricAuthResult.AuthenticationFailed
                        else -> BiometricAuthResult.Error(errorCode, errorMessage)
                    }
                    
                    continuation.resume(result)
                }
            }
        }
    }
    
    override suspend fun enableBiometricAuth(credentials: String): BiometricAuthResult {
        return try {
            // First authenticate with biometrics
            val authResult = authenticate(
                BiometricAuthConfig(
                    title = "Enable Biometric Authentication",
                    description = "Authenticate to enable biometric login for future use"
                )
            )
            
            if (authResult is BiometricAuthResult.Success) {
                // Store that biometric auth is enabled
                userDefaults.setBool(true, "biometric_enabled")
                userDefaults.setObject(credentials, "biometric_credentials")
                userDefaults.synchronize()
            }
            
            authResult
        } catch (e: Exception) {
            BiometricAuthResult.Error(-1, "Failed to enable biometric auth: ${e.message}")
        }
    }
    
    override suspend fun disableBiometricAuth(): Boolean {
        return try {
            userDefaults.removeObjectForKey("biometric_enabled")
            userDefaults.removeObjectForKey("biometric_credentials")
            userDefaults.synchronize()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    override fun isBiometricAuthEnabled(): Boolean {
        return userDefaults.boolForKey("biometric_enabled")
    }
}

/**
 * iOS implementation of BiometricAuthManagerFactory
 */
actual class BiometricAuthManagerFactory {
    actual fun create(): BiometricAuthManager {
        return IOSBiometricAuthManager()
    }
}
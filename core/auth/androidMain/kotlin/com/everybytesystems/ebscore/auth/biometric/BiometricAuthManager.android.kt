package com.everybytesystems.ebscore.auth.biometric

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.Executor
import kotlin.coroutines.resume

/**
 * Android implementation of BiometricAuthManager
 */
class AndroidBiometricAuthManager(
    private val context: Context
) : BiometricAuthManager {
    
    private val _authState = MutableStateFlow<BiometricAuthState>(BiometricAuthState.Idle)
    override val authState: StateFlow<BiometricAuthState> = _authState.asStateFlow()
    
    private val biometricManager = BiometricManager.from(context)
    private val sharedPrefs = context.getSharedPreferences("ebscore_biometric", Context.MODE_PRIVATE)
    
    override suspend fun isBiometricAvailable(): BiometricAvailability {
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricAvailability.AVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricAvailability.HARDWARE_NOT_PRESENT
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricAvailability.HARDWARE_UNAVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricAvailability.NOT_ENROLLED
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> BiometricAvailability.SECURITY_UPDATE_REQUIRED
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> BiometricAvailability.NOT_AVAILABLE
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> BiometricAvailability.UNKNOWN
            else -> BiometricAvailability.UNKNOWN
        }
    }
    
    override suspend fun isBiometricEnrolled(): Boolean {
        return biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS
    }
    
    override suspend fun authenticate(config: BiometricAuthConfig): BiometricAuthResult {
        val activity = getCurrentActivity() ?: return BiometricAuthResult.Error(
            BiometricConstants.ERROR_HARDWARE_UNAVAILABLE,
            "No activity available for biometric authentication"
        )
        
        _authState.value = BiometricAuthState.Authenticating
        
        return suspendCancellableCoroutine { continuation ->
            val executor = context.mainExecutor
            
            val biometricPrompt = BiometricPrompt(
                activity,
                executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        _authState.value = BiometricAuthState.Error(errString.toString())
                        
                        val result = when (errorCode) {
                            BiometricPrompt.ERROR_USER_CANCELED,
                            BiometricPrompt.ERROR_NEGATIVE_BUTTON -> BiometricAuthResult.UserCancelled
                            BiometricPrompt.ERROR_HW_UNAVAILABLE,
                            BiometricPrompt.ERROR_HW_NOT_PRESENT -> BiometricAuthResult.HardwareUnavailable
                            BiometricPrompt.ERROR_NO_BIOMETRICS -> BiometricAuthResult.NotEnrolled
                            else -> BiometricAuthResult.Error(errorCode, errString.toString())
                        }
                        
                        continuation.resume(result)
                    }
                    
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        _authState.value = BiometricAuthState.Authenticated
                        continuation.resume(BiometricAuthResult.Success)
                    }
                    
                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        _authState.value = BiometricAuthState.Error("Authentication failed")
                        continuation.resume(BiometricAuthResult.AuthenticationFailed)
                    }
                }
            )
            
            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(config.title)
                .setSubtitle(config.subtitle)
                .setDescription(config.description)
                .setNegativeButtonText(config.negativeButtonText)
                .setConfirmationRequired(config.confirmationRequired)
                .apply {
                    if (config.allowDeviceCredential) {
                        setAllowedAuthenticators(
                            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                            BiometricManager.Authenticators.DEVICE_CREDENTIAL
                        )
                    } else {
                        setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                    }
                }
                .build()
            
            try {
                biometricPrompt.authenticate(promptInfo)
            } catch (e: Exception) {
                _authState.value = BiometricAuthState.Error("Failed to start biometric authentication", e)
                continuation.resume(BiometricAuthResult.Error(-1, e.message ?: "Unknown error"))
            }
            
            continuation.invokeOnCancellation {
                try {
                    biometricPrompt.cancelAuthentication()
                } catch (e: Exception) {
                    // Ignore cancellation errors
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
                    subtitle = "Authenticate to enable biometric login",
                    description = "This will allow you to use biometrics for future logins"
                )
            )
            
            if (authResult is BiometricAuthResult.Success) {
                // Store that biometric auth is enabled
                sharedPrefs.edit()
                    .putBoolean("biometric_enabled", true)
                    .putString("biometric_credentials", credentials)
                    .apply()
            }
            
            authResult
        } catch (e: Exception) {
            BiometricAuthResult.Error(-1, "Failed to enable biometric auth: ${e.message}")
        }
    }
    
    override suspend fun disableBiometricAuth(): Boolean {
        return try {
            sharedPrefs.edit()
                .remove("biometric_enabled")
                .remove("biometric_credentials")
                .apply()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    override fun isBiometricAuthEnabled(): Boolean {
        return sharedPrefs.getBoolean("biometric_enabled", false)
    }
    
    private fun getCurrentActivity(): FragmentActivity? {
        return try {
            // This is a simplified approach. In a real implementation,
            // you would need to track the current activity properly
            context as? FragmentActivity
        } catch (e: Exception) {
            null
        }
    }
}

/**
 * Android implementation of BiometricAuthManagerFactory
 */
actual class BiometricAuthManagerFactory(private val context: Context) {
    actual fun create(): BiometricAuthManager {
        return AndroidBiometricAuthManager(context)
    }
}
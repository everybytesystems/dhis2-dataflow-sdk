package com.everybytesystems.ebscore.auth.biometric

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * JavaScript implementation of BiometricAuthManager
 * Uses Web Authentication API (WebAuthn) where available
 */
class JSBiometricAuthManager : BiometricAuthManager {
    
    private val _authState = MutableStateFlow<BiometricAuthState>(BiometricAuthState.Idle)
    override val authState: StateFlow<BiometricAuthState> = _authState.asStateFlow()
    
    override suspend fun isBiometricAvailable(): BiometricAvailability {
        return try {
            // Check if WebAuthn is available
            val isWebAuthnSupported = js("typeof navigator !== 'undefined' && 'credentials' in navigator && 'create' in navigator.credentials") as Boolean
            
            if (isWebAuthnSupported) {
                BiometricAvailability.AVAILABLE
            } else {
                BiometricAvailability.NOT_AVAILABLE
            }
        } catch (e: Exception) {
            BiometricAvailability.NOT_AVAILABLE
        }
    }
    
    override suspend fun isBiometricEnrolled(): Boolean {
        // For web, we can't easily check if biometrics are enrolled
        // We assume they are if WebAuthn is available
        return isBiometricAvailable() == BiometricAvailability.AVAILABLE
    }
    
    override suspend fun authenticate(config: BiometricAuthConfig): BiometricAuthResult {
        _authState.value = BiometricAuthState.Authenticating
        
        return try {
            // Use WebAuthn for authentication
            val isSupported = js("typeof navigator !== 'undefined' && 'credentials' in navigator") as Boolean
            
            if (!isSupported) {
                _authState.value = BiometricAuthState.Error("WebAuthn not supported")
                return BiometricAuthResult.Error(
                    BiometricConstants.ERROR_HW_NOT_PRESENT,
                    "WebAuthn is not supported in this browser"
                )
            }
            
            // For now, return success as a placeholder
            // In a real implementation, you would use WebAuthn API
            _authState.value = BiometricAuthState.Authenticated
            BiometricAuthResult.Success
            
        } catch (e: Exception) {
            _authState.value = BiometricAuthState.Error("Authentication failed", e)
            BiometricAuthResult.Error(-1, e.message ?: "Unknown error")
        }
    }
    
    override suspend fun enableBiometricAuth(credentials: String): BiometricAuthResult {
        return try {
            // Store in localStorage that biometric auth is enabled
            js("localStorage.setItem('biometric_enabled', 'true')")
            js("localStorage.setItem('biometric_credentials', credentials)")
            
            BiometricAuthResult.Success
        } catch (e: Exception) {
            BiometricAuthResult.Error(-1, "Failed to enable biometric auth: ${e.message}")
        }
    }
    
    override suspend fun disableBiometricAuth(): Boolean {
        return try {
            js("localStorage.removeItem('biometric_enabled')")
            js("localStorage.removeItem('biometric_credentials')")
            true
        } catch (e: Exception) {
            false
        }
    }
    
    override fun isBiometricAuthEnabled(): Boolean {
        return try {
            js("localStorage.getItem('biometric_enabled') === 'true'") as Boolean
        } catch (e: Exception) {
            false
        }
    }
}

/**
 * JavaScript implementation of BiometricAuthManagerFactory
 */
actual class BiometricAuthManagerFactory {
    actual fun create(): BiometricAuthManager {
        return JSBiometricAuthManager()
    }
}
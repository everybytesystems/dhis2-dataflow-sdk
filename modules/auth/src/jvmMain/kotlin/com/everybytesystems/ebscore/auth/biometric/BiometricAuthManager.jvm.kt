package com.everybytesystems.ebscore.auth.biometric

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * JVM implementation of BiometricAuthManager (stub - biometrics not supported on desktop)
 */
class JVMBiometricAuthManager : BiometricAuthManager {
    
    private val _authState = MutableStateFlow<BiometricAuthState>(BiometricAuthState.Idle)
    override val authState: StateFlow<BiometricAuthState> = _authState.asStateFlow()
    
    override suspend fun isBiometricAvailable(): BiometricAvailability {
        return BiometricAvailability.NOT_AVAILABLE
    }
    
    override suspend fun isBiometricEnrolled(): Boolean {
        return false
    }
    
    override suspend fun authenticate(config: BiometricAuthConfig): BiometricAuthResult {
        return BiometricAuthResult.Error(
            BiometricConstants.ERROR_HW_NOT_PRESENT,
            "Biometric authentication is not supported on desktop platforms"
        )
    }
    
    override suspend fun enableBiometricAuth(credentials: String): BiometricAuthResult {
        return BiometricAuthResult.Error(
            BiometricConstants.ERROR_HW_NOT_PRESENT,
            "Biometric authentication is not supported on desktop platforms"
        )
    }
    
    override suspend fun disableBiometricAuth(): Boolean {
        return false
    }
    
    override fun isBiometricAuthEnabled(): Boolean {
        return false
    }
}

/**
 * JVM implementation of BiometricAuthManagerFactory
 */
actual class BiometricAuthManagerFactory {
    actual fun create(): BiometricAuthManager {
        return JVMBiometricAuthManager()
    }
}
package com.everybytesystems.ebscore.sdk.platform

import com.everybytesystems.ebscore.auth.SecureStorage
import com.everybytesystems.ebscore.auth.impl.InMemorySecureStorage

/**
 * iOS platform implementation of PlatformFactory
 */
actual object PlatformFactory {
    actual fun createSecureStorage(): SecureStorage {
        // TODO: Implement iOS Keychain-based secure storage
        // For now, using in-memory storage
        return InMemorySecureStorage()
    }
}
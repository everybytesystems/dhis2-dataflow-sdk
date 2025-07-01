package com.everybytesystems.ebscore.sdk.platform

import com.everybytesystems.ebscore.auth.SecureStorage
import com.everybytesystems.ebscore.auth.SecureStorageFactory

/**
 * Android implementation of platform-specific factory functions
 */
actual object PlatformFactory {
    actual fun createSecureStorage(): SecureStorage {
        return SecureStorageFactory().create()
    }
}
package com.everybytesystems.ebscore.sdk.platform

import com.everybytesystems.ebscore.auth.SecureStorage
import com.everybytesystems.ebscore.auth.SecureStorageFactory

/**
 * iOS platform implementation of PlatformFactory
 */
actual object PlatformFactory {
    actual fun createSecureStorage(): SecureStorage {
        return SecureStorageFactory().create()
    }
}
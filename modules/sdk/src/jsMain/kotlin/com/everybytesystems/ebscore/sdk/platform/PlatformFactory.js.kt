package com.everybytesystems.ebscore.sdk.platform

import com.everybytesystems.ebscore.auth.SecureStorage
import com.everybytesystems.ebscore.auth.impl.InMemorySecureStorage

/**
 * JavaScript platform implementation of PlatformFactory
 */
actual object PlatformFactory {
    actual fun createSecureStorage(): SecureStorage {
        // For JavaScript, we use in-memory storage as browsers handle security differently
        return InMemorySecureStorage()
    }
}
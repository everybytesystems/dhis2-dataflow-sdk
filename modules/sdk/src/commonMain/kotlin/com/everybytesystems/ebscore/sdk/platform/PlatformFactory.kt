package com.everybytesystems.ebscore.sdk.platform

import com.everybytesystems.ebscore.auth.SecureStorage

/**
 * Platform-specific factory functions
 */
expect object PlatformFactory {
    fun createSecureStorage(): SecureStorage
}
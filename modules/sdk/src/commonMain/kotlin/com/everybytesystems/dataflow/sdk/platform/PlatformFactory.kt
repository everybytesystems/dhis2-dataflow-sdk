package com.everybytesystems.dataflow.sdk.platform

import com.everybytesystems.dataflow.auth.SecureStorage

/**
 * Platform-specific factory functions
 */
expect object PlatformFactory {
    fun createSecureStorage(): SecureStorage
}
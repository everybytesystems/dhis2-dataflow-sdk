package com.everybytesystems.dataflow.auth

/**
 * Factory for creating platform-specific secure storage implementations
 */
expect class SecureStorageFactory {
    fun create(): SecureStorage
}

/**
 * Configuration for secure storage
 */
data class SecureStorageConfig(
    val keyAlias: String = "dhis2_dataflow_auth",
    val serviceName: String = "DHIS2DataFlowSDK",
    val encryptionRequired: Boolean = true
)
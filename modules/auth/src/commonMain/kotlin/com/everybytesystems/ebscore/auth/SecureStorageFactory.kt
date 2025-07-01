package com.everybytesystems.ebscore.auth

import kotlinx.serialization.Serializable

/**
 * Interface for secure storage of authentication credentials
 */
interface SecureStorage {
    suspend fun storeCredentials(credentials: StoredCredentials)
    suspend fun getCredentials(): StoredCredentials?
    suspend fun clearCredentials()
}

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

/**
 * Stored authentication credentials
 */
@Serializable
data class StoredCredentials(
    val username: String,
    val password: String? = null,
    val bearerToken: String? = null,
    val refreshToken: String? = null,
    val expiresAt: Long? = null,
    val userInfo: UserInfo? = null,
    val createdAt: Long = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
)

/**
 * User information
 */
@Serializable
data class UserInfo(
    val id: String,
    val username: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val organisationUnits: List<String> = emptyList(),
    val userRoles: List<String> = emptyList()
)
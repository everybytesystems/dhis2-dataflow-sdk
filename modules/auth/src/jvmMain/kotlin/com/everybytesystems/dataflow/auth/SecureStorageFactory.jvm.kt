package com.everybytesystems.dataflow.auth

import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import java.io.File
import java.util.prefs.Preferences

/**
 * JVM implementation of SecureStorageFactory
 */
actual class SecureStorageFactory {
    actual fun create(): SecureStorage {
        return JvmSecureStorage()
    }
}

/**
 * JVM implementation using Java Preferences API
 * Note: This is a basic implementation. For production use, consider using
 * more secure storage mechanisms like encrypted files or system keystore
 */
class JvmSecureStorage : SecureStorage {
    private val prefs = Preferences.userNodeForPackage(JvmSecureStorage::class.java)
    private val json = Json { ignoreUnknownKeys = true }
    
    companion object {
        private const val CREDENTIALS_KEY = "dhis2_credentials"
    }
    
    override suspend fun storeCredentials(credentials: StoredCredentials) {
        try {
            val credentialsJson = json.encodeToString(credentials)
            prefs.put(CREDENTIALS_KEY, credentialsJson)
            prefs.flush()
        } catch (e: Exception) {
            throw SecurityException("Failed to store credentials", e)
        }
    }
    
    override suspend fun getCredentials(): StoredCredentials? {
        return try {
            val credentialsJson = prefs.get(CREDENTIALS_KEY, null)
            if (credentialsJson != null) {
                json.decodeFromString<StoredCredentials>(credentialsJson)
            } else {
                null
            }
        } catch (e: Exception) {
            null // Return null if credentials can't be retrieved
        }
    }
    
    override suspend fun clearCredentials() {
        try {
            prefs.remove(CREDENTIALS_KEY)
            prefs.flush()
        } catch (e: Exception) {
            // Ignore errors when clearing
        }
    }
}
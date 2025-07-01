package com.everybytesystems.ebscore.auth

import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import platform.Foundation.*

actual class SecureStorageFactory {
    actual fun create(): SecureStorage = NativeSecureStorage()
}

private class NativeSecureStorage : SecureStorage {
    private val json = Json { ignoreUnknownKeys = true }
    private val storageKey = "dhis2_dataflow_credentials"
    
    override suspend fun storeCredentials(credentials: StoredCredentials) {
        try {
            val jsonString = json.encodeToString(credentials)
            val userDefaults = NSUserDefaults.standardUserDefaults
            userDefaults.setObject(jsonString, storageKey)
            userDefaults.synchronize()
        } catch (e: Exception) {
            throw Exception("Failed to store credentials: ${e.message}")
        }
    }
    
    override suspend fun getCredentials(): StoredCredentials? {
        return try {
            val userDefaults = NSUserDefaults.standardUserDefaults
            val jsonString = userDefaults.stringForKey(storageKey) ?: return null
            json.decodeFromString<StoredCredentials>(jsonString)
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun clearCredentials() {
        try {
            val userDefaults = NSUserDefaults.standardUserDefaults
            userDefaults.removeObjectForKey(storageKey)
            userDefaults.synchronize()
        } catch (e: Exception) {
            // Ignore errors when clearing
        }
    }
}
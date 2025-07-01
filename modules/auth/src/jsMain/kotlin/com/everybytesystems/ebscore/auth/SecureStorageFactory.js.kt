package com.everybytesystems.ebscore.auth

import kotlinx.browser.localStorage
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

actual class SecureStorageFactory {
    actual fun create(): SecureStorage = JsSecureStorage()
}

private class JsSecureStorage : SecureStorage {
    private val json = Json { ignoreUnknownKeys = true }
    private val storageKey = "dhis2_dataflow_credentials"
    
    override suspend fun storeCredentials(credentials: StoredCredentials) {
        try {
            val jsonString = json.encodeToString(credentials)
            localStorage.setItem(storageKey, jsonString)
        } catch (e: Exception) {
            throw Exception("Failed to store credentials: ${e.message}")
        }
    }
    
    override suspend fun getCredentials(): StoredCredentials? {
        return try {
            val jsonString = localStorage.getItem(storageKey) ?: return null
            json.decodeFromString<StoredCredentials>(jsonString)
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun clearCredentials() {
        localStorage.removeItem(storageKey)
    }
}
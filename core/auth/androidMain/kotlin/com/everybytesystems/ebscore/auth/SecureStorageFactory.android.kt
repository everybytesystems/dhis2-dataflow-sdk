package com.everybytesystems.ebscore.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

actual class SecureStorageFactory {
    actual fun create(): SecureStorage = AndroidSecureStorage()
}

/**
 * Android-specific secure storage implementation
 * Note: This requires Context injection for production use
 */
class AndroidSecureStorage(
    private val context: Context? = null
) : SecureStorage {
    
    private val json = Json { ignoreUnknownKeys = true }
    private val prefsKey = "dhis2_credentials"
    
    private fun getEncryptedPrefs(): SharedPreferences? {
        return context?.let { ctx ->
            val masterKey = MasterKey.Builder(ctx)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
                
            EncryptedSharedPreferences.create(
                ctx,
                "dhis2_dataflow_secure_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
    }
    
    override suspend fun storeCredentials(credentials: StoredCredentials) {
        try {
            val prefs = getEncryptedPrefs()
            if (prefs != null) {
                val credentialsJson = json.encodeToString(credentials)
                prefs.edit().putString(prefsKey, credentialsJson).apply()
            } else {
                // Fallback to in-memory storage for testing
                inMemoryCredentials = credentials
            }
        } catch (e: Exception) {
            throw Exception("Failed to store credentials: ${e.message}")
        }
    }
    
    override suspend fun getCredentials(): StoredCredentials? {
        return try {
            val prefs = getEncryptedPrefs()
            if (prefs != null) {
                val credentialsJson = prefs.getString(prefsKey, null)
                credentialsJson?.let { json.decodeFromString<StoredCredentials>(it) }
            } else {
                // Fallback to in-memory storage for testing
                inMemoryCredentials
            }
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun clearCredentials() {
        try {
            val prefs = getEncryptedPrefs()
            if (prefs != null) {
                prefs.edit().remove(prefsKey).apply()
            } else {
                // Fallback to in-memory storage for testing
                inMemoryCredentials = null
            }
        } catch (e: Exception) {
            // Ignore errors when clearing
        }
    }
    
    companion object {
        // In-memory fallback for testing without Context
        private var inMemoryCredentials: StoredCredentials? = null
    }
}
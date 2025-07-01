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

private class AndroidSecureStorage : SecureStorage {
    private val json = Json { ignoreUnknownKeys = true }
    private val prefsKey = "dhis2_credentials"
    
    private fun getEncryptedPrefs(context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
            
        return EncryptedSharedPreferences.create(
            context,
            "dhis2_dataflow_secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
    
    override suspend fun storeCredentials(credentials: StoredCredentials) {
        try {
            // Note: In a real implementation, you'd need to pass the Android Context
            // For now, this is a placeholder that would need proper Context injection
            throw NotImplementedError("Android SecureStorage requires Context injection")
        } catch (e: Exception) {
            throw SecurityException("Failed to store credentials: ${e.message}")
        }
    }
    
    override suspend fun getCredentials(): StoredCredentials? {
        return try {
            // Note: In a real implementation, you'd need to pass the Android Context
            // For now, this is a placeholder that would need proper Context injection
            throw NotImplementedError("Android SecureStorage requires Context injection")
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun clearCredentials() {
        try {
            // Note: In a real implementation, you'd need to pass the Android Context
            // For now, this is a placeholder that would need proper Context injection
            throw NotImplementedError("Android SecureStorage requires Context injection")
        } catch (e: Exception) {
            // Ignore errors when clearing
        }
    }
}
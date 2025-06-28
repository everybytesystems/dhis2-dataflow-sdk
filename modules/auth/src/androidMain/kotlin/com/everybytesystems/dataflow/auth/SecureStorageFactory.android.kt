package com.everybytesystems.dataflow.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

actual class SecureStorageFactory(
    private val context: Context,
    private val config: SecureStorageConfig = SecureStorageConfig()
) {
    actual fun create(): SecureStorage {
        return AndroidSecureStorage(context, config)
    }
}

/**
 * Android implementation using EncryptedSharedPreferences
 */
class AndroidSecureStorage(
    private val context: Context,
    private val config: SecureStorageConfig,
    private val json: Json = Json { ignoreUnknownKeys = true }
) : SecureStorage {
    
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "dhis2_auth_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    override suspend fun storeCredentials(credentials: StoredCredentials) {
        try {
            val credentialsJson = json.encodeToString(credentials)
            sharedPreferences.edit()
                .putString(CREDENTIALS_KEY, credentialsJson)
                .apply()
        } catch (e: Exception) {
            throw SecurityException("Failed to store credentials securely", e)
        }
    }
    
    override suspend fun getCredentials(): StoredCredentials? {
        return try {
            val credentialsJson = sharedPreferences.getString(CREDENTIALS_KEY, null)
            credentialsJson?.let { json.decodeFromString<StoredCredentials>(it) }
        } catch (e: Exception) {
            // If decryption fails, clear corrupted data
            clearCredentials()
            null
        }
    }
    
    override suspend fun clearCredentials() {
        sharedPreferences.edit()
            .remove(CREDENTIALS_KEY)
            .apply()
    }
    
    companion object {
        private const val CREDENTIALS_KEY = "stored_credentials"
    }
}
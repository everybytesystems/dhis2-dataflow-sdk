package com.everybytesystems.ebscore.auth

import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.util.Base64

actual class SecureStorageFactory {
    actual fun create(): SecureStorage = JvmSecureStorage()
}

private class JvmSecureStorage : SecureStorage {
    private val json = Json { ignoreUnknownKeys = true }
    private val storageDir = System.getProperty("user.home") + "/.dhis2_dataflow"
    private val credentialsFile = "$storageDir/credentials.enc"
    private val keyFile = "$storageDir/key.enc"
    
    init {
        // Ensure storage directory exists
        File(storageDir).mkdirs()
    }
    
    private fun getOrCreateKey(): SecretKey {
        val keyPath = Paths.get(keyFile)
        return if (Files.exists(keyPath)) {
            // Load existing key
            val keyBytes = Files.readAllBytes(keyPath)
            val decodedKey = Base64.getDecoder().decode(keyBytes)
            SecretKeySpec(decodedKey, "AES")
        } else {
            // Generate new key
            val keyGen = KeyGenerator.getInstance("AES")
            keyGen.init(256)
            val key = keyGen.generateKey()
            
            // Save key
            val encodedKey = Base64.getEncoder().encode(key.encoded)
            Files.write(keyPath, encodedKey)
            
            key
        }
    }
    
    override suspend fun storeCredentials(credentials: StoredCredentials) {
        try {
            val jsonString = json.encodeToString(credentials)
            val key = getOrCreateKey()
            
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, key)
            
            val iv = cipher.iv
            val encryptedData = cipher.doFinal(jsonString.toByteArray())
            
            // Combine IV and encrypted data
            val combined = iv + encryptedData
            val encoded = Base64.getEncoder().encode(combined)
            
            Files.write(Paths.get(credentialsFile), encoded)
        } catch (e: Exception) {
            throw SecurityException("Failed to store credentials: ${e.message}")
        }
    }
    
    override suspend fun getCredentials(): StoredCredentials? {
        return try {
            val credentialsPath = Paths.get(credentialsFile)
            if (!Files.exists(credentialsPath)) return null
            
            val encoded = Files.readAllBytes(credentialsPath)
            val combined = Base64.getDecoder().decode(encoded)
            
            // Extract IV and encrypted data
            val iv = combined.sliceArray(0..11) // GCM IV is 12 bytes
            val encryptedData = combined.sliceArray(12 until combined.size)
            
            val key = getOrCreateKey()
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val spec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, key, spec)
            
            val decryptedData = cipher.doFinal(encryptedData)
            val jsonString = String(decryptedData)
            
            json.decodeFromString<StoredCredentials>(jsonString)
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun clearCredentials() {
        try {
            Files.deleteIfExists(Paths.get(credentialsFile))
        } catch (e: Exception) {
            // Ignore errors when clearing
        }
    }
}
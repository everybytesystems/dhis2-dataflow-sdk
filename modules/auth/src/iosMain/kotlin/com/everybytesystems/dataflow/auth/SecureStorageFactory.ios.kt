package com.everybytesystems.dataflow.auth

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import platform.Foundation.*
import platform.Security.*

actual class SecureStorageFactory(
    private val config: SecureStorageConfig = SecureStorageConfig()
) {
    actual fun create(): SecureStorage {
        return IOSSecureStorage(config)
    }
}

/**
 * iOS implementation using Keychain Services
 */
class IOSSecureStorage(
    private val config: SecureStorageConfig,
    private val json: Json = Json { ignoreUnknownKeys = true }
) : SecureStorage {
    
    override suspend fun storeCredentials(credentials: StoredCredentials) {
        try {
            val credentialsJson = json.encodeToString(credentials)
            val data = credentialsJson.encodeToByteArray().toNSData()
            
            // Delete existing item first
            deleteKeychainItem()
            
            // Add new item
            val query = createKeychainQuery().toMutableMap().apply {
                put(kSecValueData, data)
                put(kSecAttrAccessible, kSecAttrAccessibleWhenUnlockedThisDeviceOnly)
            }
            
            val status = SecItemAdd(query as CFDictionaryRef, null)
            if (status != errSecSuccess) {
                throw SecurityException("Failed to store credentials in keychain: $status")
            }
        } catch (e: Exception) {
            throw SecurityException("Failed to store credentials securely", e)
        }
    }
    
    override suspend fun getCredentials(): StoredCredentials? {
        return try {
            val query = createKeychainQuery().toMutableMap().apply {
                put(kSecReturnData, kCFBooleanTrue)
                put(kSecMatchLimit, kSecMatchLimitOne)
            }
            
            val result = memScoped {
                val resultPtr = alloc<CFTypeRefVar>()
                val status = SecItemCopyMatching(query as CFDictionaryRef, resultPtr.ptr)
                
                if (status == errSecSuccess) {
                    resultPtr.value
                } else {
                    null
                }
            }
            
            result?.let { data ->
                val nsData = data as NSData
                val credentialsJson = nsData.toByteArray().decodeToString()
                json.decodeFromString<StoredCredentials>(credentialsJson)
            }
        } catch (e: Exception) {
            // If decryption fails, clear corrupted data
            clearCredentials()
            null
        }
    }
    
    override suspend fun clearCredentials() {
        deleteKeychainItem()
    }
    
    private fun createKeychainQuery(): Map<CFStringRef?, Any?> {
        return mapOf(
            kSecClass to kSecClassGenericPassword,
            kSecAttrService to config.serviceName,
            kSecAttrAccount to config.keyAlias
        )
    }
    
    private fun deleteKeychainItem() {
        val query = createKeychainQuery()
        SecItemDelete(query as CFDictionaryRef)
    }
}

// Extension function to convert ByteArray to NSData
private fun ByteArray.toNSData(): NSData {
    return NSData.create(bytes = this.refTo(0), length = this.size.toULong())
}

// Extension function to convert NSData to ByteArray
private fun NSData.toByteArray(): ByteArray {
    return ByteArray(this.length.toInt()) { index ->
        this.bytes!!.reinterpret<ByteVar>()[index]
    }
}
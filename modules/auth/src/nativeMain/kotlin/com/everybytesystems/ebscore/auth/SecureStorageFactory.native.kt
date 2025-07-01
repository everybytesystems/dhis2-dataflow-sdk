package com.everybytesystems.ebscore.auth

import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import platform.Foundation.*
import platform.Security.*
import kotlinx.cinterop.*

actual class SecureStorageFactory {
    actual fun create(): SecureStorage = NativeSecureStorage()
}

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
private class NativeSecureStorage : SecureStorage {
    private val json = Json { ignoreUnknownKeys = true }
    private val serviceName = "DHIS2DataFlowSDK"
    private val accountName = "dhis2_credentials"
    
    override suspend fun storeCredentials(credentials: StoredCredentials) {
        try {
            val jsonString = json.encodeToString(credentials)
            val data = jsonString.encodeToByteArray()
            
            // First, try to delete existing item
            clearCredentials()
            
            // Create new keychain item
            data.usePinned { pinned ->
                val query = CFDictionaryCreateMutable(null, 0, null, null)
                CFDictionarySetValue(query, kSecClass, kSecClassGenericPassword)
                CFDictionarySetValue(query, kSecAttrService, CFStringCreateWithCString(null, serviceName, kCFStringEncodingUTF8))
                CFDictionarySetValue(query, kSecAttrAccount, CFStringCreateWithCString(null, accountName, kCFStringEncodingUTF8))
                CFDictionarySetValue(query, kSecValueData, CFDataCreate(null, pinned.addressOf(0).reinterpret(), data.size.convert()))
                
                val status = SecItemAdd(query, null)
                CFRelease(query)
                
                if (status != errSecSuccess) {
                    throw SecurityException("Failed to store credentials in keychain: $status")
                }
            }
        } catch (e: Exception) {
            throw SecurityException("Failed to store credentials: ${e.message}")
        }
    }
    
    override suspend fun getCredentials(): StoredCredentials? {
        return try {
            val query = CFDictionaryCreateMutable(null, 0, null, null)
            CFDictionarySetValue(query, kSecClass, kSecClassGenericPassword)
            CFDictionarySetValue(query, kSecAttrService, CFStringCreateWithCString(null, serviceName, kCFStringEncodingUTF8))
            CFDictionarySetValue(query, kSecAttrAccount, CFStringCreateWithCString(null, accountName, kCFStringEncodingUTF8))
            CFDictionarySetValue(query, kSecReturnData, kCFBooleanTrue)
            CFDictionarySetValue(query, kSecMatchLimit, kSecMatchLimitOne)
            
            memScoped {
                val result = alloc<CFTypeRefVar>()
                val status = SecItemCopyMatching(query, result.ptr)
                CFRelease(query)
                
                if (status == errSecSuccess) {
                    val data = result.value as CFDataRef
                    val length = CFDataGetLength(data).toInt()
                    val bytes = CFDataGetBytePtr(data)
                    val byteArray = ByteArray(length) { bytes!![it] }
                    val jsonString = byteArray.decodeToString()
                    json.decodeFromString<StoredCredentials>(jsonString)
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun clearCredentials() {
        val query = CFDictionaryCreateMutable(null, 0, null, null)
        CFDictionarySetValue(query, kSecClass, kSecClassGenericPassword)
        CFDictionarySetValue(query, kSecAttrService, CFStringCreateWithCString(null, serviceName, kCFStringEncodingUTF8))
        CFDictionarySetValue(query, kSecAttrAccount, CFStringCreateWithCString(null, accountName, kCFStringEncodingUTF8))
        
        SecItemDelete(query)
        CFRelease(query)
    }
}
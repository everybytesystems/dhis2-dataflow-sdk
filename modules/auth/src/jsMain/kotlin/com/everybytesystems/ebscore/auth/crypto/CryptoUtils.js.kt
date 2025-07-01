package com.everybytesystems.ebscore.auth.crypto

import com.everybytesystems.ebscore.auth.utils.Base64Utils
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get
import kotlin.js.Promise
import kotlin.random.Random

/**
 * JavaScript implementation of cryptographic utilities using Web Crypto API
 */
actual object CryptoUtils {
    
    actual fun generateSecureRandomBytes(size: Int): ByteArray {
        val array = Uint8Array(size)
        js("crypto.getRandomValues(array)")
        return ByteArray(size) { array[it] }
    }
    
    actual fun generateSecureRandomString(length: Int): String {
        val chars = CryptoConstants.SECURE_RANDOM_CHARS
        val randomBytes = generateSecureRandomBytes(length)
        return randomBytes.map { chars[(it.toInt() and 0xFF) % chars.length] }.joinToString("")
    }
    
    actual fun generateCodeVerifier(): String {
        val chars = CryptoConstants.CODE_VERIFIER_CHARS
        val randomBytes = generateSecureRandomBytes(CryptoConstants.CODE_VERIFIER_LENGTH)
        return randomBytes.map { chars[(it.toInt() and 0xFF) % chars.length] }.joinToString("")
    }
    
    actual fun generateCodeChallenge(codeVerifier: String): String {
        val encoder = js("new TextEncoder()")
        val data = encoder.encode(codeVerifier)
        
        // Use Web Crypto API for SHA-256
        val hashBuffer = js("crypto.subtle.digest('SHA-256', data)")
        
        // Convert to ByteArray synchronously (this is a limitation in JS)
        val hashArray = js("new Uint8Array(hashBuffer)") as Uint8Array
        val hash = ByteArray(hashArray.length) { hashArray[it] }
        
        return Base64Utils.encodeUrlSafe(hash)
    }
    
    actual fun generateState(): String {
        return generateSecureRandomString(CryptoConstants.STATE_LENGTH)
    }
    
    actual fun generateNonce(): String {
        return generateSecureRandomString(CryptoConstants.NONCE_LENGTH)
    }
    
    actual fun hashPassword(password: String, salt: ByteArray): ByteArray {
        // PBKDF2 implementation for JavaScript
        val encoder = js("new TextEncoder()")
        val passwordBuffer = encoder.encode(password)
        
        // Import key material
        val keyMaterial = js("crypto.subtle.importKey('raw', passwordBuffer, 'PBKDF2', false, ['deriveBits'])")
        
        // Derive key using PBKDF2
        val derivedKey = js("""
            crypto.subtle.deriveBits(
                {
                    name: 'PBKDF2',
                    salt: new Uint8Array(salt),
                    iterations: 100000,
                    hash: 'SHA-256'
                },
                keyMaterial,
                256
            )
        """)
        
        val keyArray = js("new Uint8Array(derivedKey)") as Uint8Array
        return ByteArray(keyArray.length) { keyArray[it] }
    }
    
    actual fun generateSalt(): ByteArray {
        return generateSecureRandomBytes(CryptoConstants.SALT_LENGTH)
    }
    
    actual fun constantTimeEquals(a: String, b: String): Boolean {
        if (a.length != b.length) return false
        
        var result = 0
        for (i in a.indices) {
            result = result or (a[i].code xor b[i].code)
        }
        return result == 0
    }
    
    actual fun constantTimeEquals(a: ByteArray, b: ByteArray): Boolean {
        if (a.size != b.size) return false
        
        var result = 0
        for (i in a.indices) {
            result = result or (a[i].toInt() xor b[i].toInt())
        }
        return result == 0
    }
    
    actual fun hmacSha256(key: ByteArray, data: ByteArray): ByteArray {
        // Import HMAC key
        val cryptoKey = js("""
            crypto.subtle.importKey(
                'raw',
                new Uint8Array(key),
                { name: 'HMAC', hash: 'SHA-256' },
                false,
                ['sign']
            )
        """)
        
        // Sign data
        val signature = js("crypto.subtle.sign('HMAC', cryptoKey, new Uint8Array(data))")
        val signatureArray = js("new Uint8Array(signature)") as Uint8Array
        
        return ByteArray(signatureArray.length) { signatureArray[it] }
    }
    
    actual fun sha256(data: ByteArray): ByteArray {
        val hashBuffer = js("crypto.subtle.digest('SHA-256', new Uint8Array(data))")
        val hashArray = js("new Uint8Array(hashBuffer)") as Uint8Array
        return ByteArray(hashArray.length) { hashArray[it] }
    }
    
    actual fun sha256(data: String): ByteArray {
        val encoder = js("new TextEncoder()")
        val dataBuffer = encoder.encode(data)
        val hashBuffer = js("crypto.subtle.digest('SHA-256', dataBuffer)")
        val hashArray = js("new Uint8Array(hashBuffer)") as Uint8Array
        return ByteArray(hashArray.length) { hashArray[it] }
    }
}
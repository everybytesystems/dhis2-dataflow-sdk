package com.everybytesystems.ebscore.auth.crypto

import com.everybytesystems.ebscore.auth.utils.Base64Utils
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * Android implementation of cryptographic utilities
 */
actual object CryptoUtils {
    
    private val secureRandom = SecureRandom()
    
    actual fun generateSecureRandomBytes(size: Int): ByteArray {
        val bytes = ByteArray(size)
        secureRandom.nextBytes(bytes)
        return bytes
    }
    
    actual fun generateSecureRandomString(length: Int): String {
        val chars = CryptoConstants.SECURE_RANDOM_CHARS
        return (1..length)
            .map { chars[secureRandom.nextInt(chars.length)] }
            .joinToString("")
    }
    
    actual fun generateCodeVerifier(): String {
        val chars = CryptoConstants.CODE_VERIFIER_CHARS
        return (1..CryptoConstants.CODE_VERIFIER_LENGTH)
            .map { chars[secureRandom.nextInt(chars.length)] }
            .joinToString("")
    }
    
    actual fun generateCodeChallenge(codeVerifier: String): String {
        val bytes = codeVerifier.toByteArray(Charsets.UTF_8)
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(bytes)
        return Base64Utils.encodeUrlSafe(hash)
    }
    
    actual fun generateState(): String {
        return generateSecureRandomString(CryptoConstants.STATE_LENGTH)
    }
    
    actual fun generateNonce(): String {
        return generateSecureRandomString(CryptoConstants.NONCE_LENGTH)
    }
    
    actual fun hashPassword(password: String, salt: ByteArray): ByteArray {
        // Using PBKDF2 with SHA-256
        val spec = javax.crypto.spec.PBEKeySpec(
            password.toCharArray(),
            salt,
            100000, // iterations
            256 // key length
        )
        val factory = javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        return factory.generateSecret(spec).encoded
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
        val mac = Mac.getInstance("HmacSHA256")
        val secretKey = SecretKeySpec(key, "HmacSHA256")
        mac.init(secretKey)
        return mac.doFinal(data)
    }
    
    actual fun sha256(data: ByteArray): ByteArray {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(data)
    }
    
    actual fun sha256(data: String): ByteArray {
        return sha256(data.toByteArray(Charsets.UTF_8))
    }
}
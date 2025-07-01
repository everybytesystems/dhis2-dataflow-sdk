package com.everybytesystems.ebscore.auth.crypto

import com.everybytesystems.ebscore.auth.utils.Base64Utils
import kotlinx.cinterop.*
import platform.Foundation.*
import platform.Security.*
import platform.darwin.*

/**
 * Native (iOS/macOS) implementation of cryptographic utilities using Security framework
 */
actual object CryptoUtils {
    
    actual fun generateSecureRandomBytes(size: Int): ByteArray {
        val bytes = ByteArray(size)
        bytes.usePinned { pinned ->
            val result = SecRandomCopyBytes(kSecRandomDefault, size.toULong(), pinned.addressOf(0))
            if (result != errSecSuccess) {
                throw RuntimeException("Failed to generate secure random bytes: $result")
            }
        }
        return bytes
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
        val data = codeVerifier.encodeToByteArray()
        val hash = sha256(data)
        return Base64Utils.encodeUrlSafe(hash)
    }
    
    actual fun generateState(): String {
        return generateSecureRandomString(CryptoConstants.STATE_LENGTH)
    }
    
    actual fun generateNonce(): String {
        return generateSecureRandomString(CryptoConstants.NONCE_LENGTH)
    }
    
    actual fun hashPassword(password: String, salt: ByteArray): ByteArray {
        // PBKDF2 implementation using CommonCrypto
        val passwordData = password.encodeToByteArray()
        val derivedKey = ByteArray(32) // 256 bits
        
        passwordData.usePinned { passwordPinned ->
            salt.usePinned { saltPinned ->
                derivedKey.usePinned { keyPinned ->
                    val result = CCKeyDerivationPBKDF(
                        kCCPBKDF2,
                        passwordPinned.addressOf(0),
                        passwordData.size.toULong(),
                        saltPinned.addressOf(0),
                        salt.size.toULong(),
                        kCCPRFHmacAlgSHA256,
                        100000u, // iterations
                        keyPinned.addressOf(0),
                        derivedKey.size.toULong()
                    )
                    
                    if (result != kCCSuccess) {
                        throw RuntimeException("PBKDF2 key derivation failed: $result")
                    }
                }
            }
        }
        
        return derivedKey
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
        val hmac = ByteArray(CC_SHA256_DIGEST_LENGTH)
        
        key.usePinned { keyPinned ->
            data.usePinned { dataPinned ->
                hmac.usePinned { hmacPinned ->
                    CCHmac(
                        kCCHmacAlgSHA256,
                        keyPinned.addressOf(0),
                        key.size.toULong(),
                        dataPinned.addressOf(0),
                        data.size.toULong(),
                        hmacPinned.addressOf(0)
                    )
                }
            }
        }
        
        return hmac
    }
    
    actual fun sha256(data: ByteArray): ByteArray {
        val hash = ByteArray(CC_SHA256_DIGEST_LENGTH)
        
        data.usePinned { dataPinned ->
            hash.usePinned { hashPinned ->
                CC_SHA256(dataPinned.addressOf(0), data.size.toUInt(), hashPinned.addressOf(0))
            }
        }
        
        return hash
    }
    
    actual fun sha256(data: String): ByteArray {
        return sha256(data.encodeToByteArray())
    }
}

// CommonCrypto constants and functions
@CName("kCCSuccess")
external val kCCSuccess: Int32

@CName("kCCPBKDF2")
external val kCCPBKDF2: UInt32

@CName("kCCPRFHmacAlgSHA256")
external val kCCPRFHmacAlgSHA256: UInt32

@CName("kCCHmacAlgSHA256")
external val kCCHmacAlgSHA256: UInt32

@CName("CC_SHA256_DIGEST_LENGTH")
external val CC_SHA256_DIGEST_LENGTH: Int

@CName("CCKeyDerivationPBKDF")
external fun CCKeyDerivationPBKDF(
    algorithm: UInt32,
    password: CPointer<*>,
    passwordLen: ULong,
    salt: CPointer<*>,
    saltLen: ULong,
    prf: UInt32,
    rounds: UInt32,
    derivedKey: CPointer<*>,
    derivedKeyLen: ULong
): Int32

@CName("CCHmac")
external fun CCHmac(
    algorithm: UInt32,
    key: CPointer<*>,
    keyLength: ULong,
    data: CPointer<*>,
    dataLength: ULong,
    macOut: CPointer<*>
)

@CName("CC_SHA256")
external fun CC_SHA256(
    data: CPointer<*>,
    len: UInt32,
    md: CPointer<*>
): CPointer<*>?
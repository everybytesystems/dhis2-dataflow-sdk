package com.everybytesystems.ebscore.auth.crypto

import kotlin.random.Random

/**
 * Production-grade cryptographic utilities for authentication
 */
expect object CryptoUtils {
    
    /**
     * Generate cryptographically secure random bytes
     */
    fun generateSecureRandomBytes(size: Int): ByteArray
    
    /**
     * Generate cryptographically secure random string
     */
    fun generateSecureRandomString(length: Int): String
    
    /**
     * Generate PKCE code verifier (RFC 7636)
     */
    fun generateCodeVerifier(): String
    
    /**
     * Generate PKCE code challenge from verifier (RFC 7636)
     */
    fun generateCodeChallenge(codeVerifier: String): String
    
    /**
     * Generate secure state parameter for OAuth2
     */
    fun generateState(): String
    
    /**
     * Generate secure nonce for OpenID Connect
     */
    fun generateNonce(): String
    
    /**
     * Hash password with salt (for client-side hashing if needed)
     */
    fun hashPassword(password: String, salt: ByteArray): ByteArray
    
    /**
     * Generate salt for password hashing
     */
    fun generateSalt(): ByteArray
    
    /**
     * Constant-time string comparison to prevent timing attacks
     */
    fun constantTimeEquals(a: String, b: String): Boolean
    
    /**
     * Constant-time byte array comparison to prevent timing attacks
     */
    fun constantTimeEquals(a: ByteArray, b: ByteArray): Boolean
    
    /**
     * Generate HMAC-SHA256
     */
    fun hmacSha256(key: ByteArray, data: ByteArray): ByteArray
    
    /**
     * Generate SHA-256 hash
     */
    fun sha256(data: ByteArray): ByteArray
    
    /**
     * Generate SHA-256 hash of string
     */
    fun sha256(data: String): ByteArray
}

/**
 * Extension functions for easier usage
 */
fun String.sha256(): ByteArray = CryptoUtils.sha256(this)
fun ByteArray.sha256(): ByteArray = CryptoUtils.sha256(this)

/**
 * Constants for cryptographic operations
 */
object CryptoConstants {
    const val CODE_VERIFIER_LENGTH = 128
    const val STATE_LENGTH = 32
    const val NONCE_LENGTH = 32
    const val SALT_LENGTH = 32
    const val MIN_CODE_VERIFIER_LENGTH = 43
    const val MAX_CODE_VERIFIER_LENGTH = 128
    
    // Character sets
    const val CODE_VERIFIER_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~"
    const val SECURE_RANDOM_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
}
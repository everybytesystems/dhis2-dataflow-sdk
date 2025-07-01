package com.everybytesystems.ebscore.auth.utils

/**
 * Cross-platform Base64 encoding/decoding utilities
 */
expect object Base64Utils {
    /**
     * Encode string to Base64
     */
    fun encode(input: String): String
    
    /**
     * Encode byte array to Base64
     */
    fun encode(input: ByteArray): String
    
    /**
     * Decode Base64 string to string
     */
    fun decode(input: String): String
    
    /**
     * Decode Base64 string to byte array
     */
    fun decodeToByteArray(input: String): ByteArray
    
    /**
     * URL-safe Base64 encoding
     */
    fun encodeUrlSafe(input: String): String
    
    /**
     * URL-safe Base64 encoding for byte array
     */
    fun encodeUrlSafe(input: ByteArray): String
    
    /**
     * URL-safe Base64 decoding
     */
    fun decodeUrlSafe(input: String): String
    
    /**
     * URL-safe Base64 decoding to byte array
     */
    fun decodeUrlSafeToByteArray(input: String): ByteArray
}

/**
 * Extension functions for easier usage
 */
fun String.encodeBase64(): String = Base64Utils.encode(this)
fun String.decodeBase64(): String = Base64Utils.decode(this)
fun String.encodeBase64UrlSafe(): String = Base64Utils.encodeUrlSafe(this)
fun String.decodeBase64UrlSafe(): String = Base64Utils.decodeUrlSafe(this)

fun ByteArray.encodeBase64(): String = Base64Utils.encode(this)
fun ByteArray.encodeBase64UrlSafe(): String = Base64Utils.encodeUrlSafe(this)
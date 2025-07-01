package com.everybytesystems.ebscore.auth.utils

import java.util.Base64

/**
 * JVM implementation of Base64 utilities
 */
actual object Base64Utils {
    
    actual fun encode(input: String): String {
        return try {
            Base64.getEncoder().encodeToString(input.toByteArray(Charsets.UTF_8))
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to encode Base64 on JVM: ${e.message}", e)
        }
    }
    
    actual fun encode(input: ByteArray): String {
        return try {
            Base64.getEncoder().encodeToString(input)
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to encode Base64 on JVM: ${e.message}", e)
        }
    }
    
    actual fun decode(input: String): String {
        return try {
            val bytes = Base64.getDecoder().decode(input)
            String(bytes, Charsets.UTF_8)
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to decode Base64 on JVM: ${e.message}", e)
        }
    }
    
    actual fun decodeToByteArray(input: String): ByteArray {
        return try {
            Base64.getDecoder().decode(input)
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to decode Base64 to ByteArray on JVM: ${e.message}", e)
        }
    }
    
    actual fun encodeUrlSafe(input: String): String {
        return try {
            Base64.getUrlEncoder().withoutPadding().encodeToString(input.toByteArray(Charsets.UTF_8))
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to encode URL-safe Base64 on JVM: ${e.message}", e)
        }
    }
    
    actual fun encodeUrlSafe(input: ByteArray): String {
        return try {
            Base64.getUrlEncoder().withoutPadding().encodeToString(input)
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to encode URL-safe Base64 on JVM: ${e.message}", e)
        }
    }
    
    actual fun decodeUrlSafe(input: String): String {
        return try {
            val bytes = Base64.getUrlDecoder().decode(input)
            String(bytes, Charsets.UTF_8)
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to decode URL-safe Base64 on JVM: ${e.message}", e)
        }
    }
    
    actual fun decodeUrlSafeToByteArray(input: String): ByteArray {
        return try {
            Base64.getUrlDecoder().decode(input)
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to decode URL-safe Base64 to ByteArray on JVM: ${e.message}", e)
        }
    }
}
package com.everybytesystems.ebscore.auth.utils

import android.util.Base64

/**
 * Android implementation of Base64 utilities
 */
actual object Base64Utils {
    
    actual fun encode(input: String): String {
        return try {
            Base64.encodeToString(input.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to encode Base64 on Android: ${e.message}", e)
        }
    }
    
    actual fun encode(input: ByteArray): String {
        return try {
            Base64.encodeToString(input, Base64.NO_WRAP)
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to encode Base64 on Android: ${e.message}", e)
        }
    }
    
    actual fun decode(input: String): String {
        return try {
            val bytes = Base64.decode(input, Base64.NO_WRAP)
            String(bytes, Charsets.UTF_8)
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to decode Base64 on Android: ${e.message}", e)
        }
    }
    
    actual fun decodeToByteArray(input: String): ByteArray {
        return try {
            Base64.decode(input, Base64.NO_WRAP)
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to decode Base64 to ByteArray on Android: ${e.message}", e)
        }
    }
    
    actual fun encodeUrlSafe(input: String): String {
        return try {
            Base64.encodeToString(input.toByteArray(Charsets.UTF_8), Base64.URL_SAFE or Base64.NO_WRAP)
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to encode URL-safe Base64 on Android: ${e.message}", e)
        }
    }
    
    actual fun encodeUrlSafe(input: ByteArray): String {
        return try {
            Base64.encodeToString(input, Base64.URL_SAFE or Base64.NO_WRAP)
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to encode URL-safe Base64 on Android: ${e.message}", e)
        }
    }
    
    actual fun decodeUrlSafe(input: String): String {
        return try {
            val bytes = Base64.decode(input, Base64.URL_SAFE or Base64.NO_WRAP)
            String(bytes, Charsets.UTF_8)
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to decode URL-safe Base64 on Android: ${e.message}", e)
        }
    }
    
    actual fun decodeUrlSafeToByteArray(input: String): ByteArray {
        return try {
            Base64.decode(input, Base64.URL_SAFE or Base64.NO_WRAP)
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to decode URL-safe Base64 to ByteArray on Android: ${e.message}", e)
        }
    }
}
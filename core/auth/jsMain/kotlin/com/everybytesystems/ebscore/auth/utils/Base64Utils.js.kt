package com.everybytesystems.ebscore.auth.utils

/**
 * JavaScript implementation of Base64 utilities
 */
actual object Base64Utils {
    
    actual fun encode(input: String): String {
        return try {
            js("btoa(unescape(encodeURIComponent(input)))") as String
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to encode Base64 on JS: ${e.message}", e)
        }
    }
    
    actual fun encode(input: ByteArray): String {
        return try {
            val str = input.map { (it.toInt() and 0xFF).toChar() }.joinToString("")
            js("btoa(str)") as String
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to encode Base64 on JS: ${e.message}", e)
        }
    }
    
    actual fun decode(input: String): String {
        return try {
            js("decodeURIComponent(escape(atob(input)))") as String
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to decode Base64 on JS: ${e.message}", e)
        }
    }
    
    actual fun decodeToByteArray(input: String): ByteArray {
        return try {
            val str = js("atob(input)") as String
            str.map { it.code.toByte() }.toByteArray()
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to decode Base64 to ByteArray on JS: ${e.message}", e)
        }
    }
    
    actual fun encodeUrlSafe(input: String): String {
        return try {
            encode(input)
                .replace('+', '-')
                .replace('/', '_')
                .trimEnd('=')
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to encode URL-safe Base64 on JS: ${e.message}", e)
        }
    }
    
    actual fun encodeUrlSafe(input: ByteArray): String {
        return try {
            encode(input)
                .replace('+', '-')
                .replace('/', '_')
                .trimEnd('=')
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to encode URL-safe Base64 on JS: ${e.message}", e)
        }
    }
    
    actual fun decodeUrlSafe(input: String): String {
        return try {
            val padded = input
                .replace('-', '+')
                .replace('_', '/')
                .let { 
                    val padding = (4 - it.length % 4) % 4
                    it + "=".repeat(padding)
                }
            decode(padded)
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to decode URL-safe Base64 on JS: ${e.message}", e)
        }
    }
    
    actual fun decodeUrlSafeToByteArray(input: String): ByteArray {
        return try {
            val padded = input
                .replace('-', '+')
                .replace('_', '/')
                .let { 
                    val padding = (4 - it.length % 4) % 4
                    it + "=".repeat(padding)
                }
            decodeToByteArray(padded)
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to decode URL-safe Base64 to ByteArray on JS: ${e.message}", e)
        }
    }
}
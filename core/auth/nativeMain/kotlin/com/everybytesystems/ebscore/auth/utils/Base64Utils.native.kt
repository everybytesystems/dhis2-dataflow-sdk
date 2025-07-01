package com.everybytesystems.ebscore.auth.utils

import platform.Foundation.*

/**
 * Native (iOS/macOS) implementation of Base64 utilities
 */
actual object Base64Utils {
    
    actual fun encode(input: String): String {
        return try {
            val data = input.encodeToByteArray().toNSData()
            data.base64EncodedStringWithOptions(0u)
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to encode Base64 on Native: ${e.message}", e)
        }
    }
    
    actual fun encode(input: ByteArray): String {
        return try {
            val data = input.toNSData()
            data.base64EncodedStringWithOptions(0u)
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to encode Base64 on Native: ${e.message}", e)
        }
    }
    
    actual fun decode(input: String): String {
        return try {
            val data = NSData.create(base64EncodedString = input, options = 0u)
                ?: throw IllegalArgumentException("Invalid Base64 string")
            data.toByteArray().decodeToString()
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to decode Base64 on Native: ${e.message}", e)
        }
    }
    
    actual fun decodeToByteArray(input: String): ByteArray {
        return try {
            val data = NSData.create(base64EncodedString = input, options = 0u)
                ?: throw IllegalArgumentException("Invalid Base64 string")
            data.toByteArray()
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to decode Base64 to ByteArray on Native: ${e.message}", e)
        }
    }
    
    actual fun encodeUrlSafe(input: String): String {
        return try {
            encode(input)
                .replace('+', '-')
                .replace('/', '_')
                .trimEnd('=')
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to encode URL-safe Base64 on Native: ${e.message}", e)
        }
    }
    
    actual fun encodeUrlSafe(input: ByteArray): String {
        return try {
            encode(input)
                .replace('+', '-')
                .replace('/', '_')
                .trimEnd('=')
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to encode URL-safe Base64 on Native: ${e.message}", e)
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
            throw IllegalArgumentException("Failed to decode URL-safe Base64 on Native: ${e.message}", e)
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
            throw IllegalArgumentException("Failed to decode URL-safe Base64 to ByteArray on Native: ${e.message}", e)
        }
    }
}

/**
 * Extension functions for NSData conversion
 */
private fun ByteArray.toNSData(): NSData {
    return NSData.create(bytes = this.refTo(0), length = this.size.toULong())
}

private fun NSData.toByteArray(): ByteArray {
    return ByteArray(this.length.toInt()) { index ->
        this.bytes!!.reinterpret<ByteVar>()[index]
    }
}
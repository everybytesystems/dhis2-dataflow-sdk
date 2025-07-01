package com.everybytesystems.ebscore.auth.jwt

/**
 * JavaScript-specific base64 decoding implementation
 */
actual fun decodeBase64Platform(input: String): String {
    return try {
        // Use JavaScript's built-in atob function
        js("atob(input)") as String
    } catch (e: Exception) {
        throw IllegalArgumentException("Failed to decode base64 on JS: ${e.message}", e)
    }
}
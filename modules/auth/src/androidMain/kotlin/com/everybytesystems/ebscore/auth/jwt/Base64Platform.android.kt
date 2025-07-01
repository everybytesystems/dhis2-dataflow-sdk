package com.everybytesystems.ebscore.auth.jwt

import android.util.Base64

/**
 * Android-specific base64 decoding implementation
 */
actual fun decodeBase64Platform(input: String): String {
    return try {
        val bytes = Base64.decode(input, Base64.NO_WRAP)
        String(bytes, Charsets.UTF_8)
    } catch (e: Exception) {
        throw IllegalArgumentException("Failed to decode base64 on Android: ${e.message}", e)
    }
}
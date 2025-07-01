package com.everybytesystems.ebscore.auth.jwt

import java.util.Base64

/**
 * JVM-specific base64 decoding implementation
 */
actual fun decodeBase64Platform(input: String): String {
    return try {
        val bytes = Base64.getDecoder().decode(input)
        String(bytes, Charsets.UTF_8)
    } catch (e: Exception) {
        throw IllegalArgumentException("Failed to decode base64 on JVM: ${e.message}", e)
    }
}
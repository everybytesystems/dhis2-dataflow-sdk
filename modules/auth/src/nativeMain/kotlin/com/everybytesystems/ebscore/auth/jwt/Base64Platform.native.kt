package com.everybytesystems.ebscore.auth.jwt

/**
 * Native (iOS) specific base64 decoding implementation
 */
actual fun decodeBase64Platform(input: String): String {
    return try {
        // Simple base64 decoding for native platforms
        // In production, you might want to use a proper base64 library
        val base64Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
        val cleanInput = input.replace("=", "")
        
        val bytes = mutableListOf<Byte>()
        var i = 0
        
        while (i < cleanInput.length) {
            val b1 = base64Chars.indexOf(cleanInput.getOrNull(i) ?: break)
            val b2 = base64Chars.indexOf(cleanInput.getOrNull(i + 1) ?: break)
            val b3 = base64Chars.indexOf(cleanInput.getOrNull(i + 2) ?: -1)
            val b4 = base64Chars.indexOf(cleanInput.getOrNull(i + 3) ?: -1)
            
            if (b1 == -1 || b2 == -1) break
            
            val combined = (b1 shl 18) or (b2 shl 12) or 
                          (if (b3 != -1) b3 shl 6 else 0) or 
                          (if (b4 != -1) b4 else 0)
            
            bytes.add(((combined shr 16) and 0xFF).toByte())
            if (b3 != -1) bytes.add(((combined shr 8) and 0xFF).toByte())
            if (b4 != -1) bytes.add((combined and 0xFF).toByte())
            
            i += 4
        }
        
        bytes.toByteArray().decodeToString()
    } catch (e: Exception) {
        throw IllegalArgumentException("Failed to decode base64 on Native: ${e.message}", e)
    }
}
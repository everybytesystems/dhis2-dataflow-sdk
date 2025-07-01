package com.everybytesystems.ebscore.core.api.base

import android.util.Base64

/**
 * Android-specific implementation of base64 encoding
 */
actual fun BaseApi.encodeBase64(input: String): String {
    return Base64.encodeToString(input.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
}
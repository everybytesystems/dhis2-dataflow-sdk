package com.everybytesystems.dataflow.core.api.base

import android.util.Base64

actual fun BaseApi.encodeBase64(input: String): String {
    return Base64.encodeToString(input.toByteArray(), Base64.NO_WRAP)
}
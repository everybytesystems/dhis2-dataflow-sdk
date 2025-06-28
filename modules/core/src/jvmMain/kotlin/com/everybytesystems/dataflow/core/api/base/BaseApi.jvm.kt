package com.everybytesystems.dataflow.core.api.base

import java.util.Base64

actual fun BaseApi.encodeBase64(input: String): String {
    return Base64.getEncoder().encodeToString(input.toByteArray())
}
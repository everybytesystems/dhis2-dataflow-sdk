package com.everybytesystems.ebscore.core.api.base

import kotlinx.browser.window

actual fun BaseApi.encodeBase64(input: String): String {
    return window.btoa(input)
}
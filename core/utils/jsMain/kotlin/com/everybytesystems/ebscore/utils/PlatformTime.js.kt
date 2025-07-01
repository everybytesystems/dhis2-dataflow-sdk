package com.everybytesystems.ebscore.utils

import kotlin.js.Date

actual fun getCurrentTimeMillis(): Long {
    return Date.now().toLong()
}
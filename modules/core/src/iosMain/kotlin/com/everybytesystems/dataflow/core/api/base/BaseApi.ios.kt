package com.everybytesystems.dataflow.core.api.base

import kotlinx.cinterop.addressOf
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.base64EncodedStringWithOptions
import platform.Foundation.create

actual fun BaseApi.encodeBase64(input: String): String {
    return memScoped {
        val data = input.encodeToByteArray()
        data.usePinned { pinned ->
            val nsData = NSData.create(
                bytes = pinned.addressOf(0),
                length = data.size.toULong()
            )
            nsData.base64EncodedStringWithOptions(0u)
        }
    }
}
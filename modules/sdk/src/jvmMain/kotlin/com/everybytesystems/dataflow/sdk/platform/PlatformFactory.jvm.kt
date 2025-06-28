package com.everybytesystems.dataflow.sdk.platform

import com.everybytesystems.dataflow.auth.SecureStorage
import com.everybytesystems.dataflow.auth.SecureStorageFactory

actual object PlatformFactory {
    actual fun createSecureStorage(): SecureStorage {
        return SecureStorageFactory().create()
    }
}
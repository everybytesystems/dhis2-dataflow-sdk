package com.everybytesystems.dataflow.sdk.platform

import android.content.Context
import com.everybytesystems.dataflow.auth.SecureStorage
import com.everybytesystems.dataflow.auth.SecureStorageFactory

actual object PlatformFactory {
    private var applicationContext: Context? = null
    
    fun initialize(context: Context) {
        applicationContext = context.applicationContext
    }
    
    actual fun createSecureStorage(): SecureStorage {
        val context = applicationContext 
            ?: throw IllegalStateException("PlatformFactory not initialized. Call PlatformFactory.initialize(context) first.")
        return SecureStorageFactory(context).create()
    }
}
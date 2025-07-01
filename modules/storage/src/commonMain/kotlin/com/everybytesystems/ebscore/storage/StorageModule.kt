package com.everybytesystems.ebscore.storage

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

/**
 * EBSCore Storage Module
 * Provides local storage capabilities including key-value storage and database operations
 */

interface EBSCoreStorage {
    // Key-Value Storage
    suspend fun putString(key: String, value: String)
    suspend fun getString(key: String, defaultValue: String = ""): String
    suspend fun putInt(key: String, value: Int)
    suspend fun getInt(key: String, defaultValue: Int = 0): Int
    suspend fun putBoolean(key: String, value: Boolean)
    suspend fun getBoolean(key: String, defaultValue: Boolean = false): Boolean
    suspend fun remove(key: String)
    suspend fun clear()
    
    // JSON Storage
    suspend fun <T> putObject(key: String, value: T, serializer: kotlinx.serialization.KSerializer<T>)
    suspend fun <T> getObject(key: String, defaultValue: T, serializer: kotlinx.serialization.KSerializer<T>): T
    
    // Reactive Storage
    fun observeString(key: String, defaultValue: String = ""): Flow<String>
    fun observeInt(key: String, defaultValue: Int = 0): Flow<Int>
    fun observeBoolean(key: String, defaultValue: Boolean = false): Flow<Boolean>
}

class EBSCoreStorageImpl(
    private val settings: Settings
) : EBSCoreStorage {
    
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
    
    private val stringFlows = mutableMapOf<String, MutableStateFlow<String>>()
    private val intFlows = mutableMapOf<String, MutableStateFlow<Int>>()
    private val booleanFlows = mutableMapOf<String, MutableStateFlow<Boolean>>()
    
    override suspend fun putString(key: String, value: String) {
        settings.putString(key, value)
        stringFlows[key]?.value = value
    }
    
    override suspend fun getString(key: String, defaultValue: String): String {
        return settings.getString(key, defaultValue)
    }
    
    override suspend fun putInt(key: String, value: Int) {
        settings.putInt(key, value)
        intFlows[key]?.value = value
    }
    
    override suspend fun getInt(key: String, defaultValue: Int): Int {
        return settings.getInt(key, defaultValue)
    }
    
    override suspend fun putBoolean(key: String, value: Boolean) {
        settings.putBoolean(key, value)
        booleanFlows[key]?.value = value
    }
    
    override suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return settings.getBoolean(key, defaultValue)
    }
    
    override suspend fun remove(key: String) {
        settings.remove(key)
    }
    
    override suspend fun clear() {
        settings.clear()
    }
    
    override suspend fun <T> putObject(key: String, value: T, serializer: kotlinx.serialization.KSerializer<T>) {
        val jsonString = json.encodeToString(serializer, value)
        putString(key, jsonString)
    }
    
    override suspend fun <T> getObject(key: String, defaultValue: T, serializer: kotlinx.serialization.KSerializer<T>): T {
        val jsonString = getString(key)
        return if (jsonString.isEmpty()) {
            defaultValue
        } else {
            try {
                json.decodeFromString(serializer, jsonString)
            } catch (e: Exception) {
                defaultValue
            }
        }
    }
    
    override fun observeString(key: String, defaultValue: String): Flow<String> {
        return stringFlows.getOrPut(key) {
            MutableStateFlow(settings.getString(key, defaultValue))
        }.asStateFlow()
    }
    
    override fun observeInt(key: String, defaultValue: Int): Flow<Int> {
        return intFlows.getOrPut(key) {
            MutableStateFlow(settings.getInt(key, defaultValue))
        }.asStateFlow()
    }
    
    override fun observeBoolean(key: String, defaultValue: Boolean): Flow<Boolean> {
        return booleanFlows.getOrPut(key) {
            MutableStateFlow(settings.getBoolean(key, defaultValue))
        }.asStateFlow()
    }
}

// Storage Keys Constants
object StorageKeys {
    const val USER_TOKEN = "user_token"
    const val USER_ID = "user_id"
    const val SERVER_URL = "server_url"
    const val LAST_SYNC_TIME = "last_sync_time"
    const val APP_SETTINGS = "app_settings"
    const val OFFLINE_DATA = "offline_data"
    const val CACHE_EXPIRY = "cache_expiry"
}

// Cache Management
class CacheManager(val storage: EBSCoreStorage) {
    
    suspend inline fun <reified T> cacheData(key: String, data: T, expiryMinutes: Long = 60) {
        val cacheEntry = CacheEntry(
            data = data,
            timestamp = kotlinx.datetime.Clock.System.now().toEpochMilliseconds(),
            expiryMinutes = expiryMinutes
        )
        storage.putObject(key, cacheEntry, serializer<CacheEntry<T>>())
    }
    
    suspend inline fun <reified T> getCachedData(key: String): T? {
        val cacheEntry = storage.getObject(key, null as CacheEntry<T>?, serializer<CacheEntry<T>?>())
        return if (cacheEntry != null && !cacheEntry.isExpired()) {
            cacheEntry.data
        } else {
            null
        }
    }
    
    suspend fun clearExpiredCache() {
        // Implementation would iterate through cache keys and remove expired entries
        // This is a simplified version
    }
}

@kotlinx.serialization.Serializable
data class CacheEntry<T>(
    val data: T,
    val timestamp: Long,
    val expiryMinutes: Long
) {
    fun isExpired(): Boolean {
        val expiryTime = timestamp + (expiryMinutes * 60 * 1000)
        return kotlinx.datetime.Clock.System.now().toEpochMilliseconds() > expiryTime
    }
}
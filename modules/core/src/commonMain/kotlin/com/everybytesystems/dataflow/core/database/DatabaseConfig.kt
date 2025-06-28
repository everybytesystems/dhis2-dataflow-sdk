package com.everybytesystems.dataflow.core.database

/**
 * Database configuration for DHIS2 DataFlow SDK
 */
data class DatabaseConfig(
    val databaseName: String = "dhis2_dataflow.db",
    val enableWAL: Boolean = true,
    val enableForeignKeys: Boolean = true
)

/**
 * Simple cache interface for basic data storage
 * This replaces SQLDelight temporarily to avoid compilation issues
 */
interface DataCache {
    suspend fun put(key: String, value: String)
    suspend fun get(key: String): String?
    suspend fun remove(key: String)
    suspend fun clear()
}

/**
 * In-memory implementation of DataCache
 */
class InMemoryDataCache : DataCache {
    private val cache = mutableMapOf<String, String>()
    
    override suspend fun put(key: String, value: String) {
        cache[key] = value
    }
    
    override suspend fun get(key: String): String? {
        return cache[key]
    }
    
    override suspend fun remove(key: String) {
        cache.remove(key)
    }
    
    override suspend fun clear() {
        cache.clear()
    }
}
package com.everybytesystems.dataflow.core.cache

/**
 * Simple cache manager interface
 * TODO: Implement proper caching mechanism
 */
interface CacheManager {
    suspend fun <T> get(key: String): T?
    suspend fun <T> put(key: String, value: T)
    suspend fun remove(key: String)
    suspend fun clear()
}

/**
 * In-memory cache implementation
 */
class InMemoryCacheManager : CacheManager {
    private val cache = mutableMapOf<String, Any>()
    
    @Suppress("UNCHECKED_CAST")
    override suspend fun <T> get(key: String): T? {
        return cache[key] as? T
    }
    
    override suspend fun <T> put(key: String, value: T) {
        if (value != null) {
            cache[key] = value as Any
        }
    }
    
    override suspend fun remove(key: String) {
        cache.remove(key)
    }
    
    override suspend fun clear() {
        cache.clear()
    }
}
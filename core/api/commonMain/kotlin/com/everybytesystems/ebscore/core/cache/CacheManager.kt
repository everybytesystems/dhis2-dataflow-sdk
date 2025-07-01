package com.everybytesystems.ebscore.core.cache

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * Production-ready cache manager interface with TTL support
 */
interface CacheManager {
    suspend fun <T> get(key: String): T?
    suspend fun <T> put(key: String, value: T, ttlSeconds: Long = 3600)
    suspend fun remove(key: String)
    suspend fun clear()
    suspend fun contains(key: String): Boolean
    suspend fun getStats(): CacheStats
    suspend fun cleanExpired()
}

/**
 * Enhanced in-memory cache implementation with TTL and LRU eviction
 */
class InMemoryCacheManager(
    private val maxSize: Int = 1000,
    private val defaultTtlSeconds: Long = 3600 // 1 hour
) : CacheManager {
    
    private val cache = mutableMapOf<String, CacheEntry>()
    private val accessOrder = mutableListOf<String>()
    private val mutex = Mutex()
    private var hits = 0L
    private var misses = 0L
    
    @Suppress("UNCHECKED_CAST")
    override suspend fun <T> get(key: String): T? = mutex.withLock {
        val entry = cache[key]
        
        if (entry == null) {
            misses++
            return null
        }
        
        // Check if expired
        if (isExpired(entry)) {
            cache.remove(key)
            accessOrder.remove(key)
            misses++
            return null
        }
        
        // Update access order (LRU)
        accessOrder.remove(key)
        accessOrder.add(key)
        hits++
        
        entry.value as? T
    }
    
    override suspend fun <T> put(key: String, value: T, ttlSeconds: Long) = mutex.withLock {
        if (value == null) return@withLock
        
        val expiresAt = Clock.System.now().plusSeconds(ttlSeconds)
        val entry = CacheEntry(value as Any, expiresAt)
        
        // Remove if already exists
        if (cache.containsKey(key)) {
            accessOrder.remove(key)
        }
        
        // Add new entry
        cache[key] = entry
        accessOrder.add(key)
        
        // Evict if over capacity
        evictIfNeeded()
    }
    
    override suspend fun remove(key: String): Unit = mutex.withLock {
        cache.remove(key)
        accessOrder.remove(key)
    }
    
    override suspend fun clear() = mutex.withLock {
        cache.clear()
        accessOrder.clear()
        hits = 0L
        misses = 0L
    }
    
    override suspend fun contains(key: String): Boolean = mutex.withLock {
        val entry = cache[key] ?: return false
        if (isExpired(entry)) {
            cache.remove(key)
            accessOrder.remove(key)
            return false
        }
        true
    }
    
    override suspend fun getStats(): CacheStats = mutex.withLock {
        val expired = cache.values.count { isExpired(it) }
        CacheStats(
            size = cache.size,
            maxSize = maxSize,
            expiredEntries = expired,
            hitRate = calculateHitRate(),
            hits = hits,
            misses = misses
        )
    }
    
    override suspend fun cleanExpired() = mutex.withLock {
        val expiredKeys = cache.filterValues { isExpired(it) }.keys.toList()
        expiredKeys.forEach { key ->
            cache.remove(key)
            accessOrder.remove(key)
        }
    }
    
    private fun isExpired(entry: CacheEntry): Boolean {
        return Clock.System.now() > entry.expiresAt
    }
    
    private fun evictIfNeeded() {
        while (cache.size > maxSize && accessOrder.isNotEmpty()) {
            val oldestKey = accessOrder.removeFirst()
            cache.remove(oldestKey)
        }
    }
    
    private fun calculateHitRate(): Double {
        val total = hits + misses
        return if (total == 0L) 0.0 else hits.toDouble() / total
    }
}

/**
 * Cache entry with expiration
 */
@Serializable
private data class CacheEntry(
    @Contextual val value: Any,
    val expiresAt: Instant
)

/**
 * Cache statistics
 */
@Serializable
data class CacheStats(
    val size: Int,
    val maxSize: Int,
    val expiredEntries: Int,
    val hitRate: Double,
    val hits: Long,
    val misses: Long
)

/**
 * Extension function to add seconds to Instant
 */
private fun Instant.plusSeconds(seconds: Long): Instant {
    return Instant.fromEpochMilliseconds(this.toEpochMilliseconds() + (seconds * 1000))
}
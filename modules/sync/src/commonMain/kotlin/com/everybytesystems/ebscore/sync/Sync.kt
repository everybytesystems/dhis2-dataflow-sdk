package com.everybytesystems.ebscore.sync

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

/**
 * Generic synchronization interface for EBSCore SDK
 * This provides basic sync functionality that can be used across different platforms
 */
interface Sync {
    /**
     * Start synchronization process
     */
    suspend fun startSync(): SyncResult
    
    /**
     * Stop synchronization process
     */
    suspend fun stopSync()
    
    /**
     * Get sync status as a flow
     */
    fun getSyncStatus(): Flow<SyncStatus>
    
    /**
     * Check if sync is currently running
     */
    fun isSyncRunning(): Boolean
}

/**
 * Sync result data class
 */
data class SyncResult(
    val success: Boolean,
    val message: String,
    val syncedItems: Int = 0,
    val failedItems: Int = 0
)

/**
 * Sync status enum
 */
@Serializable
enum class SyncStatus {
    IDLE,
    SYNCING,
    SUCCESS,
    ERROR,
    CANCELLED
}

/**
 * Default implementation of Sync interface
 */
class DefaultSync : Sync {
    private var isRunning = false
    
    override suspend fun startSync(): SyncResult {
        isRunning = true
        // Default implementation - can be overridden by platform-specific implementations
        println("Starting generic sync process...")
        isRunning = false
        return SyncResult(success = true, message = "Sync completed successfully")
    }
    
    override suspend fun stopSync() {
        isRunning = false
        println("Stopping sync process...")
    }
    
    override fun getSyncStatus(): Flow<SyncStatus> {
        // Return a simple flow - can be enhanced in specific implementations
        return kotlinx.coroutines.flow.flowOf(if (isRunning) SyncStatus.SYNCING else SyncStatus.IDLE)
    }
    
    override fun isSyncRunning(): Boolean = isRunning
}
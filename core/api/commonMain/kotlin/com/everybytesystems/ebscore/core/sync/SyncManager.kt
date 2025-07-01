package com.everybytesystems.ebscore.core.sync

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * Sync status enumeration
 */
enum class SyncStatus {
    IDLE,
    SYNCING,
    SUCCESS,
    ERROR,
    CANCELLED,
    PAUSED
}

/**
 * Sync result data class
 */
@Serializable
data class SyncResult(
    val status: SyncStatus,
    val message: String? = null,
    val error: String? = null,
    val itemsSynced: Int = 0,
    val totalItems: Int = 0,
    val startTime: Instant? = null,
    val endTime: Instant? = null,
    val syncType: SyncType = SyncType.FULL
)

/**
 * Sync type enumeration
 */
enum class SyncType {
    FULL,
    INCREMENTAL,
    METADATA_ONLY,
    DATA_ONLY
}

/**
 * Sync configuration
 */
@Serializable
data class SyncConfig(
    val syncType: SyncType = SyncType.FULL,
    val batchSize: Int = 100,
    val maxRetries: Int = 3,
    val retryDelayMs: Long = 1000,
    val timeoutMs: Long = 30000,
    val enableConflictResolution: Boolean = true
)

/**
 * Syncable item interface
 */
interface SyncableItem {
    val id: String
    val lastModified: Instant
    val version: Long
    fun toSyncData(): Map<String, Any>
    fun fromSyncData(data: Map<String, Any>): SyncableItem
}

/**
 * Sync conflict resolution strategy
 */
enum class ConflictResolution {
    SERVER_WINS,
    CLIENT_WINS,
    MERGE,
    MANUAL
}

/**
 * Production-ready sync manager interface
 */
interface SyncManager {
    val syncStatus: StateFlow<SyncStatus>
    val syncProgress: StateFlow<Float>
    val lastSyncTime: StateFlow<Instant?>
    
    suspend fun startSync(config: SyncConfig = SyncConfig()): Flow<SyncResult>
    suspend fun cancelSync()
    suspend fun pauseSync()
    suspend fun resumeSync()
    fun isSyncing(): Boolean
    suspend fun getConflicts(): List<SyncConflict>
    suspend fun resolveConflict(conflictId: String, resolution: ConflictResolution)
}

/**
 * Sync conflict data class
 */
@Serializable
data class SyncConflict(
    val id: String,
    val itemId: String,
    val serverVersion: Map<String, @Contextual Any>,
    val clientVersion: Map<String, @Contextual Any>,
    val conflictType: ConflictType,
    val timestamp: Instant
)

/**
 * Conflict type enumeration
 */
enum class ConflictType {
    UPDATE_UPDATE,
    UPDATE_DELETE,
    DELETE_UPDATE
}

/**
 * Production-ready sync manager implementation
 */
class ProductionSyncManager(
    private val syncRepository: SyncRepository,
    private val conflictResolver: ConflictResolver = DefaultConflictResolver()
) : SyncManager {
    
    private val _syncStatus = MutableStateFlow(SyncStatus.IDLE)
    override val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()
    
    private val _syncProgress = MutableStateFlow(0f)
    override val syncProgress: StateFlow<Float> = _syncProgress.asStateFlow()
    
    private val _lastSyncTime = MutableStateFlow<Instant?>(null)
    override val lastSyncTime: StateFlow<Instant?> = _lastSyncTime.asStateFlow()
    
    private var syncJob: Job? = null
    private val conflicts = mutableMapOf<String, SyncConflict>()
    
    override suspend fun startSync(config: SyncConfig): Flow<SyncResult> = flow {
        if (isSyncing()) {
            emit(SyncResult(
                status = SyncStatus.ERROR,
                message = "Sync already in progress"
            ))
            return@flow
        }
        
        _syncStatus.value = SyncStatus.SYNCING
        _syncProgress.value = 0f
        val startTime = Clock.System.now()
        
        syncJob = coroutineScope {
            launch {
                try {
                    val result = performSync(config, startTime)
                    emit(result)
                    _lastSyncTime.value = Clock.System.now()
                } catch (e: CancellationException) {
                    _syncStatus.value = SyncStatus.CANCELLED
                    emit(SyncResult(
                        status = SyncStatus.CANCELLED,
                        message = "Sync was cancelled",
                        startTime = startTime,
                        endTime = Clock.System.now()
                    ))
                } catch (e: Exception) {
                    _syncStatus.value = SyncStatus.ERROR
                    emit(SyncResult(
                        status = SyncStatus.ERROR,
                        message = "Sync failed: ${e.message}",
                        error = e.message,
                        startTime = startTime,
                        endTime = Clock.System.now()
                    ))
                }
            }
        }
        
        syncJob?.join()
    }
    
    private suspend fun performSync(config: SyncConfig, startTime: Instant): SyncResult {
        val items = syncRepository.getItemsToSync(config.syncType)
        val totalItems = items.size
        var syncedItems = 0
        
        // Process items in batches
        items.chunked(config.batchSize).forEachIndexed { batchIndex, batch ->
            if (!isSyncing()) return@forEachIndexed
            
            try {
                val batchResult = syncBatch(batch, config)
                syncedItems += batchResult.itemsSynced
                
                // Update progress
                _syncProgress.value = syncedItems.toFloat() / totalItems
                
                // Handle conflicts
                if (config.enableConflictResolution) {
                    handleConflicts(batchResult.conflicts)
                }
                
            } catch (e: Exception) {
                // Retry logic
                var retries = 0
                while (retries < config.maxRetries && isSyncing()) {
                    delay(config.retryDelayMs * (retries + 1))
                    try {
                        val retryResult = syncBatch(batch, config)
                        syncedItems += retryResult.itemsSynced
                        break
                    } catch (retryException: Exception) {
                        retries++
                        if (retries >= config.maxRetries) {
                            throw retryException
                        }
                    }
                }
            }
        }
        
        _syncStatus.value = SyncStatus.SUCCESS
        _syncProgress.value = 1f
        
        return SyncResult(
            status = SyncStatus.SUCCESS,
            message = "Sync completed successfully",
            itemsSynced = syncedItems,
            totalItems = totalItems,
            startTime = startTime,
            endTime = Clock.System.now(),
            syncType = config.syncType
        )
    }
    
    private suspend fun syncBatch(batch: List<SyncableItem>, config: SyncConfig): BatchSyncResult {
        return syncRepository.syncBatch(batch, config)
    }
    
    private suspend fun handleConflicts(conflicts: List<SyncConflict>) {
        conflicts.forEach { conflict ->
            this.conflicts[conflict.id] = conflict
            
            // Auto-resolve based on strategy
            val resolution = conflictResolver.resolveConflict(conflict)
            if (resolution != ConflictResolution.MANUAL) {
                resolveConflict(conflict.id, resolution)
            }
        }
    }
    
    override suspend fun cancelSync() {
        syncJob?.cancel()
        syncJob = null
        _syncStatus.value = SyncStatus.CANCELLED
        _syncProgress.value = 0f
    }
    
    override suspend fun pauseSync() {
        if (isSyncing()) {
            _syncStatus.value = SyncStatus.PAUSED
        }
    }
    
    override suspend fun resumeSync() {
        if (_syncStatus.value == SyncStatus.PAUSED) {
            _syncStatus.value = SyncStatus.SYNCING
        }
    }
    
    override fun isSyncing(): Boolean {
        return _syncStatus.value == SyncStatus.SYNCING
    }
    
    override suspend fun getConflicts(): List<SyncConflict> {
        return conflicts.values.toList()
    }
    
    override suspend fun resolveConflict(conflictId: String, resolution: ConflictResolution) {
        val conflict = conflicts[conflictId] ?: return
        
        when (resolution) {
            ConflictResolution.SERVER_WINS -> {
                syncRepository.applyServerVersion(conflict)
            }
            ConflictResolution.CLIENT_WINS -> {
                syncRepository.applyClientVersion(conflict)
            }
            ConflictResolution.MERGE -> {
                val merged = conflictResolver.mergeVersions(conflict)
                syncRepository.applyMergedVersion(conflict, merged)
            }
            ConflictResolution.MANUAL -> {
                // Keep conflict for manual resolution
                return
            }
        }
        
        conflicts.remove(conflictId)
    }
}

/**
 * Sync repository interface
 */
interface SyncRepository {
    suspend fun getItemsToSync(syncType: SyncType): List<SyncableItem>
    suspend fun syncBatch(batch: List<SyncableItem>, config: SyncConfig): BatchSyncResult
    suspend fun applyServerVersion(conflict: SyncConflict)
    suspend fun applyClientVersion(conflict: SyncConflict)
    suspend fun applyMergedVersion(conflict: SyncConflict, mergedData: Map<String, Any>)
}

/**
 * Batch sync result
 */
data class BatchSyncResult(
    val itemsSynced: Int,
    val conflicts: List<SyncConflict> = emptyList(),
    val errors: List<String> = emptyList()
)

/**
 * Conflict resolver interface
 */
interface ConflictResolver {
    suspend fun resolveConflict(conflict: SyncConflict): ConflictResolution
    suspend fun mergeVersions(conflict: SyncConflict): Map<String, Any>
}

/**
 * Default conflict resolver implementation
 */
class DefaultConflictResolver : ConflictResolver {
    override suspend fun resolveConflict(conflict: SyncConflict): ConflictResolution {
        // Default strategy: server wins for most conflicts
        return when (conflict.conflictType) {
            ConflictType.UPDATE_UPDATE -> ConflictResolution.SERVER_WINS
            ConflictType.UPDATE_DELETE -> ConflictResolution.SERVER_WINS
            ConflictType.DELETE_UPDATE -> ConflictResolution.SERVER_WINS
        }
    }
    
    override suspend fun mergeVersions(conflict: SyncConflict): Map<String, Any> {
        // Simple merge strategy: combine non-conflicting fields
        val merged = conflict.clientVersion.toMutableMap()
        
        conflict.serverVersion.forEach { (key, value) ->
            if (!merged.containsKey(key) || merged[key] == null) {
                merged[key] = value
            }
        }
        
        return merged
    }
}
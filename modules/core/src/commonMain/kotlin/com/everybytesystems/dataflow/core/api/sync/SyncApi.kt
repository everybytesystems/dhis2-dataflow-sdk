package com.everybytesystems.dataflow.core.api.sync

import com.everybytesystems.dataflow.core.api.base.BaseApi
import com.everybytesystems.dataflow.core.config.DHIS2Config
import com.everybytesystems.dataflow.core.network.ApiResponse
import com.everybytesystems.dataflow.core.version.DHIS2Version
import io.ktor.client.*
import kotlinx.serialization.Serializable

/**
 * Advanced Synchronization API for DHIS2 - Data synchronization between instances
 * 
 * Provides comprehensive data synchronization capabilities including:
 * - Data synchronization between DHIS2 instances
 * - Conflict resolution mechanisms
 * - Incremental sync with change tracking
 * - Bidirectional sync support
 * - Sync monitoring and analytics
 * - Custom sync rules and filters
 */
class SyncApi(
    httpClient: HttpClient,
    config: DHIS2Config,
    private val version: DHIS2Version
) : BaseApi(httpClient, config) {
    
    // ========================================
    // DATA SYNCHRONIZATION
    // ========================================
    
    /**
     * Synchronize data between DHIS2 instances
     */
    suspend fun synchronizeData(
        sourceInstance: String,
        targetInstance: String,
        syncOptions: SyncOptions
    ): ApiResponse<SyncResult> {
        if (!version.supportsAdvancedSync()) {
            return ApiResponse.Error(UnsupportedOperationException("Advanced synchronization not supported in version ${version.versionString}"))
        }
        
        val request = SyncRequest(
            sourceInstance = sourceInstance,
            targetInstance = targetInstance,
            options = syncOptions
        )
        
        return post("sync/data", request)
    }
    
    /**
     * Get synchronization status
     */
    suspend fun getSyncStatus(syncId: String): ApiResponse<SyncStatus> {
        return get("sync/status/$syncId")
    }
    
    /**
     * Get synchronization history
     */
    suspend fun getSyncHistory(
        page: Int = 1,
        pageSize: Int = 50,
        startDate: String? = null,
        endDate: String? = null
    ): ApiResponse<SyncHistoryResponse> {
        val params = mutableMapOf(
            "page" to page.toString(),
            "pageSize" to pageSize.toString()
        )
        
        startDate?.let { params["startDate"] = it }
        endDate?.let { params["endDate"] = it }
        
        return get("sync/history", params)
    }
    
    /**
     * Cancel ongoing synchronization
     */
    suspend fun cancelSync(syncId: String): ApiResponse<SyncCancelResponse> {
        return delete("sync/$syncId")
    }
    
    // ========================================
    // METADATA SYNCHRONIZATION
    // ========================================
    
    /**
     * Synchronize metadata between instances
     */
    suspend fun synchronizeMetadata(
        sourceInstance: String,
        targetInstance: String,
        metadataTypes: List<String> = emptyList()
    ): ApiResponse<SyncResult> {
        val request = MetadataSyncRequest(
            sourceInstance = sourceInstance,
            targetInstance = targetInstance,
            metadataTypes = metadataTypes
        )
        
        return post("sync/metadata", request)
    }
    
    /**
     * Compare metadata between instances
     */
    suspend fun compareMetadata(
        sourceInstance: String,
        targetInstance: String,
        metadataTypes: List<String> = emptyList()
    ): ApiResponse<MetadataComparisonResult> {
        val request = MetadataComparisonRequest(
            sourceInstance = sourceInstance,
            targetInstance = targetInstance,
            metadataTypes = metadataTypes
        )
        
        return post("sync/metadata/compare", request)
    }
    
    // ========================================
    // CONFLICT RESOLUTION
    // ========================================
    
    /**
     * Get sync conflicts
     */
    suspend fun getSyncConflicts(syncId: String): ApiResponse<SyncConflictsResponse> {
        return get("sync/$syncId/conflicts")
    }
    
    /**
     * Resolve sync conflicts
     */
    suspend fun resolveSyncConflicts(
        syncId: String,
        resolutions: List<ConflictResolution>
    ): ApiResponse<ConflictResolutionResult> {
        val request = ConflictResolutionRequest(resolutions)
        return post("sync/$syncId/conflicts/resolve", request)
    }
    
    // ========================================
    // SYNC RULES AND FILTERS
    // ========================================
    
    /**
     * Create sync rule
     */
    suspend fun createSyncRule(rule: SyncRule): ApiResponse<SyncRuleResponse> {
        return post("sync/rules", rule)
    }
    
    /**
     * Get sync rules
     */
    suspend fun getSyncRules(
        page: Int = 1,
        pageSize: Int = 50
    ): ApiResponse<SyncRulesResponse> {
        val params = mapOf(
            "page" to page.toString(),
            "pageSize" to pageSize.toString()
        )
        
        return get("sync/rules", params)
    }
    
    /**
     * Update sync rule
     */
    suspend fun updateSyncRule(id: String, rule: SyncRule): ApiResponse<SyncRuleResponse> {
        return put("sync/rules/$id", rule)
    }
    
    /**
     * Delete sync rule
     */
    suspend fun deleteSyncRule(id: String): ApiResponse<Unit> {
        return delete("sync/rules/$id")
    }
    
    // ========================================
    // SYNC MONITORING AND ANALYTICS
    // ========================================
    
    /**
     * Get sync analytics
     */
    suspend fun getSyncAnalytics(
        startDate: String,
        endDate: String,
        groupBy: String = "day"
    ): ApiResponse<SyncAnalyticsResponse> {
        val params = mapOf(
            "startDate" to startDate,
            "endDate" to endDate,
            "groupBy" to groupBy
        )
        
        return get("sync/analytics", params)
    }
    
    /**
     * Get sync performance metrics
     */
    suspend fun getSyncPerformanceMetrics(
        syncId: String? = null
    ): ApiResponse<SyncPerformanceMetrics> {
        val endpoint = if (syncId != null) {
            "sync/$syncId/performance"
        } else {
            "sync/performance"
        }
        
        return get(endpoint)
    }
    
    /**
     * Get sync health status
     */
    suspend fun getSyncHealthStatus(): ApiResponse<SyncHealthStatus> {
        return get("sync/health")
    }
    
    // ========================================
    // INCREMENTAL SYNC
    // ========================================
    
    /**
     * Start incremental sync
     */
    suspend fun startIncrementalSync(
        sourceInstance: String,
        targetInstance: String,
        lastSyncTime: String? = null
    ): ApiResponse<SyncResult> {
        val request = IncrementalSyncRequest(
            sourceInstance = sourceInstance,
            targetInstance = targetInstance,
            lastSyncTime = lastSyncTime
        )
        
        return post("sync/incremental", request)
    }
    
    /**
     * Get change tracking information
     */
    suspend fun getChangeTracking(
        since: String,
        entityTypes: List<String> = emptyList()
    ): ApiResponse<ChangeTrackingResponse> {
        val params = mutableMapOf("since" to since)
        if (entityTypes.isNotEmpty()) {
            params["entityTypes"] = entityTypes.joinToString(",")
        }
        
        return get("sync/changes", params)
    }
}

// ========================================
// SYNC MODELS
// ========================================

@Serializable
data class SyncRequest(
    val sourceInstance: String,
    val targetInstance: String,
    val options: SyncOptions
)

@Serializable
data class SyncOptions(
    val includeMetadata: Boolean = true,
    val includeData: Boolean = true,
    val conflictResolution: ConflictResolutionStrategy = ConflictResolutionStrategy.MANUAL,
    val dataFilters: List<DataFilter> = emptyList(),
    val metadataFilters: List<MetadataFilter> = emptyList(),
    val batchSize: Int = 1000,
    val timeout: Long = 300000, // 5 minutes
    val retryAttempts: Int = 3
)

@Serializable
data class SyncResult(
    val syncId: String,
    val status: SyncStatusType,
    val startTime: String,
    val endTime: String? = null,
    val progress: SyncProgress,
    val conflicts: List<SyncConflict> = emptyList(),
    val errors: List<SyncError> = emptyList(),
    val summary: SyncSummary
)

@Serializable
data class SyncStatus(
    val syncId: String,
    val status: SyncStatusType,
    val progress: SyncProgress,
    val currentOperation: String? = null,
    val estimatedTimeRemaining: Long? = null,
    val lastUpdate: String
)

@Serializable
data class SyncProgress(
    val totalItems: Int,
    val processedItems: Int,
    val successfulItems: Int,
    val failedItems: Int,
    val conflictItems: Int,
    val percentage: Double
)

@Serializable
data class SyncSummary(
    val totalRecords: Int,
    val successfulRecords: Int,
    val failedRecords: Int,
    val conflictRecords: Int,
    val skippedRecords: Int,
    val duration: Long,
    val throughput: Double // records per second
)

@Serializable
data class SyncConflict(
    val id: String,
    val entityType: String,
    val entityId: String,
    val conflictType: ConflictType,
    val sourceValue: String,
    val targetValue: String,
    val description: String,
    val resolution: ConflictResolutionStrategy? = null
)

@Serializable
data class SyncError(
    val id: String,
    val entityType: String,
    val entityId: String? = null,
    val errorType: SyncErrorType,
    val message: String,
    val details: String? = null,
    val timestamp: String
)

@Serializable
data class MetadataSyncRequest(
    val sourceInstance: String,
    val targetInstance: String,
    val metadataTypes: List<String> = emptyList()
)

@Serializable
data class MetadataComparisonRequest(
    val sourceInstance: String,
    val targetInstance: String,
    val metadataTypes: List<String> = emptyList()
)

@Serializable
data class MetadataComparisonResult(
    val differences: List<MetadataDifference>,
    val summary: ComparisonSummary
)

@Serializable
data class MetadataDifference(
    val entityType: String,
    val entityId: String,
    val differenceType: DifferenceType,
    val sourceValue: String? = null,
    val targetValue: String? = null,
    val fieldName: String? = null
)

@Serializable
data class ComparisonSummary(
    val totalEntities: Int,
    val identicalEntities: Int,
    val differentEntities: Int,
    val sourceOnlyEntities: Int,
    val targetOnlyEntities: Int
)

@Serializable
data class SyncRule(
    val id: String? = null,
    val name: String,
    val description: String? = null,
    val sourceInstance: String,
    val targetInstance: String,
    val schedule: SyncSchedule? = null,
    val filters: List<SyncFilter> = emptyList(),
    val options: SyncOptions,
    val isActive: Boolean = true,
    val created: String? = null,
    val lastModified: String? = null
)

@Serializable
data class SyncSchedule(
    val type: ScheduleType,
    val cronExpression: String? = null,
    val intervalMinutes: Int? = null,
    val startTime: String? = null,
    val endTime: String? = null
)

@Serializable
data class SyncFilter(
    val entityType: String,
    val field: String,
    val operator: FilterOperator,
    val value: String
)

@Serializable
data class DataFilter(
    val dataElementId: String? = null,
    val organisationUnitId: String? = null,
    val periodId: String? = null,
    val startDate: String? = null,
    val endDate: String? = null
)

@Serializable
data class MetadataFilter(
    val metadataType: String,
    val includeFields: List<String> = emptyList(),
    val excludeFields: List<String> = emptyList(),
    val filters: List<String> = emptyList()
)

@Serializable
data class ConflictResolutionRequest(
    val resolutions: List<ConflictResolution>
)

@Serializable
data class ConflictResolution(
    val conflictId: String,
    val resolution: ConflictResolutionStrategy,
    val customValue: String? = null
)

@Serializable
data class ConflictResolutionResult(
    val resolvedConflicts: Int,
    val failedResolutions: Int,
    val errors: List<String> = emptyList()
)

@Serializable
data class IncrementalSyncRequest(
    val sourceInstance: String,
    val targetInstance: String,
    val lastSyncTime: String? = null
)

@Serializable
data class ChangeTrackingResponse(
    val changes: List<EntityChange>,
    val lastChangeTime: String,
    val hasMoreChanges: Boolean
)

@Serializable
data class EntityChange(
    val entityType: String,
    val entityId: String,
    val changeType: ChangeType,
    val timestamp: String,
    val userId: String? = null,
    val details: Map<String, String> = emptyMap()
)

// Response Models
@Serializable
data class SyncHistoryResponse(
    val syncHistory: List<SyncHistoryItem> = emptyList(),
    val pager: Pager? = null
)

@Serializable
data class SyncHistoryItem(
    val syncId: String,
    val sourceInstance: String,
    val targetInstance: String,
    val status: SyncStatusType,
    val startTime: String,
    val endTime: String? = null,
    val duration: Long? = null,
    val recordsProcessed: Int,
    val recordsSuccessful: Int,
    val recordsFailed: Int,
    val conflictsCount: Int
)

@Serializable
data class SyncCancelResponse(
    val syncId: String,
    val cancelled: Boolean,
    val message: String
)

@Serializable
data class SyncRuleResponse(
    val id: String,
    val name: String,
    val status: String,
    val created: String,
    val lastModified: String
)

@Serializable
data class SyncRulesResponse(
    val syncRules: List<SyncRule> = emptyList(),
    val pager: Pager? = null
)

@Serializable
data class SyncConflictsResponse(
    val conflicts: List<SyncConflict> = emptyList(),
    val totalConflicts: Int,
    val unresolvedConflicts: Int
)

@Serializable
data class SyncAnalyticsResponse(
    val analytics: List<SyncAnalyticsItem> = emptyList(),
    val summary: SyncAnalyticsSummary
)

@Serializable
data class SyncAnalyticsItem(
    val date: String,
    val totalSyncs: Int,
    val successfulSyncs: Int,
    val failedSyncs: Int,
    val averageDuration: Double,
    val totalRecords: Int
)

@Serializable
data class SyncAnalyticsSummary(
    val totalSyncs: Int,
    val successRate: Double,
    val averageDuration: Double,
    val totalRecordsProcessed: Int,
    val averageThroughput: Double
)

@Serializable
data class SyncPerformanceMetrics(
    val averageResponseTime: Double,
    val throughput: Double,
    val errorRate: Double,
    val resourceUtilization: ResourceUtilization,
    val bottlenecks: List<PerformanceBottleneck> = emptyList()
)

@Serializable
data class ResourceUtilization(
    val cpuUsage: Double,
    val memoryUsage: Double,
    val networkUsage: Double,
    val diskUsage: Double
)

@Serializable
data class PerformanceBottleneck(
    val component: String,
    val severity: BottleneckSeverity,
    val description: String,
    val recommendation: String
)

@Serializable
data class SyncHealthStatus(
    val overallHealth: HealthStatus,
    val components: List<ComponentHealth>,
    val lastCheck: String
)

@Serializable
data class ComponentHealth(
    val component: String,
    val status: HealthStatus,
    val message: String? = null,
    val lastCheck: String
)

@Serializable
data class Pager(
    val page: Int,
    val pageSize: Int,
    val total: Int,
    val pageCount: Int
)

// Enums
enum class SyncStatusType { PENDING, RUNNING, COMPLETED, FAILED, CANCELLED, PAUSED }
enum class ConflictType { DATA_CONFLICT, METADATA_CONFLICT, PERMISSION_CONFLICT, VALIDATION_CONFLICT }
enum class ConflictResolutionStrategy { SOURCE_WINS, TARGET_WINS, MANUAL, MERGE, SKIP }
enum class SyncErrorType { NETWORK_ERROR, PERMISSION_ERROR, VALIDATION_ERROR, DATA_ERROR, SYSTEM_ERROR }
enum class DifferenceType { ADDED, MODIFIED, DELETED, MOVED }
enum class ScheduleType { CRON, INTERVAL, ONE_TIME }
enum class FilterOperator { EQUALS, NOT_EQUALS, CONTAINS, STARTS_WITH, ENDS_WITH, GREATER_THAN, LESS_THAN, IN, NOT_IN }
enum class ChangeType { CREATE, UPDATE, DELETE }
enum class BottleneckSeverity { LOW, MEDIUM, HIGH, CRITICAL }
enum class HealthStatus { HEALTHY, WARNING, CRITICAL, UNKNOWN }
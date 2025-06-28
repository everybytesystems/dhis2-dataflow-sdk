package com.everybytesystems.dataflow.core.api.system

import com.everybytesystems.dataflow.core.api.base.BaseApi
import com.everybytesystems.dataflow.core.config.DHIS2Config
import com.everybytesystems.dataflow.core.network.ApiResponse
import com.everybytesystems.dataflow.core.version.DHIS2Version
import io.ktor.client.*
import kotlinx.serialization.Serializable

/**
 * System API for DHIS2 - Enhanced system operations with version support
 */
class SystemApi(
    httpClient: HttpClient,
    config: DHIS2Config,
    private val version: DHIS2Version
) : BaseApi(httpClient, config) {
    
    suspend fun getSystemInfo(): ApiResponse<SystemInfo> {
        return get("system/info")
    }
    
    suspend fun ping(): ApiResponse<String> {
        return get("system/ping")
    }
    
    suspend fun getSystemSettings(): ApiResponse<Map<String, Any>> {
        return get("systemSettings")
    }
    
    suspend fun getSystemSetting(key: String): ApiResponse<Any> {
        return get("systemSettings/$key")
    }
    
    suspend fun setSystemSetting(key: String, value: Any): ApiResponse<Unit> {
        return post("systemSettings/$key", value)
    }
    
    suspend fun deleteSystemSetting(key: String): ApiResponse<Unit> {
        return delete("systemSettings/$key")
    }
    
    suspend fun getFlags(): ApiResponse<Map<String, Any>> {
        return get("system/flags")
    }
    
    suspend fun getStyles(): ApiResponse<Map<String, Any>> {
        return get("system/styles")
    }
    
    suspend fun getUidGeneration(limit: Int = 1): ApiResponse<UidResponse> {
        return get("system/uid", mapOf("limit" to limit.toString()))
    }
    
    suspend fun getTaskSummaries(): ApiResponse<List<TaskSummary>> {
        return get("system/tasks")
    }
    
    suspend fun getTaskSummary(category: String, id: String): ApiResponse<TaskSummary> {
        return get("system/tasks/$category/$id")
    }
    
    // ========================================
    // VERSION-AWARE METHODS
    // ========================================
    
    /**
     * Get system settings with enhanced filtering
     */
    suspend fun getSystemSettings(
        filter: String? = null
    ): ApiResponse<Map<String, Any>> {
        val params = buildMap {
            filter?.let { put("filter", it) }
        }
        return get("systemSettings", params)
    }
    
    /**
     * Set system setting with validation
     */
    suspend fun setSystemSetting(
        key: String, 
        value: Any,
        validate: Boolean = true
    ): ApiResponse<SystemSettingResponse> {
        val params = buildMap {
            put("validate", validate.toString())
        }
        return post("systemSettings/$key", value, params)
    }
    
    /**
     * Get system statistics (2.37+)
     */
    suspend fun getSystemStatistics(): ApiResponse<SystemStatisticsResponse> {
        if (!version.supportsSystemStatistics()) {
            return ApiResponse.Error(UnsupportedOperationException("System statistics not supported in version ${version.versionString}"))
        }
        
        return get("system/statistics")
    }
    
    /**
     * Get system health status (2.38+)
     */
    suspend fun getSystemHealth(): ApiResponse<SystemHealthResponse> {
        if (!version.supportsSystemHealth()) {
            return ApiResponse.Error(UnsupportedOperationException("System health not supported in version ${version.versionString}"))
        }
        
        return get("system/health")
    }
    
    /**
     * Get system performance metrics (2.38+)
     */
    suspend fun getSystemPerformance(): ApiResponse<SystemPerformanceResponse> {
        if (!version.supportsSystemPerformance()) {
            return ApiResponse.Error(UnsupportedOperationException("System performance not supported in version ${version.versionString}"))
        }
        
        return get("system/performance")
    }
    
    /**
     * Clear system cache with type specification
     */
    suspend fun clearCache(cacheType: String? = null): ApiResponse<CacheOperationResponse> {
        val params = buildMap {
            cacheType?.let { put("type", it) }
        }
        return post("system/cache/clear", emptyMap<String, Any>(), params)
    }
    
    /**
     * Get cache information
     */
    suspend fun getCacheInfo(): ApiResponse<CacheInfoResponse> {
        return get("system/cache")
    }
    
    /**
     * Reload system configuration
     */
    suspend fun reloadConfiguration(): ApiResponse<SystemOperationResponse> {
        return post("system/reload", emptyMap<String, Any>())
    }
    
    /**
     * Get system maintenance status (2.37+)
     */
    suspend fun getMaintenanceStatus(): ApiResponse<MaintenanceStatusResponse> {
        if (!version.supportsMaintenanceMode()) {
            return ApiResponse.Error(UnsupportedOperationException("Maintenance mode not supported in version ${version.versionString}"))
        }
        
        return get("system/maintenance")
    }
    
    /**
     * Enable maintenance mode (2.37+)
     */
    suspend fun enableMaintenanceMode(message: String? = null): ApiResponse<MaintenanceOperationResponse> {
        if (!version.supportsMaintenanceMode()) {
            return ApiResponse.Error(UnsupportedOperationException("Maintenance mode not supported in version ${version.versionString}"))
        }
        
        val params = buildMap {
            message?.let { put("message", it) }
        }
        return post("system/maintenance/enable", emptyMap<String, Any>(), params)
    }
    
    /**
     * Disable maintenance mode (2.37+)
     */
    suspend fun disableMaintenanceMode(): ApiResponse<MaintenanceOperationResponse> {
        if (!version.supportsMaintenanceMode()) {
            return ApiResponse.Error(UnsupportedOperationException("Maintenance mode not supported in version ${version.versionString}"))
        }
        
        return post("system/maintenance/disable", emptyMap<String, Any>())
    }
}

@Serializable
data class SystemInfo(
    val version: String,
    val revision: String? = null,
    val buildTime: String? = null,
    val serverDate: String? = null,
    val instanceBaseUrl: String? = null,
    val contextPath: String? = null,
    val userAgent: String? = null,
    val calendar: String? = null,
    val dateFormat: String? = null,
    val systemId: String? = null,
    val systemName: String? = null,
    val databaseInfo: DatabaseInfo? = null
)

@Serializable
data class DatabaseInfo(
    val name: String? = null,
    val user: String? = null,
    val url: String? = null,
    val databaseVersion: String? = null,
    val spatialSupport: Boolean = false
)

@Serializable
data class UidResponse(
    val codes: List<String> = emptyList()
)

@Serializable
data class TaskSummary(
    val uid: String,
    val level: String,
    val category: String,
    val time: String,
    val message: String,
    val completed: Boolean
)

// ========================================
// VERSION-AWARE MODELS
// ========================================

@Serializable
data class SystemSettingResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null
)

@Serializable
data class SystemStatisticsResponse(
    val statistics: SystemStatistics
)

@Serializable
data class SystemStatistics(
    val objectCounts: Map<String, Int> = emptyMap(),
    val userCounts: Map<String, Int> = emptyMap(),
    val dataValueCount: Long = 0,
    val eventCount: Long = 0,
    val enrollmentCount: Long = 0,
    val trackedEntityInstanceCount: Long = 0
)

@Serializable
data class SystemHealthResponse(
    val status: String,
    val checks: List<HealthCheck> = emptyList(),
    val timestamp: String
)

@Serializable
data class HealthCheck(
    val name: String,
    val status: String,
    val message: String? = null,
    val duration: Long? = null
)

@Serializable
data class SystemPerformanceResponse(
    val performance: SystemPerformance
)

@Serializable
data class SystemPerformance(
    val cpuUsage: Double = 0.0,
    val memoryUsage: Double = 0.0,
    val diskUsage: Double = 0.0,
    val responseTime: Long = 0,
    val throughput: Double = 0.0,
    val activeConnections: Int = 0
)

@Serializable
data class CacheOperationResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null
)

@Serializable
data class CacheInfoResponse(
    val caches: List<CacheInfo> = emptyList()
)

@Serializable
data class CacheInfo(
    val name: String,
    val size: Long = 0,
    val hitCount: Long = 0,
    val missCount: Long = 0,
    val hitRate: Double = 0.0
)

@Serializable
data class SystemOperationResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null
)

@Serializable
data class MaintenanceStatusResponse(
    val maintenanceMode: Boolean = false,
    val message: String? = null,
    val enabledAt: String? = null,
    val enabledBy: String? = null
)

@Serializable
data class MaintenanceOperationResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null
)
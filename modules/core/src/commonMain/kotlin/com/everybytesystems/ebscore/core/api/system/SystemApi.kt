package com.everybytesystems.ebscore.core.api.system

import com.everybytesystems.ebscore.core.api.base.BaseApi
import com.everybytesystems.ebscore.core.config.DHIS2Config
import com.everybytesystems.ebscore.core.network.ApiResponse
import com.everybytesystems.ebscore.core.version.DHIS2Version
import io.ktor.client.*
import kotlinx.serialization.Serializable

/**
 * Complete System API implementation for DHIS2
 */
class SystemApi(
    httpClient: HttpClient,
    config: DHIS2Config,
    private val version: DHIS2Version
) : BaseApi(httpClient, config) {

    /**
     * Get system information
     */
    suspend fun getSystemInfo(): ApiResponse<SystemInfo> {
        return get("system/info")
    }

    /**
     * Ping the system
     */
    suspend fun ping(): ApiResponse<String> {
        return get("system/ping")
    }

    /**
     * Get system flags
     */
    suspend fun getFlags(): ApiResponse<Map<String, Boolean>> {
        return get("system/flags")
    }

    /**
     * Get system styles
     */
    suspend fun getStyles(): ApiResponse<SystemStyles> {
        return get("system/styles")
    }

    /**
     * Get system settings
     */
    suspend fun getSystemSettings(): ApiResponse<Map<String, Any>> {
        return get("systemSettings")
    }

    /**
     * Get specific system setting
     */
    suspend fun getSystemSetting(key: String): ApiResponse<String> {
        return get("systemSettings/$key")
    }

    /**
     * Set system setting
     */
    suspend fun setSystemSetting(key: String, value: String): ApiResponse<Unit> {
        return post("systemSettings/$key", value)
    }

    /**
     * Delete system setting
     */
    suspend fun deleteSystemSetting(key: String): ApiResponse<Unit> {
        return delete("systemSettings/$key")
    }

    /**
     * Get user settings
     */
    suspend fun getUserSettings(): ApiResponse<Map<String, Any>> {
        return get("userSettings")
    }

    /**
     * Get specific user setting
     */
    suspend fun getUserSetting(key: String): ApiResponse<String> {
        return get("userSettings/$key")
    }

    /**
     * Set user setting
     */
    suspend fun setUserSetting(key: String, value: String): ApiResponse<Unit> {
        return post("userSettings/$key", value)
    }

    /**
     * Delete user setting
     */
    suspend fun deleteUserSetting(key: String): ApiResponse<Unit> {
        return delete("userSettings/$key")
    }

    /**
     * Get configuration
     */
    suspend fun getConfiguration(): ApiResponse<Configuration> {
        return get("configuration")
    }

    /**
     * Get server date
     */
    suspend fun getServerDate(): ApiResponse<ServerDate> {
        return get("system/serverDate")
    }

    /**
     * Get tasks
     */
    suspend fun getTasks(
        category: String? = null,
        page: Int = 1,
        pageSize: Int = 50
    ): ApiResponse<TasksResponse> {
        val params = mutableMapOf<String, String>().apply {
            category?.let { put("category", it) }
            put("page", page.toString())
            put("pageSize", pageSize.toString())
        }
        
        return get("system/tasks", params)
    }

    /**
     * Get task by ID
     */
    suspend fun getTask(id: String): ApiResponse<Task> {
        return get("system/tasks/$id")
    }

    /**
     * Get task summaries
     */
    suspend fun getTaskSummaries(
        category: String? = null
    ): ApiResponse<List<TaskSummary>> {
        val params = mutableMapOf<String, String>().apply {
            category?.let { put("category", it) }
        }
        
        return get("system/taskSummaries", params)
    }

    /**
     * Get notifications
     */
    suspend fun getNotifications(
        page: Int = 1,
        pageSize: Int = 50
    ): ApiResponse<NotificationsResponse> {
        val params = mapOf(
            "page" to page.toString(),
            "pageSize" to pageSize.toString()
        )
        
        return get("system/notifications", params)
    }

    /**
     * Clear cache
     */
    suspend fun clearCache(): ApiResponse<Unit> {
        return post("maintenance/cacheClear")
    }

    /**
     * Reload apps
     */
    suspend fun reloadApps(): ApiResponse<Unit> {
        return post("maintenance/appReload")
    }

    /**
     * Generate analytics tables
     */
    suspend fun generateAnalyticsTables(
        skipResourceTables: Boolean = false,
        skipAggregate: Boolean = false,
        skipEvents: Boolean = false,
        skipEnrollments: Boolean = false,
        lastYears: Int? = null
    ): ApiResponse<TaskSummary> {
        val params = mutableMapOf<String, String>().apply {
            if (skipResourceTables) put("skipResourceTables", "true")
            if (skipAggregate) put("skipAggregate", "true")
            if (skipEvents) put("skipEvents", "true")
            if (skipEnrollments) put("skipEnrollments", "true")
            lastYears?.let { put("lastYears", it.toString()) }
        }
        
        return post("resourceTables/analytics", params = params)
    }

    /**
     * Generate resource tables
     */
    suspend fun generateResourceTables(): ApiResponse<TaskSummary> {
        return post("resourceTables")
    }

    /**
     * Perform maintenance
     */
    suspend fun performMaintenance(
        dataPruning: Boolean = false,
        zeroDataValueRemoval: Boolean = false,
        softDeletedDataValueRemoval: Boolean = false,
        softDeletedProgramStageInstanceRemoval: Boolean = false,
        softDeletedProgramInstanceRemoval: Boolean = false,
        softDeletedTrackedEntityInstanceRemoval: Boolean = false,
        expiredInvitationsClearing: Boolean = false,
        ouPathsUpdate: Boolean = false,
        cacheClear: Boolean = false,
        appReload: Boolean = false
    ): ApiResponse<Unit> {
        val params = mutableMapOf<String, String>().apply {
            if (dataPruning) put("dataPruning", "true")
            if (zeroDataValueRemoval) put("zeroDataValueRemoval", "true")
            if (softDeletedDataValueRemoval) put("softDeletedDataValueRemoval", "true")
            if (softDeletedProgramStageInstanceRemoval) put("softDeletedProgramStageInstanceRemoval", "true")
            if (softDeletedProgramInstanceRemoval) put("softDeletedProgramInstanceRemoval", "true")
            if (softDeletedTrackedEntityInstanceRemoval) put("softDeletedTrackedEntityInstanceRemoval", "true")
            if (expiredInvitationsClearing) put("expiredInvitationsClearing", "true")
            if (ouPathsUpdate) put("ouPathsUpdate", "true")
            if (cacheClear) put("cacheClear", "true")
            if (appReload) put("appReload", "true")
        }
        
        return post("maintenance", params = params)
    }

    /**
     * Get database info
     */
    suspend fun getDatabaseInfo(): ApiResponse<DatabaseInfo> {
        return get("system/databaseInfo")
    }

    /**
     * Get statistics
     */
    suspend fun getStatistics(): ApiResponse<Statistics> {
        return get("system/statistics")
    }
}

// Data Models
@Serializable
data class SystemInfo(
    val contextPath: String? = null,
    val userAgent: String? = null,
    val version: String,
    val revision: String? = null,
    val buildTime: String? = null,
    val serverDate: String,
    val serverTimeZoneId: String? = null,
    val serverTimeZoneDisplayName: String? = null,
    val instanceBaseUrl: String? = null,
    val emailConfigured: Boolean = false,
    val redisEnabled: Boolean = false,
    val systemId: String? = null,
    val systemName: String? = null,
    val databaseInfo: DatabaseInfo? = null,
    val readOnlyMode: String? = null,
    val nodeId: String? = null,
    val systemMonitoring: SystemMonitoring? = null,
    val calendar: String? = null,
    val dateFormat: String? = null,
    val startModule: String? = null,
    val analyticsMaxLimit: Int? = null
)

@Serializable
data class SystemStyles(
    val keyStyle: String? = null,
    val keyApplicationTitle: String? = null,
    val keyApplicationIntro: String? = null,
    val keyApplicationNotification: String? = null,
    val keyApplicationLeftFooter: String? = null,
    val keyApplicationRightFooter: String? = null,
    val keyFlag: String? = null,
    val keyFlagImage: String? = null,
    val keyLoginPopup: String? = null
)

@Serializable
data class Configuration(
    val systemId: String? = null,
    val feedbackRecipients: String? = null,
    val offlineOrganisationUnitLevel: Int? = null,
    val infrastructuralIndicators: String? = null,
    val infrastructuralDataElements: String? = null,
    val infrastructuralPeriodType: String? = null,
    val selfRegistrationRole: String? = null,
    val selfRegistrationOrgUnit: String? = null,
    val multiOrganisationUnitForms: Boolean = false,
    val corsWhitelist: List<String> = emptyList()
)

@Serializable
data class ServerDate(
    val serverDate: String
)

@Serializable
data class TasksResponse(
    val tasks: List<Task> = emptyList(),
    val pager: Pager? = null
)

@Serializable
data class Task(
    val id: String,
    val level: String,
    val category: String,
    val time: String,
    val message: String,
    val completed: Boolean = false,
    val uid: String? = null
)

@Serializable
data class TaskSummary(
    val uid: String,
    val level: String,
    val category: String,
    val time: String,
    val message: String,
    val completed: Boolean = false
)

@Serializable
data class NotificationsResponse(
    val notifications: List<Notification> = emptyList(),
    val pager: Pager? = null
)

@Serializable
data class Notification(
    val uid: String,
    val level: String,
    val category: String,
    val time: String,
    val message: String,
    val completed: Boolean = false
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
data class SystemMonitoring(
    val uptime: Long? = null,
    val cpuUsage: Double? = null,
    val memoryUsage: Double? = null
)

@Serializable
data class Statistics(
    val userCount: Int = 0,
    val organisationUnitCount: Int = 0,
    val dataElementCount: Int = 0,
    val dataSetCount: Int = 0,
    val programCount: Int = 0,
    val indicatorCount: Int = 0,
    val dashboardCount: Int = 0,
    val dataValueCount: Long = 0,
    val eventCount: Long = 0,
    val enrollmentCount: Long = 0,
    val trackedEntityInstanceCount: Long = 0
)

@Serializable
data class Pager(
    val page: Int,
    val pageCount: Int,
    val total: Int,
    val pageSize: Int,
    val nextPage: String? = null,
    val prevPage: String? = null
)
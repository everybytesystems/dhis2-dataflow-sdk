package com.everybytesystems.dataflow.core.api.systemsettings

import com.everybytesystems.dataflow.core.api.base.BaseApi
import com.everybytesystems.dataflow.core.config.DHIS2Config
import com.everybytesystems.dataflow.core.network.ApiResponse
import com.everybytesystems.dataflow.core.version.DHIS2Version
import io.ktor.client.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Complete System Settings API implementation for DHIS2 2.36+
 * Supports system configuration, maintenance, monitoring, and advanced management
 */
class SystemSettingsApi(
    httpClient: HttpClient,
    config: DHIS2Config,
    private val version: DHIS2Version
) : BaseApi(httpClient, config) {
    
    // ========================================
    // SYSTEM SETTINGS OPERATIONS
    // ========================================
    
    /**
     * Get all system settings
     */
    suspend fun getSystemSettings(): ApiResponse<SystemSettingsResponse> {
        return get("systemSettings")
    }
    
    /**
     * Get a specific system setting
     */
    suspend fun getSystemSetting(key: String): ApiResponse<JsonElement> {
        return get("systemSettings/$key")
    }
    
    /**
     * Set a system setting
     */
    suspend fun setSystemSetting(
        key: String,
        value: JsonElement
    ): ApiResponse<SystemSettingResponse> {
        return post("systemSettings/$key", value)
    }
    
    /**
     * Update a system setting
     */
    suspend fun updateSystemSetting(
        key: String,
        value: JsonElement
    ): ApiResponse<SystemSettingResponse> {
        return put("systemSettings/$key", value)
    }
    
    /**
     * Delete a system setting
     */
    suspend fun deleteSystemSetting(key: String): ApiResponse<SystemSettingResponse> {
        return delete("systemSettings/$key")
    }
    
    /**
     * Bulk update system settings
     */
    suspend fun bulkUpdateSystemSettings(
        settings: Map<String, JsonElement>
    ): ApiResponse<SystemSettingsBulkResponse> {
        return post("systemSettings", settings)
    }
    
    /**
     * Reset system setting to default
     */
    suspend fun resetSystemSetting(key: String): ApiResponse<SystemSettingResponse> {
        return post("systemSettings/$key/reset", emptyMap<String, Any>())
    }
    
    /**
     * Get system setting metadata
     */
    suspend fun getSystemSettingMetadata(key: String): ApiResponse<SystemSettingMetadata> {
        return get("systemSettings/$key/metadata")
    }
    
    // ========================================
    // CONFIGURATION MANAGEMENT
    // ========================================
    
    /**
     * Get system configuration
     */
    suspend fun getSystemConfiguration(): ApiResponse<SystemConfiguration> {
        return get("system/configuration")
    }
    
    /**
     * Update system configuration
     */
    suspend fun updateSystemConfiguration(
        config: SystemConfiguration
    ): ApiResponse<SystemConfigurationResponse> {
        return put("system/configuration", config)
    }
    
    /**
     * Get appearance settings
     */
    suspend fun getAppearanceSettings(): ApiResponse<AppearanceSettings> {
        return get("system/appearance")
    }
    
    /**
     * Update appearance settings
     */
    suspend fun updateAppearanceSettings(
        settings: AppearanceSettings
    ): ApiResponse<AppearanceSettingsResponse> {
        return put("system/appearance", settings)
    }
    
    /**
     * Get email settings
     */
    suspend fun getEmailSettings(): ApiResponse<EmailSettings> {
        return get("system/email")
    }
    
    /**
     * Update email settings
     */
    suspend fun updateEmailSettings(
        settings: EmailSettings
    ): ApiResponse<EmailSettingsResponse> {
        return put("system/email", settings)
    }
    
    /**
     * Test email configuration
     */
    suspend fun testEmailConfiguration(): ApiResponse<EmailTestResponse> {
        return post("system/email/test", emptyMap<String, Any>())
    }
    
    // ========================================
    // MAINTENANCE OPERATIONS
    // ========================================
    
    /**
     * Perform system maintenance
     */
    suspend fun performMaintenance(
        operations: List<MaintenanceOperation>
    ): ApiResponse<MaintenanceResponse> {
        val maintenanceRequest = MaintenanceRequest(operations)
        return post("system/maintenance", maintenanceRequest)
    }
    
    /**
     * Clear application cache
     */
    suspend fun clearCache(cacheType: CacheType? = null): ApiResponse<CacheResponse> {
        val params = buildMap {
            cacheType?.let { put("type", it.name) }
        }
        return post("system/cache/clear", emptyMap<String, Any>(), params)
    }
    
    /**
     * Reload system configuration
     */
    suspend fun reloadConfiguration(): ApiResponse<SystemResponse> {
        return post("system/reload", emptyMap<String, Any>())
    }
    
    /**
     * Perform database maintenance
     */
    suspend fun performDatabaseMaintenance(
        operations: List<DatabaseMaintenanceOperation>
    ): ApiResponse<DatabaseMaintenanceResponse> {
        val request = DatabaseMaintenanceRequest(operations)
        return post("system/database/maintenance", request)
    }
    
    /**
     * Optimize database
     */
    suspend fun optimizeDatabase(): ApiResponse<DatabaseOptimizationResponse> {
        return post("system/database/optimize", emptyMap<String, Any>())
    }
    
    /**
     * Analyze database
     */
    suspend fun analyzeDatabase(): ApiResponse<DatabaseAnalysisResponse> {
        return post("system/database/analyze", emptyMap<String, Any>())
    }
    
    /**
     * Vacuum database
     */
    suspend fun vacuumDatabase(): ApiResponse<DatabaseVacuumResponse> {
        return post("system/database/vacuum", emptyMap<String, Any>())
    }
    
    // ========================================
    // MONITORING & HEALTH
    // ========================================
    
    /**
     * Get system health status
     */
    suspend fun getSystemHealth(): ApiResponse<SystemHealthResponse> {
        return get("system/health")
    }
    
    /**
     * Get system metrics
     */
    suspend fun getSystemMetrics(): ApiResponse<SystemMetricsResponse> {
        return get("system/metrics")
    }
    
    /**
     * Get system performance
     */
    suspend fun getSystemPerformance(
        startDate: String? = null,
        endDate: String? = null
    ): ApiResponse<SystemPerformanceResponse> {
        val params = buildMap {
            startDate?.let { put("startDate", it) }
            endDate?.let { put("endDate", it) }
        }
        return get("system/performance", params)
    }
    
    /**
     * Get system logs
     */
    suspend fun getSystemLogs(
        level: LogLevel? = null,
        startDate: String? = null,
        endDate: String? = null,
        page: Int? = null,
        pageSize: Int? = null
    ): ApiResponse<SystemLogsResponse> {
        val params = buildMap {
            level?.let { put("level", it.name) }
            startDate?.let { put("startDate", it) }
            endDate?.let { put("endDate", it) }
            page?.let { put("page", it.toString()) }
            pageSize?.let { put("pageSize", it.toString()) }
        }
        return get("system/logs", params)
    }
    
    /**
     * Get audit logs
     */
    suspend fun getAuditLogs(
        auditType: AuditType? = null,
        startDate: String? = null,
        endDate: String? = null,
        page: Int? = null,
        pageSize: Int? = null
    ): ApiResponse<AuditLogsResponse> {
        val params = buildMap {
            auditType?.let { put("auditType", it.name) }
            startDate?.let { put("startDate", it) }
            endDate?.let { put("endDate", it) }
            page?.let { put("page", it.toString()) }
            pageSize?.let { put("pageSize", it.toString()) }
        }
        return get("system/audit", params)
    }
    
    // ========================================
    // SECURITY SETTINGS (2.37+)
    // ========================================
    
    /**
     * Get security settings (2.37+)
     */
    suspend fun getSecuritySettings(): ApiResponse<SecuritySettings> {
        if (!version.supportsSecuritySettings()) {
            return ApiResponse.Error(UnsupportedOperationException("Security settings not supported in version ${version.versionString}"))
        }
        
        return get("system/security")
    }
    
    /**
     * Update security settings (2.37+)
     */
    suspend fun updateSecuritySettings(
        settings: SecuritySettings
    ): ApiResponse<SecuritySettingsResponse> {
        if (!version.supportsSecuritySettings()) {
            return ApiResponse.Error(UnsupportedOperationException("Security settings not supported in version ${version.versionString}"))
        }
        
        return put("system/security", settings)
    }
    
    /**
     * Get password policy (2.37+)
     */
    suspend fun getPasswordPolicy(): ApiResponse<PasswordPolicy> {
        if (!version.supportsSecuritySettings()) {
            return ApiResponse.Error(UnsupportedOperationException("Security settings not supported in version ${version.versionString}"))
        }
        
        return get("system/security/passwordPolicy")
    }
    
    /**
     * Update password policy (2.37+)
     */
    suspend fun updatePasswordPolicy(
        policy: PasswordPolicy
    ): ApiResponse<PasswordPolicyResponse> {
        if (!version.supportsSecuritySettings()) {
            return ApiResponse.Error(UnsupportedOperationException("Security settings not supported in version ${version.versionString}"))
        }
        
        return put("system/security/passwordPolicy", policy)
    }
    
    /**
     * Get login configuration (2.37+)
     */
    suspend fun getLoginConfiguration(): ApiResponse<LoginConfiguration> {
        if (!version.supportsSecuritySettings()) {
            return ApiResponse.Error(UnsupportedOperationException("Security settings not supported in version ${version.versionString}"))
        }
        
        return get("system/security/login")
    }
    
    /**
     * Update login configuration (2.37+)
     */
    suspend fun updateLoginConfiguration(
        config: LoginConfiguration
    ): ApiResponse<LoginConfigurationResponse> {
        if (!version.supportsSecuritySettings()) {
            return ApiResponse.Error(UnsupportedOperationException("Security settings not supported in version ${version.versionString}"))
        }
        
        return put("system/security/login", config)
    }
    
    // ========================================
    // BACKUP & RESTORE (2.38+)
    // ========================================
    
    /**
     * Create system backup (2.38+)
     */
    suspend fun createSystemBackup(
        backupType: BackupType = BackupType.FULL,
        includeFiles: Boolean = true
    ): ApiResponse<SystemBackupResponse> {
        if (!version.supportsSystemBackup()) {
            return ApiResponse.Error(UnsupportedOperationException("System backup not supported in version ${version.versionString}"))
        }
        
        val params = buildMap {
            put("type", backupType.name)
            put("includeFiles", includeFiles.toString())
        }
        
        return post("system/backup", emptyMap<String, Any>(), params)
    }
    
    /**
     * Get backup status (2.38+)
     */
    suspend fun getBackupStatus(backupId: String): ApiResponse<BackupStatus> {
        if (!version.supportsSystemBackup()) {
            return ApiResponse.Error(UnsupportedOperationException("System backup not supported in version ${version.versionString}"))
        }
        
        return get("system/backup/$backupId/status")
    }
    
    /**
     * List system backups (2.38+)
     */
    suspend fun listSystemBackups(): ApiResponse<SystemBackupsResponse> {
        if (!version.supportsSystemBackup()) {
            return ApiResponse.Error(UnsupportedOperationException("System backup not supported in version ${version.versionString}"))
        }
        
        return get("system/backups")
    }
    
    /**
     * Restore from backup (2.38+)
     */
    suspend fun restoreFromBackup(
        backupId: String,
        restoreOptions: RestoreOptions
    ): ApiResponse<SystemRestoreResponse> {
        if (!version.supportsSystemBackup()) {
            return ApiResponse.Error(UnsupportedOperationException("System backup not supported in version ${version.versionString}"))
        }
        
        return post("system/backup/$backupId/restore", restoreOptions)
    }
    
    /**
     * Delete backup (2.38+)
     */
    suspend fun deleteBackup(backupId: String): ApiResponse<SystemBackupResponse> {
        if (!version.supportsSystemBackup()) {
            return ApiResponse.Error(UnsupportedOperationException("System backup not supported in version ${version.versionString}"))
        }
        
        return delete("system/backup/$backupId")
    }
    
    // ========================================
    // SYSTEM UPDATES (2.39+)
    // ========================================
    
    /**
     * Check for system updates (2.39+)
     */
    suspend fun checkForUpdates(): ApiResponse<SystemUpdatesResponse> {
        if (!version.supportsSystemUpdates()) {
            return ApiResponse.Error(UnsupportedOperationException("System updates not supported in version ${version.versionString}"))
        }
        
        return get("system/updates")
    }
    
    /**
     * Install system update (2.39+)
     */
    suspend fun installUpdate(
        updateId: String,
        installOptions: UpdateInstallOptions
    ): ApiResponse<UpdateInstallResponse> {
        if (!version.supportsSystemUpdates()) {
            return ApiResponse.Error(UnsupportedOperationException("System updates not supported in version ${version.versionString}"))
        }
        
        return post("system/updates/$updateId/install", installOptions)
    }
    
    /**
     * Get update status (2.39+)
     */
    suspend fun getUpdateStatus(updateId: String): ApiResponse<UpdateStatus> {
        if (!version.supportsSystemUpdates()) {
            return ApiResponse.Error(UnsupportedOperationException("System updates not supported in version ${version.versionString}"))
        }
        
        return get("system/updates/$updateId/status")
    }
    
    // ========================================
    // CLUSTER MANAGEMENT (2.40+)
    // ========================================
    
    /**
     * Get cluster information (2.40+)
     */
    suspend fun getClusterInfo(): ApiResponse<ClusterInfo> {
        if (!version.supportsClusterManagement()) {
            return ApiResponse.Error(UnsupportedOperationException("Cluster management not supported in version ${version.versionString}"))
        }
        
        return get("system/cluster")
    }
    
    /**
     * Get cluster nodes (2.40+)
     */
    suspend fun getClusterNodes(): ApiResponse<ClusterNodesResponse> {
        if (!version.supportsClusterManagement()) {
            return ApiResponse.Error(UnsupportedOperationException("Cluster management not supported in version ${version.versionString}"))
        }
        
        return get("system/cluster/nodes")
    }
    
    /**
     * Get node health (2.40+)
     */
    suspend fun getNodeHealth(nodeId: String): ApiResponse<NodeHealth> {
        if (!version.supportsClusterManagement()) {
            return ApiResponse.Error(UnsupportedOperationException("Cluster management not supported in version ${version.versionString}"))
        }
        
        return get("system/cluster/nodes/$nodeId/health")
    }
    
    /**
     * Restart cluster node (2.40+)
     */
    suspend fun restartNode(nodeId: String): ApiResponse<NodeOperationResponse> {
        if (!version.supportsClusterManagement()) {
            return ApiResponse.Error(UnsupportedOperationException("Cluster management not supported in version ${version.versionString}"))
        }
        
        return post("system/cluster/nodes/$nodeId/restart", emptyMap<String, Any>())
    }
    
    /**
     * Scale cluster (2.40+)
     */
    suspend fun scaleCluster(
        targetNodes: Int,
        scaleOptions: ClusterScaleOptions
    ): ApiResponse<ClusterScaleResponse> {
        if (!version.supportsClusterManagement()) {
            return ApiResponse.Error(UnsupportedOperationException("Cluster management not supported in version ${version.versionString}"))
        }
        
        val params = mapOf("targetNodes" to targetNodes.toString())
        return post("system/cluster/scale", scaleOptions, params)
    }
    
    // ========================================
    // SYSTEM ANALYTICS
    // ========================================
    
    /**
     * Get system usage analytics
     */
    suspend fun getSystemUsageAnalytics(
        startDate: String? = null,
        endDate: String? = null,
        groupBy: List<SystemAnalyticsGroupBy> = emptyList()
    ): ApiResponse<SystemUsageAnalyticsResponse> {
        
        val params = buildMap {
            startDate?.let { put("startDate", it) }
            endDate?.let { put("endDate", it) }
            if (groupBy.isNotEmpty()) put("groupBy", groupBy.joinToString(",") { it.name })
        }
        
        return get("system/analytics/usage", params)
    }
    
    /**
     * Get system resource usage
     */
    suspend fun getSystemResourceUsage(): ApiResponse<SystemResourceUsageResponse> {
        return get("system/analytics/resources")
    }
    
    /**
     * Get system capacity planning
     */
    suspend fun getSystemCapacityPlanning(): ApiResponse<SystemCapacityPlanningResponse> {
        return get("system/analytics/capacity")
    }
    
    // ========================================
    // SYSTEM NOTIFICATIONS
    // ========================================
    
    /**
     * Get system notifications
     */
    suspend fun getSystemNotifications(
        severity: NotificationSeverity? = null,
        read: Boolean? = null,
        page: Int? = null,
        pageSize: Int? = null
    ): ApiResponse<SystemNotificationsResponse> {
        
        val params = buildMap {
            severity?.let { put("severity", it.name) }
            read?.let { put("read", it.toString()) }
            page?.let { put("page", it.toString()) }
            pageSize?.let { put("pageSize", it.toString()) }
        }
        
        return get("system/notifications", params)
    }
    
    /**
     * Mark notification as read
     */
    suspend fun markNotificationAsRead(notificationId: String): ApiResponse<SystemNotificationResponse> {
        return post("system/notifications/$notificationId/read", emptyMap<String, Any>())
    }
    
    /**
     * Dismiss notification
     */
    suspend fun dismissNotification(notificationId: String): ApiResponse<SystemNotificationResponse> {
        return post("system/notifications/$notificationId/dismiss", emptyMap<String, Any>())
    }
    
    /**
     * Create system notification
     */
    suspend fun createSystemNotification(
        notification: SystemNotificationCreate
    ): ApiResponse<SystemNotificationResponse> {
        return post("system/notifications", notification)
    }
}

// ========================================
// ENUMS
// ========================================

enum class CacheType { ALL, METADATA, ANALYTICS, PROGRAMS, ORGANISATION_UNITS, USER_SETTINGS }
enum class MaintenanceOperation { CLEAR_CACHE, RELOAD_APPS, PRUNE_DATA, UPDATE_CATEGORY_OPTION_COMBOS, UPDATE_ORGANISATION_UNIT_PATHS }
enum class DatabaseMaintenanceOperation { ANALYZE, VACUUM, REINDEX, UPDATE_STATISTICS }
enum class LogLevel { TRACE, DEBUG, INFO, WARN, ERROR, FATAL }
enum class AuditType { READ, CREATE, UPDATE, DELETE, SEARCH }
enum class BackupType { FULL, INCREMENTAL, METADATA_ONLY, DATA_ONLY }
enum class SystemAnalyticsGroupBy { DATE, USER, MODULE, ACTION }
enum class NotificationSeverity { INFO, WARNING, ERROR, CRITICAL }
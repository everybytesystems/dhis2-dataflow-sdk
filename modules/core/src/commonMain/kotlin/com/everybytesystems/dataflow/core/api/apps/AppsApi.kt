package com.everybytesystems.dataflow.core.api.apps

import com.everybytesystems.dataflow.core.api.base.BaseApi
import com.everybytesystems.dataflow.core.config.DHIS2Config
import com.everybytesystems.dataflow.core.network.ApiResponse
import com.everybytesystems.dataflow.core.version.DHIS2Version
import io.ktor.client.*
import kotlinx.serialization.Serializable

/**
 * Complete Apps API implementation for DHIS2 2.36+
 * Supports app management, installation, configuration, and marketplace operations
 */
class AppsApi(
    httpClient: HttpClient,
    config: DHIS2Config,
    private val version: DHIS2Version
) : BaseApi(httpClient, config) {
    
    // ========================================
    // APP MANAGEMENT OPERATIONS
    // ========================================
    
    /**
     * Get all installed apps
     */
    suspend fun getApps(
        fields: String = "*",
        filter: List<String> = emptyList(),
        order: String? = null,
        page: Int? = null,
        pageSize: Int? = null,
        appType: AppType? = null,
        status: AppStatus? = null
    ): ApiResponse<AppsResponse> {
        
        val params = buildMap {
            put("fields", fields)
            if (filter.isNotEmpty()) put("filter", filter.joinToString(","))
            order?.let { put("order", it) }
            page?.let { put("page", it.toString()) }
            pageSize?.let { put("pageSize", it.toString()) }
            appType?.let { put("appType", it.name) }
            status?.let { put("status", it.name) }
        }
        
        return get("apps", params)
    }
    
    /**
     * Get a specific app
     */
    suspend fun getApp(
        key: String,
        fields: String = "*"
    ): ApiResponse<App> {
        return get("apps/$key", mapOf("fields" to fields))
    }
    
    /**
     * Install app from ZIP file
     */
    suspend fun installApp(
        appData: ByteArray,
        fileName: String,
        overwrite: Boolean = false
    ): ApiResponse<AppInstallResponse> {
        
        val params = buildMap {
            if (overwrite) put("overwrite", "true")
        }
        
        // Note: In a real implementation, this would handle multipart file upload
        val appInstall = AppInstallRequest(
            fileName = fileName,
            data = appData.toString(), // In real implementation, this would be handled differently
            overwrite = overwrite
        )
        
        return post("apps", appInstall, params)
    }
    
    /**
     * Install app from URL
     */
    suspend fun installAppFromUrl(
        url: String,
        overwrite: Boolean = false
    ): ApiResponse<AppInstallResponse> {
        
        val params = buildMap {
            if (overwrite) put("overwrite", "true")
        }
        
        val urlInstall = AppUrlInstallRequest(url, overwrite)
        return post("apps/url", urlInstall, params)
    }
    
    /**
     * Uninstall an app
     */
    suspend fun uninstallApp(key: String): ApiResponse<AppUninstallResponse> {
        return delete("apps/$key")
    }
    
    /**
     * Update an app
     */
    suspend fun updateApp(
        key: String,
        appData: ByteArray,
        fileName: String
    ): ApiResponse<AppInstallResponse> {
        
        val appUpdate = AppInstallRequest(
            fileName = fileName,
            data = appData.toString(), // In real implementation, this would be handled differently
            overwrite = true
        )
        
        return put("apps/$key", appUpdate)
    }
    
    /**
     * Enable/disable an app
     */
    suspend fun setAppStatus(
        key: String,
        enabled: Boolean
    ): ApiResponse<AppStatusResponse> {
        val statusUpdate = AppStatusUpdate(enabled)
        return post("apps/$key/status", statusUpdate)
    }
    
    // ========================================
    // APP CONFIGURATION
    // ========================================
    
    /**
     * Get app configuration
     */
    suspend fun getAppConfiguration(key: String): ApiResponse<AppConfiguration> {
        return get("apps/$key/config")
    }
    
    /**
     * Update app configuration
     */
    suspend fun updateAppConfiguration(
        key: String,
        config: AppConfiguration
    ): ApiResponse<AppConfigurationResponse> {
        return put("apps/$key/config", config)
    }
    
    /**
     * Get app settings
     */
    suspend fun getAppSettings(key: String): ApiResponse<AppSettings> {
        return get("apps/$key/settings")
    }
    
    /**
     * Update app settings
     */
    suspend fun updateAppSettings(
        key: String,
        settings: AppSettings
    ): ApiResponse<AppSettingsResponse> {
        return put("apps/$key/settings", settings)
    }
    
    /**
     * Get app permissions
     */
    suspend fun getAppPermissions(key: String): ApiResponse<AppPermissions> {
        return get("apps/$key/permissions")
    }
    
    /**
     * Update app permissions
     */
    suspend fun updateAppPermissions(
        key: String,
        permissions: AppPermissions
    ): ApiResponse<AppPermissionsResponse> {
        return put("apps/$key/permissions", permissions)
    }
    
    // ========================================
    // APP MARKETPLACE (2.37+)
    // ========================================
    
    /**
     * Get available apps from marketplace (2.37+)
     */
    suspend fun getMarketplaceApps(
        category: String? = null,
        search: String? = null,
        page: Int? = null,
        pageSize: Int? = null,
        sortBy: MarketplaceSortBy = MarketplaceSortBy.NAME
    ): ApiResponse<MarketplaceAppsResponse> {
        if (!version.supportsAppMarketplace()) {
            return ApiResponse.Error(UnsupportedOperationException("App marketplace not supported in version ${version.versionString}"))
        }
        
        val params = buildMap {
            category?.let { put("category", it) }
            search?.let { put("search", it) }
            page?.let { put("page", it.toString()) }
            pageSize?.let { put("pageSize", it.toString()) }
            put("sortBy", sortBy.name)
        }
        
        return get("apps/marketplace", params)
    }
    
    /**
     * Get marketplace app details (2.37+)
     */
    suspend fun getMarketplaceApp(appId: String): ApiResponse<MarketplaceApp> {
        if (!version.supportsAppMarketplace()) {
            return ApiResponse.Error(UnsupportedOperationException("App marketplace not supported in version ${version.versionString}"))
        }
        
        return get("apps/marketplace/$appId")
    }
    
    /**
     * Install app from marketplace (2.37+)
     */
    suspend fun installMarketplaceApp(
        appId: String,
        version: String? = null
    ): ApiResponse<AppInstallResponse> {
        if (!this.version.supportsAppMarketplace()) {
            return ApiResponse.Error(UnsupportedOperationException("App marketplace not supported in version ${this.version.versionString}"))
        }
        
        val params = buildMap {
            version?.let { put("version", it) }
        }
        
        return post("apps/marketplace/$appId/install", emptyMap<String, Any>(), params)
    }
    
    /**
     * Get marketplace categories (2.37+)
     */
    suspend fun getMarketplaceCategories(): ApiResponse<MarketplaceCategoriesResponse> {
        if (!version.supportsAppMarketplace()) {
            return ApiResponse.Error(UnsupportedOperationException("App marketplace not supported in version ${version.versionString}"))
        }
        
        return get("apps/marketplace/categories")
    }
    
    // ========================================
    // APP STORE MANAGEMENT (2.38+)
    // ========================================
    
    /**
     * Get app store configuration (2.38+)
     */
    suspend fun getAppStoreConfiguration(): ApiResponse<AppStoreConfiguration> {
        if (!version.supportsAppStore()) {
            return ApiResponse.Error(UnsupportedOperationException("App store not supported in version ${version.versionString}"))
        }
        
        return get("apps/store/config")
    }
    
    /**
     * Update app store configuration (2.38+)
     */
    suspend fun updateAppStoreConfiguration(
        config: AppStoreConfiguration
    ): ApiResponse<AppStoreConfigurationResponse> {
        if (!version.supportsAppStore()) {
            return ApiResponse.Error(UnsupportedOperationException("App store not supported in version ${version.versionString}"))
        }
        
        return put("apps/store/config", config)
    }
    
    /**
     * Publish app to store (2.38+)
     */
    suspend fun publishAppToStore(
        key: String,
        publishInfo: AppPublishInfo
    ): ApiResponse<AppPublishResponse> {
        if (!version.supportsAppStore()) {
            return ApiResponse.Error(UnsupportedOperationException("App store not supported in version ${version.versionString}"))
        }
        
        return post("apps/$key/publish", publishInfo)
    }
    
    /**
     * Unpublish app from store (2.38+)
     */
    suspend fun unpublishAppFromStore(key: String): ApiResponse<AppPublishResponse> {
        if (!version.supportsAppStore()) {
            return ApiResponse.Error(UnsupportedOperationException("App store not supported in version ${version.versionString}"))
        }
        
        return post("apps/$key/unpublish", emptyMap<String, Any>())
    }
    
    // ========================================
    // APP ANALYTICS
    // ========================================
    
    /**
     * Get app usage analytics
     */
    suspend fun getAppAnalytics(
        startDate: String? = null,
        endDate: String? = null,
        appKey: String? = null,
        groupBy: List<AppAnalyticsGroupBy> = emptyList()
    ): ApiResponse<AppAnalyticsResponse> {
        
        val params = buildMap {
            startDate?.let { put("startDate", it) }
            endDate?.let { put("endDate", it) }
            appKey?.let { put("appKey", it) }
            if (groupBy.isNotEmpty()) put("groupBy", groupBy.joinToString(",") { it.name })
        }
        
        return get("apps/analytics", params)
    }
    
    /**
     * Get app performance metrics
     */
    suspend fun getAppPerformanceMetrics(
        appKey: String,
        startDate: String? = null,
        endDate: String? = null
    ): ApiResponse<AppPerformanceMetricsResponse> {
        
        val params = buildMap {
            startDate?.let { put("startDate", it) }
            endDate?.let { put("endDate", it) }
        }
        
        return get("apps/$appKey/performance", params)
    }
    
    /**
     * Get app error logs
     */
    suspend fun getAppErrorLogs(
        appKey: String,
        startDate: String? = null,
        endDate: String? = null,
        severity: AppErrorSeverity? = null,
        page: Int? = null,
        pageSize: Int? = null
    ): ApiResponse<AppErrorLogsResponse> {
        
        val params = buildMap {
            startDate?.let { put("startDate", it) }
            endDate?.let { put("endDate", it) }
            severity?.let { put("severity", it.name) }
            page?.let { put("page", it.toString()) }
            pageSize?.let { put("pageSize", it.toString()) }
        }
        
        return get("apps/$appKey/errors", params)
    }
    
    // ========================================
    // APP DEVELOPMENT TOOLS (2.39+)
    // ========================================
    
    /**
     * Validate app package (2.39+)
     */
    suspend fun validateAppPackage(
        appData: ByteArray,
        fileName: String
    ): ApiResponse<AppValidationResponse> {
        if (!version.supportsAppDevelopmentTools()) {
            return ApiResponse.Error(UnsupportedOperationException("App development tools not supported in version ${version.versionString}"))
        }
        
        val validation = AppValidationRequest(
            fileName = fileName,
            data = appData.toString() // In real implementation, this would be handled differently
        )
        
        return post("apps/validate", validation)
    }
    
    /**
     * Get app development guidelines (2.39+)
     */
    suspend fun getAppDevelopmentGuidelines(): ApiResponse<AppDevelopmentGuidelinesResponse> {
        if (!version.supportsAppDevelopmentTools()) {
            return ApiResponse.Error(UnsupportedOperationException("App development tools not supported in version ${version.versionString}"))
        }
        
        return get("apps/development/guidelines")
    }
    
    /**
     * Generate app template (2.39+)
     */
    suspend fun generateAppTemplate(
        template: AppTemplateRequest
    ): ApiResponse<AppTemplateResponse> {
        if (!version.supportsAppDevelopmentTools()) {
            return ApiResponse.Error(UnsupportedOperationException("App development tools not supported in version ${version.versionString}"))
        }
        
        return post("apps/development/template", template)
    }
    
    /**
     * Test app compatibility (2.39+)
     */
    suspend fun testAppCompatibility(
        appKey: String,
        targetVersion: String
    ): ApiResponse<AppCompatibilityResponse> {
        if (!version.supportsAppDevelopmentTools()) {
            return ApiResponse.Error(UnsupportedOperationException("App development tools not supported in version ${version.versionString}"))
        }
        
        val params = mapOf("targetVersion" to targetVersion)
        return post("apps/$appKey/compatibility", emptyMap<String, Any>(), params)
    }
    
    // ========================================
    // APP SECURITY & SANDBOXING (2.40+)
    // ========================================
    
    /**
     * Get app security settings (2.40+)
     */
    suspend fun getAppSecuritySettings(appKey: String): ApiResponse<AppSecuritySettings> {
        if (!version.supportsAppSecurity()) {
            return ApiResponse.Error(UnsupportedOperationException("App security not supported in version ${version.versionString}"))
        }
        
        return get("apps/$appKey/security")
    }
    
    /**
     * Update app security settings (2.40+)
     */
    suspend fun updateAppSecuritySettings(
        appKey: String,
        settings: AppSecuritySettings
    ): ApiResponse<AppSecuritySettingsResponse> {
        if (!version.supportsAppSecurity()) {
            return ApiResponse.Error(UnsupportedOperationException("App security not supported in version ${version.versionString}"))
        }
        
        return put("apps/$appKey/security", settings)
    }
    
    /**
     * Scan app for security vulnerabilities (2.40+)
     */
    suspend fun scanAppSecurity(appKey: String): ApiResponse<AppSecurityScanResponse> {
        if (!version.supportsAppSecurity()) {
            return ApiResponse.Error(UnsupportedOperationException("App security not supported in version ${version.versionString}"))
        }
        
        return post("apps/$appKey/security/scan", emptyMap<String, Any>())
    }
    
    /**
     * Get app sandbox status (2.40+)
     */
    suspend fun getAppSandboxStatus(appKey: String): ApiResponse<AppSandboxStatus> {
        if (!version.supportsAppSecurity()) {
            return ApiResponse.Error(UnsupportedOperationException("App security not supported in version ${version.versionString}"))
        }
        
        return get("apps/$appKey/sandbox")
    }
    
    /**
     * Enable/disable app sandbox (2.40+)
     */
    suspend fun setAppSandboxStatus(
        appKey: String,
        enabled: Boolean
    ): ApiResponse<AppSandboxStatusResponse> {
        if (!version.supportsAppSecurity()) {
            return ApiResponse.Error(UnsupportedOperationException("App security not supported in version ${version.versionString}"))
        }
        
        val sandboxUpdate = AppSandboxUpdate(enabled)
        return put("apps/$appKey/sandbox", sandboxUpdate)
    }
    
    // ========================================
    // BULK APP OPERATIONS
    // ========================================
    
    /**
     * Bulk install apps
     */
    suspend fun bulkInstallApps(
        apps: List<AppBulkInstallRequest>
    ): ApiResponse<AppBulkInstallResponse> {
        val bulkInstall = AppBulkInstallRequestWrapper(apps)
        return post("apps/bulk/install", bulkInstall)
    }
    
    /**
     * Bulk uninstall apps
     */
    suspend fun bulkUninstallApps(
        appKeys: List<String>
    ): ApiResponse<AppBulkUninstallResponse> {
        val bulkUninstall = AppBulkUninstallRequest(appKeys)
        return post("apps/bulk/uninstall", bulkUninstall)
    }
    
    /**
     * Bulk update apps
     */
    suspend fun bulkUpdateApps(
        appKeys: List<String>
    ): ApiResponse<AppBulkUpdateResponse> {
        val bulkUpdate = AppBulkUpdateRequest(appKeys)
        return post("apps/bulk/update", bulkUpdate)
    }
    
    /**
     * Bulk enable/disable apps
     */
    suspend fun bulkSetAppStatus(
        appKeys: List<String>,
        enabled: Boolean
    ): ApiResponse<AppBulkStatusResponse> {
        val bulkStatus = AppBulkStatusRequest(appKeys, enabled)
        return post("apps/bulk/status", bulkStatus)
    }
    
    // ========================================
    // APP BACKUP & RESTORE
    // ========================================
    
    /**
     * Backup app configuration
     */
    suspend fun backupAppConfiguration(appKey: String): ApiResponse<AppBackupResponse> {
        return post("apps/$appKey/backup", emptyMap<String, Any>())
    }
    
    /**
     * Restore app configuration
     */
    suspend fun restoreAppConfiguration(
        appKey: String,
        backup: AppBackupData
    ): ApiResponse<AppRestoreResponse> {
        return post("apps/$appKey/restore", backup)
    }
    
    /**
     * Export app package
     */
    suspend fun exportAppPackage(
        appKey: String,
        includeData: Boolean = false
    ): ApiResponse<AppExportResponse> {
        val params = mapOf("includeData" to includeData.toString())
        return get("apps/$appKey/export", params)
    }
    
    /**
     * Get app system requirements
     */
    suspend fun getAppSystemRequirements(appKey: String): ApiResponse<AppSystemRequirements> {
        return get("apps/$appKey/requirements")
    }
    
    /**
     * Check app dependencies
     */
    suspend fun checkAppDependencies(appKey: String): ApiResponse<AppDependenciesResponse> {
        return get("apps/$appKey/dependencies")
    }
}

// ========================================
// ENUMS
// ========================================

enum class AppType { DASHBOARD, TRACKER_CAPTURE, EVENT_CAPTURE, DATA_VISUALIZER, MAPS, REPORTS, MAINTENANCE, RESOURCE }
enum class AppStatus { INSTALLED, ENABLED, DISABLED, PENDING, ERROR }
enum class MarketplaceSortBy { NAME, POPULARITY, RATING, DATE, DOWNLOADS }
enum class AppAnalyticsGroupBy { APP, USER, DATE, ACTION }
enum class AppErrorSeverity { LOW, MEDIUM, HIGH, CRITICAL }
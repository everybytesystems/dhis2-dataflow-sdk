package com.everybytesystems.dataflow.core.api.apps

import kotlinx.serialization.Serializable

// ========================================
// APP MODELS
// ========================================

@Serializable
data class App(
    val id: String? = null,
    val uid: String? = null,
    val key: String,
    val name: String,
    val displayName: String? = null,
    val description: String? = null,
    val version: String? = null,
    val appType: String? = null,
    val status: String? = null,
    val enabled: Boolean = true,
    val installed: String? = null,
    val lastUpdated: String? = null,
    val developer: AppDeveloper? = null,
    val manifest: AppManifest? = null,
    val configuration: AppConfiguration? = null,
    val settings: AppSettings? = null,
    val permissions: AppPermissions? = null,
    val icons: AppIcons? = null,
    val screenshots: List<String> = emptyList(),
    val launchUrl: String? = null,
    val baseUrl: String? = null,
    val bundled: Boolean = false,
    val namespace: String? = null,
    val folderName: String? = null,
    val zipFileName: String? = null,
    val appStorageUsed: Long = 0,
    val shortName: String? = null,
    val activities: AppActivities? = null
)

@Serializable
data class AppsResponse(
    val apps: List<App> = emptyList(),
    val pager: Pager? = null
)

@Serializable
data class AppDeveloper(
    val name: String? = null,
    val email: String? = null,
    val url: String? = null,
    val organization: String? = null,
    val address: String? = null
)

@Serializable
data class AppManifest(
    val version: String,
    val name: String,
    val description: String? = null,
    val appType: String? = null,
    val entryPoints: AppEntryPoints? = null,
    val icons: AppIcons? = null,
    val developer: AppDeveloper? = null,
    val activities: AppActivities? = null,
    val authorities: List<String> = emptyList(),
    val dhis2: AppDhis2Info? = null
)

@Serializable
data class AppEntryPoints(
    val webapp: String? = null,
    val plugin: String? = null
)

@Serializable
data class AppIcons(
    val icon16: String? = null,
    val icon48: String? = null,
    val icon128: String? = null
)

@Serializable
data class AppActivities(
    val dhis: AppDhisActivities? = null,
    val href: String? = null
)

@Serializable
data class AppDhisActivities(
    val href: String? = null
)

@Serializable
data class AppDhis2Info(
    val apiVersion: String? = null,
    val minVersion: String? = null,
    val maxVersion: String? = null
)

// ========================================
// APP CONFIGURATION MODELS
// ========================================

@Serializable
data class AppConfiguration(
    val id: String? = null,
    val appKey: String? = null,
    val configuration: Map<String, String> = emptyMap(),
    val created: String? = null,
    val createdBy: UserReference? = null,
    val lastUpdated: String? = null,
    val lastUpdatedBy: UserReference? = null
)

@Serializable
data class AppConfigurationResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val configuration: AppConfiguration? = null
)

@Serializable
data class AppSettings(
    val id: String? = null,
    val appKey: String? = null,
    val settings: Map<String, String> = emptyMap(),
    val userSettings: Map<String, String> = emptyMap(),
    val systemSettings: Map<String, String> = emptyMap(),
    val created: String? = null,
    val createdBy: UserReference? = null,
    val lastUpdated: String? = null,
    val lastUpdatedBy: UserReference? = null
)

@Serializable
data class AppSettingsResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val settings: AppSettings? = null
)

@Serializable
data class AppPermissions(
    val id: String? = null,
    val appKey: String? = null,
    val authorities: List<String> = emptyList(),
    val userRoles: List<String> = emptyList(),
    val userGroups: List<String> = emptyList(),
    val organisationUnits: List<String> = emptyList(),
    val dataAccess: AppDataAccess? = null,
    val apiAccess: AppApiAccess? = null,
    val created: String? = null,
    val createdBy: UserReference? = null,
    val lastUpdated: String? = null,
    val lastUpdatedBy: UserReference? = null
)

@Serializable
data class AppPermissionsResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val permissions: AppPermissions? = null
)

@Serializable
data class AppDataAccess(
    val canRead: Boolean = false,
    val canWrite: Boolean = false,
    val canDelete: Boolean = false,
    val dataElements: List<String> = emptyList(),
    val dataSets: List<String> = emptyList(),
    val programs: List<String> = emptyList(),
    val organisationUnits: List<String> = emptyList()
)

@Serializable
data class AppApiAccess(
    val allowedEndpoints: List<String> = emptyList(),
    val blockedEndpoints: List<String> = emptyList(),
    val rateLimits: AppRateLimits? = null
)

@Serializable
data class AppRateLimits(
    val requestsPerMinute: Int = 60,
    val requestsPerHour: Int = 3600,
    val requestsPerDay: Int = 86400
)

@Serializable
data class UserReference(
    val id: String,
    val uid: String? = null,
    val username: String? = null,
    val firstName: String? = null,
    val surname: String? = null,
    val displayName: String? = null
)

// ========================================
// APP INSTALLATION MODELS
// ========================================

@Serializable
data class AppInstallRequest(
    val fileName: String,
    val data: String, // In real implementation, this would be handled differently
    val overwrite: Boolean = false
)

@Serializable
data class AppUrlInstallRequest(
    val url: String,
    val overwrite: Boolean = false
)

@Serializable
data class AppInstallResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val response: AppInstallResponseDetails? = null
)

@Serializable
data class AppInstallResponseDetails(
    val responseType: String? = null,
    val appKey: String? = null,
    val appName: String? = null,
    val version: String? = null,
    val installed: Boolean = false,
    val updated: Boolean = false,
    val errors: List<String> = emptyList(),
    val warnings: List<String> = emptyList()
)

@Serializable
data class AppUninstallResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val appKey: String? = null,
    val uninstalled: Boolean = false
)

@Serializable
data class AppStatusUpdate(
    val enabled: Boolean
)

@Serializable
data class AppStatusResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val appKey: String? = null,
    val enabled: Boolean = false
)

// ========================================
// MARKETPLACE MODELS
// ========================================

@Serializable
data class MarketplaceAppsResponse(
    val apps: List<MarketplaceApp> = emptyList(),
    val pager: Pager? = null,
    val categories: List<String> = emptyList()
)

@Serializable
data class MarketplaceApp(
    val id: String,
    val name: String,
    val displayName: String? = null,
    val description: String? = null,
    val shortDescription: String? = null,
    val version: String? = null,
    val latestVersion: String? = null,
    val category: String? = null,
    val developer: AppDeveloper? = null,
    val rating: Double = 0.0,
    val ratingCount: Int = 0,
    val downloads: Int = 0,
    val popularity: Int = 0,
    val featured: Boolean = false,
    val verified: Boolean = false,
    val price: AppPrice? = null,
    val license: String? = null,
    val sourceUrl: String? = null,
    val demoUrl: String? = null,
    val supportUrl: String? = null,
    val documentationUrl: String? = null,
    val icons: AppIcons? = null,
    val screenshots: List<String> = emptyList(),
    val videos: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val requirements: AppSystemRequirements? = null,
    val changelog: List<AppChangelogEntry> = emptyList(),
    val reviews: List<AppReview> = emptyList(),
    val published: String? = null,
    val lastUpdated: String? = null,
    val downloadUrl: String? = null,
    val installUrl: String? = null
)

@Serializable
data class AppPrice(
    val amount: Double = 0.0,
    val currency: String = "USD",
    val type: String = "FREE" // FREE, PAID, FREEMIUM, SUBSCRIPTION
)

@Serializable
data class AppChangelogEntry(
    val version: String,
    val date: String? = null,
    val changes: List<String> = emptyList()
)

@Serializable
data class AppReview(
    val id: String,
    val user: String,
    val rating: Int,
    val comment: String? = null,
    val date: String? = null,
    val helpful: Int = 0
)

@Serializable
data class MarketplaceCategoriesResponse(
    val categories: List<MarketplaceCategory> = emptyList()
)

@Serializable
data class MarketplaceCategory(
    val id: String,
    val name: String,
    val displayName: String? = null,
    val description: String? = null,
    val icon: String? = null,
    val appCount: Int = 0
)

// ========================================
// APP STORE MODELS
// ========================================

@Serializable
data class AppStoreConfiguration(
    val enabled: Boolean = false,
    val url: String? = null,
    val apiKey: String? = null,
    val allowPublicApps: Boolean = true,
    val allowPrivateApps: Boolean = false,
    val requireApproval: Boolean = true,
    val autoUpdate: Boolean = false,
    val categories: List<String> = emptyList(),
    val moderators: List<String> = emptyList()
)

@Serializable
data class AppStoreConfigurationResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val configuration: AppStoreConfiguration? = null
)

@Serializable
data class AppPublishInfo(
    val category: String,
    val description: String,
    val shortDescription: String? = null,
    val tags: List<String> = emptyList(),
    val screenshots: List<String> = emptyList(),
    val videos: List<String> = emptyList(),
    val sourceUrl: String? = null,
    val demoUrl: String? = null,
    val supportUrl: String? = null,
    val documentationUrl: String? = null,
    val license: String? = null,
    val price: AppPrice? = null,
    val public: Boolean = true
)

@Serializable
data class AppPublishResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val published: Boolean = false,
    val approvalRequired: Boolean = false,
    val publishUrl: String? = null
)

// ========================================
// APP ANALYTICS MODELS
// ========================================

@Serializable
data class AppAnalyticsResponse(
    val analytics: List<AppAnalytic> = emptyList(),
    val summary: AppAnalyticsSummary? = null
)

@Serializable
data class AppAnalytic(
    val appKey: String? = null,
    val appName: String? = null,
    val user: String? = null,
    val date: String? = null,
    val action: String? = null,
    val count: Int = 0,
    val duration: Long = 0,
    val errors: Int = 0
)

@Serializable
data class AppAnalyticsSummary(
    val totalUsage: Int = 0,
    val totalUsers: Int = 0,
    val totalErrors: Int = 0,
    val averageDuration: Long = 0,
    val byApp: Map<String, AppUsageStats> = emptyMap(),
    val byUser: Map<String, AppUserStats> = emptyMap()
)

@Serializable
data class AppUsageStats(
    val usage: Int = 0,
    val users: Int = 0,
    val errors: Int = 0,
    val averageDuration: Long = 0
)

@Serializable
data class AppUserStats(
    val usage: Int = 0,
    val appsUsed: Int = 0,
    val errors: Int = 0,
    val totalDuration: Long = 0
)

@Serializable
data class AppPerformanceMetricsResponse(
    val metrics: AppPerformanceMetrics
)

@Serializable
data class AppPerformanceMetrics(
    val appKey: String,
    val averageLoadTime: Double = 0.0,
    val averageResponseTime: Double = 0.0,
    val errorRate: Double = 0.0,
    val uptime: Double = 0.0,
    val memoryUsage: AppMemoryUsage? = null,
    val cpuUsage: AppCpuUsage? = null,
    val networkUsage: AppNetworkUsage? = null
)

@Serializable
data class AppMemoryUsage(
    val average: Long = 0,
    val peak: Long = 0,
    val current: Long = 0
)

@Serializable
data class AppCpuUsage(
    val average: Double = 0.0,
    val peak: Double = 0.0,
    val current: Double = 0.0
)

@Serializable
data class AppNetworkUsage(
    val bytesIn: Long = 0,
    val bytesOut: Long = 0,
    val requestCount: Int = 0
)

@Serializable
data class AppErrorLogsResponse(
    val errors: List<AppErrorLog> = emptyList(),
    val pager: Pager? = null
)

@Serializable
data class AppErrorLog(
    val id: String,
    val appKey: String,
    val timestamp: String,
    val severity: String,
    val message: String,
    val stackTrace: String? = null,
    val user: String? = null,
    val userAgent: String? = null,
    val url: String? = null,
    val resolved: Boolean = false
)

// ========================================
// APP DEVELOPMENT MODELS
// ========================================

@Serializable
data class AppValidationRequest(
    val fileName: String,
    val data: String // In real implementation, this would be handled differently
)

@Serializable
data class AppValidationResponse(
    val valid: Boolean,
    val errors: List<AppValidationError> = emptyList(),
    val warnings: List<AppValidationWarning> = emptyList(),
    val manifest: AppManifest? = null,
    val size: Long = 0,
    val checksum: String? = null
)

@Serializable
data class AppValidationError(
    val code: String,
    val message: String,
    val file: String? = null,
    val line: Int? = null
)

@Serializable
data class AppValidationWarning(
    val code: String,
    val message: String,
    val file: String? = null,
    val line: Int? = null
)

@Serializable
data class AppDevelopmentGuidelinesResponse(
    val guidelines: AppDevelopmentGuidelines
)

@Serializable
data class AppDevelopmentGuidelines(
    val version: String,
    val apiGuidelines: List<String> = emptyList(),
    val uiGuidelines: List<String> = emptyList(),
    val securityGuidelines: List<String> = emptyList(),
    val performanceGuidelines: List<String> = emptyList(),
    val bestPractices: List<String> = emptyList(),
    val examples: List<AppExample> = emptyList()
)

@Serializable
data class AppExample(
    val name: String,
    val description: String,
    val url: String,
    val category: String
)

@Serializable
data class AppTemplateRequest(
    val name: String,
    val type: String, // DASHBOARD, TRACKER, EVENT, etc.
    val features: List<String> = emptyList(),
    val framework: String = "REACT", // REACT, ANGULAR, VUE, VANILLA
    val includeExamples: Boolean = true
)

@Serializable
data class AppTemplateResponse(
    val templateUrl: String,
    val downloadUrl: String,
    val instructions: String,
    val dependencies: List<String> = emptyList()
)

@Serializable
data class AppCompatibilityResponse(
    val compatible: Boolean,
    val targetVersion: String,
    val currentVersion: String,
    val issues: List<AppCompatibilityIssue> = emptyList(),
    val recommendations: List<String> = emptyList()
)

@Serializable
data class AppCompatibilityIssue(
    val type: String, // API_CHANGE, DEPRECATION, BREAKING_CHANGE
    val severity: String, // LOW, MEDIUM, HIGH, CRITICAL
    val description: String,
    val solution: String? = null
)

// ========================================
// APP SECURITY MODELS
// ========================================

@Serializable
data class AppSecuritySettings(
    val sandboxEnabled: Boolean = true,
    val allowedDomains: List<String> = emptyList(),
    val blockedDomains: List<String> = emptyList(),
    val allowedApis: List<String> = emptyList(),
    val blockedApis: List<String> = emptyList(),
    val contentSecurityPolicy: String? = null,
    val crossOriginPolicy: String = "SAME_ORIGIN",
    val dataEncryption: Boolean = false,
    val auditLogging: Boolean = true
)

@Serializable
data class AppSecuritySettingsResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val settings: AppSecuritySettings? = null
)

@Serializable
data class AppSecurityScanResponse(
    val scanId: String,
    val status: String,
    val vulnerabilities: List<AppVulnerability> = emptyList(),
    val score: Int = 0,
    val recommendations: List<String> = emptyList(),
    val scanDate: String? = null
)

@Serializable
data class AppVulnerability(
    val id: String,
    val type: String,
    val severity: String,
    val description: String,
    val file: String? = null,
    val line: Int? = null,
    val solution: String? = null
)

@Serializable
data class AppSandboxStatus(
    val enabled: Boolean,
    val restrictions: List<String> = emptyList(),
    val allowedResources: List<String> = emptyList(),
    val blockedResources: List<String> = emptyList()
)

@Serializable
data class AppSandboxUpdate(
    val enabled: Boolean
)

@Serializable
data class AppSandboxStatusResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val sandbox: AppSandboxStatus? = null
)

// ========================================
// BULK OPERATIONS MODELS
// ========================================

@Serializable
data class AppBulkInstallRequest(
    val url: String? = null,
    val fileName: String? = null,
    val data: String? = null // In real implementation, this would be handled differently
)

@Serializable
data class AppBulkInstallRequestWrapper(
    val apps: List<AppBulkInstallRequest>
)

@Serializable
data class AppBulkInstallResponse(
    val responses: List<AppInstallResponse> = emptyList(),
    val summary: AppBulkSummary? = null
)

@Serializable
data class AppBulkUninstallRequest(
    val appKeys: List<String>
)

@Serializable
data class AppBulkUninstallResponse(
    val responses: List<AppUninstallResponse> = emptyList(),
    val summary: AppBulkSummary? = null
)

@Serializable
data class AppBulkUpdateRequest(
    val appKeys: List<String>
)

@Serializable
data class AppBulkUpdateResponse(
    val responses: List<AppInstallResponse> = emptyList(),
    val summary: AppBulkSummary? = null
)

@Serializable
data class AppBulkStatusRequest(
    val appKeys: List<String>,
    val enabled: Boolean
)

@Serializable
data class AppBulkStatusResponse(
    val responses: List<AppStatusResponse> = emptyList(),
    val summary: AppBulkSummary? = null
)

@Serializable
data class AppBulkSummary(
    val total: Int = 0,
    val successful: Int = 0,
    val failed: Int = 0,
    val errors: List<String> = emptyList()
)

// ========================================
// BACKUP & RESTORE MODELS
// ========================================

@Serializable
data class AppBackupResponse(
    val backupId: String,
    val appKey: String,
    val backupDate: String,
    val backupSize: Long = 0,
    val downloadUrl: String? = null
)

@Serializable
data class AppBackupData(
    val backupId: String,
    val appKey: String,
    val configuration: AppConfiguration? = null,
    val settings: AppSettings? = null,
    val permissions: AppPermissions? = null,
    val data: Map<String, String> = emptyMap()
)

@Serializable
data class AppRestoreResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val restored: Boolean = false
)

@Serializable
data class AppExportResponse(
    val exportUrl: String,
    val exportDate: String,
    val exportSize: Long = 0,
    val includesData: Boolean = false
)

@Serializable
data class AppSystemRequirements(
    val minDhis2Version: String? = null,
    val maxDhis2Version: String? = null,
    val requiredAuthorities: List<String> = emptyList(),
    val requiredApis: List<String> = emptyList(),
    val minMemory: Long = 0,
    val minStorage: Long = 0,
    val supportedBrowsers: List<String> = emptyList(),
    val dependencies: List<AppDependency> = emptyList()
)

@Serializable
data class AppDependency(
    val name: String,
    val version: String? = null,
    val required: Boolean = true,
    val type: String = "APP" // APP, LIBRARY, SERVICE
)

@Serializable
data class AppDependenciesResponse(
    val dependencies: List<AppDependencyStatus> = emptyList(),
    val allSatisfied: Boolean = true
)

@Serializable
data class AppDependencyStatus(
    val dependency: AppDependency,
    val satisfied: Boolean,
    val installedVersion: String? = null,
    val message: String? = null
)

// ========================================
// COMMON MODELS
// ========================================

@Serializable
data class Pager(
    val page: Int = 1,
    val pageCount: Int = 1,
    val total: Int = 0,
    val pageSize: Int = 50,
    val nextPage: String? = null,
    val prevPage: String? = null
)
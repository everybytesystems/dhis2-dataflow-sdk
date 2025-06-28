package com.everybytesystems.dataflow.core.api.systemsettings

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

// ========================================
// SYSTEM SETTINGS MODELS
// ========================================

@Serializable
data class SystemSettingsResponse(
    val settings: Map<String, JsonElement> = emptyMap()
)

@Serializable
data class SystemSettingResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val key: String? = null,
    val value: JsonElement? = null
)

@Serializable
data class SystemSettingsBulkResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val updated: Int = 0,
    val failed: Int = 0,
    val errors: List<SystemSettingError> = emptyList()
)

@Serializable
data class SystemSettingError(
    val key: String,
    val message: String,
    val errorCode: String? = null
)

@Serializable
data class SystemSettingMetadata(
    val key: String,
    val displayName: String? = null,
    val description: String? = null,
    val dataType: String,
    val defaultValue: JsonElement? = null,
    val allowedValues: List<JsonElement> = emptyList(),
    val category: String? = null,
    val required: Boolean = false,
    val readOnly: Boolean = false,
    val sensitive: Boolean = false,
    val validation: SystemSettingValidation? = null
)

@Serializable
data class SystemSettingValidation(
    val pattern: String? = null,
    val minValue: Double? = null,
    val maxValue: Double? = null,
    val minLength: Int? = null,
    val maxLength: Int? = null
)

// ========================================
// SYSTEM CONFIGURATION MODELS
// ========================================

@Serializable
data class SystemConfiguration(
    val systemTitle: String? = null,
    val systemSubtitle: String? = null,
    val applicationTitle: String? = null,
    val applicationIntro: String? = null,
    val applicationNotification: String? = null,
    val applicationFooter: String? = null,
    val applicationRightFooter: String? = null,
    val flag: String? = null,
    val flagImage: String? = null,
    val startModule: String? = null,
    val helpPageLink: String? = null,
    val instanceBaseUrl: String? = null,
    val corsWhitelist: List<String> = emptyList(),
    val systemMonitoringUrl: String? = null,
    val systemNotificationEmail: String? = null,
    val accountRecoveryLockTimeoutMinutes: Int = 15,
    val maxSessionsPerUser: Int = 10,
    val credentialsExpires: Boolean = false,
    val credentialsExpiryAlert: Boolean = false,
    val accountExpiry: Boolean = false,
    val selfRegistrationNoRecaptcha: Boolean = false,
    val recaptchaSecret: String? = null,
    val recaptchaSite: String? = null,
    val canGrantOwnUserAuthorityGroups: Boolean = false,
    val sqlViewMaxLimit: Int = 0,
    val respectMetaDataStartEndDatesInAnalyticsTableExport: Boolean = false,
    val skipDataTypeValidationInAnalyticsTableExport: Boolean = false,
    val customLoginPageLogo: Boolean = false,
    val customTopMenuLogo: Boolean = false,
    val analyticsMaintenanceMode: Boolean = false,
    val databaseServerCpus: Int = 0,
    val lastSuccessfulAnalyticsTablesRuntime: String? = null,
    val lastAnalyticsTableSuccess: String? = null,
    val lastSuccessfulLatestAnalyticsPartitionRuntime: String? = null,
    val lastLatestAnalyticsPartitionSuccess: String? = null,
    val lastSuccessfulResourceTablesRuntime: String? = null,
    val lastResourceTableSuccess: String? = null,
    val lastSuccessfulSystemMonitoringPush: String? = null,
    val lastSuccessfulDataSynch: String? = null,
    val lastDataSynchSuccess: String? = null,
    val lastSuccessfulCompleteDataSetRegistrationSyncJob: String? = null,
    val completedDataSetRegistrationSyncJobLastSuccess: String? = null,
    val lastSuccessfulDataStatistics: String? = null,
    val lastDataStatisticsSuccess: String? = null,
    val lastSuccessfulValidationResultsNotification: String? = null,
    val lastValidationResultsNotificationSuccess: String? = null,
    val lastSuccessfulDataSetNotification: String? = null,
    val lastDataSetNotificationSuccess: String? = null,
    val remoteInstanceUrl: String? = null,
    val remoteInstanceUsername: String? = null,
    val remoteInstancePassword: String? = null,
    val keyGoogleMapsApiKey: String? = null,
    val keyBingMapsApiKey: String? = null,
    val keyMapzenSearchApiKey: String? = null,
    val keyGoogleAnalyticsUA: String? = null
)

@Serializable
data class SystemConfigurationResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val configuration: SystemConfiguration? = null
)

// ========================================
// APPEARANCE SETTINGS MODELS
// ========================================

@Serializable
data class AppearanceSettings(
    val applicationTitle: String? = null,
    val applicationIntro: String? = null,
    val applicationNotification: String? = null,
    val applicationFooter: String? = null,
    val applicationRightFooter: String? = null,
    val currentStyle: String? = null,
    val customLoginPageLogo: Boolean = false,
    val useCustomTopMenuLogo: Boolean = false,
    val loginPageLayout: String? = null,
    val loginPageTemplate: String? = null,
    val customCss: String? = null,
    val customJs: String? = null,
    val favicon: String? = null,
    val flagImage: String? = null,
    val interfaceLanguage: String? = null,
    val databaseLanguage: String? = null,
    val analysisDisplayProperty: String? = null,
    val analysisDigitGroupSeparator: String? = null,
    val requireAddToView: Boolean = false,
    val useCustomTopMenu: Boolean = false,
    val useCustomLeftMenu: Boolean = false,
    val customTopMenuLogo: String? = null,
    val customLeftMenuLogo: String? = null,
    val customTopMenuTitle: String? = null,
    val customLeftMenuTitle: String? = null
)

@Serializable
data class AppearanceSettingsResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val settings: AppearanceSettings? = null
)

// ========================================
// EMAIL SETTINGS MODELS
// ========================================

@Serializable
data class EmailSettings(
    val hostName: String? = null,
    val port: Int = 587,
    val username: String? = null,
    val password: String? = null,
    val tls: Boolean = true,
    val sender: String? = null,
    val emailNotification: Boolean = false
)

@Serializable
data class EmailSettingsResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val settings: EmailSettings? = null
)

@Serializable
data class EmailTestResponse(
    val success: Boolean,
    val message: String? = null,
    val deliveryTime: Long? = null
)

// ========================================
// MAINTENANCE MODELS
// ========================================

@Serializable
data class MaintenanceRequest(
    val operations: List<MaintenanceOperation>
)

@Serializable
data class MaintenanceResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val results: List<MaintenanceResult> = emptyList()
)

@Serializable
data class MaintenanceResult(
    val operation: String,
    val success: Boolean,
    val message: String? = null,
    val duration: Long = 0
)

@Serializable
data class CacheResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val clearedCaches: List<String> = emptyList()
)

@Serializable
data class SystemResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null
)

@Serializable
data class DatabaseMaintenanceRequest(
    val operations: List<DatabaseMaintenanceOperation>
)

@Serializable
data class DatabaseMaintenanceResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val results: List<DatabaseMaintenanceResult> = emptyList()
)

@Serializable
data class DatabaseMaintenanceResult(
    val operation: String,
    val success: Boolean,
    val message: String? = null,
    val duration: Long = 0,
    val rowsAffected: Long = 0
)

@Serializable
data class DatabaseOptimizationResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val optimizations: List<DatabaseOptimization> = emptyList()
)

@Serializable
data class DatabaseOptimization(
    val table: String,
    val operation: String,
    val success: Boolean,
    val message: String? = null,
    val duration: Long = 0
)

@Serializable
data class DatabaseAnalysisResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val analysis: DatabaseAnalysis? = null
)

@Serializable
data class DatabaseAnalysis(
    val totalTables: Int = 0,
    val totalSize: Long = 0,
    val indexSize: Long = 0,
    val dataSize: Long = 0,
    val fragmentationLevel: Double = 0.0,
    val recommendations: List<String> = emptyList(),
    val tableStats: List<DatabaseTableStats> = emptyList()
)

@Serializable
data class DatabaseTableStats(
    val tableName: String,
    val rowCount: Long = 0,
    val dataSize: Long = 0,
    val indexSize: Long = 0,
    val fragmentationLevel: Double = 0.0
)

@Serializable
data class DatabaseVacuumResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val reclaimedSpace: Long = 0,
    val duration: Long = 0
)

// ========================================
// MONITORING & HEALTH MODELS
// ========================================

@Serializable
data class SystemHealthResponse(
    val status: String,
    val checks: List<HealthCheck> = emptyList(),
    val overall: HealthStatus? = null
)

@Serializable
data class HealthCheck(
    val name: String,
    val status: String,
    val message: String? = null,
    val duration: Long = 0,
    val details: Map<String, String> = emptyMap()
)

@Serializable
data class HealthStatus(
    val status: String,
    val uptime: Long = 0,
    val timestamp: String? = null
)

@Serializable
data class SystemMetricsResponse(
    val metrics: SystemMetrics
)

@Serializable
data class SystemMetrics(
    val cpu: CpuMetrics? = null,
    val memory: MemoryMetrics? = null,
    val disk: DiskMetrics? = null,
    val network: NetworkMetrics? = null,
    val database: DatabaseMetrics? = null,
    val jvm: JvmMetrics? = null,
    val application: ApplicationMetrics? = null
)

@Serializable
data class CpuMetrics(
    val usage: Double = 0.0,
    val cores: Int = 0,
    val loadAverage: Double = 0.0
)

@Serializable
data class MemoryMetrics(
    val used: Long = 0,
    val free: Long = 0,
    val total: Long = 0,
    val usagePercentage: Double = 0.0
)

@Serializable
data class DiskMetrics(
    val used: Long = 0,
    val free: Long = 0,
    val total: Long = 0,
    val usagePercentage: Double = 0.0,
    val ioOperations: Long = 0
)

@Serializable
data class NetworkMetrics(
    val bytesIn: Long = 0,
    val bytesOut: Long = 0,
    val packetsIn: Long = 0,
    val packetsOut: Long = 0,
    val errors: Long = 0
)

@Serializable
data class DatabaseMetrics(
    val connections: Int = 0,
    val maxConnections: Int = 0,
    val activeConnections: Int = 0,
    val idleConnections: Int = 0,
    val queryTime: Double = 0.0,
    val slowQueries: Int = 0
)

@Serializable
data class JvmMetrics(
    val heapUsed: Long = 0,
    val heapMax: Long = 0,
    val nonHeapUsed: Long = 0,
    val nonHeapMax: Long = 0,
    val gcCollections: Long = 0,
    val gcTime: Long = 0,
    val threads: Int = 0,
    val classes: Int = 0
)

@Serializable
data class ApplicationMetrics(
    val activeUsers: Int = 0,
    val activeSessions: Int = 0,
    val requestsPerSecond: Double = 0.0,
    val averageResponseTime: Double = 0.0,
    val errorRate: Double = 0.0
)

@Serializable
data class SystemPerformanceResponse(
    val performance: SystemPerformance
)

@Serializable
data class SystemPerformance(
    val period: String,
    val averageResponseTime: Double = 0.0,
    val requestsPerSecond: Double = 0.0,
    val errorRate: Double = 0.0,
    val uptime: Double = 0.0,
    val throughput: Double = 0.0,
    val resourceUtilization: ResourceUtilization? = null,
    val trends: PerformanceTrends? = null
)

@Serializable
data class ResourceUtilization(
    val cpu: Double = 0.0,
    val memory: Double = 0.0,
    val disk: Double = 0.0,
    val network: Double = 0.0
)

@Serializable
data class PerformanceTrends(
    val responseTime: String = "STABLE", // IMPROVING, STABLE, DEGRADING
    val throughput: String = "STABLE",
    val errorRate: String = "STABLE",
    val resourceUsage: String = "STABLE"
)

@Serializable
data class SystemLogsResponse(
    val logs: List<SystemLogEntry> = emptyList(),
    val pager: Pager? = null
)

@Serializable
data class SystemLogEntry(
    val id: String,
    val timestamp: String,
    val level: String,
    val logger: String,
    val message: String,
    val thread: String? = null,
    val exception: String? = null,
    val mdc: Map<String, String> = emptyMap()
)

@Serializable
data class AuditLogsResponse(
    val audits: List<AuditLogEntry> = emptyList(),
    val pager: Pager? = null
)

@Serializable
data class AuditLogEntry(
    val id: String,
    val auditType: String,
    val auditScope: String,
    val createdAt: String,
    val createdBy: String,
    val klass: String? = null,
    val uid: String? = null,
    val code: String? = null,
    val data: String? = null
)

// ========================================
// SECURITY SETTINGS MODELS
// ========================================

@Serializable
data class SecuritySettings(
    val passwordPolicy: PasswordPolicy? = null,
    val loginConfiguration: LoginConfiguration? = null,
    val sessionConfiguration: SessionConfiguration? = null,
    val corsConfiguration: CorsConfiguration? = null,
    val csrfProtection: Boolean = true,
    val httpsRequired: Boolean = false,
    val twoFactorAuth: TwoFactorAuthConfiguration? = null,
    val accountLockout: AccountLockoutConfiguration? = null,
    val auditConfiguration: AuditConfiguration? = null
)

@Serializable
data class SecuritySettingsResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val settings: SecuritySettings? = null
)

@Serializable
data class PasswordPolicy(
    val minLength: Int = 8,
    val maxLength: Int = 72,
    val requireUppercase: Boolean = false,
    val requireLowercase: Boolean = false,
    val requireDigits: Boolean = false,
    val requireSpecialChars: Boolean = false,
    val forbidCommonPasswords: Boolean = false,
    val forbidUserInfo: Boolean = false,
    val expiryDays: Int = 0,
    val historySize: Int = 0,
    val strengthMeter: Boolean = false
)

@Serializable
data class PasswordPolicyResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val policy: PasswordPolicy? = null
)

@Serializable
data class LoginConfiguration(
    val maxAttempts: Int = 5,
    val lockoutDuration: Int = 15,
    val allowSelfRegistration: Boolean = false,
    val allowPasswordReset: Boolean = true,
    val requireEmailVerification: Boolean = false,
    val captchaEnabled: Boolean = false,
    val captchaThreshold: Int = 3,
    val rememberMeEnabled: Boolean = true,
    val rememberMeDuration: Int = 30
)

@Serializable
data class LoginConfigurationResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val configuration: LoginConfiguration? = null
)

@Serializable
data class SessionConfiguration(
    val timeout: Int = 30,
    val maxSessions: Int = 10,
    val concurrentSessionControl: Boolean = false,
    val sessionFixationProtection: Boolean = true,
    val invalidateOnLogout: Boolean = true
)

@Serializable
data class CorsConfiguration(
    val enabled: Boolean = false,
    val allowedOrigins: List<String> = emptyList(),
    val allowedMethods: List<String> = emptyList(),
    val allowedHeaders: List<String> = emptyList(),
    val allowCredentials: Boolean = false,
    val maxAge: Int = 3600
)

@Serializable
data class TwoFactorAuthConfiguration(
    val enabled: Boolean = false,
    val required: Boolean = false,
    val issuer: String? = null,
    val qrCodeSize: Int = 200,
    val windowSize: Int = 3
)

@Serializable
data class AccountLockoutConfiguration(
    val enabled: Boolean = true,
    val maxAttempts: Int = 5,
    val lockoutDuration: Int = 15,
    val resetOnSuccess: Boolean = true
)

@Serializable
data class AuditConfiguration(
    val enabled: Boolean = true,
    val auditRead: Boolean = false,
    val auditCreate: Boolean = true,
    val auditUpdate: Boolean = true,
    val auditDelete: Boolean = true,
    val retentionDays: Int = 365
)

// ========================================
// BACKUP & RESTORE MODELS
// ========================================

@Serializable
data class SystemBackupResponse(
    val backupId: String,
    val status: String,
    val message: String? = null,
    val startTime: String? = null,
    val estimatedCompletion: String? = null,
    val downloadUrl: String? = null
)

@Serializable
data class BackupStatus(
    val backupId: String,
    val status: String,
    val progress: Int = 0,
    val message: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val size: Long = 0,
    val downloadUrl: String? = null
)

@Serializable
data class SystemBackupsResponse(
    val backups: List<SystemBackupInfo> = emptyList()
)

@Serializable
data class SystemBackupInfo(
    val backupId: String,
    val type: String,
    val status: String,
    val created: String,
    val size: Long = 0,
    val includesFiles: Boolean = false,
    val downloadUrl: String? = null
)

@Serializable
data class RestoreOptions(
    val restoreMetadata: Boolean = true,
    val restoreData: Boolean = true,
    val restoreFiles: Boolean = true,
    val restoreUsers: Boolean = false,
    val overwriteExisting: Boolean = false
)

@Serializable
data class SystemRestoreResponse(
    val restoreId: String,
    val status: String,
    val message: String? = null,
    val startTime: String? = null,
    val estimatedCompletion: String? = null
)

// ========================================
// SYSTEM UPDATES MODELS
// ========================================

@Serializable
data class SystemUpdatesResponse(
    val updates: List<SystemUpdate> = emptyList(),
    val currentVersion: String? = null,
    val latestVersion: String? = null,
    val updateAvailable: Boolean = false
)

@Serializable
data class SystemUpdate(
    val updateId: String,
    val version: String,
    val type: String, // PATCH, MINOR, MAJOR
    val severity: String, // LOW, MEDIUM, HIGH, CRITICAL
    val description: String,
    val releaseNotes: String? = null,
    val size: Long = 0,
    val releaseDate: String? = null,
    val downloadUrl: String? = null,
    val checksumMd5: String? = null,
    val checksumSha256: String? = null
)

@Serializable
data class UpdateInstallOptions(
    val backupBeforeUpdate: Boolean = true,
    val restartAfterUpdate: Boolean = true,
    val maintenanceMode: Boolean = true,
    val notifyUsers: Boolean = true
)

@Serializable
data class UpdateInstallResponse(
    val installId: String,
    val status: String,
    val message: String? = null,
    val startTime: String? = null,
    val estimatedCompletion: String? = null
)

@Serializable
data class UpdateStatus(
    val installId: String,
    val status: String,
    val progress: Int = 0,
    val message: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val errors: List<String> = emptyList()
)

// ========================================
// CLUSTER MANAGEMENT MODELS
// ========================================

@Serializable
data class ClusterInfo(
    val clusterId: String,
    val clusterName: String? = null,
    val nodeCount: Int = 0,
    val activeNodes: Int = 0,
    val status: String,
    val loadBalancer: LoadBalancerInfo? = null,
    val sharedStorage: SharedStorageInfo? = null
)

@Serializable
data class LoadBalancerInfo(
    val type: String,
    val endpoint: String? = null,
    val healthCheckPath: String? = null
)

@Serializable
data class SharedStorageInfo(
    val type: String,
    val endpoint: String? = null,
    val capacity: Long = 0,
    val used: Long = 0
)

@Serializable
data class ClusterNodesResponse(
    val nodes: List<ClusterNode> = emptyList()
)

@Serializable
data class ClusterNode(
    val nodeId: String,
    val hostname: String,
    val ipAddress: String? = null,
    val port: Int = 8080,
    val status: String,
    val role: String, // MASTER, WORKER, STANDBY
    val version: String? = null,
    val startTime: String? = null,
    val lastHeartbeat: String? = null,
    val resources: NodeResources? = null
)

@Serializable
data class NodeResources(
    val cpu: Double = 0.0,
    val memory: Long = 0,
    val disk: Long = 0,
    val network: Double = 0.0
)

@Serializable
data class NodeHealth(
    val nodeId: String,
    val status: String,
    val checks: List<HealthCheck> = emptyList(),
    val metrics: SystemMetrics? = null
)

@Serializable
data class NodeOperationResponse(
    val nodeId: String,
    val operation: String,
    val status: String,
    val message: String? = null
)

@Serializable
data class ClusterScaleOptions(
    val strategy: String = "GRADUAL", // GRADUAL, IMMEDIATE
    val drainTimeout: Int = 300,
    val healthCheckTimeout: Int = 60
)

@Serializable
data class ClusterScaleResponse(
    val scaleId: String,
    val status: String,
    val currentNodes: Int = 0,
    val targetNodes: Int = 0,
    val message: String? = null
)

// ========================================
// SYSTEM ANALYTICS MODELS
// ========================================

@Serializable
data class SystemUsageAnalyticsResponse(
    val analytics: List<SystemUsageAnalytic> = emptyList(),
    val summary: SystemUsageAnalyticsSummary? = null
)

@Serializable
data class SystemUsageAnalytic(
    val date: String? = null,
    val user: String? = null,
    val module: String? = null,
    val action: String? = null,
    val count: Int = 0,
    val duration: Long = 0
)

@Serializable
data class SystemUsageAnalyticsSummary(
    val totalUsers: Int = 0,
    val activeUsers: Int = 0,
    val totalSessions: Int = 0,
    val averageSessionDuration: Long = 0,
    val byModule: Map<String, ModuleUsageStats> = emptyMap(),
    val byUser: Map<String, UserUsageStats> = emptyMap()
)

@Serializable
data class ModuleUsageStats(
    val usage: Int = 0,
    val users: Int = 0,
    val averageDuration: Long = 0
)

@Serializable
data class UserUsageStats(
    val sessions: Int = 0,
    val totalDuration: Long = 0,
    val modulesUsed: Int = 0
)

@Serializable
data class SystemResourceUsageResponse(
    val usage: SystemResourceUsage
)

@Serializable
data class SystemResourceUsage(
    val cpu: ResourceUsageStats,
    val memory: ResourceUsageStats,
    val disk: ResourceUsageStats,
    val network: ResourceUsageStats,
    val database: ResourceUsageStats
)

@Serializable
data class ResourceUsageStats(
    val current: Double = 0.0,
    val average: Double = 0.0,
    val peak: Double = 0.0,
    val trend: String = "STABLE" // INCREASING, STABLE, DECREASING
)

@Serializable
data class SystemCapacityPlanningResponse(
    val planning: SystemCapacityPlanning
)

@Serializable
data class SystemCapacityPlanning(
    val currentCapacity: CapacityInfo,
    val projectedCapacity: CapacityInfo,
    val recommendations: List<CapacityRecommendation> = emptyList(),
    val growthRate: GrowthRate? = null
)

@Serializable
data class CapacityInfo(
    val users: Int = 0,
    val dataVolume: Long = 0,
    val storage: Long = 0,
    val bandwidth: Long = 0
)

@Serializable
data class CapacityRecommendation(
    val type: String, // SCALE_UP, SCALE_OUT, OPTIMIZE
    val priority: String, // LOW, MEDIUM, HIGH, CRITICAL
    val description: String,
    val estimatedCost: Double = 0.0,
    val timeframe: String? = null
)

@Serializable
data class GrowthRate(
    val users: Double = 0.0,
    val dataVolume: Double = 0.0,
    val storage: Double = 0.0,
    val bandwidth: Double = 0.0
)

// ========================================
// SYSTEM NOTIFICATIONS MODELS
// ========================================

@Serializable
data class SystemNotificationsResponse(
    val notifications: List<SystemNotification> = emptyList(),
    val pager: Pager? = null
)

@Serializable
data class SystemNotification(
    val id: String,
    val title: String,
    val message: String,
    val severity: String,
    val category: String? = null,
    val created: String? = null,
    val read: Boolean = false,
    val dismissed: Boolean = false,
    val actionUrl: String? = null,
    val actionText: String? = null,
    val expiresAt: String? = null
)

@Serializable
data class SystemNotificationCreate(
    val title: String,
    val message: String,
    val severity: String,
    val category: String? = null,
    val actionUrl: String? = null,
    val actionText: String? = null,
    val expiresAt: String? = null,
    val targetUsers: List<String> = emptyList(),
    val targetRoles: List<String> = emptyList()
)

@Serializable
data class SystemNotificationResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val notification: SystemNotification? = null
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
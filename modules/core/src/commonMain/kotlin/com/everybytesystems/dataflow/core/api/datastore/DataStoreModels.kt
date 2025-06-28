package com.everybytesystems.dataflow.core.api.datastore

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

// ========================================
// DATA STORE BASIC MODELS
// ========================================

@Serializable
data class DataStoreNamespacesResponse(
    val namespaces: List<String> = emptyList()
)

@Serializable
data class DataStoreKeysResponse(
    val keys: List<String> = emptyList()
)

@Serializable
data class DataStoreEntryResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val response: DataStoreEntryResponseDetails? = null
)

@Serializable
data class DataStoreEntryResponseDetails(
    val responseType: String? = null,
    val uid: String? = null,
    val key: String? = null,
    val namespace: String? = null
)

@Serializable
data class DataStoreEntryMetadata(
    val id: String,
    val key: String,
    val namespace: String,
    val value: JsonElement? = null,
    val created: String? = null,
    val createdBy: UserReference? = null,
    val lastUpdated: String? = null,
    val lastUpdatedBy: UserReference? = null,
    val encrypted: Boolean = false,
    val jbPlainText: Boolean = false,
    val sharing: DataStoreSharing? = null
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
// DATA STORE SHARING MODELS
// ========================================

@Serializable
data class DataStoreSharing(
    val owner: String? = null,
    val external: Boolean = false,
    val public: String? = null,
    val users: Map<String, DataStoreUserAccess> = emptyMap(),
    val userGroups: Map<String, DataStoreUserGroupAccess> = emptyMap()
)

@Serializable
data class DataStoreSharingResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val sharing: DataStoreSharing? = null
)

@Serializable
data class DataStoreUserAccess(
    val id: String,
    val access: String,
    val displayName: String? = null,
    val username: String? = null
)

@Serializable
data class DataStoreUserGroupAccess(
    val id: String,
    val access: String,
    val displayName: String? = null
)

// ========================================
// BULK OPERATIONS MODELS
// ========================================

@Serializable
data class DataStoreBulkGetRequest(
    val namespace: String,
    val key: String,
    val fields: List<String> = emptyList()
)

@Serializable
data class DataStoreBulkGetRequestWrapper(
    val requests: List<DataStoreBulkGetRequest>
)

@Serializable
data class DataStoreBulkGetResponse(
    val responses: List<DataStoreBulkGetResult> = emptyList(),
    val summary: DataStoreBulkSummary? = null
)

@Serializable
data class DataStoreBulkGetResult(
    val namespace: String,
    val key: String,
    val value: JsonElement? = null,
    val success: Boolean,
    val error: String? = null
)

@Serializable
data class DataStoreBulkSetRequest(
    val namespace: String,
    val key: String,
    val value: JsonElement,
    val encrypt: Boolean = false
)

@Serializable
data class DataStoreBulkSetRequestWrapper(
    val requests: List<DataStoreBulkSetRequest>
)

@Serializable
data class DataStoreBulkSetResponse(
    val responses: List<DataStoreBulkSetResult> = emptyList(),
    val summary: DataStoreBulkSummary? = null
)

@Serializable
data class DataStoreBulkSetResult(
    val namespace: String,
    val key: String,
    val success: Boolean,
    val error: String? = null,
    val created: Boolean = false,
    val updated: Boolean = false
)

@Serializable
data class DataStoreBulkDeleteRequest(
    val namespace: String,
    val key: String
)

@Serializable
data class DataStoreBulkDeleteRequestWrapper(
    val requests: List<DataStoreBulkDeleteRequest>
)

@Serializable
data class DataStoreBulkDeleteResponse(
    val responses: List<DataStoreBulkDeleteResult> = emptyList(),
    val summary: DataStoreBulkSummary? = null
)

@Serializable
data class DataStoreBulkDeleteResult(
    val namespace: String,
    val key: String,
    val success: Boolean,
    val error: String? = null
)

@Serializable
data class DataStoreBulkSummary(
    val total: Int = 0,
    val successful: Int = 0,
    val failed: Int = 0,
    val created: Int = 0,
    val updated: Int = 0,
    val deleted: Int = 0
)

// ========================================
// QUERY OPERATIONS MODELS
// ========================================

@Serializable
data class DataStoreQuery(
    val namespaces: List<String> = emptyList(),
    val keys: List<String> = emptyList(),
    val fields: List<String> = emptyList(),
    val filters: List<DataStoreFilter> = emptyList(),
    val sort: List<DataStoreSort> = emptyList(),
    val page: Int? = null,
    val pageSize: Int? = null,
    val includeMetadata: Boolean = false
)

@Serializable
data class DataStoreFilter(
    val field: String,
    val operator: DataStoreFilterOperator,
    val value: JsonElement
)

@Serializable
data class DataStoreSort(
    val field: String,
    val direction: DataStoreSortDirection = DataStoreSortDirection.ASC
)

enum class DataStoreFilterOperator {
    EQ, NE, GT, GE, LT, LE, LIKE, ILIKE, IN, NIN, NULL, NNULL, CONTAINS
}

enum class DataStoreSortDirection {
    ASC, DESC
}

@Serializable
data class DataStoreQueryResponse(
    val entries: List<DataStoreQueryResult> = emptyList(),
    val pager: Pager? = null
)

@Serializable
data class DataStoreQueryResult(
    val namespace: String,
    val key: String,
    val value: JsonElement,
    val metadata: DataStoreEntryMetadata? = null
)

@Serializable
data class DataStoreSearchResponse(
    val entries: List<DataStoreSearchResult> = emptyList(),
    val pager: Pager? = null,
    val facets: DataStoreSearchFacets? = null
)

@Serializable
data class DataStoreSearchResult(
    val namespace: String,
    val key: String,
    val value: JsonElement,
    val score: Double = 0.0,
    val highlights: List<String> = emptyList()
)

@Serializable
data class DataStoreSearchFacets(
    val namespaces: Map<String, Int> = emptyMap(),
    val types: Map<String, Int> = emptyMap(),
    val users: Map<String, Int> = emptyMap()
)

// ========================================
// ANALYTICS & STATISTICS MODELS
// ========================================

@Serializable
data class DataStoreAnalyticsResponse(
    val analytics: List<DataStoreAnalytic> = emptyList(),
    val summary: DataStoreAnalyticsSummary? = null
)

@Serializable
data class DataStoreAnalytic(
    val namespace: String? = null,
    val user: String? = null,
    val date: String? = null,
    val operations: Int = 0,
    val reads: Int = 0,
    val writes: Int = 0,
    val deletes: Int = 0,
    val dataSize: Long = 0
)

@Serializable
data class DataStoreAnalyticsSummary(
    val totalOperations: Int = 0,
    val totalReads: Int = 0,
    val totalWrites: Int = 0,
    val totalDeletes: Int = 0,
    val totalDataSize: Long = 0,
    val byNamespace: Map<String, DataStoreNamespaceStats> = emptyMap(),
    val byUser: Map<String, DataStoreUserStats> = emptyMap()
)

@Serializable
data class DataStoreNamespaceStats(
    val operations: Int = 0,
    val reads: Int = 0,
    val writes: Int = 0,
    val deletes: Int = 0,
    val dataSize: Long = 0,
    val keyCount: Int = 0
)

@Serializable
data class DataStoreUserStats(
    val operations: Int = 0,
    val reads: Int = 0,
    val writes: Int = 0,
    val deletes: Int = 0,
    val namespacesAccessed: Int = 0
)

@Serializable
data class DataStoreStatisticsResponse(
    val statistics: DataStoreStatistics
)

@Serializable
data class DataStoreStatistics(
    val totalNamespaces: Int = 0,
    val totalKeys: Int = 0,
    val totalDataSize: Long = 0,
    val averageKeySize: Long = 0,
    val largestKey: DataStoreKeyInfo? = null,
    val oldestKey: DataStoreKeyInfo? = null,
    val newestKey: DataStoreKeyInfo? = null,
    val byNamespace: Map<String, NamespaceStatistics> = emptyMap(),
    val storageInfo: DataStoreStorageInfo? = null
)

@Serializable
data class DataStoreKeyInfo(
    val namespace: String,
    val key: String,
    val size: Long,
    val created: String? = null,
    val lastUpdated: String? = null
)

@Serializable
data class NamespaceStatisticsResponse(
    val statistics: NamespaceStatistics
)

@Serializable
data class NamespaceStatistics(
    val namespace: String,
    val keyCount: Int = 0,
    val totalSize: Long = 0,
    val averageKeySize: Long = 0,
    val largestKey: DataStoreKeyInfo? = null,
    val oldestKey: DataStoreKeyInfo? = null,
    val newestKey: DataStoreKeyInfo? = null,
    val created: String? = null,
    val lastUpdated: String? = null
)

@Serializable
data class DataStoreStorageInfo(
    val totalStorageUsed: Long = 0,
    val availableStorage: Long? = null,
    val storageLimit: Long? = null,
    val usagePercentage: Double = 0.0,
    val encryptedEntries: Int = 0,
    val encryptedSize: Long = 0
)

// ========================================
// BACKUP & RESTORE MODELS
// ========================================

@Serializable
data class DataStoreExportResponse(
    val format: String,
    val namespace: String,
    val exportDate: String,
    val data: JsonElement,
    val metadata: DataStoreExportMetadata? = null
)

@Serializable
data class DataStoreExportMetadata(
    val version: String,
    val keyCount: Int = 0,
    val totalSize: Long = 0,
    val exportedBy: UserReference? = null
)

@Serializable
data class DataStoreImportData(
    val format: String,
    val data: JsonElement,
    val metadata: DataStoreImportMetadata? = null
)

@Serializable
data class DataStoreImportMetadata(
    val version: String? = null,
    val preserveTimestamps: Boolean = false,
    val preserveOwnership: Boolean = false
)

@Serializable
data class DataStoreImportResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val response: DataStoreImportResponseDetails? = null
)

@Serializable
data class DataStoreImportResponseDetails(
    val responseType: String? = null,
    val namespace: String? = null,
    val importCount: DataStoreImportCount? = null,
    val conflicts: List<DataStoreImportConflict> = emptyList()
)

@Serializable
data class DataStoreImportCount(
    val imported: Int = 0,
    val updated: Int = 0,
    val deleted: Int = 0,
    val ignored: Int = 0
)

@Serializable
data class DataStoreImportConflict(
    val key: String,
    val namespace: String,
    val message: String,
    val errorCode: String? = null
)

@Serializable
data class DataStoreBackupResponse(
    val backupId: String,
    val status: String,
    val message: String? = null,
    val backupDate: String,
    val backupSize: Long = 0,
    val namespacesIncluded: List<String> = emptyList(),
    val downloadUrl: String? = null
)

@Serializable
data class DataStoreBackupData(
    val backupId: String,
    val backupDate: String,
    val data: JsonElement,
    val metadata: DataStoreBackupMetadata? = null
)

@Serializable
data class DataStoreBackupMetadata(
    val version: String,
    val totalNamespaces: Int = 0,
    val totalKeys: Int = 0,
    val totalSize: Long = 0,
    val includesUserDataStore: Boolean = false,
    val includesAppDataStore: Boolean = false,
    val createdBy: UserReference? = null
)

@Serializable
data class DataStoreRestoreResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val response: DataStoreRestoreResponseDetails? = null
)

@Serializable
data class DataStoreRestoreResponseDetails(
    val responseType: String? = null,
    val backupId: String? = null,
    val restoreCount: DataStoreImportCount? = null,
    val conflicts: List<DataStoreImportConflict> = emptyList()
)

// ========================================
// VERSIONING MODELS
// ========================================

@Serializable
data class DataStoreVersionsResponse(
    val versions: List<DataStoreVersion> = emptyList()
)

@Serializable
data class DataStoreVersion(
    val version: Int,
    val namespace: String,
    val key: String,
    val created: String? = null,
    val createdBy: UserReference? = null,
    val size: Long = 0,
    val checksum: String? = null,
    val comment: String? = null,
    val current: Boolean = false
)

// ========================================
// APP DATA STORE MODELS
// ========================================

@Serializable
data class AppDataStoreInfo(
    val appKey: String,
    val appName: String? = null,
    val namespaces: List<String> = emptyList(),
    val totalKeys: Int = 0,
    val totalSize: Long = 0,
    val lastAccessed: String? = null
)

@Serializable
data class AppDataStoreResponse(
    val apps: List<AppDataStoreInfo> = emptyList()
)

// ========================================
// CONFIGURATION MODELS
// ========================================

@Serializable
data class DataStoreConfiguration(
    val maxNamespaceLength: Int = 255,
    val maxKeyLength: Int = 255,
    val maxValueSize: Long = 1048576, // 1MB
    val maxKeysPerNamespace: Int = 10000,
    val encryptionEnabled: Boolean = false,
    val versioningEnabled: Boolean = false,
    val backupEnabled: Boolean = false,
    val quotaEnabled: Boolean = false,
    val defaultQuotaPerUser: Long = 104857600, // 100MB
    val defaultQuotaPerApp: Long = 52428800 // 50MB
)

@Serializable
data class DataStoreConfigurationResponse(
    val configuration: DataStoreConfiguration,
    val httpStatus: String? = null,
    val httpStatusCode: Int? = null,
    val status: String? = null,
    val message: String? = null
)

// ========================================
// MONITORING MODELS
// ========================================

@Serializable
data class DataStoreMonitoringInfo(
    val status: String,
    val uptime: Long = 0,
    val totalRequests: Long = 0,
    val requestsPerSecond: Double = 0.0,
    val averageResponseTime: Double = 0.0,
    val errorRate: Double = 0.0,
    val cacheHitRate: Double = 0.0,
    val storageHealth: DataStoreStorageHealth? = null
)

@Serializable
data class DataStoreStorageHealth(
    val status: String,
    val totalSpace: Long = 0,
    val usedSpace: Long = 0,
    val freeSpace: Long = 0,
    val usagePercentage: Double = 0.0,
    val fragmentationLevel: Double = 0.0
)

@Serializable
data class DataStoreMonitoringResponse(
    val monitoring: DataStoreMonitoringInfo
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

// ========================================
// VALIDATION MODELS
// ========================================

@Serializable
data class DataStoreValidationRequest(
    val namespace: String,
    val key: String,
    val value: JsonElement,
    val schema: JsonElement? = null
)

@Serializable
data class DataStoreValidationResponse(
    val valid: Boolean,
    val errors: List<DataStoreValidationError> = emptyList(),
    val warnings: List<DataStoreValidationWarning> = emptyList()
)

@Serializable
data class DataStoreValidationError(
    val code: String,
    val message: String,
    val path: String? = null,
    val value: JsonElement? = null
)

@Serializable
data class DataStoreValidationWarning(
    val code: String,
    val message: String,
    val path: String? = null,
    val value: JsonElement? = null
)

// ========================================
// SCHEMA MODELS
// ========================================

@Serializable
data class DataStoreSchema(
    val id: String,
    val namespace: String,
    val schema: JsonElement,
    val version: String = "1.0",
    val description: String? = null,
    val created: String? = null,
    val createdBy: UserReference? = null,
    val lastUpdated: String? = null,
    val lastUpdatedBy: UserReference? = null,
    val active: Boolean = true
)

@Serializable
data class DataStoreSchemasResponse(
    val schemas: List<DataStoreSchema> = emptyList(),
    val pager: Pager? = null
)

@Serializable
data class DataStoreSchemaResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val schema: DataStoreSchema? = null
)
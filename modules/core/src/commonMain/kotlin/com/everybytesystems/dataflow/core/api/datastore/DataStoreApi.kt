package com.everybytesystems.dataflow.core.api.datastore

import com.everybytesystems.dataflow.core.api.base.BaseApi
import com.everybytesystems.dataflow.core.config.DHIS2Config
import com.everybytesystems.dataflow.core.network.ApiResponse
import com.everybytesystems.dataflow.core.version.DHIS2Version
import io.ktor.client.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Complete Data Store API implementation for DHIS2 2.36+
 * Supports data store, user data store, and app data store operations
 */
class DataStoreApi(
    httpClient: HttpClient,
    config: DHIS2Config,
    private val version: DHIS2Version
) : BaseApi(httpClient, config) {
    
    // ========================================
    // DATA STORE OPERATIONS
    // ========================================
    
    /**
     * Get all namespaces in data store
     */
    suspend fun getDataStoreNamespaces(): ApiResponse<DataStoreNamespacesResponse> {
        return get("dataStore")
    }
    
    /**
     * Get all keys in a namespace
     */
    suspend fun getDataStoreKeys(namespace: String): ApiResponse<DataStoreKeysResponse> {
        return get("dataStore/$namespace")
    }
    
    /**
     * Get a specific data store entry
     */
    suspend fun getDataStoreEntry(
        namespace: String,
        key: String,
        fields: List<String> = emptyList()
    ): ApiResponse<JsonElement> {
        val params = if (fields.isNotEmpty()) {
            mapOf("fields" to fields.joinToString(","))
        } else {
            emptyMap()
        }
        return get("dataStore/$namespace/$key", params)
    }
    
    /**
     * Create or update a data store entry
     */
    suspend fun setDataStoreEntry(
        namespace: String,
        key: String,
        value: JsonElement,
        encrypt: Boolean = false
    ): ApiResponse<DataStoreEntryResponse> {
        val params = if (encrypt) {
            mapOf("encrypt" to "true")
        } else {
            emptyMap()
        }
        return post("dataStore/$namespace/$key", value, params)
    }
    
    /**
     * Update a data store entry
     */
    suspend fun updateDataStoreEntry(
        namespace: String,
        key: String,
        value: JsonElement,
        encrypt: Boolean = false
    ): ApiResponse<DataStoreEntryResponse> {
        val params = if (encrypt) {
            mapOf("encrypt" to "true")
        } else {
            emptyMap()
        }
        return put("dataStore/$namespace/$key", value, params)
    }
    
    /**
     * Delete a data store entry
     */
    suspend fun deleteDataStoreEntry(
        namespace: String,
        key: String
    ): ApiResponse<DataStoreEntryResponse> {
        return delete("dataStore/$namespace/$key")
    }
    
    /**
     * Delete all entries in a namespace
     */
    suspend fun deleteDataStoreNamespace(namespace: String): ApiResponse<DataStoreEntryResponse> {
        return delete("dataStore/$namespace")
    }
    
    /**
     * Get data store entry metadata
     */
    suspend fun getDataStoreEntryMetadata(
        namespace: String,
        key: String
    ): ApiResponse<DataStoreEntryMetadata> {
        return get("dataStore/$namespace/$key/metaData")
    }
    
    // ========================================
    // DATA STORE SHARING (2.37+)
    // ========================================
    
    /**
     * Get data store entry sharing settings (2.37+)
     */
    suspend fun getDataStoreEntrySharing(
        namespace: String,
        key: String
    ): ApiResponse<DataStoreSharing> {
        if (!version.supportsDataStoreSharing()) {
            return ApiResponse.Error(UnsupportedOperationException("Data store sharing not supported in version ${version.versionString}"))
        }
        
        return get("dataStore/$namespace/$key/sharing")
    }
    
    /**
     * Update data store entry sharing settings (2.37+)
     */
    suspend fun updateDataStoreEntrySharing(
        namespace: String,
        key: String,
        sharing: DataStoreSharing
    ): ApiResponse<DataStoreSharingResponse> {
        if (!version.supportsDataStoreSharing()) {
            return ApiResponse.Error(UnsupportedOperationException("Data store sharing not supported in version ${version.versionString}"))
        }
        
        return put("dataStore/$namespace/$key/sharing", sharing)
    }
    
    // ========================================
    // USER DATA STORE OPERATIONS
    // ========================================
    
    /**
     * Get all namespaces in user data store
     */
    suspend fun getUserDataStoreNamespaces(userId: String? = null): ApiResponse<DataStoreNamespacesResponse> {
        val endpoint = if (userId != null) "userDataStore/$userId" else "userDataStore"
        return get(endpoint)
    }
    
    /**
     * Get all keys in a user data store namespace
     */
    suspend fun getUserDataStoreKeys(
        namespace: String,
        userId: String? = null
    ): ApiResponse<DataStoreKeysResponse> {
        val endpoint = if (userId != null) "userDataStore/$userId/$namespace" else "userDataStore/$namespace"
        return get(endpoint)
    }
    
    /**
     * Get a specific user data store entry
     */
    suspend fun getUserDataStoreEntry(
        namespace: String,
        key: String,
        userId: String? = null,
        fields: List<String> = emptyList()
    ): ApiResponse<JsonElement> {
        val endpoint = if (userId != null) "userDataStore/$userId/$namespace/$key" else "userDataStore/$namespace/$key"
        val params = if (fields.isNotEmpty()) {
            mapOf("fields" to fields.joinToString(","))
        } else {
            emptyMap()
        }
        return get(endpoint, params)
    }
    
    /**
     * Create or update a user data store entry
     */
    suspend fun setUserDataStoreEntry(
        namespace: String,
        key: String,
        value: JsonElement,
        userId: String? = null,
        encrypt: Boolean = false
    ): ApiResponse<DataStoreEntryResponse> {
        val endpoint = if (userId != null) "userDataStore/$userId/$namespace/$key" else "userDataStore/$namespace/$key"
        val params = if (encrypt) {
            mapOf("encrypt" to "true")
        } else {
            emptyMap()
        }
        return post(endpoint, value, params)
    }
    
    /**
     * Update a user data store entry
     */
    suspend fun updateUserDataStoreEntry(
        namespace: String,
        key: String,
        value: JsonElement,
        userId: String? = null,
        encrypt: Boolean = false
    ): ApiResponse<DataStoreEntryResponse> {
        val endpoint = if (userId != null) "userDataStore/$userId/$namespace/$key" else "userDataStore/$namespace/$key"
        val params = if (encrypt) {
            mapOf("encrypt" to "true")
        } else {
            emptyMap()
        }
        return put(endpoint, value, params)
    }
    
    /**
     * Delete a user data store entry
     */
    suspend fun deleteUserDataStoreEntry(
        namespace: String,
        key: String,
        userId: String? = null
    ): ApiResponse<DataStoreEntryResponse> {
        val endpoint = if (userId != null) "userDataStore/$userId/$namespace/$key" else "userDataStore/$namespace/$key"
        return delete(endpoint)
    }
    
    /**
     * Delete all entries in a user data store namespace
     */
    suspend fun deleteUserDataStoreNamespace(
        namespace: String,
        userId: String? = null
    ): ApiResponse<DataStoreEntryResponse> {
        val endpoint = if (userId != null) "userDataStore/$userId/$namespace" else "userDataStore/$namespace"
        return delete(endpoint)
    }
    
    // ========================================
    // APP DATA STORE OPERATIONS (2.38+)
    // ========================================
    
    /**
     * Get all namespaces in app data store (2.38+)
     */
    suspend fun getAppDataStoreNamespaces(appKey: String): ApiResponse<DataStoreNamespacesResponse> {
        if (!version.supportsAppDataStore()) {
            return ApiResponse.Error(UnsupportedOperationException("App data store not supported in version ${version.versionString}"))
        }
        
        return get("apps/$appKey/dataStore")
    }
    
    /**
     * Get all keys in an app data store namespace (2.38+)
     */
    suspend fun getAppDataStoreKeys(
        appKey: String,
        namespace: String
    ): ApiResponse<DataStoreKeysResponse> {
        if (!version.supportsAppDataStore()) {
            return ApiResponse.Error(UnsupportedOperationException("App data store not supported in version ${version.versionString}"))
        }
        
        return get("apps/$appKey/dataStore/$namespace")
    }
    
    /**
     * Get a specific app data store entry (2.38+)
     */
    suspend fun getAppDataStoreEntry(
        appKey: String,
        namespace: String,
        key: String,
        fields: List<String> = emptyList()
    ): ApiResponse<JsonElement> {
        if (!version.supportsAppDataStore()) {
            return ApiResponse.Error(UnsupportedOperationException("App data store not supported in version ${version.versionString}"))
        }
        
        val params = if (fields.isNotEmpty()) {
            mapOf("fields" to fields.joinToString(","))
        } else {
            emptyMap()
        }
        return get("apps/$appKey/dataStore/$namespace/$key", params)
    }
    
    /**
     * Create or update an app data store entry (2.38+)
     */
    suspend fun setAppDataStoreEntry(
        appKey: String,
        namespace: String,
        key: String,
        value: JsonElement,
        encrypt: Boolean = false
    ): ApiResponse<DataStoreEntryResponse> {
        if (!version.supportsAppDataStore()) {
            return ApiResponse.Error(UnsupportedOperationException("App data store not supported in version ${version.versionString}"))
        }
        
        val params = if (encrypt) {
            mapOf("encrypt" to "true")
        } else {
            emptyMap()
        }
        return post("apps/$appKey/dataStore/$namespace/$key", value, params)
    }
    
    /**
     * Update an app data store entry (2.38+)
     */
    suspend fun updateAppDataStoreEntry(
        appKey: String,
        namespace: String,
        key: String,
        value: JsonElement,
        encrypt: Boolean = false
    ): ApiResponse<DataStoreEntryResponse> {
        if (!version.supportsAppDataStore()) {
            return ApiResponse.Error(UnsupportedOperationException("App data store not supported in version ${version.versionString}"))
        }
        
        val params = if (encrypt) {
            mapOf("encrypt" to "true")
        } else {
            emptyMap()
        }
        return put("apps/$appKey/dataStore/$namespace/$key", value, params)
    }
    
    /**
     * Delete an app data store entry (2.38+)
     */
    suspend fun deleteAppDataStoreEntry(
        appKey: String,
        namespace: String,
        key: String
    ): ApiResponse<DataStoreEntryResponse> {
        if (!version.supportsAppDataStore()) {
            return ApiResponse.Error(UnsupportedOperationException("App data store not supported in version ${version.versionString}"))
        }
        
        return delete("apps/$appKey/dataStore/$namespace/$key")
    }
    
    /**
     * Delete all entries in an app data store namespace (2.38+)
     */
    suspend fun deleteAppDataStoreNamespace(
        appKey: String,
        namespace: String
    ): ApiResponse<DataStoreEntryResponse> {
        if (!version.supportsAppDataStore()) {
            return ApiResponse.Error(UnsupportedOperationException("App data store not supported in version ${version.versionString}"))
        }
        
        return delete("apps/$appKey/dataStore/$namespace")
    }
    
    // ========================================
    // BULK OPERATIONS (2.39+)
    // ========================================
    
    /**
     * Bulk get data store entries (2.39+)
     */
    suspend fun bulkGetDataStoreEntries(
        requests: List<DataStoreBulkGetRequest>
    ): ApiResponse<DataStoreBulkGetResponse> {
        if (!version.supportsDataStoreBulkOperations()) {
            return ApiResponse.Error(UnsupportedOperationException("Data store bulk operations not supported in version ${version.versionString}"))
        }
        
        val bulkRequest = DataStoreBulkGetRequestWrapper(requests)
        return post("dataStore/bulk/get", bulkRequest)
    }
    
    /**
     * Bulk set data store entries (2.39+)
     */
    suspend fun bulkSetDataStoreEntries(
        requests: List<DataStoreBulkSetRequest>
    ): ApiResponse<DataStoreBulkSetResponse> {
        if (!version.supportsDataStoreBulkOperations()) {
            return ApiResponse.Error(UnsupportedOperationException("Data store bulk operations not supported in version ${version.versionString}"))
        }
        
        val bulkRequest = DataStoreBulkSetRequestWrapper(requests)
        return post("dataStore/bulk/set", bulkRequest)
    }
    
    /**
     * Bulk delete data store entries (2.39+)
     */
    suspend fun bulkDeleteDataStoreEntries(
        requests: List<DataStoreBulkDeleteRequest>
    ): ApiResponse<DataStoreBulkDeleteResponse> {
        if (!version.supportsDataStoreBulkOperations()) {
            return ApiResponse.Error(UnsupportedOperationException("Data store bulk operations not supported in version ${version.versionString}"))
        }
        
        val bulkRequest = DataStoreBulkDeleteRequestWrapper(requests)
        return post("dataStore/bulk/delete", bulkRequest)
    }
    
    // ========================================
    // QUERY OPERATIONS (2.40+)
    // ========================================
    
    /**
     * Query data store entries (2.40+)
     */
    suspend fun queryDataStoreEntries(
        query: DataStoreQuery
    ): ApiResponse<DataStoreQueryResponse> {
        if (!version.supportsDataStoreQuery()) {
            return ApiResponse.Error(UnsupportedOperationException("Data store query not supported in version ${version.versionString}"))
        }
        
        return post("dataStore/query", query)
    }
    
    /**
     * Search data store entries (2.40+)
     */
    suspend fun searchDataStoreEntries(
        searchTerm: String,
        namespaces: List<String> = emptyList(),
        fields: List<String> = emptyList(),
        page: Int? = null,
        pageSize: Int? = null
    ): ApiResponse<DataStoreSearchResponse> {
        if (!version.supportsDataStoreQuery()) {
            return ApiResponse.Error(UnsupportedOperationException("Data store search not supported in version ${version.versionString}"))
        }
        
        val params = buildMap {
            put("q", searchTerm)
            if (namespaces.isNotEmpty()) put("namespaces", namespaces.joinToString(","))
            if (fields.isNotEmpty()) put("fields", fields.joinToString(","))
            page?.let { put("page", it.toString()) }
            pageSize?.let { put("pageSize", it.toString()) }
        }
        
        return get("dataStore/search", params)
    }
    
    // ========================================
    // ANALYTICS & STATISTICS
    // ========================================
    
    /**
     * Get data store analytics
     */
    suspend fun getDataStoreAnalytics(
        startDate: String? = null,
        endDate: String? = null,
        namespace: String? = null,
        groupBy: List<DataStoreAnalyticsGroupBy> = emptyList()
    ): ApiResponse<DataStoreAnalyticsResponse> {
        
        val params = buildMap {
            startDate?.let { put("startDate", it) }
            endDate?.let { put("endDate", it) }
            namespace?.let { put("namespace", it) }
            if (groupBy.isNotEmpty()) put("groupBy", groupBy.joinToString(",") { it.name })
        }
        
        return get("dataStore/analytics", params)
    }
    
    /**
     * Get data store statistics
     */
    suspend fun getDataStoreStatistics(): ApiResponse<DataStoreStatisticsResponse> {
        return get("dataStore/statistics")
    }
    
    /**
     * Get namespace statistics
     */
    suspend fun getNamespaceStatistics(namespace: String): ApiResponse<NamespaceStatisticsResponse> {
        return get("dataStore/$namespace/statistics")
    }
    
    // ========================================
    // BACKUP & RESTORE (2.41+)
    // ========================================
    
    /**
     * Export data store namespace (2.41+)
     */
    suspend fun exportDataStoreNamespace(
        namespace: String,
        format: DataStoreExportFormat = DataStoreExportFormat.JSON
    ): ApiResponse<DataStoreExportResponse> {
        if (!version.supportsDataStoreBackup()) {
            return ApiResponse.Error(UnsupportedOperationException("Data store backup not supported in version ${version.versionString}"))
        }
        
        val params = mapOf("format" to format.name)
        return get("dataStore/$namespace/export", params)
    }
    
    /**
     * Import data store namespace (2.41+)
     */
    suspend fun importDataStoreNamespace(
        namespace: String,
        data: DataStoreImportData,
        strategy: DataStoreImportStrategy = DataStoreImportStrategy.CREATE_AND_UPDATE
    ): ApiResponse<DataStoreImportResponse> {
        if (!version.supportsDataStoreBackup()) {
            return ApiResponse.Error(UnsupportedOperationException("Data store backup not supported in version ${version.versionString}"))
        }
        
        val params = mapOf("strategy" to strategy.name)
        return post("dataStore/$namespace/import", data, params)
    }
    
    /**
     * Backup entire data store (2.41+)
     */
    suspend fun backupDataStore(
        includeUserDataStore: Boolean = false,
        includeAppDataStore: Boolean = false
    ): ApiResponse<DataStoreBackupResponse> {
        if (!version.supportsDataStoreBackup()) {
            return ApiResponse.Error(UnsupportedOperationException("Data store backup not supported in version ${version.versionString}"))
        }
        
        val params = buildMap {
            put("includeUserDataStore", includeUserDataStore.toString())
            put("includeAppDataStore", includeAppDataStore.toString())
        }
        
        return post("dataStore/backup", emptyMap<String, Any>(), params)
    }
    
    /**
     * Restore data store from backup (2.41+)
     */
    suspend fun restoreDataStore(
        backup: DataStoreBackupData,
        strategy: DataStoreImportStrategy = DataStoreImportStrategy.CREATE_AND_UPDATE
    ): ApiResponse<DataStoreRestoreResponse> {
        if (!version.supportsDataStoreBackup()) {
            return ApiResponse.Error(UnsupportedOperationException("Data store backup not supported in version ${version.versionString}"))
        }
        
        val params = mapOf("strategy" to strategy.name)
        return post("dataStore/restore", backup, params)
    }
    
    // ========================================
    // VERSIONING (2.42+)
    // ========================================
    
    /**
     * Get data store entry versions (2.42+)
     */
    suspend fun getDataStoreEntryVersions(
        namespace: String,
        key: String
    ): ApiResponse<DataStoreVersionsResponse> {
        if (!version.supportsDataStoreVersioning()) {
            return ApiResponse.Error(UnsupportedOperationException("Data store versioning not supported in version ${version.versionString}"))
        }
        
        return get("dataStore/$namespace/$key/versions")
    }
    
    /**
     * Get specific data store entry version (2.42+)
     */
    suspend fun getDataStoreEntryVersion(
        namespace: String,
        key: String,
        version: Int
    ): ApiResponse<JsonElement> {
        if (!this.version.supportsDataStoreVersioning()) {
            return ApiResponse.Error(UnsupportedOperationException("Data store versioning not supported in version ${this.version.versionString}"))
        }
        
        return get("dataStore/$namespace/$key/versions/$version")
    }
    
    /**
     * Restore data store entry to specific version (2.42+)
     */
    suspend fun restoreDataStoreEntryVersion(
        namespace: String,
        key: String,
        version: Int
    ): ApiResponse<DataStoreEntryResponse> {
        if (!this.version.supportsDataStoreVersioning()) {
            return ApiResponse.Error(UnsupportedOperationException("Data store versioning not supported in version ${this.version.versionString}"))
        }
        
        return post("dataStore/$namespace/$key/versions/$version/restore", emptyMap<String, Any>())
    }
}

// ========================================
// ENUMS
// ========================================

enum class DataStoreAnalyticsGroupBy { NAMESPACE, USER, DATE }
enum class DataStoreExportFormat { JSON, XML, CSV }
enum class DataStoreImportStrategy { CREATE, UPDATE, CREATE_AND_UPDATE, DELETE }
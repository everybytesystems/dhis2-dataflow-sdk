package com.everybytesystems.dataflow.core.api.data

import com.everybytesystems.dataflow.core.api.base.BaseApi
import com.everybytesystems.dataflow.core.config.DHIS2Config
import com.everybytesystems.dataflow.core.network.ApiResponse
import com.everybytesystems.dataflow.core.version.DHIS2Version
import io.ktor.client.*
import kotlinx.serialization.Serializable

/**
 * Complete Data Values API implementation for DHIS2 2.36+
 * Supports data value operations, data value sets, and data completion
 */
class DataValuesApi(
    httpClient: HttpClient,
    config: DHIS2Config,
    private val version: DHIS2Version
) : BaseApi(httpClient, config) {
    
    // ========================================
    // SINGLE DATA VALUE OPERATIONS
    // ========================================
    
    /**
     * Get a single data value
     */
    suspend fun getDataValue(
        dataElement: String,
        period: String,
        orgUnit: String,
        categoryOptionCombo: String? = null,
        attributeOptionCombo: String? = null
    ): ApiResponse<DataValue> {
        val params = buildMap {
            put("de", dataElement)
            put("pe", period)
            put("ou", orgUnit)
            categoryOptionCombo?.let { put("co", it) }
            attributeOptionCombo?.let { put("cc", it) }
        }
        return get("dataValues", params)
    }
    
    /**
     * Save or update a single data value
     */
    suspend fun saveDataValue(
        dataElement: String,
        period: String,
        orgUnit: String,
        value: String,
        categoryOptionCombo: String? = null,
        attributeOptionCombo: String? = null,
        comment: String? = null,
        followUp: Boolean? = null
    ): ApiResponse<DataValueResponse> {
        val params = buildMap {
            put("de", dataElement)
            put("pe", period)
            put("ou", orgUnit)
            put("value", value)
            categoryOptionCombo?.let { put("co", it) }
            attributeOptionCombo?.let { put("cc", it) }
            comment?.let { put("comment", it) }
            followUp?.let { put("followUp", it.toString()) }
        }
        return post("dataValues", emptyMap<String, Any>(), params)
    }
    
    /**
     * Delete a single data value
     */
    suspend fun deleteDataValue(
        dataElement: String,
        period: String,
        orgUnit: String,
        categoryOptionCombo: String? = null,
        attributeOptionCombo: String? = null
    ): ApiResponse<DataValueResponse> {
        val params = buildMap {
            put("de", dataElement)
            put("pe", period)
            put("ou", orgUnit)
            categoryOptionCombo?.let { put("co", it) }
            attributeOptionCombo?.let { put("cc", it) }
        }
        return delete("dataValues", params)
    }
    
    // ========================================
    // DATA VALUE SETS
    // ========================================
    
    /**
     * Get data value sets with comprehensive filtering
     */
    suspend fun getDataValueSets(
        dataSet: List<String> = emptyList(),
        dataElementGroup: List<String> = emptyList(),
        period: List<String> = emptyList(),
        startDate: String? = null,
        endDate: String? = null,
        orgUnit: List<String> = emptyList(),
        children: Boolean = false,
        orgUnitGroup: List<String> = emptyList(),
        attributeOptionCombo: List<String> = emptyList(),
        includeDeleted: Boolean = false,
        lastUpdated: String? = null,
        lastUpdatedDuration: String? = null,
        limit: Int? = null,
        idScheme: String = "UID",
        dataElementIdScheme: String = "UID",
        orgUnitIdScheme: String = "UID",
        categoryOptionComboIdScheme: String = "UID",
        format: DataValueFormat = DataValueFormat.JSON,
        compression: DataValueCompression = DataValueCompression.NONE
    ): ApiResponse<DataValueSet> {
        
        val params = buildMap {
            if (dataSet.isNotEmpty()) put("dataSet", dataSet.joinToString(","))
            if (dataElementGroup.isNotEmpty()) put("dataElementGroup", dataElementGroup.joinToString(","))
            if (period.isNotEmpty()) put("period", period.joinToString(","))
            startDate?.let { put("startDate", it) }
            endDate?.let { put("endDate", it) }
            if (orgUnit.isNotEmpty()) put("orgUnit", orgUnit.joinToString(","))
            put("children", children.toString())
            if (orgUnitGroup.isNotEmpty()) put("orgUnitGroup", orgUnitGroup.joinToString(","))
            if (attributeOptionCombo.isNotEmpty()) put("attributeOptionCombo", attributeOptionCombo.joinToString(","))
            put("includeDeleted", includeDeleted.toString())
            lastUpdated?.let { put("lastUpdated", it) }
            lastUpdatedDuration?.let { put("lastUpdatedDuration", it) }
            limit?.let { put("limit", it.toString()) }
            put("idScheme", idScheme)
            put("dataElementIdScheme", dataElementIdScheme)
            put("orgUnitIdScheme", orgUnitIdScheme)
            put("categoryOptionComboIdScheme", categoryOptionComboIdScheme)
            put("format", format.value)
            put("compression", compression.value)
        }
        
        return get("dataValueSets", params)
    }
    
    /**
     * Import data value sets with comprehensive options
     */
    suspend fun importDataValueSets(
        dataValueSet: DataValueSet,
        dryRun: Boolean = false,
        strategy: ImportStrategy = ImportStrategy.NEW_AND_UPDATES,
        preheatCache: Boolean = true,
        skipExistingCheck: Boolean = false,
        skipAudit: Boolean = false,
        dataElementIdScheme: String = "UID",
        orgUnitIdScheme: String = "UID",
        categoryOptionComboIdScheme: String = "UID",
        idScheme: String = "UID",
        async: Boolean = false,
        format: DataValueFormat = DataValueFormat.JSON
    ): ApiResponse<DataValueImportResponse> {
        
        val params = buildMap {
            put("dryRun", dryRun.toString())
            put("strategy", strategy.value)
            put("preheatCache", preheatCache.toString())
            put("skipExistingCheck", skipExistingCheck.toString())
            put("skipAudit", skipAudit.toString())
            put("dataElementIdScheme", dataElementIdScheme)
            put("orgUnitIdScheme", orgUnitIdScheme)
            put("categoryOptionComboIdScheme", categoryOptionComboIdScheme)
            put("idScheme", idScheme)
            put("async", async.toString())
            put("format", format.value)
        }
        
        return post("dataValueSets", dataValueSet, params)
    }
    
    /**
     * Import data value sets from file
     */
    suspend fun importDataValueSetsFromFile(
        fileContent: String,
        dryRun: Boolean = false,
        strategy: ImportStrategy = ImportStrategy.NEW_AND_UPDATES,
        preheatCache: Boolean = true,
        skipExistingCheck: Boolean = false,
        skipAudit: Boolean = false,
        dataElementIdScheme: String = "UID",
        orgUnitIdScheme: String = "UID",
        categoryOptionComboIdScheme: String = "UID",
        idScheme: String = "UID",
        async: Boolean = false,
        format: DataValueFormat = DataValueFormat.JSON
    ): ApiResponse<DataValueImportResponse> {
        
        val params = buildMap {
            put("dryRun", dryRun.toString())
            put("strategy", strategy.value)
            put("preheatCache", preheatCache.toString())
            put("skipExistingCheck", skipExistingCheck.toString())
            put("skipAudit", skipAudit.toString())
            put("dataElementIdScheme", dataElementIdScheme)
            put("orgUnitIdScheme", orgUnitIdScheme)
            put("categoryOptionComboIdScheme", categoryOptionComboIdScheme)
            put("idScheme", idScheme)
            put("async", async.toString())
            put("format", format.value)
        }
        
        return post("dataValueSets", fileContent, params)
    }
    
    // ========================================
    // DATA SET COMPLETION
    // ========================================
    
    /**
     * Get data set completion information
     */
    suspend fun getDataSetCompletion(
        dataSet: String,
        period: String,
        orgUnit: String,
        attributeOptionCombo: String? = null
    ): ApiResponse<DataSetCompletion> {
        val params = buildMap {
            put("ds", dataSet)
            put("pe", period)
            put("ou", orgUnit)
            attributeOptionCombo?.let { put("cc", it) }
        }
        return get("completeDataSetRegistrations", params)
    }
    
    /**
     * Mark data set as complete
     */
    suspend fun completeDataSet(
        dataSet: String,
        period: String,
        orgUnit: String,
        attributeOptionCombo: String? = null,
        completedBy: String? = null,
        completedDate: String? = null,
        multiOrgUnit: Boolean = false
    ): ApiResponse<DataSetCompletionResponse> {
        val params = buildMap {
            put("ds", dataSet)
            put("pe", period)
            put("ou", orgUnit)
            attributeOptionCombo?.let { put("cc", it) }
            completedBy?.let { put("completedBy", it) }
            completedDate?.let { put("completedDate", it) }
            put("multiOrgUnit", multiOrgUnit.toString())
        }
        return post("completeDataSetRegistrations", emptyMap<String, Any>(), params)
    }
    
    /**
     * Mark data set as incomplete
     */
    suspend fun uncompleteDataSet(
        dataSet: String,
        period: String,
        orgUnit: String,
        attributeOptionCombo: String? = null,
        multiOrgUnit: Boolean = false
    ): ApiResponse<DataSetCompletionResponse> {
        val params = buildMap {
            put("ds", dataSet)
            put("pe", period)
            put("ou", orgUnit)
            attributeOptionCombo?.let { put("cc", it) }
            put("multiOrgUnit", multiOrgUnit.toString())
        }
        return delete("completeDataSetRegistrations", params)
    }
    
    /**
     * Bulk complete data sets
     */
    suspend fun bulkCompleteDataSets(
        completions: List<DataSetCompletionRequest>
    ): ApiResponse<DataSetCompletionResponse> {
        val payload = DataSetCompletionBulkRequest(completions)
        return post("completeDataSetRegistrations/multiple", payload)
    }
    
    // ========================================
    // DATA VALIDATION AND FOLLOW-UP
    // ========================================
    
    /**
     * Get data values with follow-up flag
     */
    suspend fun getFollowUpDataValues(
        dataSet: List<String> = emptyList(),
        period: List<String> = emptyList(),
        orgUnit: List<String> = emptyList(),
        fields: String = "*"
    ): ApiResponse<DataValueSet> {
        val params = buildMap {
            if (dataSet.isNotEmpty()) put("dataSet", dataSet.joinToString(","))
            if (period.isNotEmpty()) put("period", period.joinToString(","))
            if (orgUnit.isNotEmpty()) put("orgUnit", orgUnit.joinToString(","))
            put("followUp", "true")
            put("fields", fields)
        }
        return get("dataValueSets", params)
    }
    
    /**
     * Mark data value for follow-up
     */
    suspend fun markDataValueForFollowUp(
        dataElement: String,
        period: String,
        orgUnit: String,
        categoryOptionCombo: String? = null,
        attributeOptionCombo: String? = null,
        followUp: Boolean = true
    ): ApiResponse<DataValueResponse> {
        val params = buildMap {
            put("de", dataElement)
            put("pe", period)
            put("ou", orgUnit)
            categoryOptionCombo?.let { put("co", it) }
            attributeOptionCombo?.let { put("cc", it) }
            put("followUp", followUp.toString())
        }
        return post("dataValues/followups", emptyMap<String, Any>(), params)
    }
    
    // ========================================
    // DATA AUDIT (2.36+)
    // ========================================
    
    /**
     * Get data value audit trail (2.36+)
     */
    suspend fun getDataValueAudit(
        dataElement: List<String> = emptyList(),
        period: List<String> = emptyList(),
        orgUnit: List<String> = emptyList(),
        categoryOptionCombo: List<String> = emptyList(),
        attributeOptionCombo: List<String> = emptyList(),
        auditType: DataAuditType? = null,
        skipPaging: Boolean = false,
        page: Int? = null,
        pageSize: Int? = null
    ): ApiResponse<DataValueAuditResponse> {
        if (!version.supportsDataValueAudit()) {
            return ApiResponse.Error(UnsupportedOperationException("Data value audit not supported in version ${version.versionString}"))
        }
        
        val params = buildMap {
            if (dataElement.isNotEmpty()) put("de", dataElement.joinToString(","))
            if (period.isNotEmpty()) put("pe", period.joinToString(","))
            if (orgUnit.isNotEmpty()) put("ou", orgUnit.joinToString(","))
            if (categoryOptionCombo.isNotEmpty()) put("co", categoryOptionCombo.joinToString(","))
            if (attributeOptionCombo.isNotEmpty()) put("cc", attributeOptionCombo.joinToString(","))
            auditType?.let { put("auditType", it.name) }
            put("skipPaging", skipPaging.toString())
            page?.let { put("page", it.toString()) }
            pageSize?.let { put("pageSize", it.toString()) }
        }
        
        return get("audits/dataValue", params)
    }
    
    // ========================================
    // ASYNC OPERATIONS (2.38+)
    // ========================================
    
    /**
     * Get async import job status (2.38+)
     */
    suspend fun getAsyncImportStatus(jobId: String): ApiResponse<AsyncJobResponse> {
        if (!version.supportsDataValueSetAsync()) {
            return ApiResponse.Error(UnsupportedOperationException("Async operations not supported in version ${version.versionString}"))
        }
        
        return get("system/tasks/DATAVALUE_IMPORT/$jobId")
    }
    
    /**
     * Cancel async import job (2.38+)
     */
    suspend fun cancelAsyncImport(jobId: String): ApiResponse<AsyncJobResponse> {
        if (!version.supportsDataValueSetAsync()) {
            return ApiResponse.Error(UnsupportedOperationException("Async operations not supported in version ${version.versionString}"))
        }
        
        return delete("system/tasks/DATAVALUE_IMPORT/$jobId")
    }
    
    // ========================================
    // BULK OPERATIONS
    // ========================================
    
    /**
     * Bulk delete data values
     */
    suspend fun bulkDeleteDataValues(
        dataValues: List<DataValueIdentifier>
    ): ApiResponse<DataValueImportResponse> {
        val payload = BulkDataValueOperation(
            operation = BulkDataValueOperationType.DELETE,
            dataValues = dataValues
        )
        return post("dataValues/bulk", payload)
    }
    
    /**
     * Bulk update data values
     */
    suspend fun bulkUpdateDataValues(
        dataValues: List<DataValue>
    ): ApiResponse<DataValueImportResponse> {
        val payload = BulkDataValueOperation(
            operation = BulkDataValueOperationType.UPDATE,
            dataValues = dataValues.map { dv ->
                DataValueIdentifier(
                    dataElement = dv.dataElement,
                    period = dv.period,
                    orgUnit = dv.orgUnit,
                    categoryOptionCombo = dv.categoryOptionCombo,
                    attributeOptionCombo = dv.attributeOptionCombo,
                    value = dv.value
                )
            }
        )
        return post("dataValues/bulk", payload)
    }
}

// ========================================
// ENUMS
// ========================================

enum class ImportStrategy(val value: String) {
    NEW_AND_UPDATES("NEW_AND_UPDATES"),
    NEW("NEW"),
    UPDATES("UPDATES"),
    DELETE("DELETE")
}

enum class DataValueFormat(val value: String) {
    JSON("json"),
    XML("xml"),
    CSV("csv"),
    PDF("pdf"),
    XLS("xls")
}

enum class DataValueCompression(val value: String) {
    NONE("none"),
    GZIP("gzip"),
    ZIP("zip")
}

enum class DataAuditType {
    READ, CREATE, UPDATE, DELETE
}

enum class BulkDataValueOperationType {
    CREATE, UPDATE, DELETE
}
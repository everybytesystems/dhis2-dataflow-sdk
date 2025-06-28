package com.everybytesystems.dataflow.core.api.approval

import com.everybytesystems.dataflow.core.api.base.BaseApi
import com.everybytesystems.dataflow.core.config.DHIS2Config
import com.everybytesystems.dataflow.core.network.ApiResponse
import com.everybytesystems.dataflow.core.version.DHIS2Version
import io.ktor.client.*
import kotlinx.serialization.Serializable

/**
 * Complete Data Approval API implementation for DHIS2 2.36+
 * Supports data approval workflows, levels, and comprehensive approval operations
 */
class DataApprovalApi(
    httpClient: HttpClient,
    config: DHIS2Config,
    private val version: DHIS2Version
) : BaseApi(httpClient, config) {
    
    // ========================================
    // DATA APPROVAL OPERATIONS
    // ========================================
    
    /**
     * Get data approval status
     */
    suspend fun getDataApprovalStatus(
        dataSet: List<String> = emptyList(),
        workflow: List<String> = emptyList(),
        period: List<String> = emptyList(),
        orgUnit: List<String> = emptyList(),
        attributeOptionCombo: List<String> = emptyList(),
        startDate: String? = null,
        endDate: String? = null
    ): ApiResponse<DataApprovalStatusResponse> {
        
        val params = buildMap {
            if (dataSet.isNotEmpty()) put("ds", dataSet.joinToString(","))
            if (workflow.isNotEmpty()) put("wf", workflow.joinToString(","))
            if (period.isNotEmpty()) put("pe", period.joinToString(","))
            if (orgUnit.isNotEmpty()) put("ou", orgUnit.joinToString(","))
            if (attributeOptionCombo.isNotEmpty()) put("aoc", attributeOptionCombo.joinToString(","))
            startDate?.let { put("startDate", it) }
            endDate?.let { put("endDate", it) }
        }
        
        return get("dataApprovals", params)
    }
    
    /**
     * Approve data
     */
    suspend fun approveData(
        approvals: List<DataApproval>
    ): ApiResponse<DataApprovalResponse> {
        val payload = DataApprovalRequest(approvals)
        return post("dataApprovals", payload)
    }
    
    /**
     * Unapprove data
     */
    suspend fun unapproveData(
        approvals: List<DataApproval>
    ): ApiResponse<DataApprovalResponse> {
        val payload = DataApprovalRequest(approvals)
        return deleteWithBody("dataApprovals", payload)
    }
    
    /**
     * Accept data approval
     */
    suspend fun acceptDataApproval(
        approvals: List<DataApproval>
    ): ApiResponse<DataApprovalResponse> {
        val payload = DataApprovalRequest(approvals)
        return post("dataAcceptances", payload)
    }
    
    /**
     * Unaccept data approval
     */
    suspend fun unacceptDataApproval(
        approvals: List<DataApproval>
    ): ApiResponse<DataApprovalResponse> {
        val payload = DataApprovalRequest(approvals)
        return deleteWithBody("dataAcceptances", payload)
    }
    
    /**
     * Get data approval levels for user
     */
    suspend fun getDataApprovalLevels(
        workflow: String? = null
    ): ApiResponse<DataApprovalLevelsResponse> {
        val params = buildMap {
            workflow?.let { put("wf", it) }
        }
        return get("dataApprovalLevels", params)
    }
    
    // ========================================
    // BULK APPROVAL OPERATIONS
    // ========================================
    
    /**
     * Bulk approve data
     */
    suspend fun bulkApproveData(
        dataSet: List<String> = emptyList(),
        workflow: List<String> = emptyList(),
        period: List<String> = emptyList(),
        orgUnit: List<String> = emptyList(),
        attributeOptionCombo: List<String> = emptyList()
    ): ApiResponse<DataApprovalResponse> {
        
        val params = buildMap {
            if (dataSet.isNotEmpty()) put("ds", dataSet.joinToString(","))
            if (workflow.isNotEmpty()) put("wf", workflow.joinToString(","))
            if (period.isNotEmpty()) put("pe", period.joinToString(","))
            if (orgUnit.isNotEmpty()) put("ou", orgUnit.joinToString(","))
            if (attributeOptionCombo.isNotEmpty()) put("aoc", attributeOptionCombo.joinToString(","))
        }
        
        return post("dataApprovals/approvals", emptyMap<String, Any>(), params)
    }
    
    /**
     * Bulk unapprove data
     */
    suspend fun bulkUnapproveData(
        dataSet: List<String> = emptyList(),
        workflow: List<String> = emptyList(),
        period: List<String> = emptyList(),
        orgUnit: List<String> = emptyList(),
        attributeOptionCombo: List<String> = emptyList()
    ): ApiResponse<DataApprovalResponse> {
        
        val params = buildMap {
            if (dataSet.isNotEmpty()) put("ds", dataSet.joinToString(","))
            if (workflow.isNotEmpty()) put("wf", workflow.joinToString(","))
            if (period.isNotEmpty()) put("pe", period.joinToString(","))
            if (orgUnit.isNotEmpty()) put("ou", orgUnit.joinToString(","))
            if (attributeOptionCombo.isNotEmpty()) put("aoc", attributeOptionCombo.joinToString(","))
        }
        
        return delete("dataApprovals/approvals", params)
    }
    
    /**
     * Bulk accept data approval
     */
    suspend fun bulkAcceptDataApproval(
        dataSet: List<String> = emptyList(),
        workflow: List<String> = emptyList(),
        period: List<String> = emptyList(),
        orgUnit: List<String> = emptyList(),
        attributeOptionCombo: List<String> = emptyList()
    ): ApiResponse<DataApprovalResponse> {
        
        val params = buildMap {
            if (dataSet.isNotEmpty()) put("ds", dataSet.joinToString(","))
            if (workflow.isNotEmpty()) put("wf", workflow.joinToString(","))
            if (period.isNotEmpty()) put("pe", period.joinToString(","))
            if (orgUnit.isNotEmpty()) put("ou", orgUnit.joinToString(","))
            if (attributeOptionCombo.isNotEmpty()) put("aoc", attributeOptionCombo.joinToString(","))
        }
        
        return post("dataAcceptances/acceptances", emptyMap<String, Any>(), params)
    }
    
    /**
     * Bulk unaccept data approval
     */
    suspend fun bulkUnacceptDataApproval(
        dataSet: List<String> = emptyList(),
        workflow: List<String> = emptyList(),
        period: List<String> = emptyList(),
        orgUnit: List<String> = emptyList(),
        attributeOptionCombo: List<String> = emptyList()
    ): ApiResponse<DataApprovalResponse> {
        
        val params = buildMap {
            if (dataSet.isNotEmpty()) put("ds", dataSet.joinToString(","))
            if (workflow.isNotEmpty()) put("wf", workflow.joinToString(","))
            if (period.isNotEmpty()) put("pe", period.joinToString(","))
            if (orgUnit.isNotEmpty()) put("ou", orgUnit.joinToString(","))
            if (attributeOptionCombo.isNotEmpty()) put("aoc", attributeOptionCombo.joinToString(","))
        }
        
        return delete("dataAcceptances/acceptances", params)
    }
    
    // ========================================
    // DATA APPROVAL WORKFLOWS
    // ========================================
    
    /**
     * Get data approval workflows
     */
    suspend fun getDataApprovalWorkflows(
        fields: String = "*",
        filter: List<String> = emptyList(),
        order: String? = null,
        page: Int? = null,
        pageSize: Int? = null
    ): ApiResponse<DataApprovalWorkflowsResponse> {
        
        val params = buildMap {
            put("fields", fields)
            if (filter.isNotEmpty()) put("filter", filter.joinToString(","))
            order?.let { put("order", it) }
            page?.let { put("page", it.toString()) }
            pageSize?.let { put("pageSize", it.toString()) }
        }
        
        return get("dataApprovalWorkflows", params)
    }
    
    /**
     * Get a specific data approval workflow
     */
    suspend fun getDataApprovalWorkflow(
        id: String,
        fields: String = "*"
    ): ApiResponse<DataApprovalWorkflow> {
        return get("dataApprovalWorkflows/$id", mapOf("fields" to fields))
    }
    
    /**
     * Create a new data approval workflow
     */
    suspend fun createDataApprovalWorkflow(
        workflow: DataApprovalWorkflow
    ): ApiResponse<DataApprovalImportResponse> {
        return post("dataApprovalWorkflows", workflow)
    }
    
    /**
     * Update an existing data approval workflow
     */
    suspend fun updateDataApprovalWorkflow(
        id: String,
        workflow: DataApprovalWorkflow
    ): ApiResponse<DataApprovalImportResponse> {
        return put("dataApprovalWorkflows/$id", workflow)
    }
    
    /**
     * Delete a data approval workflow
     */
    suspend fun deleteDataApprovalWorkflow(id: String): ApiResponse<DataApprovalImportResponse> {
        return delete("dataApprovalWorkflows/$id")
    }
    
    // ========================================
    // DATA APPROVAL LEVELS
    // ========================================
    
    /**
     * Get data approval levels
     */
    suspend fun getDataApprovalLevelsList(
        fields: String = "*",
        filter: List<String> = emptyList(),
        order: String? = null,
        page: Int? = null,
        pageSize: Int? = null
    ): ApiResponse<DataApprovalLevelsListResponse> {
        
        val params = buildMap {
            put("fields", fields)
            if (filter.isNotEmpty()) put("filter", filter.joinToString(","))
            order?.let { put("order", it) }
            page?.let { put("page", it.toString()) }
            pageSize?.let { put("pageSize", it.toString()) }
        }
        
        return get("dataApprovalLevels", params)
    }
    
    /**
     * Get a specific data approval level
     */
    suspend fun getDataApprovalLevel(
        id: String,
        fields: String = "*"
    ): ApiResponse<DataApprovalLevel> {
        return get("dataApprovalLevels/$id", mapOf("fields" to fields))
    }
    
    /**
     * Create a new data approval level
     */
    suspend fun createDataApprovalLevel(
        level: DataApprovalLevel
    ): ApiResponse<DataApprovalImportResponse> {
        return post("dataApprovalLevels", level)
    }
    
    /**
     * Update an existing data approval level
     */
    suspend fun updateDataApprovalLevel(
        id: String,
        level: DataApprovalLevel
    ): ApiResponse<DataApprovalImportResponse> {
        return put("dataApprovalLevels/$id", level)
    }
    
    /**
     * Delete a data approval level
     */
    suspend fun deleteDataApprovalLevel(id: String): ApiResponse<DataApprovalImportResponse> {
        return delete("dataApprovalLevels/$id")
    }
    
    // ========================================
    // DATA APPROVAL AUDIT (2.37+)
    // ========================================
    
    /**
     * Get data approval audit trail (2.37+)
     */
    suspend fun getDataApprovalAudit(
        dataSet: List<String> = emptyList(),
        workflow: List<String> = emptyList(),
        period: List<String> = emptyList(),
        orgUnit: List<String> = emptyList(),
        attributeOptionCombo: List<String> = emptyList(),
        startDate: String? = null,
        endDate: String? = null,
        skipPaging: Boolean = false,
        page: Int? = null,
        pageSize: Int? = null
    ): ApiResponse<DataApprovalAuditResponse> {
        if (!version.supportsDataApprovalAudit()) {
            return ApiResponse.Error(UnsupportedOperationException("Data approval audit not supported in version ${version.versionString}"))
        }
        
        val params = buildMap {
            if (dataSet.isNotEmpty()) put("ds", dataSet.joinToString(","))
            if (workflow.isNotEmpty()) put("wf", workflow.joinToString(","))
            if (period.isNotEmpty()) put("pe", period.joinToString(","))
            if (orgUnit.isNotEmpty()) put("ou", orgUnit.joinToString(","))
            if (attributeOptionCombo.isNotEmpty()) put("aoc", attributeOptionCombo.joinToString(","))
            startDate?.let { put("startDate", it) }
            endDate?.let { put("endDate", it) }
            put("skipPaging", skipPaging.toString())
            page?.let { put("page", it.toString()) }
            pageSize?.let { put("pageSize", it.toString()) }
        }
        
        return get("audits/dataApproval", params)
    }
    
    // ========================================
    // MULTIPLE DATA APPROVAL (2.38+)
    // ========================================
    
    /**
     * Get multiple data approval status (2.38+)
     */
    suspend fun getMultipleDataApprovalStatus(
        requests: List<DataApprovalStatusRequest>
    ): ApiResponse<MultipleDataApprovalStatusResponse> {
        if (!version.supportsDataApprovalMultiple()) {
            return ApiResponse.Error(UnsupportedOperationException("Multiple data approval not supported in version ${version.versionString}"))
        }
        
        val payload = MultipleDataApprovalStatusRequest(requests)
        return post("dataApprovals/multiple", payload)
    }
    
    /**
     * Approve multiple data sets (2.38+)
     */
    suspend fun approveMultipleData(
        requests: List<DataApprovalRequest>
    ): ApiResponse<MultipleDataApprovalResponse> {
        if (!version.supportsDataApprovalMultiple()) {
            return ApiResponse.Error(UnsupportedOperationException("Multiple data approval not supported in version ${version.versionString}"))
        }
        
        val payload = MultipleDataApprovalRequest(requests)
        return post("dataApprovals/multiple/approvals", payload)
    }
    
    /**
     * Unapprove multiple data sets (2.38+)
     */
    suspend fun unapproveMultipleData(
        requests: List<DataApprovalRequest>
    ): ApiResponse<MultipleDataApprovalResponse> {
        if (!version.supportsDataApprovalMultiple()) {
            return ApiResponse.Error(UnsupportedOperationException("Multiple data approval not supported in version ${version.versionString}"))
        }
        
        val payload = MultipleDataApprovalRequest(requests)
        return deleteWithBody("dataApprovals/multiple/approvals", payload)
    }
    
    // ========================================
    // DATA APPROVAL PERMISSIONS
    // ========================================
    
    /**
     * Get data approval permissions for user
     */
    suspend fun getDataApprovalPermissions(
        dataSet: List<String> = emptyList(),
        workflow: List<String> = emptyList(),
        period: List<String> = emptyList(),
        orgUnit: List<String> = emptyList(),
        attributeOptionCombo: List<String> = emptyList()
    ): ApiResponse<DataApprovalPermissionsResponse> {
        
        val params = buildMap {
            if (dataSet.isNotEmpty()) put("ds", dataSet.joinToString(","))
            if (workflow.isNotEmpty()) put("wf", workflow.joinToString(","))
            if (period.isNotEmpty()) put("pe", period.joinToString(","))
            if (orgUnit.isNotEmpty()) put("ou", orgUnit.joinToString(","))
            if (attributeOptionCombo.isNotEmpty()) put("aoc", attributeOptionCombo.joinToString(","))
        }
        
        return get("dataApprovals/permissions", params)
    }
    
    // ========================================
    // DATA APPROVAL ANALYTICS
    // ========================================
    
    /**
     * Get data approval analytics
     */
    suspend fun getDataApprovalAnalytics(
        startDate: String,
        endDate: String,
        workflow: List<String> = emptyList(),
        orgUnit: List<String> = emptyList(),
        level: List<String> = emptyList(),
        status: List<DataApprovalStatus> = emptyList(),
        groupBy: List<DataApprovalGroupBy> = emptyList()
    ): ApiResponse<DataApprovalAnalyticsResponse> {
        
        val params = buildMap {
            put("startDate", startDate)
            put("endDate", endDate)
            if (workflow.isNotEmpty()) put("wf", workflow.joinToString(","))
            if (orgUnit.isNotEmpty()) put("ou", orgUnit.joinToString(","))
            if (level.isNotEmpty()) put("level", level.joinToString(","))
            if (status.isNotEmpty()) put("status", status.joinToString(",") { it.name })
            if (groupBy.isNotEmpty()) put("groupBy", groupBy.joinToString(",") { it.name })
        }
        
        return get("dataApprovals/analytics", params)
    }
    
    // ========================================
    // CUSTOM APPROVAL OPERATIONS
    // ========================================
    
    /**
     * Execute custom approval operation
     */
    suspend fun executeCustomApprovalOperation(
        operation: CustomApprovalOperation
    ): ApiResponse<DataApprovalResponse> {
        return when (operation.type) {
            CustomApprovalOperationType.APPROVE -> approveData(operation.approvals)
            CustomApprovalOperationType.UNAPPROVE -> unapproveData(operation.approvals)
            CustomApprovalOperationType.ACCEPT -> acceptDataApproval(operation.approvals)
            CustomApprovalOperationType.UNACCEPT -> unacceptDataApproval(operation.approvals)
        }
    }
    
    /**
     * Get approval workflow for data set
     */
    suspend fun getWorkflowForDataSet(dataSetId: String): ApiResponse<DataApprovalWorkflow> {
        return get("dataSets/$dataSetId/workflow")
    }
    
    /**
     * Get approval status summary
     */
    suspend fun getApprovalStatusSummary(
        dataSet: List<String> = emptyList(),
        period: List<String> = emptyList(),
        orgUnit: List<String> = emptyList()
    ): ApiResponse<DataApprovalStatusSummaryResponse> {
        
        val params = buildMap {
            if (dataSet.isNotEmpty()) put("ds", dataSet.joinToString(","))
            if (period.isNotEmpty()) put("pe", period.joinToString(","))
            if (orgUnit.isNotEmpty()) put("ou", orgUnit.joinToString(","))
        }
        
        return get("dataApprovals/summary", params)
    }
}

// ========================================
// ENUMS
// ========================================

enum class DataApprovalStatus { UNAPPROVABLE, UNAPPROVED_WAITING, UNAPPROVED_READY, APPROVED_HERE, APPROVED_ELSEWHERE, ACCEPTED_HERE, ACCEPTED_ELSEWHERE }
enum class DataApprovalAction { APPROVE, UNAPPROVE, ACCEPT, UNACCEPT }
enum class DataApprovalGroupBy { WORKFLOW, LEVEL, PERIOD, ORGUNIT, STATUS }
enum class CustomApprovalOperationType { APPROVE, UNAPPROVE, ACCEPT, UNACCEPT }
package com.everybytesystems.dataflow.core.api.approval

import kotlinx.serialization.Serializable

// ========================================
// DATA APPROVAL MODELS
// ========================================

@Serializable
data class DataApproval(
    val dataSet: String? = null,
    val workflow: String? = null,
    val period: String,
    val organisationUnit: String,
    val attributeOptionCombo: String? = null,
    val level: String? = null
)

@Serializable
data class DataApprovalRequest(
    val approvals: List<DataApproval>
)

@Serializable
data class DataApprovalResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val response: DataApprovalResponseDetails? = null
)

@Serializable
data class DataApprovalResponseDetails(
    val responseType: String? = null,
    val status: String? = null,
    val importCount: DataApprovalImportCount? = null,
    val conflicts: List<DataApprovalConflict> = emptyList()
)

@Serializable
data class DataApprovalImportCount(
    val imported: Int = 0,
    val updated: Int = 0,
    val ignored: Int = 0,
    val deleted: Int = 0
)

@Serializable
data class DataApprovalConflict(
    val `object`: String,
    val value: String,
    val errorCode: String? = null,
    val property: String? = null,
    val args: List<String> = emptyList()
)

// ========================================
// DATA APPROVAL STATUS MODELS
// ========================================

@Serializable
data class DataApprovalStatusResponse(
    val dataApprovalStateResponses: List<DataApprovalStateResponse> = emptyList()
)

@Serializable
data class DataApprovalStateResponse(
    val dataSet: String? = null,
    val workflow: String? = null,
    val period: String,
    val organisationUnit: String,
    val attributeOptionCombo: String? = null,
    val state: String,
    val mayApprove: Boolean = false,
    val mayUnapprove: Boolean = false,
    val mayAccept: Boolean = false,
    val mayUnaccept: Boolean = false,
    val permissions: DataApprovalPermissions? = null,
    val approvedBy: UserInfo? = null,
    val approvedAt: String? = null,
    val acceptedBy: UserInfo? = null,
    val acceptedAt: String? = null
)

@Serializable
data class DataApprovalPermissions(
    val mayApprove: Boolean = false,
    val mayUnapprove: Boolean = false,
    val mayAccept: Boolean = false,
    val mayUnaccept: Boolean = false,
    val mayReadData: Boolean = false
)

@Serializable
data class UserInfo(
    val id: String,
    val uid: String? = null,
    val username: String? = null,
    val firstName: String? = null,
    val surname: String? = null,
    val displayName: String? = null
)

// ========================================
// DATA APPROVAL WORKFLOW MODELS
// ========================================

@Serializable
data class DataApprovalWorkflow(
    val id: String? = null,
    val uid: String? = null,
    val code: String? = null,
    val name: String,
    val displayName: String? = null,
    val description: String? = null,
    val created: String? = null,
    val createdBy: UserInfo? = null,
    val lastUpdated: String? = null,
    val lastUpdatedBy: UserInfo? = null,
    val periodType: String,
    val categoryCombo: CategoryComboReference? = null,
    val dataApprovalLevels: List<DataApprovalLevel> = emptyList(),
    val dataSets: List<DataSetReference> = emptyList(),
    val access: Access? = null,
    val sharing: Sharing? = null,
    val userAccesses: List<UserAccess> = emptyList(),
    val userGroupAccesses: List<UserGroupAccess> = emptyList(),
    val attributeValues: List<AttributeValue> = emptyList(),
    val translations: List<Translation> = emptyList()
)

@Serializable
data class DataApprovalWorkflowsResponse(
    val dataApprovalWorkflows: List<DataApprovalWorkflow> = emptyList(),
    val pager: Pager? = null
)

@Serializable
data class CategoryComboReference(
    val id: String,
    val uid: String? = null,
    val code: String? = null,
    val name: String? = null,
    val displayName: String? = null
)

@Serializable
data class DataSetReference(
    val id: String,
    val uid: String? = null,
    val code: String? = null,
    val name: String? = null,
    val displayName: String? = null,
    val periodType: String? = null
)

// ========================================
// DATA APPROVAL LEVEL MODELS
// ========================================

@Serializable
data class DataApprovalLevel(
    val id: String? = null,
    val uid: String? = null,
    val code: String? = null,
    val name: String,
    val displayName: String? = null,
    val description: String? = null,
    val created: String? = null,
    val createdBy: UserInfo? = null,
    val lastUpdated: String? = null,
    val lastUpdatedBy: UserInfo? = null,
    val level: Int,
    val orgUnitLevel: Int? = null,
    val categoryOptionGroupSet: CategoryOptionGroupSetReference? = null,
    val access: Access? = null,
    val sharing: Sharing? = null,
    val userAccesses: List<UserAccess> = emptyList(),
    val userGroupAccesses: List<UserGroupAccess> = emptyList(),
    val attributeValues: List<AttributeValue> = emptyList(),
    val translations: List<Translation> = emptyList()
)

@Serializable
data class DataApprovalLevelsResponse(
    val dataApprovalLevels: List<DataApprovalLevel> = emptyList()
)

@Serializable
data class DataApprovalLevelsListResponse(
    val dataApprovalLevels: List<DataApprovalLevel> = emptyList(),
    val pager: Pager? = null
)

@Serializable
data class CategoryOptionGroupSetReference(
    val id: String,
    val uid: String? = null,
    val code: String? = null,
    val name: String? = null,
    val displayName: String? = null
)

// ========================================
// DATA APPROVAL AUDIT MODELS
// ========================================

@Serializable
data class DataApprovalAuditResponse(
    val dataApprovalAudits: List<DataApprovalAudit> = emptyList(),
    val pager: Pager? = null
)

@Serializable
data class DataApprovalAudit(
    val dataSet: String? = null,
    val workflow: String? = null,
    val period: String,
    val organisationUnit: String,
    val attributeOptionCombo: String? = null,
    val level: String,
    val action: String,
    val created: String? = null,
    val createdBy: UserInfo? = null,
    val modifiedBy: UserInfo? = null
)

// ========================================
// MULTIPLE DATA APPROVAL MODELS
// ========================================

@Serializable
data class DataApprovalStatusRequest(
    val dataSet: String? = null,
    val workflow: String? = null,
    val period: String,
    val organisationUnit: String,
    val attributeOptionCombo: String? = null
)

@Serializable
data class MultipleDataApprovalStatusRequest(
    val requests: List<DataApprovalStatusRequest>
)

@Serializable
data class MultipleDataApprovalStatusResponse(
    val responses: List<DataApprovalStateResponse> = emptyList()
)

@Serializable
data class MultipleDataApprovalRequest(
    val requests: List<DataApprovalRequest>
)

@Serializable
data class MultipleDataApprovalResponse(
    val responses: List<DataApprovalResponse> = emptyList(),
    val summary: DataApprovalSummary? = null
)

@Serializable
data class DataApprovalSummary(
    val total: Int = 0,
    val successful: Int = 0,
    val failed: Int = 0,
    val ignored: Int = 0
)

// ========================================
// DATA APPROVAL PERMISSIONS MODELS
// ========================================

@Serializable
data class DataApprovalPermissionsResponse(
    val dataApprovalPermissions: List<DataApprovalPermissionInfo> = emptyList()
)

@Serializable
data class DataApprovalPermissionInfo(
    val dataSet: String? = null,
    val workflow: String? = null,
    val period: String,
    val organisationUnit: String,
    val attributeOptionCombo: String? = null,
    val permissions: DataApprovalPermissions
)

// ========================================
// DATA APPROVAL ANALYTICS MODELS
// ========================================

@Serializable
data class DataApprovalAnalyticsResponse(
    val dataApprovalAnalytics: List<DataApprovalAnalytic> = emptyList(),
    val summary: DataApprovalAnalyticsSummary? = null
)

@Serializable
data class DataApprovalAnalytic(
    val workflow: String? = null,
    val workflowName: String? = null,
    val level: String? = null,
    val levelName: String? = null,
    val period: String,
    val periodName: String? = null,
    val organisationUnit: String,
    val organisationUnitName: String? = null,
    val status: String,
    val count: Int = 0,
    val percentage: Double = 0.0
)

@Serializable
data class DataApprovalAnalyticsSummary(
    val totalApprovals: Int = 0,
    val approvedCount: Int = 0,
    val unapprovedCount: Int = 0,
    val acceptedCount: Int = 0,
    val unacceptedCount: Int = 0,
    val approvalRate: Double = 0.0,
    val acceptanceRate: Double = 0.0
)

// ========================================
// DATA APPROVAL STATUS SUMMARY MODELS
// ========================================

@Serializable
data class DataApprovalStatusSummaryResponse(
    val summary: DataApprovalStatusSummary
)

@Serializable
data class DataApprovalStatusSummary(
    val total: Int = 0,
    val unapprovable: Int = 0,
    val unapprovedWaiting: Int = 0,
    val unapprovedReady: Int = 0,
    val approvedHere: Int = 0,
    val approvedElsewhere: Int = 0,
    val acceptedHere: Int = 0,
    val acceptedElsewhere: Int = 0,
    val byDataSet: Map<String, DataApprovalStatusSummary> = emptyMap(),
    val byPeriod: Map<String, DataApprovalStatusSummary> = emptyMap(),
    val byOrgUnit: Map<String, DataApprovalStatusSummary> = emptyMap()
)

// ========================================
// CUSTOM APPROVAL OPERATION MODELS
// ========================================

@Serializable
data class CustomApprovalOperation(
    val type: CustomApprovalOperationType,
    val approvals: List<DataApproval>,
    val options: CustomApprovalOptions? = null
)

@Serializable
data class CustomApprovalOptions(
    val skipNotifications: Boolean = false,
    val skipValidation: Boolean = false,
    val force: Boolean = false,
    val comment: String? = null
)

// CustomApprovalOperationType enum is defined in DataApprovalApi.kt to avoid duplication

// ========================================
// IMPORT/EXPORT MODELS
// ========================================

@Serializable
data class DataApprovalImportResponse(
    val responseType: String,
    val status: String,
    val importCount: DataApprovalImportCount,
    val conflicts: List<DataApprovalConflict> = emptyList(),
    val typeReports: List<DataApprovalTypeReport> = emptyList(),
    val stats: DataApprovalImportStats? = null,
    val reference: String? = null
)

@Serializable
data class DataApprovalTypeReport(
    val klass: String,
    val stats: DataApprovalImportCount,
    val objectReports: List<DataApprovalObjectReport> = emptyList()
)

@Serializable
data class DataApprovalObjectReport(
    val klass: String,
    val index: Int,
    val uid: String? = null,
    val errorReports: List<DataApprovalErrorReport> = emptyList()
)

@Serializable
data class DataApprovalErrorReport(
    val message: String,
    val mainKlass: String? = null,
    val errorCode: String? = null,
    val errorProperty: String? = null,
    val args: List<String> = emptyList()
)

@Serializable
data class DataApprovalImportStats(
    val created: Int = 0,
    val updated: Int = 0,
    val deleted: Int = 0,
    val ignored: Int = 0,
    val total: Int = 0
)

// ========================================
// SHARING MODELS
// ========================================

@Serializable
data class Access(
    val read: Boolean = false,
    val update: Boolean = false,
    val externalize: Boolean = false,
    val delete: Boolean = false,
    val write: Boolean = false,
    val manage: Boolean = false
)

@Serializable
data class Sharing(
    val owner: String? = null,
    val external: Boolean = false,
    val public: String? = null,
    val users: Map<String, UserAccess> = emptyMap(),
    val userGroups: Map<String, UserGroupAccess> = emptyMap()
)

@Serializable
data class UserAccess(
    val id: String,
    val access: String,
    val displayName: String? = null,
    val username: String? = null
)

@Serializable
data class UserGroupAccess(
    val id: String,
    val access: String,
    val displayName: String? = null
)

// ========================================
// ATTRIBUTE MODELS
// ========================================

@Serializable
data class AttributeValue(
    val attribute: AttributeReference,
    val value: String
)

@Serializable
data class AttributeReference(
    val id: String,
    val uid: String? = null,
    val code: String? = null,
    val name: String? = null,
    val displayName: String? = null,
    val valueType: String? = null
)

// ========================================
// TRANSLATION MODELS
// ========================================

@Serializable
data class Translation(
    val property: String,
    val locale: String,
    val value: String
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
// WORKFLOW CONFIGURATION MODELS
// ========================================

@Serializable
data class DataApprovalWorkflowConfiguration(
    val workflow: DataApprovalWorkflow,
    val levels: List<DataApprovalLevel>,
    val dataSets: List<DataSetReference>,
    val permissions: List<DataApprovalWorkflowPermission> = emptyList()
)

@Serializable
data class DataApprovalWorkflowPermission(
    val level: String,
    val userRole: String,
    val organisationUnit: String? = null,
    val categoryOptionGroupSet: String? = null,
    val permissions: List<String> = emptyList()
)

// ========================================
// APPROVAL NOTIFICATION MODELS
// ========================================

@Serializable
data class DataApprovalNotification(
    val id: String? = null,
    val name: String,
    val subject: String,
    val message: String,
    val workflow: String,
    val level: String? = null,
    val action: String, // APPROVE, UNAPPROVE, ACCEPT, UNACCEPT
    val recipients: List<DataApprovalNotificationRecipient> = emptyList(),
    val enabled: Boolean = true
)

@Serializable
data class DataApprovalNotificationRecipient(
    val type: String, // USER, USER_GROUP, ORGANISATION_UNIT_CONTACT
    val id: String,
    val name: String? = null
)

// ========================================
// APPROVAL HISTORY MODELS
// ========================================

@Serializable
data class DataApprovalHistory(
    val dataSet: String? = null,
    val workflow: String? = null,
    val period: String,
    val organisationUnit: String,
    val attributeOptionCombo: String? = null,
    val history: List<DataApprovalHistoryEntry> = emptyList()
)

@Serializable
data class DataApprovalHistoryEntry(
    val level: String,
    val action: String,
    val timestamp: String,
    val user: UserInfo,
    val comment: String? = null
)

// ========================================
// BULK OPERATION MODELS
// ========================================

@Serializable
data class DataApprovalBulkOperation(
    val operation: DataApprovalBulkOperationType,
    val criteria: DataApprovalBulkCriteria,
    val options: DataApprovalBulkOptions? = null
)

@Serializable
data class DataApprovalBulkCriteria(
    val dataSet: List<String> = emptyList(),
    val workflow: List<String> = emptyList(),
    val period: List<String> = emptyList(),
    val orgUnit: List<String> = emptyList(),
    val attributeOptionCombo: List<String> = emptyList(),
    val level: List<String> = emptyList(),
    val status: List<String> = emptyList()
)

@Serializable
data class DataApprovalBulkOptions(
    val skipNotifications: Boolean = false,
    val skipValidation: Boolean = false,
    val force: Boolean = false,
    val async: Boolean = false,
    val dryRun: Boolean = false
)

enum class DataApprovalBulkOperationType {
    APPROVE, UNAPPROVE, ACCEPT, UNACCEPT, RESET
}

@Serializable
data class DataApprovalBulkOperationResponse(
    val status: String,
    val message: String? = null,
    val processed: Int = 0,
    val successful: Int = 0,
    val failed: Int = 0,
    val errors: List<DataApprovalBulkOperationError> = emptyList(),
    val jobId: String? = null
)

@Serializable
data class DataApprovalBulkOperationError(
    val dataSet: String? = null,
    val workflow: String? = null,
    val period: String,
    val orgUnit: String,
    val attributeOptionCombo: String? = null,
    val message: String,
    val errorCode: String? = null
)
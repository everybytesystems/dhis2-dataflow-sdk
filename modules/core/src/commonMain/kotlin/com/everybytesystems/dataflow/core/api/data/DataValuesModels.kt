package com.everybytesystems.dataflow.core.api.data

import kotlinx.serialization.Serializable

// ========================================
// DATA VALUE MODELS
// ========================================

@Serializable
data class DataValue(
    val dataElement: String,
    val period: String,
    val orgUnit: String,
    val categoryOptionCombo: String? = null,
    val attributeOptionCombo: String? = null,
    val value: String? = null,
    val storedBy: String? = null,
    val created: String? = null,
    val lastUpdated: String? = null,
    val comment: String? = null,
    val followUp: Boolean = false,
    val deleted: Boolean = false
)

@Serializable
data class DataValueResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val response: DataValueResponseDetails? = null
)

@Serializable
data class DataValueResponseDetails(
    val responseType: String? = null,
    val status: String? = null,
    val importCount: DataValueImportCount? = null
)

@Serializable
data class DataValueImportCount(
    val imported: Int = 0,
    val updated: Int = 0,
    val ignored: Int = 0,
    val deleted: Int = 0
)

// ========================================
// DATA VALUE SET MODELS
// ========================================

@Serializable
data class DataValueSet(
    val dataSet: String? = null,
    val completeDate: String? = null,
    val period: String? = null,
    val orgUnit: String? = null,
    val attributeOptionCombo: String? = null,
    val dataValues: List<DataValue> = emptyList()
)

@Serializable
data class DataValueImportResponse(
    val responseType: String,
    val status: String,
    val description: String? = null,
    val importCount: DataValueImportCount,
    val conflicts: List<DataValueConflict> = emptyList(),
    val dataSetComplete: String? = null,
    val reference: String? = null
)

@Serializable
data class DataValueConflict(
    val `object`: String,
    val value: String,
    val errorCode: String? = null,
    val property: String? = null,
    val args: List<String> = emptyList()
)

// ========================================
// DATA SET COMPLETION MODELS
// ========================================

@Serializable
data class DataSetCompletion(
    val dataSet: String,
    val period: String,
    val organisationUnit: String,
    val attributeOptionCombo: String? = null,
    val date: String? = null,
    val storedBy: String? = null,
    val completed: Boolean = false
)

@Serializable
data class DataSetCompletionResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null
)

@Serializable
data class DataSetCompletionRequest(
    val dataSet: String,
    val period: String,
    val orgUnit: String,
    val attributeOptionCombo: String? = null,
    val completedBy: String? = null,
    val completedDate: String? = null
)

@Serializable
data class DataSetCompletionBulkRequest(
    val completeDataSetRegistrations: List<DataSetCompletionRequest>
)

// ========================================
// DATA VALUE AUDIT MODELS
// ========================================

@Serializable
data class DataValueAuditResponse(
    val dataValueAudits: List<DataValueAudit> = emptyList(),
    val pager: Pager? = null
)

@Serializable
data class DataValueAudit(
    val dataElement: String,
    val period: String,
    val organisationUnit: String,
    val categoryOptionCombo: String? = null,
    val attributeOptionCombo: String? = null,
    val value: String? = null,
    val modifiedBy: String? = null,
    val created: String? = null,
    val auditType: String,
    val comment: String? = null
)

// ========================================
// ASYNC JOB MODELS
// ========================================

@Serializable
data class AsyncJobResponse(
    val id: String,
    val level: String,
    val category: String,
    val time: String,
    val message: String,
    val completed: Boolean = false,
    val uid: String? = null
)

// ========================================
// BULK OPERATION MODELS
// ========================================

@Serializable
data class BulkDataValueOperation(
    val operation: BulkDataValueOperationType,
    val dataValues: List<DataValueIdentifier>
)

@Serializable
data class DataValueIdentifier(
    val dataElement: String,
    val period: String,
    val orgUnit: String,
    val categoryOptionCombo: String? = null,
    val attributeOptionCombo: String? = null,
    val value: String? = null
)

// ========================================
// FOLLOW-UP MODELS
// ========================================

@Serializable
data class FollowUpDataValue(
    val dataElement: String,
    val dataElementName: String? = null,
    val period: String,
    val periodName: String? = null,
    val orgUnit: String,
    val orgUnitName: String? = null,
    val categoryOptionCombo: String? = null,
    val categoryOptionComboName: String? = null,
    val attributeOptionCombo: String? = null,
    val attributeOptionComboName: String? = null,
    val value: String? = null,
    val comment: String? = null,
    val followUp: Boolean = true,
    val created: String? = null,
    val lastUpdated: String? = null,
    val storedBy: String? = null
)

@Serializable
data class FollowUpDataValuesResponse(
    val followUpDataValues: List<FollowUpDataValue> = emptyList(),
    val pager: Pager? = null
)

// ========================================
// DATA VALIDATION MODELS
// ========================================

@Serializable
data class DataValidationResult(
    val dataElement: String,
    val period: String,
    val orgUnit: String,
    val categoryOptionCombo: String? = null,
    val attributeOptionCombo: String? = null,
    val value: String? = null,
    val validationRules: List<ValidationRuleViolation> = emptyList(),
    val outliers: List<OutlierValue> = emptyList(),
    val missingValues: List<MissingValue> = emptyList()
)

@Serializable
data class ValidationRuleViolation(
    val validationRule: String,
    val validationRuleName: String? = null,
    val leftSideValue: Double? = null,
    val rightSideValue: Double? = null,
    val operator: String? = null,
    val importance: String? = null
)

@Serializable
data class OutlierValue(
    val value: Double,
    val mean: Double,
    val standardDeviation: Double,
    val zScore: Double,
    val lowerBound: Double,
    val upperBound: Double,
    val isOutlier: Boolean
)

@Serializable
data class MissingValue(
    val dataElement: String,
    val dataElementName: String? = null,
    val period: String,
    val orgUnit: String,
    val categoryOptionCombo: String? = null,
    val attributeOptionCombo: String? = null,
    val mandatory: Boolean = false
)

// ========================================
// DATA QUALITY MODELS
// ========================================

@Serializable
data class DataQualityReport(
    val dataSet: String? = null,
    val period: String? = null,
    val orgUnit: String? = null,
    val completeness: DataCompletenessReport,
    val timeliness: DataTimelinessReport,
    val consistency: DataConsistencyReport,
    val accuracy: DataAccuracyReport
)

@Serializable
data class DataCompletenessReport(
    val totalDataSets: Int = 0,
    val completeDataSets: Int = 0,
    val incompleteDataSets: Int = 0,
    val completenessPercentage: Double = 0.0,
    val details: List<DataCompletenessDetail> = emptyList()
)

@Serializable
data class DataCompletenessDetail(
    val dataSet: String,
    val dataSetName: String? = null,
    val orgUnit: String,
    val orgUnitName: String? = null,
    val period: String,
    val completed: Boolean,
    val completedDate: String? = null,
    val completedBy: String? = null
)

@Serializable
data class DataTimelinessReport(
    val onTimeSubmissions: Int = 0,
    val lateSubmissions: Int = 0,
    val timelinessPercentage: Double = 0.0,
    val averageDelayDays: Double = 0.0,
    val details: List<DataTimelinessDetail> = emptyList()
)

@Serializable
data class DataTimelinessDetail(
    val dataSet: String,
    val dataSetName: String? = null,
    val orgUnit: String,
    val orgUnitName: String? = null,
    val period: String,
    val submissionDate: String? = null,
    val deadlineDate: String? = null,
    val delayDays: Int = 0,
    val onTime: Boolean
)

@Serializable
data class DataConsistencyReport(
    val consistentValues: Int = 0,
    val inconsistentValues: Int = 0,
    val consistencyPercentage: Double = 0.0,
    val details: List<DataConsistencyDetail> = emptyList()
)

@Serializable
data class DataConsistencyDetail(
    val dataElement: String,
    val dataElementName: String? = null,
    val period: String,
    val orgUnit: String,
    val orgUnitName: String? = null,
    val value: String? = null,
    val expectedRange: String? = null,
    val consistent: Boolean,
    val inconsistencyType: String? = null
)

@Serializable
data class DataAccuracyReport(
    val accurateValues: Int = 0,
    val inaccurateValues: Int = 0,
    val accuracyPercentage: Double = 0.0,
    val details: List<DataAccuracyDetail> = emptyList()
)

@Serializable
data class DataAccuracyDetail(
    val dataElement: String,
    val dataElementName: String? = null,
    val period: String,
    val orgUnit: String,
    val orgUnitName: String? = null,
    val value: String? = null,
    val accurate: Boolean,
    val inaccuracyType: String? = null,
    val correctedValue: String? = null
)

// ========================================
// DATA EXCHANGE MODELS
// ========================================

@Serializable
data class DataExchangeRequest(
    val source: DataExchangeSource,
    val target: DataExchangeTarget,
    val mapping: DataExchangeMapping,
    val filters: DataExchangeFilters? = null,
    val options: DataExchangeOptions? = null
)

@Serializable
data class DataExchangeSource(
    val type: String, // "dhis2", "csv", "json", "xml"
    val url: String? = null,
    val username: String? = null,
    val password: String? = null,
    val apiKey: String? = null,
    val query: Map<String, String> = emptyMap()
)

@Serializable
data class DataExchangeTarget(
    val type: String, // "dhis2", "csv", "json", "xml"
    val url: String? = null,
    val username: String? = null,
    val password: String? = null,
    val apiKey: String? = null,
    val options: Map<String, String> = emptyMap()
)

@Serializable
data class DataExchangeMapping(
    val dataElementMappings: List<DataElementMapping> = emptyList(),
    val orgUnitMappings: List<OrgUnitMapping> = emptyList(),
    val periodMappings: List<PeriodMapping> = emptyList(),
    val categoryMappings: List<CategoryMapping> = emptyList()
)

@Serializable
data class DataElementMapping(
    val sourceId: String,
    val targetId: String,
    val transformation: String? = null
)

@Serializable
data class OrgUnitMapping(
    val sourceId: String,
    val targetId: String,
    val includeChildren: Boolean = false
)

@Serializable
data class PeriodMapping(
    val sourcePeriod: String,
    val targetPeriod: String,
    val aggregation: String? = null
)

@Serializable
data class CategoryMapping(
    val sourceCategory: String,
    val targetCategory: String,
    val optionMappings: List<CategoryOptionMapping> = emptyList()
)

@Serializable
data class CategoryOptionMapping(
    val sourceOption: String,
    val targetOption: String
)

@Serializable
data class DataExchangeFilters(
    val startDate: String? = null,
    val endDate: String? = null,
    val orgUnits: List<String> = emptyList(),
    val dataElements: List<String> = emptyList(),
    val periods: List<String> = emptyList()
)

@Serializable
data class DataExchangeOptions(
    val dryRun: Boolean = false,
    val skipValidation: Boolean = false,
    val async: Boolean = false,
    val notifyOnCompletion: Boolean = false,
    val compressionType: String? = null,
    val format: String = "json"
)

@Serializable
data class DataExchangeResponse(
    val status: String,
    val message: String? = null,
    val exchangeId: String? = null,
    val summary: DataExchangeSummary? = null,
    val errors: List<DataExchangeError> = emptyList()
)

@Serializable
data class DataExchangeSummary(
    val totalRecords: Int = 0,
    val processedRecords: Int = 0,
    val successfulRecords: Int = 0,
    val failedRecords: Int = 0,
    val skippedRecords: Int = 0,
    val startTime: String? = null,
    val endTime: String? = null,
    val duration: String? = null
)

@Serializable
data class DataExchangeError(
    val record: String? = null,
    val field: String? = null,
    val message: String,
    val errorCode: String? = null
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
// STATISTICS MODELS
// ========================================

@Serializable
data class DataStatistics(
    val totalDataValues: Int = 0,
    val totalDataSets: Int = 0,
    val totalOrgUnits: Int = 0,
    val totalPeriods: Int = 0,
    val completenessRate: Double = 0.0,
    val timelinessRate: Double = 0.0,
    val accuracyRate: Double = 0.0,
    val lastUpdated: String? = null
)

@Serializable
data class DataValueStatistics(
    val dataElement: String,
    val dataElementName: String? = null,
    val totalValues: Int = 0,
    val nonEmptyValues: Int = 0,
    val emptyValues: Int = 0,
    val followUpValues: Int = 0,
    val minValue: Double? = null,
    val maxValue: Double? = null,
    val averageValue: Double? = null,
    val standardDeviation: Double? = null,
    val lastUpdated: String? = null
)

@Serializable
data class DataSetStatistics(
    val dataSet: String,
    val dataSetName: String? = null,
    val totalRegistrations: Int = 0,
    val completeRegistrations: Int = 0,
    val incompleteRegistrations: Int = 0,
    val onTimeRegistrations: Int = 0,
    val lateRegistrations: Int = 0,
    val completenessRate: Double = 0.0,
    val timelinessRate: Double = 0.0,
    val lastUpdated: String? = null
)
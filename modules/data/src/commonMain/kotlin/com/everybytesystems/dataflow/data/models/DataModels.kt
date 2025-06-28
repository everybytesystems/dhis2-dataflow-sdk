package com.everybytesystems.dataflow.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DHIS2 Data models for DataValueSets, Analytics, and Tracker
 */

// DataValueSet models
@Serializable
data class DataValueSet(
    val dataSet: String? = null,
    val completeDate: String? = null,
    val period: String,
    val orgUnit: String,
    val attributeOptionCombo: String? = null,
    val dataValues: List<DataValue> = emptyList()
)

@Serializable
data class DataValue(
    val dataElement: String,
    val period: String? = null,
    val orgUnit: String? = null,
    val categoryOptionCombo: String? = null,
    val attributeOptionCombo: String? = null,
    val value: String,
    val storedBy: String? = null,
    val created: String? = null,
    val lastUpdated: String? = null,
    val comment: String? = null,
    val followUp: Boolean? = null
)

@Serializable
data class DataValueSetResponse(
    val httpStatus: String? = null,
    val httpStatusCode: Int? = null,
    val status: String? = null,
    val message: String? = null,
    val response: DataValueSetImportSummary? = null
)

@Serializable
data class DataValueSetImportSummary(
    val responseType: String? = null,
    val status: String? = null,
    val description: String? = null,
    val importCount: ImportCount? = null,
    val conflicts: List<Conflict> = emptyList(),
    val dataSetComplete: String? = null
)

@Serializable
data class ImportCount(
    val imported: Int = 0,
    val updated: Int = 0,
    val ignored: Int = 0,
    val deleted: Int = 0
)

@Serializable
data class Conflict(
    @SerialName("object") val objectRef: String? = null,
    val value: String? = null,
    val errorCode: String? = null
)

@Serializable
data class ImportSummary(
    val status: String,
    val description: String? = null,
    val importCount: ImportCount? = null,
    val conflicts: List<Conflict> = emptyList(),
    val reference: String? = null,
    val href: String? = null
)

@Serializable
data class DataSetCompletion(
    val dataSet: String,
    val period: String,
    val organisationUnit: String,
    val attributeOptionCombo: String? = null,
    val date: String? = null,
    val storedBy: String? = null,
    val completed: Boolean = true
)

// Analytics models
@Serializable
data class AnalyticsRequest(
    val dimensions: List<String> = emptyList(),
    val filters: List<String> = emptyList(),
    val aggregationType: String? = null,
    val measureCriteria: String? = null,
    val preAggregationMeasureCriteria: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val skipMeta: Boolean = false,
    val skipData: Boolean = false,
    val skipRounding: Boolean = false,
    val completedOnly: Boolean = false,
    val hierarchyMeta: Boolean = false,
    val ignoreLimit: Boolean = false,
    val tableLayout: Boolean = false,
    val hideEmptyRows: Boolean = false,
    val hideEmptyColumns: Boolean = false,
    val showHierarchy: Boolean = false,
    val includeNumDen: Boolean = false,
    val includeMetadataDetails: Boolean = false,
    val displayProperty: String? = null,
    val outputIdScheme: String? = null,
    val inputIdScheme: String? = null,
    val approvalLevel: String? = null,
    val relativePeriodDate: String? = null,
    val userOrgUnit: String? = null,
    val columns: List<String> = emptyList(),
    val rows: List<String> = emptyList()
)

@Serializable
data class AnalyticsResponse(
    val headers: List<Header> = emptyList(),
    val metaData: MetaData? = null,
    val rows: List<List<String>> = emptyList(),
    val width: Int = 0,
    val height: Int = 0,
    val headerWidth: Int = 0
)

@Serializable
data class Header(
    val name: String,
    val column: String,
    val valueType: String,
    val type: String,
    val hidden: Boolean = false,
    val meta: Boolean = false
)

@Serializable
data class MetaData(
    val items: Map<String, MetaDataItem> = emptyMap(),
    val dimensions: Map<String, List<String>> = emptyMap(),
    val ouHierarchy: Map<String, List<String>> = emptyMap(),
    val co: List<String> = emptyList(),
    val dx: List<String> = emptyList(),
    val pe: List<String> = emptyList(),
    val ou: List<String> = emptyList()
)

@Serializable
data class MetaDataItem(
    val uid: String? = null,
    val code: String? = null,
    val name: String,
    val dimensionItemType: String? = null,
    val valueType: String? = null,
    val totalAggregationType: String? = null,
    val minDate: String? = null,
    val maxDate: String? = null
)

// Tracker models
@Serializable
data class TrackedEntityInstance(
    val trackedEntityInstance: String? = null,
    val trackedEntityType: String,
    val orgUnit: String,
    val attributes: List<TrackedEntityAttributeValue> = emptyList(),
    val enrollments: List<Enrollment> = emptyList(),
    val relationships: List<Relationship> = emptyList(),
    val programOwners: List<ProgramOwner> = emptyList(),
    val created: String? = null,
    val createdAtClient: String? = null,
    val lastUpdated: String? = null,
    val lastUpdatedAtClient: String? = null,
    val inactive: Boolean = false,
    val deleted: Boolean = false,
    val featureType: String? = null,
    val coordinates: String? = null,
    val storedBy: String? = null,
    val createdByUserInfo: UserInfo? = null,
    val lastUpdatedByUserInfo: UserInfo? = null
)

@Serializable
data class TrackedEntityAttributeValue(
    val attribute: String,
    val value: String,
    val displayName: String? = null,
    val created: String? = null,
    val lastUpdated: String? = null,
    val storedBy: String? = null
)

@Serializable
data class Enrollment(
    val enrollment: String? = null,
    val trackedEntityInstance: String? = null,
    val trackedEntityType: String? = null,
    val program: String,
    val status: EnrollmentStatus = EnrollmentStatus.ACTIVE,
    val orgUnit: String,
    val orgUnitName: String? = null,
    val enrollmentDate: String,
    val incidentDate: String? = null,
    val followup: Boolean = false,
    val completedDate: String? = null,
    val completedBy: String? = null,
    val attributes: List<TrackedEntityAttributeValue> = emptyList(),
    val events: List<Event> = emptyList(),
    val relationships: List<Relationship> = emptyList(),
    val notes: List<Note> = emptyList(),
    val created: String? = null,
    val createdAtClient: String? = null,
    val lastUpdated: String? = null,
    val lastUpdatedAtClient: String? = null,
    val createdByUserInfo: UserInfo? = null,
    val lastUpdatedByUserInfo: UserInfo? = null,
    val geometry: Geometry? = null,
    val deleted: Boolean = false,
    val storedBy: String? = null
)

@Serializable
data class Event(
    val event: String? = null,
    val status: EventStatus = EventStatus.ACTIVE,
    val program: String,
    val programStage: String,
    val enrollment: String? = null,
    val enrollmentStatus: EnrollmentStatus? = null,
    val orgUnit: String,
    val orgUnitName: String? = null,
    val trackedEntityInstance: String? = null,
    val relationships: List<Relationship> = emptyList(),
    val eventDate: String? = null,
    val dueDate: String? = null,
    val completedDate: String? = null,
    val completedBy: String? = null,
    val storedBy: String? = null,
    val dataValues: List<EventDataValue> = emptyList(),
    val notes: List<Note> = emptyList(),
    val followup: Boolean = false,
    val deleted: Boolean = false,
    val created: String? = null,
    val createdAtClient: String? = null,
    val lastUpdated: String? = null,
    val lastUpdatedAtClient: String? = null,
    val createdByUserInfo: UserInfo? = null,
    val lastUpdatedByUserInfo: UserInfo? = null,
    val assignedUser: String? = null,
    val assignedUserDisplayName: String? = null,
    val attributeOptionCombo: String? = null,
    val attributeCategoryOptions: String? = null,
    val geometry: Geometry? = null
)

@Serializable
data class EventDataValue(
    val dataElement: String,
    val value: String,
    val providedElsewhere: Boolean = false,
    val created: String? = null,
    val lastUpdated: String? = null,
    val storedBy: String? = null
)

@Serializable
data class Relationship(
    val relationship: String? = null,
    val relationshipType: String,
    val from: RelationshipItem,
    val to: RelationshipItem,
    val created: String? = null,
    val lastUpdated: String? = null
)

@Serializable
data class RelationshipItem(
    val trackedEntityInstance: TrackedEntityInstanceRef? = null,
    val enrollment: EnrollmentRef? = null,
    val event: EventRef? = null
)

@Serializable
data class TrackedEntityInstanceRef(
    val trackedEntityInstance: String
)

@Serializable
data class EnrollmentRef(
    val enrollment: String
)

@Serializable
data class EventRef(
    val event: String
)

@Serializable
data class Note(
    val note: String? = null,
    val value: String,
    val storedDate: String? = null,
    val storedBy: String? = null
)

@Serializable
data class ProgramOwner(
    val ownerOrgUnit: String,
    val program: String,
    val trackedEntityInstance: String
)

@Serializable
data class UserInfo(
    val uid: String,
    val username: String? = null,
    val firstName: String? = null,
    val surname: String? = null
)

@Serializable
data class Geometry(
    val type: String,
    val coordinates: List<Double>
)

// Enums
@Serializable
enum class EnrollmentStatus {
    ACTIVE, COMPLETED, CANCELLED
}

@Serializable
enum class EventStatus {
    ACTIVE, COMPLETED, VISITED, SCHEDULE, OVERDUE, SKIPPED
}

// Response wrappers
@Serializable
data class TrackerResponse<T>(
    val httpStatus: String? = null,
    val httpStatusCode: Int? = null,
    val status: String? = null,
    val message: String? = null,
    val response: T? = null
)

@Serializable
data class TrackerImportSummary(
    val responseType: String? = null,
    val status: String? = null,
    val bundleReport: BundleReport? = null,
    val stats: TrackerStats? = null
)

@Serializable
data class BundleReport(
    val status: String? = null,
    val stats: TrackerStats? = null,
    val typeReportMap: Map<String, List<TrackerObjectReport>> = emptyMap()
)

@Serializable
data class TrackerStats(
    val created: Int = 0,
    val updated: Int = 0,
    val deleted: Int = 0,
    val ignored: Int = 0,
    val total: Int = 0
)

@Serializable
data class TrackerObjectReport(
    val trackerType: String? = null,
    val uid: String? = null,
    val index: Int? = null,
    val errorReports: List<TrackerErrorReport> = emptyList(),
    val warningReports: List<TrackerWarningReport> = emptyList()
)

@Serializable
data class TrackerErrorReport(
    val message: String,
    val errorCode: String? = null,
    val trackerType: String? = null,
    val uid: String? = null,
    val args: List<String> = emptyList()
)

@Serializable
data class TrackerWarningReport(
    val message: String,
    val warningCode: String? = null,
    val trackerType: String? = null,
    val uid: String? = null,
    val args: List<String> = emptyList()
)
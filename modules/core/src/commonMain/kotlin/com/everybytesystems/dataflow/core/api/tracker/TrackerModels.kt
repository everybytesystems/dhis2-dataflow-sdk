package com.everybytesystems.dataflow.core.api.tracker

import kotlinx.serialization.Serializable

// ========================================
// TRACKED ENTITY MODELS
// ========================================

@Serializable
data class TrackedEntity(
    val trackedEntity: String? = null,
    val trackedEntityType: String,
    val createdAt: String? = null,
    val createdAtClient: String? = null,
    val updatedAt: String? = null,
    val updatedAtClient: String? = null,
    val orgUnit: String,
    val inactive: Boolean = false,
    val deleted: Boolean = false,
    val potentialDuplicate: Boolean = false,
    val geometry: Geometry? = null,
    val storedBy: String? = null,
    val createdBy: User? = null,
    val updatedBy: User? = null,
    val attributes: List<Attribute> = emptyList(),
    val enrollments: List<Enrollment> = emptyList(),
    val relationships: List<Relationship> = emptyList(),
    val programOwners: List<ProgramOwner> = emptyList()
)

@Serializable
data class TrackedEntitiesResponse(
    val trackedEntities: List<TrackedEntity> = emptyList(),
    val pager: Pager? = null
)

@Serializable
data class Attribute(
    val attribute: String,
    val code: String? = null,
    val displayName: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val storedBy: String? = null,
    val value: String? = null,
    val valueType: String? = null
)

@Serializable
data class ProgramOwner(
    val ownerOrgUnit: String,
    val program: String,
    val trackedEntity: String
)

// ========================================
// ENROLLMENT MODELS
// ========================================

@Serializable
data class Enrollment(
    val enrollment: String? = null,
    val trackedEntity: String? = null,
    val program: String,
    val status: String,
    val orgUnit: String,
    val orgUnitName: String? = null,
    val enrolledAt: String? = null,
    val occurredAt: String? = null,
    val followUp: Boolean = false,
    val deleted: Boolean = false,
    val createdAt: String? = null,
    val createdAtClient: String? = null,
    val updatedAt: String? = null,
    val updatedAtClient: String? = null,
    val completedAt: String? = null,
    val completedBy: String? = null,
    val storedBy: String? = null,
    val createdBy: User? = null,
    val updatedBy: User? = null,
    val geometry: Geometry? = null,
    val attributes: List<Attribute> = emptyList(),
    val events: List<Event> = emptyList(),
    val relationships: List<Relationship> = emptyList(),
    val notes: List<Note> = emptyList()
)

@Serializable
data class EnrollmentsResponse(
    val enrollments: List<Enrollment> = emptyList(),
    val pager: Pager? = null
)

@Serializable
data class Note(
    val note: String? = null,
    val storedDate: String? = null,
    val storedBy: String? = null,
    val value: String
)

// ========================================
// EVENT MODELS
// ========================================

@Serializable
data class Event(
    val event: String? = null,
    val status: String,
    val program: String,
    val programStage: String,
    val enrollment: String? = null,
    val enrollmentStatus: String? = null,
    val orgUnit: String,
    val orgUnitName: String? = null,
    val trackedEntity: String? = null,
    val relationships: List<Relationship> = emptyList(),
    val scheduledAt: String? = null,
    val occurredAt: String? = null,
    val createdAt: String? = null,
    val createdAtClient: String? = null,
    val updatedAt: String? = null,
    val updatedAtClient: String? = null,
    val completedAt: String? = null,
    val completedBy: String? = null,
    val storedBy: String? = null,
    val createdBy: User? = null,
    val updatedBy: User? = null,
    val attributeOptionCombo: String? = null,
    val attributeCategoryOptions: String? = null,
    val assignedUser: User? = null,
    val geometry: Geometry? = null,
    val deleted: Boolean = false,
    val dataValues: List<DataValue> = emptyList(),
    val notes: List<Note> = emptyList()
)

@Serializable
data class EventsResponse(
    val events: List<Event> = emptyList(),
    val pager: Pager? = null
)

@Serializable
data class DataValue(
    val dataElement: String,
    val value: String? = null,
    val providedElsewhere: Boolean = false,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val storedBy: String? = null,
    val createdBy: User? = null,
    val updatedBy: User? = null
)

// ========================================
// RELATIONSHIP MODELS
// ========================================

@Serializable
data class Relationship(
    val relationship: String? = null,
    val relationshipType: String,
    val relationshipName: String? = null,
    val bidirectional: Boolean = false,
    val from: RelationshipItem,
    val to: RelationshipItem,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val createdBy: User? = null,
    val updatedBy: User? = null
)

@Serializable
data class RelationshipsResponse(
    val relationships: List<Relationship> = emptyList(),
    val pager: Pager? = null
)

@Serializable
data class RelationshipItem(
    val trackedEntity: TrackedEntityRef? = null,
    val enrollment: EnrollmentRef? = null,
    val event: EventRef? = null
)

@Serializable
data class TrackedEntityRef(
    val trackedEntity: String
)

@Serializable
data class EnrollmentRef(
    val enrollment: String
)

@Serializable
data class EventRef(
    val event: String
)

// ========================================
// IMPORT/EXPORT MODELS
// ========================================

@Serializable
data class TrackerImportPayload(
    val trackedEntities: List<TrackedEntity> = emptyList(),
    val enrollments: List<Enrollment> = emptyList(),
    val events: List<Event> = emptyList(),
    val relationships: List<Relationship> = emptyList()
)

@Serializable
data class TrackerExportResponse(
    val trackedEntities: List<TrackedEntity> = emptyList(),
    val enrollments: List<Enrollment> = emptyList(),
    val events: List<Event> = emptyList(),
    val relationships: List<Relationship> = emptyList()
)

@Serializable
data class TrackerImportResponse(
    val status: String,
    val validationReport: ValidationReport? = null,
    val stats: TrackerImportStats,
    val bundleReport: BundleReport? = null,
    val message: String? = null
)

@Serializable
data class ValidationReport(
    val errorReports: List<TrackerErrorReport> = emptyList(),
    val warningReports: List<TrackerWarningReport> = emptyList(),
    val perf: PerformanceReport? = null
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

@Serializable
data class PerformanceReport(
    val programRuleEvaluation: String? = null,
    val validation: String? = null,
    val commit: String? = null,
    val total: String? = null
)

@Serializable
data class TrackerImportStats(
    val created: Int = 0,
    val updated: Int = 0,
    val deleted: Int = 0,
    val ignored: Int = 0,
    val total: Int = 0
)

@Serializable
data class BundleReport(
    val status: String,
    val stats: TrackerImportStats,
    val typeReportMap: Map<String, TypeReport> = emptyMap()
)

@Serializable
data class TypeReport(
    val trackerType: String,
    val stats: TrackerImportStats,
    val objectReports: List<ObjectReport> = emptyList()
)

@Serializable
data class ObjectReport(
    val trackerType: String,
    val uid: String,
    val index: Int,
    val errorReports: List<TrackerErrorReport> = emptyList(),
    val warningReports: List<TrackerWarningReport> = emptyList()
)

// ========================================
// WORKING LIST MODELS (2.37+)
// ========================================

@Serializable
data class WorkingList(
    val id: String? = null,
    val name: String,
    val description: String? = null,
    val program: String,
    val programStage: String? = null,
    val created: String? = null,
    val lastUpdated: String? = null,
    val filters: List<WorkingListFilter> = emptyList(),
    val columnOrder: List<String> = emptyList(),
    val displayColumnOrder: List<String> = emptyList()
)

@Serializable
data class WorkingListsResponse(
    val workingLists: List<WorkingList> = emptyList(),
    val pager: Pager? = null
)

@Serializable
data class WorkingListFilter(
    val property: String,
    val operator: String,
    val value: String? = null,
    val values: List<String> = emptyList()
)

// ========================================
// POTENTIAL DUPLICATES MODELS (2.39+)
// ========================================

@Serializable
data class PotentialDuplicate(
    val id: String? = null,
    val trackedEntity: String,
    val duplicate: String,
    val status: String,
    val created: String? = null,
    val lastUpdated: String? = null,
    val createdBy: User? = null,
    val lastUpdatedBy: User? = null
)

@Serializable
data class PotentialDuplicatesResponse(
    val potentialDuplicates: List<PotentialDuplicate> = emptyList(),
    val pager: Pager? = null
)

// ========================================
// OWNERSHIP MODELS (2.38+)
// ========================================

@Serializable
data class OwnershipResponse(
    val trackedEntity: String,
    val program: String,
    val orgUnit: String,
    val ownerOrgUnit: String,
    val hasAccess: Boolean
)

// ========================================
// COMMON MODELS
// ========================================

@Serializable
data class User(
    val uid: String? = null,
    val username: String? = null,
    val firstName: String? = null,
    val surname: String? = null,
    val displayName: String? = null
)

@Serializable
data class Geometry(
    val type: String,
    val coordinates: List<Double> = emptyList()
)

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
// FILTER MODELS
// ========================================

@Serializable
data class TrackerFilter(
    val property: String,
    val operator: FilterOperator,
    val value: String? = null,
    val values: List<String> = emptyList()
)

enum class FilterOperator {
    EQ, NE, GT, GE, LT, LE, LIKE, ILIKE, IN, NIN, SW, EW, EXISTS
}

// ========================================
// QUERY MODELS
// ========================================

@Serializable
data class TrackedEntityQuery(
    val trackedEntityType: String? = null,
    val orgUnit: String? = null,
    val ouMode: OrgUnitSelectionMode = OrgUnitSelectionMode.SELECTED,
    val program: String? = null,
    val programStatus: ProgramStatus? = null,
    val followUp: Boolean? = null,
    val updatedAfter: String? = null,
    val updatedBefore: String? = null,
    val updatedWithin: String? = null,
    val enrollmentEnrolledAfter: String? = null,
    val enrollmentEnrolledBefore: String? = null,
    val enrollmentOccurredAfter: String? = null,
    val enrollmentOccurredBefore: String? = null,
    val trackedEntity: String? = null,
    val fields: String = "*",
    val filter: List<String> = emptyList(),
    val order: String? = null,
    val includeDeleted: Boolean = false,
    val includeAllAttributes: Boolean = false,
    val page: Int? = null,
    val pageSize: Int? = null,
    val totalPages: Boolean = false,
    val skipPaging: Boolean = false,
    val skipMeta: Boolean = false,
    val assignedUserMode: AssignedUserSelectionMode? = null,
    val assignedUser: List<String> = emptyList(),
    val potentialDuplicate: Boolean? = null
)

@Serializable
data class EnrollmentQuery(
    val orgUnit: String? = null,
    val ouMode: OrgUnitSelectionMode = OrgUnitSelectionMode.SELECTED,
    val program: String? = null,
    val programStatus: ProgramStatus? = null,
    val followUp: Boolean? = null,
    val updatedAfter: String? = null,
    val updatedBefore: String? = null,
    val updatedWithin: String? = null,
    val enrollmentEnrolledAfter: String? = null,
    val enrollmentEnrolledBefore: String? = null,
    val enrollmentOccurredAfter: String? = null,
    val enrollmentOccurredBefore: String? = null,
    val trackedEntity: String? = null,
    val trackedEntityType: String? = null,
    val enrollment: String? = null,
    val fields: String = "*",
    val order: String? = null,
    val includeDeleted: Boolean = false,
    val page: Int? = null,
    val pageSize: Int? = null,
    val totalPages: Boolean = false,
    val skipPaging: Boolean = false,
    val skipMeta: Boolean = false,
    val assignedUserMode: AssignedUserSelectionMode? = null,
    val assignedUser: List<String> = emptyList()
)

@Serializable
data class EventQuery(
    val program: String? = null,
    val programStage: String? = null,
    val programStatus: ProgramStatus? = null,
    val followUp: Boolean? = null,
    val orgUnit: String? = null,
    val ouMode: OrgUnitSelectionMode = OrgUnitSelectionMode.SELECTED,
    val assignedUserMode: AssignedUserSelectionMode? = null,
    val assignedUser: List<String> = emptyList(),
    val trackedEntity: String? = null,
    val enrollment: String? = null,
    val event: String? = null,
    val scheduledAfter: String? = null,
    val scheduledBefore: String? = null,
    val updatedAfter: String? = null,
    val updatedBefore: String? = null,
    val updatedWithin: String? = null,
    val occurredAfter: String? = null,
    val occurredBefore: String? = null,
    val status: EventStatus? = null,
    val filter: List<String> = emptyList(),
    val filterAttributes: List<String> = emptyList(),
    val order: String? = null,
    val fields: String = "*",
    val includeDeleted: Boolean = false,
    val page: Int? = null,
    val pageSize: Int? = null,
    val totalPages: Boolean = false,
    val skipPaging: Boolean = false,
    val skipMeta: Boolean = false,
    val skipEventId: Boolean = false
)

// ========================================
// BULK OPERATION MODELS
// ========================================

@Serializable
data class BulkTrackerOperation(
    val operation: BulkOperationType,
    val trackedEntities: List<String> = emptyList(),
    val enrollments: List<String> = emptyList(),
    val events: List<String> = emptyList(),
    val relationships: List<String> = emptyList(),
    val parameters: Map<String, String> = emptyMap()
)

enum class BulkOperationType {
    DELETE, SOFT_DELETE, RESTORE, TRANSFER_OWNERSHIP, ASSIGN_USER, UNASSIGN_USER
}

@Serializable
data class BulkOperationResponse(
    val status: String,
    val message: String? = null,
    val processed: Int = 0,
    val successful: Int = 0,
    val failed: Int = 0,
    val errors: List<BulkOperationError> = emptyList()
)

@Serializable
data class BulkOperationError(
    val uid: String,
    val message: String,
    val errorCode: String? = null
)
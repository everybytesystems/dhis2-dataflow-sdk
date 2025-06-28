package com.everybytesystems.dataflow.core.api.tracker

import com.everybytesystems.dataflow.core.api.base.BaseApi
import com.everybytesystems.dataflow.core.config.DHIS2Config
import com.everybytesystems.dataflow.core.network.ApiResponse
import com.everybytesystems.dataflow.core.version.DHIS2Version
import io.ktor.client.*
import kotlinx.serialization.Serializable

/**
 * Complete Tracker API implementation for DHIS2 2.36+
 * Supports the new tracker API format with comprehensive functionality
 */
class TrackerApi(
    httpClient: HttpClient,
    config: DHIS2Config,
    private val version: DHIS2Version
) : BaseApi(httpClient, config) {
    
    // ========================================
    // TRACKED ENTITIES
    // ========================================
    
    /**
     * Get tracked entities with comprehensive filtering
     */
    suspend fun getTrackedEntities(
        trackedEntityType: String? = null,
        orgUnit: String? = null,
        ouMode: OrgUnitSelectionMode = OrgUnitSelectionMode.SELECTED,
        program: String? = null,
        programStatus: ProgramStatus? = null,
        followUp: Boolean? = null,
        updatedAfter: String? = null,
        updatedBefore: String? = null,
        updatedWithin: String? = null,
        enrollmentEnrolledAfter: String? = null,
        enrollmentEnrolledBefore: String? = null,
        enrollmentOccurredAfter: String? = null,
        enrollmentOccurredBefore: String? = null,
        trackedEntity: String? = null,
        fields: String = "*",
        filter: List<String> = emptyList(),
        order: String? = null,
        includeDeleted: Boolean = false,
        includeAllAttributes: Boolean = false,
        page: Int? = null,
        pageSize: Int? = null,
        totalPages: Boolean = false,
        skipPaging: Boolean = false,
        skipMeta: Boolean = false,
        assignedUserMode: AssignedUserSelectionMode? = null,
        assignedUser: List<String> = emptyList(),
        potentialDuplicate: Boolean? = null
    ): ApiResponse<TrackedEntitiesResponse> {
        
        val params = buildMap {
            trackedEntityType?.let { put("trackedEntityType", it) }
            orgUnit?.let { put("orgUnit", it) }
            put("ouMode", ouMode.name)
            program?.let { put("program", it) }
            programStatus?.let { put("programStatus", it.name) }
            followUp?.let { put("followUp", it.toString()) }
            updatedAfter?.let { put("updatedAfter", it) }
            updatedBefore?.let { put("updatedBefore", it) }
            updatedWithin?.let { put("updatedWithin", it) }
            enrollmentEnrolledAfter?.let { put("enrollmentEnrolledAfter", it) }
            enrollmentEnrolledBefore?.let { put("enrollmentEnrolledBefore", it) }
            enrollmentOccurredAfter?.let { put("enrollmentOccurredAfter", it) }
            enrollmentOccurredBefore?.let { put("enrollmentOccurredBefore", it) }
            trackedEntity?.let { put("trackedEntity", it) }
            put("fields", fields)
            if (filter.isNotEmpty()) put("filter", filter.joinToString(","))
            order?.let { put("order", it) }
            put("includeDeleted", includeDeleted.toString())
            put("includeAllAttributes", includeAllAttributes.toString())
            page?.let { put("page", it.toString()) }
            pageSize?.let { put("pageSize", it.toString()) }
            put("totalPages", totalPages.toString())
            put("skipPaging", skipPaging.toString())
            put("skipMeta", skipMeta.toString())
            assignedUserMode?.let { put("assignedUserMode", it.name) }
            if (assignedUser.isNotEmpty()) put("assignedUser", assignedUser.joinToString(","))
            
            // Version-specific features
            if (version.supportsTrackerPotentialDuplicates()) {
                potentialDuplicate?.let { put("potentialDuplicate", it.toString()) }
            }
        }
        
        return get("tracker/trackedEntities", params)
    }
    
    /**
     * Get a specific tracked entity
     */
    suspend fun getTrackedEntity(
        id: String,
        program: String? = null,
        fields: String = "*"
    ): ApiResponse<TrackedEntity> {
        val params = buildMap {
            program?.let { put("program", it) }
            put("fields", fields)
        }
        return get("tracker/trackedEntities/$id", params)
    }
    
    // ========================================
    // ENROLLMENTS
    // ========================================
    
    /**
     * Get enrollments with comprehensive filtering
     */
    suspend fun getEnrollments(
        orgUnit: String? = null,
        ouMode: OrgUnitSelectionMode = OrgUnitSelectionMode.SELECTED,
        program: String? = null,
        programStatus: ProgramStatus? = null,
        followUp: Boolean? = null,
        updatedAfter: String? = null,
        updatedBefore: String? = null,
        updatedWithin: String? = null,
        enrollmentEnrolledAfter: String? = null,
        enrollmentEnrolledBefore: String? = null,
        enrollmentOccurredAfter: String? = null,
        enrollmentOccurredBefore: String? = null,
        trackedEntity: String? = null,
        trackedEntityType: String? = null,
        enrollment: String? = null,
        fields: String = "*",
        order: String? = null,
        includeDeleted: Boolean = false,
        page: Int? = null,
        pageSize: Int? = null,
        totalPages: Boolean = false,
        skipPaging: Boolean = false,
        skipMeta: Boolean = false,
        assignedUserMode: AssignedUserSelectionMode? = null,
        assignedUser: List<String> = emptyList()
    ): ApiResponse<EnrollmentsResponse> {
        
        val params = buildMap {
            orgUnit?.let { put("orgUnit", it) }
            put("ouMode", ouMode.name)
            program?.let { put("program", it) }
            programStatus?.let { put("programStatus", it.name) }
            followUp?.let { put("followUp", it.toString()) }
            updatedAfter?.let { put("updatedAfter", it) }
            updatedBefore?.let { put("updatedBefore", it) }
            updatedWithin?.let { put("updatedWithin", it) }
            enrollmentEnrolledAfter?.let { put("enrollmentEnrolledAfter", it) }
            enrollmentEnrolledBefore?.let { put("enrollmentEnrolledBefore", it) }
            enrollmentOccurredAfter?.let { put("enrollmentOccurredAfter", it) }
            enrollmentOccurredBefore?.let { put("enrollmentOccurredBefore", it) }
            trackedEntity?.let { put("trackedEntity", it) }
            trackedEntityType?.let { put("trackedEntityType", it) }
            enrollment?.let { put("enrollment", it) }
            put("fields", fields)
            order?.let { put("order", it) }
            put("includeDeleted", includeDeleted.toString())
            page?.let { put("page", it.toString()) }
            pageSize?.let { put("pageSize", it.toString()) }
            put("totalPages", totalPages.toString())
            put("skipPaging", skipPaging.toString())
            put("skipMeta", skipMeta.toString())
            assignedUserMode?.let { put("assignedUserMode", it.name) }
            if (assignedUser.isNotEmpty()) put("assignedUser", assignedUser.joinToString(","))
        }
        
        return get("tracker/enrollments", params)
    }
    
    /**
     * Get a specific enrollment
     */
    suspend fun getEnrollment(
        id: String,
        fields: String = "*"
    ): ApiResponse<Enrollment> {
        return get("tracker/enrollments/$id", mapOf("fields" to fields))
    }
    
    // ========================================
    // EVENTS
    // ========================================
    
    /**
     * Get events with comprehensive filtering
     */
    suspend fun getEvents(
        program: String? = null,
        programStage: String? = null,
        programStatus: ProgramStatus? = null,
        followUp: Boolean? = null,
        orgUnit: String? = null,
        ouMode: OrgUnitSelectionMode = OrgUnitSelectionMode.SELECTED,
        assignedUserMode: AssignedUserSelectionMode? = null,
        assignedUser: List<String> = emptyList(),
        trackedEntity: String? = null,
        enrollment: String? = null,
        event: String? = null,
        scheduledAfter: String? = null,
        scheduledBefore: String? = null,
        updatedAfter: String? = null,
        updatedBefore: String? = null,
        updatedWithin: String? = null,
        occurredAfter: String? = null,
        occurredBefore: String? = null,
        status: EventStatus? = null,
        filter: List<String> = emptyList(),
        filterAttributes: List<String> = emptyList(),
        order: String? = null,
        fields: String = "*",
        includeDeleted: Boolean = false,
        page: Int? = null,
        pageSize: Int? = null,
        totalPages: Boolean = false,
        skipPaging: Boolean = false,
        skipMeta: Boolean = false,
        skipEventId: Boolean = false
    ): ApiResponse<EventsResponse> {
        
        val params = buildMap {
            program?.let { put("program", it) }
            programStage?.let { put("programStage", it) }
            programStatus?.let { put("programStatus", it.name) }
            followUp?.let { put("followUp", it.toString()) }
            orgUnit?.let { put("orgUnit", it) }
            put("ouMode", ouMode.name)
            assignedUserMode?.let { put("assignedUserMode", it.name) }
            if (assignedUser.isNotEmpty()) put("assignedUser", assignedUser.joinToString(","))
            trackedEntity?.let { put("trackedEntity", it) }
            enrollment?.let { put("enrollment", it) }
            event?.let { put("event", it) }
            scheduledAfter?.let { put("scheduledAfter", it) }
            scheduledBefore?.let { put("scheduledBefore", it) }
            updatedAfter?.let { put("updatedAfter", it) }
            updatedBefore?.let { put("updatedBefore", it) }
            updatedWithin?.let { put("updatedWithin", it) }
            occurredAfter?.let { put("occurredAfter", it) }
            occurredBefore?.let { put("occurredBefore", it) }
            status?.let { put("status", it.name) }
            if (filter.isNotEmpty()) put("filter", filter.joinToString(","))
            if (filterAttributes.isNotEmpty()) put("filterAttributes", filterAttributes.joinToString(","))
            order?.let { put("order", it) }
            put("fields", fields)
            put("includeDeleted", includeDeleted.toString())
            page?.let { put("page", it.toString()) }
            pageSize?.let { put("pageSize", it.toString()) }
            put("totalPages", totalPages.toString())
            put("skipPaging", skipPaging.toString())
            put("skipMeta", skipMeta.toString())
            put("skipEventId", skipEventId.toString())
        }
        
        return get("tracker/events", params)
    }
    
    /**
     * Get a specific event
     */
    suspend fun getEvent(
        id: String,
        fields: String = "*"
    ): ApiResponse<Event> {
        return get("tracker/events/$id", mapOf("fields" to fields))
    }
    
    // ========================================
    // RELATIONSHIPS
    // ========================================
    
    /**
     * Get relationships
     */
    suspend fun getRelationships(
        trackedEntity: String? = null,
        enrollment: String? = null,
        event: String? = null,
        fields: String = "*",
        includeDeleted: Boolean = false,
        page: Int? = null,
        pageSize: Int? = null,
        totalPages: Boolean = false,
        skipPaging: Boolean = false
    ): ApiResponse<RelationshipsResponse> {
        
        val params = buildMap {
            trackedEntity?.let { put("trackedEntity", it) }
            enrollment?.let { put("enrollment", it) }
            event?.let { put("event", it) }
            put("fields", fields)
            put("includeDeleted", includeDeleted.toString())
            page?.let { put("page", it.toString()) }
            pageSize?.let { put("pageSize", it.toString()) }
            put("totalPages", totalPages.toString())
            put("skipPaging", skipPaging.toString())
        }
        
        return get("tracker/relationships", params)
    }
    
    /**
     * Get a specific relationship
     */
    suspend fun getRelationship(
        id: String,
        fields: String = "*"
    ): ApiResponse<Relationship> {
        return get("tracker/relationships/$id", mapOf("fields" to fields))
    }
    
    // ========================================
    // IMPORT/EXPORT OPERATIONS
    // ========================================
    
    /**
     * Import tracker data with comprehensive options
     */
    suspend fun importTrackerData(
        payload: TrackerImportPayload,
        dryRun: Boolean = false,
        importStrategy: TrackerImportStrategy = TrackerImportStrategy.CREATE_AND_UPDATE,
        atomicMode: TrackerAtomicMode = TrackerAtomicMode.ALL,
        flushMode: TrackerFlushMode = TrackerFlushMode.AUTO,
        validationMode: TrackerValidationMode = TrackerValidationMode.FULL,
        skipSideEffects: Boolean = false,
        skipRuleEngine: Boolean = false,
        skipNotifications: Boolean = false,
        async: Boolean = false,
        reportMode: TrackerImportReportMode = TrackerImportReportMode.ERRORS,
        idScheme: String = "UID",
        dataElementIdScheme: String = "UID",
        orgUnitIdScheme: String = "UID",
        programIdScheme: String = "UID",
        programStageIdScheme: String = "UID",
        categoryOptionComboIdScheme: String = "UID",
        categoryOptionIdScheme: String = "UID"
    ): ApiResponse<TrackerImportResponse> {
        
        val params = buildMap {
            put("dryRun", dryRun.toString())
            put("importStrategy", importStrategy.name)
            put("atomicMode", atomicMode.name)
            put("flushMode", flushMode.name)
            put("validationMode", validationMode.name)
            put("skipSideEffects", skipSideEffects.toString())
            put("skipRuleEngine", skipRuleEngine.toString())
            put("skipNotifications", skipNotifications.toString())
            put("async", async.toString())
            put("reportMode", reportMode.name)
            put("idScheme", idScheme)
            put("dataElementIdScheme", dataElementIdScheme)
            put("orgUnitIdScheme", orgUnitIdScheme)
            put("programIdScheme", programIdScheme)
            put("programStageIdScheme", programStageIdScheme)
            put("categoryOptionComboIdScheme", categoryOptionComboIdScheme)
            put("categoryOptionIdScheme", categoryOptionIdScheme)
        }
        
        return post("tracker", payload, params)
    }
    
    /**
     * Export tracker data
     */
    suspend fun exportTrackerData(
        trackedEntities: List<String> = emptyList(),
        enrollments: List<String> = emptyList(),
        events: List<String> = emptyList(),
        relationships: List<String> = emptyList(),
        fields: String = "*",
        attachment: Boolean = false,
        format: TrackerExportFormat = TrackerExportFormat.JSON
    ): ApiResponse<TrackerExportResponse> {
        
        val params = buildMap {
            if (trackedEntities.isNotEmpty()) put("trackedEntities", trackedEntities.joinToString(","))
            if (enrollments.isNotEmpty()) put("enrollments", enrollments.joinToString(","))
            if (events.isNotEmpty()) put("events", events.joinToString(","))
            if (relationships.isNotEmpty()) put("relationships", relationships.joinToString(","))
            put("fields", fields)
            put("attachment", attachment.toString())
            put("format", format.name.lowercase())
        }
        
        return get("tracker/export", params)
    }
    
    // ========================================
    // CSV IMPORT (2.40+)
    // ========================================
    
    /**
     * Import tracker data from CSV (2.40+)
     */
    suspend fun importTrackerCSV(
        csvData: String,
        program: String,
        orgUnit: String,
        dryRun: Boolean = false,
        skipFirstRow: Boolean = true,
        importStrategy: TrackerImportStrategy = TrackerImportStrategy.CREATE_AND_UPDATE
    ): ApiResponse<TrackerImportResponse> {
        if (!version.supportsTrackerCSVImport()) {
            return ApiResponse.Error(UnsupportedOperationException("CSV import not supported in version ${version.versionString}"))
        }
        
        val params = mapOf(
            "program" to program,
            "orgUnit" to orgUnit,
            "dryRun" to dryRun.toString(),
            "skipFirstRow" to skipFirstRow.toString(),
            "importStrategy" to importStrategy.name
        )
        
        return post("tracker/import/csv", csvData, params)
    }
    
    // ========================================
    // WORKING LISTS (2.37+)
    // ========================================
    
    /**
     * Get working lists (2.37+)
     */
    suspend fun getWorkingLists(
        program: String? = null,
        fields: String = "*"
    ): ApiResponse<WorkingListsResponse> {
        if (!version.supportsTrackerWorkingLists()) {
            return ApiResponse.Error(UnsupportedOperationException("Working lists not supported in version ${version.versionString}"))
        }
        
        val params = buildMap {
            program?.let { put("program", it) }
            put("fields", fields)
        }
        
        return get("tracker/workingLists", params)
    }
    
    /**
     * Create working list (2.37+)
     */
    suspend fun createWorkingList(workingList: WorkingList): ApiResponse<TrackerImportResponse> {
        if (!version.supportsTrackerWorkingLists()) {
            return ApiResponse.Error(UnsupportedOperationException("Working lists not supported in version ${version.versionString}"))
        }
        
        return post("tracker/workingLists", workingList)
    }
    
    // ========================================
    // POTENTIAL DUPLICATES (2.39+)
    // ========================================
    
    /**
     * Get potential duplicates (2.39+)
     */
    suspend fun getPotentialDuplicates(
        trackedEntityType: String? = null,
        program: String? = null,
        fields: String = "*",
        page: Int? = null,
        pageSize: Int? = null
    ): ApiResponse<PotentialDuplicatesResponse> {
        if (!version.supportsTrackerPotentialDuplicates()) {
            return ApiResponse.Error(UnsupportedOperationException("Potential duplicates not supported in version ${version.versionString}"))
        }
        
        val params = buildMap {
            trackedEntityType?.let { put("trackedEntityType", it) }
            program?.let { put("program", it) }
            put("fields", fields)
            page?.let { put("page", it.toString()) }
            pageSize?.let { put("pageSize", it.toString()) }
        }
        
        return get("tracker/potentialDuplicates", params)
    }
    
    /**
     * Mark potential duplicate as invalid (2.39+)
     */
    suspend fun invalidatePotentialDuplicate(id: String): ApiResponse<TrackerImportResponse> {
        if (!version.supportsTrackerPotentialDuplicates()) {
            return ApiResponse.Error(UnsupportedOperationException("Potential duplicates not supported in version ${version.versionString}"))
        }
        
        return post("tracker/potentialDuplicates/$id/invalidation", emptyMap<String, Any>())
    }
    
    // ========================================
    // OWNERSHIP (2.38+)
    // ========================================
    
    /**
     * Get ownership information (2.38+)
     */
    suspend fun getOwnership(
        trackedEntity: String,
        program: String,
        orgUnit: String? = null
    ): ApiResponse<OwnershipResponse> {
        if (!version.supportsTrackerOwnership()) {
            return ApiResponse.Error(UnsupportedOperationException("Ownership not supported in version ${version.versionString}"))
        }
        
        val params = buildMap {
            put("trackedEntity", trackedEntity)
            put("program", program)
            orgUnit?.let { put("orgUnit", it) }
        }
        
        return get("tracker/ownership", params)
    }
    
    /**
     * Transfer ownership (2.38+)
     */
    suspend fun transferOwnership(
        trackedEntity: String,
        program: String,
        orgUnit: String
    ): ApiResponse<TrackerImportResponse> {
        if (!version.supportsTrackerOwnership()) {
            return ApiResponse.Error(UnsupportedOperationException("Ownership not supported in version ${version.versionString}"))
        }
        
        val payload = mapOf(
            "trackedEntity" to trackedEntity,
            "program" to program,
            "orgUnit" to orgUnit
        )
        
        return post("tracker/ownership/transfer", payload)
    }
}

// ========================================
// ENUMS
// ========================================

enum class OrgUnitSelectionMode { SELECTED, CHILDREN, DESCENDANTS, ACCESSIBLE, CAPTURE, ALL }
enum class AssignedUserSelectionMode { CURRENT, PROVIDED, NONE, ANY }
enum class ProgramStatus { ACTIVE, COMPLETED, CANCELLED }
enum class EventStatus { ACTIVE, COMPLETED, VISITED, SCHEDULE, OVERDUE, SKIPPED }
enum class TrackerImportStrategy { CREATE, UPDATE, CREATE_AND_UPDATE, DELETE }
enum class TrackerAtomicMode { ALL, OBJECT }
enum class TrackerFlushMode { AUTO, OBJECT }
enum class TrackerValidationMode { FULL, FAIL_FAST, SKIP }
enum class TrackerImportReportMode { FULL, ERRORS, WARNINGS }
enum class TrackerExportFormat { JSON, CSV }
package com.everybytesystems.ebscore.sdk.services

import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDate

/**
 * Represents a DHIS2 tracked entity instance
 */
@Serializable
data class TrackedEntity(
    val trackedEntityInstance: String? = null,
    val trackedEntityType: String,
    val orgUnit: String,
    val attributes: List<TrackedEntityAttribute> = emptyList(),
    val enrollments: List<Enrollment> = emptyList(),
    val relationships: List<Relationship> = emptyList(),
    val created: String? = null,
    val createdBy: String? = null,
    val lastUpdated: String? = null,
    val lastUpdatedBy: String? = null,
    val inactive: Boolean = false,
    val deleted: Boolean = false,
    val featureType: String? = null,
    val coordinates: String? = null
)

/**
 * Tracked entity attribute value
 */
@Serializable
data class TrackedEntityAttribute(
    val attribute: String,
    val value: String,
    val displayName: String? = null,
    val created: String? = null,
    val lastUpdated: String? = null,
    val storedBy: String? = null
)

/**
 * Program enrollment
 */
@Serializable
data class Enrollment(
    val enrollment: String? = null,
    val trackedEntityInstance: String? = null,
    val program: String,
    val status: String = "ACTIVE",
    val orgUnit: String,
    val enrollmentDate: String,
    val incidentDate: String? = null,
    val followup: Boolean = false,
    val completed: Boolean = false,
    val completedDate: String? = null,
    val completedBy: String? = null,
    val events: List<Event> = emptyList(),
    val attributes: List<TrackedEntityAttribute> = emptyList(),
    val notes: List<Note> = emptyList(),
    val relationships: List<Relationship> = emptyList(),
    val created: String? = null,
    val createdBy: String? = null,
    val lastUpdated: String? = null,
    val lastUpdatedBy: String? = null,
    val deleted: Boolean = false
)

/**
 * Program stage event
 */
@Serializable
data class Event(
    val event: String? = null,
    val status: String = "ACTIVE",
    val program: String,
    val programStage: String,
    val enrollment: String? = null,
    val trackedEntityInstance: String? = null,
    val orgUnit: String,
    val eventDate: String,
    val dueDate: String? = null,
    val completedDate: String? = null,
    val completedBy: String? = null,
    val dataValues: List<EventDataValue> = emptyList(),
    val notes: List<Note> = emptyList(),
    val followup: Boolean = false,
    val deleted: Boolean = false,
    val created: String? = null,
    val createdBy: String? = null,
    val lastUpdated: String? = null,
    val lastUpdatedBy: String? = null,
    val attributeOptionCombo: String? = null,
    val attributeCategoryOptions: String? = null,
    val coordinate: Coordinate? = null
)

/**
 * Event data value
 */
@Serializable
data class EventDataValue(
    val dataElement: String,
    val value: String,
    val providedElsewhere: Boolean = false,
    val created: String? = null,
    val lastUpdated: String? = null,
    val storedBy: String? = null
)

/**
 * Note/comment
 */
@Serializable
data class Note(
    val note: String? = null,
    val value: String,
    val storedDate: String? = null,
    val storedBy: String? = null
)

/**
 * Relationship between entities
 */
@Serializable
data class Relationship(
    val relationship: String? = null,
    val relationshipType: String,
    val from: RelationshipItem,
    val to: RelationshipItem,
    val created: String? = null,
    val lastUpdated: String? = null
)

/**
 * Relationship item (can be tracked entity, enrollment, or event)
 */
@Serializable
data class RelationshipItem(
    val trackedEntityInstance: TrackedEntityRef? = null,
    val enrollment: EnrollmentRef? = null,
    val event: EventRef? = null
)

/**
 * Reference to tracked entity
 */
@Serializable
data class TrackedEntityRef(
    val trackedEntityInstance: String
)

/**
 * Reference to enrollment
 */
@Serializable
data class EnrollmentRef(
    val enrollment: String
)

/**
 * Reference to event
 */
@Serializable
data class EventRef(
    val event: String
)

/**
 * Coordinate/location
 */
@Serializable
data class Coordinate(
    val latitude: Double,
    val longitude: Double
)

/**
 * Search criteria for tracked entities
 */
data class TrackedEntitySearchCriteria(
    val program: String? = null,
    val trackedEntityType: String? = null,
    val orgUnit: String? = null,
    val ouMode: String = "SELECTED",
    val filter: List<String> = emptyList(),
    val attribute: List<String> = emptyList(),
    val skipPaging: Boolean = false,
    val page: Int = 1,
    val pageSize: Int = 50,
    val totalPages: Boolean = false,
    val skipMeta: Boolean = false,
    val includeDeleted: Boolean = false,
    val includeAllAttributes: Boolean = false,
    val fields: String? = null,
    val order: String? = null
)
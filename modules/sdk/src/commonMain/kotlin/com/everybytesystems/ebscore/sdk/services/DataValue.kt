package com.everybytesystems.ebscore.sdk.services

import kotlinx.serialization.Serializable

/**
 * Represents a DHIS2 data value
 */
@Serializable
data class DataValue(
    val dataElement: String,
    val period: String,
    val orgUnit: String,
    val value: String,
    val categoryOptionCombo: String? = null,
    val attributeOptionCombo: String? = null,
    val comment: String? = null,
    val followUp: Boolean? = null,
    val storedBy: String? = null,
    val created: String? = null,
    val lastUpdated: String? = null
)

/**
 * Data value set for bulk operations
 */
@Serializable
data class DataValueSet(
    val dataSet: String? = null,
    val completeDate: String? = null,
    val period: String? = null,
    val orgUnit: String? = null,
    val attributeOptionCombo: String? = null,
    val dataValues: List<DataValue> = emptyList()
)
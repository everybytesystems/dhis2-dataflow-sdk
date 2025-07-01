package com.everybytesystems.ebscore.core.api.analytics

import kotlinx.serialization.Serializable

/**
 * Basic analytics response model
 */
@Serializable
data class AnalyticsResponse(
    val headers: List<AnalyticsHeader> = emptyList(),
    val rows: List<List<String>> = emptyList(),
    val metaData: AnalyticsMetaData? = null
)

@Serializable
data class AnalyticsHeader(
    val name: String,
    val column: String,
    val valueType: String,
    val type: String,
    val hidden: Boolean = false,
    val meta: Boolean = false
)

@Serializable
data class AnalyticsMetaData(
    val items: Map<String, AnalyticsMetaDataItem> = emptyMap(),
    val dimensions: Map<String, List<String>> = emptyMap()
)

@Serializable
data class AnalyticsMetaDataItem(
    val name: String,
    val uid: String? = null
)
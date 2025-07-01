package com.everybytesystems.ebscore.core.api.analytics

import com.everybytesystems.ebscore.core.api.base.BaseApi
import com.everybytesystems.ebscore.core.config.DHIS2Config
import com.everybytesystems.ebscore.core.network.ApiResponse
import com.everybytesystems.ebscore.core.version.DHIS2Version
import io.ktor.client.*
import kotlinx.serialization.Serializable

/**
 * Simplified Analytics API implementation for DHIS2 2.36+
 * Basic analytics functionality to avoid compiler issues
 */
class AnalyticsApi(
    httpClient: HttpClient,
    config: DHIS2Config,
    private val version: DHIS2Version
) : BaseApi(httpClient, config) {
    
    /**
     * Get basic aggregate analytics data
     */
    suspend fun getAggregateAnalytics(
        dimension: List<String>,
        filter: List<String> = emptyList(),
        format: AnalyticsFormat = AnalyticsFormat.JSON
    ): ApiResponse<AnalyticsResponse> {
        val params = buildMap {
            put("dimension", dimension.joinToString(";"))
            if (filter.isNotEmpty()) {
                put("filter", filter.joinToString(";"))
            }
            put("format", format.value)
        }
        
        return get("analytics", params)
    }
    
    /**
     * Execute analytics favorite
     */
    suspend fun executeAnalyticsFavorite(
        id: String,
        format: AnalyticsFormat = AnalyticsFormat.JSON
    ): ApiResponse<AnalyticsResponse> {
        val params = buildMap {
            put("format", format.value)
        }
        
        return get("analytics/favorites/$id/data", params)
    }
}

// ========================================
// ENUMS
// ========================================

enum class AggregationType { SUM, AVERAGE, AVERAGE_SUM_ORG_UNIT, LAST, LAST_AVERAGE_ORG_UNIT, COUNT, STDDEV, VARIANCE, MIN, MAX, NONE, CUSTOM, DEFAULT }
enum class DisplayProperty { NAME, SHORTNAME, CODE }
enum class AnalyticsFormat(val value: String) { JSON("json"), XML("xml"), CSV("csv"), XLS("xls"), PDF("pdf"), HTML("html") }
enum class SortOrder { ASC, DESC }
enum class EventOutputType { EVENT, ENROLLMENT, TRACKED_ENTITY_INSTANCE }
enum class EnrollmentOutputType { ENROLLMENT, EVENT }
enum class OutlierAlgorithm { Z_SCORE, MODIFIED_Z_SCORE, IQR }
enum class OutlierOrderBy { MEAN_ABS_DEVIATION, ABS_DEV, Z_SCORE, MODIFIED_Z_SCORE }
enum class ValidationImportance { HIGH, MEDIUM, LOW }
enum class StatisticsInterval { DAY, WEEK, MONTH, YEAR }
enum class FavoriteType { CHART, PIVOT_TABLE, REPORT_TABLE, MAP, EVENT_CHART, EVENT_REPORT }
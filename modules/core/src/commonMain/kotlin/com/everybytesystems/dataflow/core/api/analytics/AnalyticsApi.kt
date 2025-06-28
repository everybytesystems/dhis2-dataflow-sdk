package com.everybytesystems.dataflow.core.api.analytics

import com.everybytesystems.dataflow.core.api.base.BaseApi
import com.everybytesystems.dataflow.core.config.DHIS2Config
import com.everybytesystems.dataflow.core.network.ApiResponse
import com.everybytesystems.dataflow.core.version.DHIS2Version
import io.ktor.client.*
import kotlinx.serialization.Serializable

/**
 * Complete Analytics API implementation for DHIS2 2.36+
 * Supports aggregate analytics, event analytics, enrollment analytics, and advanced features
 */
class AnalyticsApi(
    httpClient: HttpClient,
    config: DHIS2Config,
    private val version: DHIS2Version
) : BaseApi(httpClient, config) {
    
    // ========================================
    // AGGREGATE ANALYTICS
    // ========================================
    
    /**
     * Get aggregate analytics data with comprehensive filtering
     */
    suspend fun getAggregateAnalytics(
        dimension: List<String>,
        filter: List<String> = emptyList(),
        aggregationType: AggregationType? = null,
        measureCriteria: String? = null,
        preAggregationMeasureCriteria: String? = null,
        startDate: String? = null,
        endDate: String? = null,
        skipMeta: Boolean = false,
        skipData: Boolean = false,
        skipRounding: Boolean = false,
        completedOnly: Boolean = false,
        hierarchyMeta: Boolean = false,
        ignoreLimit: Boolean = false,
        hideEmptyRows: Boolean = false,
        hideEmptyColumns: Boolean = false,
        showHierarchy: Boolean = false,
        includeNumDen: Boolean = false,
        includeMetadataDetails: Boolean = false,
        displayProperty: DisplayProperty = DisplayProperty.NAME,
        outputIdScheme: String = "UID",
        inputIdScheme: String = "UID",
        approvalLevel: String? = null,
        relativePeriodDate: String? = null,
        userOrgUnit: String? = null,
        columns: List<String> = emptyList(),
        rows: List<String> = emptyList(),
        order: String? = null,
        timeField: String? = null,
        orgUnitField: String? = null,
        format: AnalyticsFormat = AnalyticsFormat.JSON
    ): ApiResponse<AnalyticsResponse> {
        
        val params = buildMap {
            put("dimension", dimension.joinToString(";"))
            if (filter.isNotEmpty()) put("filter", filter.joinToString(";"))
            aggregationType?.let { put("aggregationType", it.name) }
            measureCriteria?.let { put("measureCriteria", it) }
            preAggregationMeasureCriteria?.let { put("preAggregationMeasureCriteria", it) }
            startDate?.let { put("startDate", it) }
            endDate?.let { put("endDate", it) }
            put("skipMeta", skipMeta.toString())
            put("skipData", skipData.toString())
            put("skipRounding", skipRounding.toString())
            put("completedOnly", completedOnly.toString())
            put("hierarchyMeta", hierarchyMeta.toString())
            put("ignoreLimit", ignoreLimit.toString())
            put("hideEmptyRows", hideEmptyRows.toString())
            put("hideEmptyColumns", hideEmptyColumns.toString())
            put("showHierarchy", showHierarchy.toString())
            put("includeNumDen", includeNumDen.toString())
            put("includeMetadataDetails", includeMetadataDetails.toString())
            put("displayProperty", displayProperty.name)
            put("outputIdScheme", outputIdScheme)
            put("inputIdScheme", inputIdScheme)
            approvalLevel?.let { put("approvalLevel", it) }
            relativePeriodDate?.let { put("relativePeriodDate", it) }
            userOrgUnit?.let { put("userOrgUnit", it) }
            if (columns.isNotEmpty()) put("columns", columns.joinToString(";"))
            if (rows.isNotEmpty()) put("rows", rows.joinToString(";"))
            order?.let { put("order", it) }
            timeField?.let { put("timeField", it) }
            orgUnitField?.let { put("orgUnitField", it) }
            put("format", format.value)
        }
        
        return get("analytics", params)
    }
    
    /**
     * Get raw analytics data (no aggregation)
     */
    suspend fun getRawAnalytics(
        dimension: List<String>,
        filter: List<String> = emptyList(),
        startDate: String? = null,
        endDate: String? = null,
        skipMeta: Boolean = false,
        skipData: Boolean = false,
        hierarchyMeta: Boolean = false,
        showHierarchy: Boolean = false,
        displayProperty: DisplayProperty = DisplayProperty.NAME,
        outputIdScheme: String = "UID",
        inputIdScheme: String = "UID",
        userOrgUnit: String? = null,
        format: AnalyticsFormat = AnalyticsFormat.JSON
    ): ApiResponse<AnalyticsResponse> {
        
        val params = buildMap {
            put("dimension", dimension.joinToString(";"))
            if (filter.isNotEmpty()) put("filter", filter.joinToString(";"))
            startDate?.let { put("startDate", it) }
            endDate?.let { put("endDate", it) }
            put("skipMeta", skipMeta.toString())
            put("skipData", skipData.toString())
            put("hierarchyMeta", hierarchyMeta.toString())
            put("showHierarchy", showHierarchy.toString())
            put("displayProperty", displayProperty.name)
            put("outputIdScheme", outputIdScheme)
            put("inputIdScheme", inputIdScheme)
            userOrgUnit?.let { put("userOrgUnit", it) }
            put("format", format.value)
        }
        
        return get("analytics/rawData", params)
    }
    
    // ========================================
    // EVENT ANALYTICS
    // ========================================
    
    /**
     * Get event analytics data
     */
    suspend fun getEventAnalytics(
        program: String,
        stage: String? = null,
        startDate: String,
        endDate: String,
        dimension: List<String> = emptyList(),
        filter: List<String> = emptyList(),
        value: String? = null,
        aggregationType: AggregationType? = null,
        skipMeta: Boolean = false,
        skipData: Boolean = false,
        skipRounding: Boolean = false,
        completedOnly: Boolean = false,
        hierarchyMeta: Boolean = false,
        showHierarchy: Boolean = false,
        sortOrder: SortOrder = SortOrder.ASC,
        limit: Int? = null,
        outputType: EventOutputType = EventOutputType.EVENT,
        collapseDataDimensions: Boolean = false,
        coordinatesOnly: Boolean = false,
        dataIdScheme: String = "UID",
        outputIdScheme: String = "UID",
        inputIdScheme: String = "UID",
        displayProperty: DisplayProperty = DisplayProperty.NAME,
        relativePeriodDate: String? = null,
        userOrgUnit: String? = null,
        coordinateField: String? = null,
        bbox: String? = null,
        clusterSize: Int? = null,
        includeClusterPoints: Boolean = false,
        page: Int? = null,
        pageSize: Int? = null,
        format: AnalyticsFormat = AnalyticsFormat.JSON
    ): ApiResponse<EventAnalyticsResponse> {
        
        val params = buildMap {
            put("program", program)
            stage?.let { put("stage", it) }
            put("startDate", startDate)
            put("endDate", endDate)
            if (dimension.isNotEmpty()) put("dimension", dimension.joinToString(";"))
            if (filter.isNotEmpty()) put("filter", filter.joinToString(";"))
            value?.let { put("value", it) }
            aggregationType?.let { put("aggregationType", it.name) }
            put("skipMeta", skipMeta.toString())
            put("skipData", skipData.toString())
            put("skipRounding", skipRounding.toString())
            put("completedOnly", completedOnly.toString())
            put("hierarchyMeta", hierarchyMeta.toString())
            put("showHierarchy", showHierarchy.toString())
            put("sortOrder", sortOrder.name)
            limit?.let { put("limit", it.toString()) }
            put("outputType", outputType.name)
            put("collapseDataDimensions", collapseDataDimensions.toString())
            put("coordinatesOnly", coordinatesOnly.toString())
            put("dataIdScheme", dataIdScheme)
            put("outputIdScheme", outputIdScheme)
            put("inputIdScheme", inputIdScheme)
            put("displayProperty", displayProperty.name)
            relativePeriodDate?.let { put("relativePeriodDate", it) }
            userOrgUnit?.let { put("userOrgUnit", it) }
            coordinateField?.let { put("coordinateField", it) }
            bbox?.let { put("bbox", it) }
            
            // Version-specific features
            if (version.supportsAnalyticsEventClusters()) {
                clusterSize?.let { put("clusterSize", it.toString()) }
                put("includeClusterPoints", includeClusterPoints.toString())
            }
            
            page?.let { put("page", it.toString()) }
            pageSize?.let { put("pageSize", it.toString()) }
            put("format", format.value)
        }
        
        return get("analytics/events/query/$program", params)
    }
    
    /**
     * Get event analytics aggregate data
     */
    suspend fun getEventAnalyticsAggregate(
        program: String,
        stage: String? = null,
        startDate: String,
        endDate: String,
        dimension: List<String>,
        filter: List<String> = emptyList(),
        value: String? = null,
        aggregationType: AggregationType? = null,
        skipMeta: Boolean = false,
        skipData: Boolean = false,
        skipRounding: Boolean = false,
        completedOnly: Boolean = false,
        hierarchyMeta: Boolean = false,
        showHierarchy: Boolean = false,
        displayProperty: DisplayProperty = DisplayProperty.NAME,
        outputIdScheme: String = "UID",
        inputIdScheme: String = "UID",
        approvalLevel: String? = null,
        relativePeriodDate: String? = null,
        userOrgUnit: String? = null,
        format: AnalyticsFormat = AnalyticsFormat.JSON
    ): ApiResponse<AnalyticsResponse> {
        
        val params = buildMap {
            put("program", program)
            stage?.let { put("stage", it) }
            put("startDate", startDate)
            put("endDate", endDate)
            put("dimension", dimension.joinToString(";"))
            if (filter.isNotEmpty()) put("filter", filter.joinToString(";"))
            value?.let { put("value", it) }
            aggregationType?.let { put("aggregationType", it.name) }
            put("skipMeta", skipMeta.toString())
            put("skipData", skipData.toString())
            put("skipRounding", skipRounding.toString())
            put("completedOnly", completedOnly.toString())
            put("hierarchyMeta", hierarchyMeta.toString())
            put("showHierarchy", showHierarchy.toString())
            put("displayProperty", displayProperty.name)
            put("outputIdScheme", outputIdScheme)
            put("inputIdScheme", inputIdScheme)
            approvalLevel?.let { put("approvalLevel", it) }
            relativePeriodDate?.let { put("relativePeriodDate", it) }
            userOrgUnit?.let { put("userOrgUnit", it) }
            put("format", format.value)
        }
        
        return get("analytics/events/aggregate/$program", params)
    }
    
    // ========================================
    // ENROLLMENT ANALYTICS (2.37+)
    // ========================================
    
    /**
     * Get enrollment analytics data (2.37+)
     */
    suspend fun getEnrollmentAnalytics(
        program: String,
        startDate: String,
        endDate: String,
        dimension: List<String> = emptyList(),
        filter: List<String> = emptyList(),
        value: String? = null,
        aggregationType: AggregationType? = null,
        skipMeta: Boolean = false,
        skipData: Boolean = false,
        skipRounding: Boolean = false,
        completedOnly: Boolean = false,
        hierarchyMeta: Boolean = false,
        showHierarchy: Boolean = false,
        sortOrder: SortOrder = SortOrder.ASC,
        limit: Int? = null,
        outputType: EnrollmentOutputType = EnrollmentOutputType.ENROLLMENT,
        collapseDataDimensions: Boolean = false,
        coordinatesOnly: Boolean = false,
        dataIdScheme: String = "UID",
        outputIdScheme: String = "UID",
        inputIdScheme: String = "UID",
        displayProperty: DisplayProperty = DisplayProperty.NAME,
        relativePeriodDate: String? = null,
        userOrgUnit: String? = null,
        coordinateField: String? = null,
        page: Int? = null,
        pageSize: Int? = null,
        format: AnalyticsFormat = AnalyticsFormat.JSON
    ): ApiResponse<EnrollmentAnalyticsResponse> {
        if (!version.supportsAnalyticsEnrollments()) {
            return ApiResponse.Error(UnsupportedOperationException("Enrollment analytics not supported in version ${version.versionString}"))
        }
        
        val params = buildMap {
            put("program", program)
            put("startDate", startDate)
            put("endDate", endDate)
            if (dimension.isNotEmpty()) put("dimension", dimension.joinToString(";"))
            if (filter.isNotEmpty()) put("filter", filter.joinToString(";"))
            value?.let { put("value", it) }
            aggregationType?.let { put("aggregationType", it.name) }
            put("skipMeta", skipMeta.toString())
            put("skipData", skipData.toString())
            put("skipRounding", skipRounding.toString())
            put("completedOnly", completedOnly.toString())
            put("hierarchyMeta", hierarchyMeta.toString())
            put("showHierarchy", showHierarchy.toString())
            put("sortOrder", sortOrder.name)
            limit?.let { put("limit", it.toString()) }
            put("outputType", outputType.name)
            put("collapseDataDimensions", collapseDataDimensions.toString())
            put("coordinatesOnly", coordinatesOnly.toString())
            put("dataIdScheme", dataIdScheme)
            put("outputIdScheme", outputIdScheme)
            put("inputIdScheme", inputIdScheme)
            put("displayProperty", displayProperty.name)
            relativePeriodDate?.let { put("relativePeriodDate", it) }
            userOrgUnit?.let { put("userOrgUnit", it) }
            coordinateField?.let { put("coordinateField", it) }
            page?.let { put("page", it.toString()) }
            pageSize?.let { put("pageSize", it.toString()) }
            put("format", format.value)
        }
        
        return get("analytics/enrollments/query/$program", params)
    }
    
    // ========================================
    // OUTLIER DETECTION (2.36+)
    // ========================================
    
    /**
     * Get outlier detection results (2.36+)
     */
    suspend fun getOutlierDetection(
        dataElement: List<String> = emptyList(),
        dataSet: List<String> = emptyList(),
        startDate: String,
        endDate: String,
        orgUnit: List<String>,
        algorithm: OutlierAlgorithm = OutlierAlgorithm.Z_SCORE,
        threshold: Double = 3.0,
        dataStartDate: String? = null,
        dataEndDate: String? = null,
        orderBy: OutlierOrderBy = OutlierOrderBy.MEAN_ABS_DEVIATION,
        sortOrder: SortOrder = SortOrder.DESC,
        maxResults: Int = 500,
        skipRounding: Boolean = false,
        format: AnalyticsFormat = AnalyticsFormat.JSON
    ): ApiResponse<OutlierDetectionResponse> {
        if (!version.supportsAnalyticsOutlierDetection()) {
            return ApiResponse.Error(UnsupportedOperationException("Outlier detection not supported in version ${version.versionString}"))
        }
        
        val params = buildMap {
            if (dataElement.isNotEmpty()) put("de", dataElement.joinToString(","))
            if (dataSet.isNotEmpty()) put("ds", dataSet.joinToString(","))
            put("startDate", startDate)
            put("endDate", endDate)
            put("ou", orgUnit.joinToString(","))
            put("algorithm", algorithm.name)
            put("threshold", threshold.toString())
            dataStartDate?.let { put("dataStartDate", it) }
            dataEndDate?.let { put("dataEndDate", it) }
            put("orderBy", orderBy.name)
            put("sortOrder", sortOrder.name)
            put("maxResults", maxResults.toString())
            put("skipRounding", skipRounding.toString())
            put("format", format.value)
        }
        
        return get("outlierDetection", params)
    }
    
    // ========================================
    // VALIDATION RESULTS (2.36+)
    // ========================================
    
    /**
     * Get validation results analytics (2.36+)
     */
    suspend fun getValidationResults(
        validationRule: List<String> = emptyList(),
        validationRuleGroup: List<String> = emptyList(),
        orgUnit: List<String>,
        startDate: String,
        endDate: String,
        created: String? = null,
        notificationSent: Boolean? = null,
        importance: ValidationImportance? = null,
        skipPaging: Boolean = false,
        page: Int? = null,
        pageSize: Int? = null,
        format: AnalyticsFormat = AnalyticsFormat.JSON
    ): ApiResponse<ValidationResultsResponse> {
        if (!version.supportsAnalyticsValidationResults()) {
            return ApiResponse.Error(UnsupportedOperationException("Validation results analytics not supported in version ${version.versionString}"))
        }
        
        val params = buildMap {
            if (validationRule.isNotEmpty()) put("vr", validationRule.joinToString(","))
            if (validationRuleGroup.isNotEmpty()) put("vrg", validationRuleGroup.joinToString(","))
            put("ou", orgUnit.joinToString(","))
            put("startDate", startDate)
            put("endDate", endDate)
            created?.let { put("created", it) }
            notificationSent?.let { put("notificationSent", it.toString()) }
            importance?.let { put("importance", it.name) }
            put("skipPaging", skipPaging.toString())
            page?.let { put("page", it.toString()) }
            pageSize?.let { put("pageSize", it.toString()) }
            put("format", format.value)
        }
        
        return get("validationResults", params)
    }
    
    // ========================================
    // DATA STATISTICS (2.37+)
    // ========================================
    
    /**
     * Get data statistics (2.37+)
     */
    suspend fun getDataStatistics(
        favorite: String? = null,
        startDate: String? = null,
        endDate: String? = null,
        interval: StatisticsInterval = StatisticsInterval.DAY,
        format: AnalyticsFormat = AnalyticsFormat.JSON
    ): ApiResponse<DataStatisticsResponse> {
        if (!version.supportsAnalyticsDataStatistics()) {
            return ApiResponse.Error(UnsupportedOperationException("Data statistics not supported in version ${version.versionString}"))
        }
        
        val params = buildMap {
            favorite?.let { put("favorite", it) }
            startDate?.let { put("startDate", it) }
            endDate?.let { put("endDate", it) }
            put("interval", interval.name)
            put("format", format.value)
        }
        
        return get("dataStatistics", params)
    }
    
    // ========================================
    // GEOSPATIAL FEATURES (2.38+)
    // ========================================
    
    /**
     * Get geospatial analytics data (2.38+)
     */
    suspend fun getGeospatialAnalytics(
        dimension: List<String>,
        filter: List<String> = emptyList(),
        bbox: String? = null,
        clusterSize: Int? = null,
        coordinateField: String? = null,
        includeClusterPoints: Boolean = false,
        skipMeta: Boolean = false,
        skipData: Boolean = false,
        displayProperty: DisplayProperty = DisplayProperty.NAME,
        outputIdScheme: String = "UID",
        format: AnalyticsFormat = AnalyticsFormat.JSON
    ): ApiResponse<GeospatialAnalyticsResponse> {
        if (!version.supportsAnalyticsGeoFeatures()) {
            return ApiResponse.Error(UnsupportedOperationException("Geospatial analytics not supported in version ${version.versionString}"))
        }
        
        val params = buildMap {
            put("dimension", dimension.joinToString(";"))
            if (filter.isNotEmpty()) put("filter", filter.joinToString(";"))
            bbox?.let { put("bbox", it) }
            clusterSize?.let { put("clusterSize", it.toString()) }
            coordinateField?.let { put("coordinateField", it) }
            put("includeClusterPoints", includeClusterPoints.toString())
            put("skipMeta", skipMeta.toString())
            put("skipData", skipData.toString())
            put("displayProperty", displayProperty.name)
            put("outputIdScheme", outputIdScheme)
            put("format", format.value)
        }
        
        return get("analytics/geoFeatures", params)
    }
    
    // ========================================
    // CUSTOM ANALYTICS QUERIES
    // ========================================
    
    /**
     * Execute custom analytics query
     */
    suspend fun executeCustomQuery(
        query: AnalyticsQuery
    ): ApiResponse<AnalyticsResponse> {
        val params = buildMap {
            put("dimension", query.dimensions.joinToString(";"))
            if (query.filters.isNotEmpty()) put("filter", query.filters.joinToString(";"))
            query.aggregationType?.let { put("aggregationType", it.name) }
            query.measureCriteria?.let { put("measureCriteria", it) }
            query.startDate?.let { put("startDate", it) }
            query.endDate?.let { put("endDate", it) }
            put("skipMeta", query.skipMeta.toString())
            put("skipData", query.skipData.toString())
            put("skipRounding", query.skipRounding.toString())
            put("completedOnly", query.completedOnly.toString())
            put("hierarchyMeta", query.hierarchyMeta.toString())
            put("ignoreLimit", query.ignoreLimit.toString())
            put("hideEmptyRows", query.hideEmptyRows.toString())
            put("hideEmptyColumns", query.hideEmptyColumns.toString())
            put("showHierarchy", query.showHierarchy.toString())
            put("includeNumDen", query.includeNumDen.toString())
            put("includeMetadataDetails", query.includeMetadataDetails.toString())
            put("displayProperty", query.displayProperty.name)
            put("outputIdScheme", query.outputIdScheme)
            put("inputIdScheme", query.inputIdScheme)
            query.approvalLevel?.let { put("approvalLevel", it) }
            query.relativePeriodDate?.let { put("relativePeriodDate", it) }
            query.userOrgUnit?.let { put("userOrgUnit", it) }
            if (query.columns.isNotEmpty()) put("columns", query.columns.joinToString(";"))
            if (query.rows.isNotEmpty()) put("rows", query.rows.joinToString(";"))
            query.order?.let { put("order", it) }
            query.timeField?.let { put("timeField", it) }
            query.orgUnitField?.let { put("orgUnitField", it) }
            put("format", query.format.value)
        }
        
        return get("analytics", params)
    }
    
    // ========================================
    // ANALYTICS DIMENSIONS
    // ========================================
    
    /**
     * Get available analytics dimensions
     */
    suspend fun getAnalyticsDimensions(
        fields: String = "*"
    ): ApiResponse<AnalyticsDimensionsResponse> {
        return get("dimensions", mapOf("fields" to fields))
    }
    
    /**
     * Get specific analytics dimension
     */
    suspend fun getAnalyticsDimension(
        id: String,
        fields: String = "*"
    ): ApiResponse<AnalyticsDimension> {
        return get("dimensions/$id", mapOf("fields" to fields))
    }
    
    // ========================================
    // ANALYTICS FAVORITES
    // ========================================
    
    /**
     * Get analytics favorites (charts, pivot tables, etc.)
     */
    suspend fun getAnalyticsFavorites(
        type: FavoriteType? = null,
        fields: String = "*",
        page: Int? = null,
        pageSize: Int? = null
    ): ApiResponse<AnalyticsFavoritesResponse> {
        val params = buildMap {
            type?.let { put("type", it.name) }
            put("fields", fields)
            page?.let { put("page", it.toString()) }
            pageSize?.let { put("pageSize", it.toString()) }
        }
        
        return get("analytics/favorites", params)
    }
    
    /**
     * Execute analytics favorite
     */
    suspend fun executeAnalyticsFavorite(
        id: String,
        relativePeriodDate: String? = null,
        userOrgUnit: String? = null,
        format: AnalyticsFormat = AnalyticsFormat.JSON
    ): ApiResponse<AnalyticsResponse> {
        val params = buildMap {
            relativePeriodDate?.let { put("relativePeriodDate", it) }
            userOrgUnit?.let { put("userOrgUnit", it) }
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
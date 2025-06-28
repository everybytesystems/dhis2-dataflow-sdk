package com.everybytesystems.dataflow.core.api.analytics

import kotlinx.serialization.Serializable
import kotlinx.serialization.Contextual

// ========================================
// ANALYTICS RESPONSE MODELS
// ========================================

@Serializable
data class AnalyticsResponse(
    val headers: List<AnalyticsHeader> = emptyList(),
    val metaData: AnalyticsMetaData? = null,
    val rows: List<List<String>> = emptyList(),
    val width: Int = 0,
    val height: Int = 0
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
    val dimensions: Map<String, List<String>> = emptyMap(),
    val pager: AnalyticsPager? = null,
    val ouHierarchy: Map<String, List<String>> = emptyMap()
)

@Serializable
data class AnalyticsMetaDataItem(
    val uid: String? = null,
    val code: String? = null,
    val name: String,
    val description: String? = null,
    val dimensionType: String? = null,
    val valueType: String? = null,
    val totalAggregationType: String? = null,
    val minDate: String? = null,
    val maxDate: String? = null,
    val pe: List<String> = emptyList(),
    val ou: List<String> = emptyList(),
    val co: List<String> = emptyList()
)

@Serializable
data class AnalyticsPager(
    val page: Int = 1,
    val pageCount: Int = 1,
    val total: Int = 0,
    val pageSize: Int = 50,
    val nextPage: String? = null,
    val prevPage: String? = null
)

// ========================================
// EVENT ANALYTICS MODELS
// ========================================

@Serializable
data class EventAnalyticsResponse(
    val headers: List<EventAnalyticsHeader> = emptyList(),
    val metaData: EventAnalyticsMetadata? = null,
    val rows: List<List<String>> = emptyList(),
    val width: Int = 0,
    val height: Int = 0
)

@Serializable
data class EventAnalyticsHeader(
    val name: String,
    val column: String,
    val valueType: String,
    val type: String,
    val hidden: Boolean = false,
    val meta: Boolean = false,
    val programStage: String? = null,
    val repeatIndex: Int? = null
)

@Serializable
data class EventAnalyticsMetadata(
    val items: Map<String, EventAnalyticsMetadataItem> = emptyMap(),
    val dimensions: Map<String, List<String>> = emptyMap(),
    val pager: AnalyticsPager? = null,
    val ouHierarchy: Map<String, List<String>> = emptyMap()
)

@Serializable
data class EventAnalyticsMetadataItem(
    val uid: String? = null,
    val code: String? = null,
    val name: String? = null,
    val programStage: String? = null,
    val valueType: String? = null,
    val optionSet: String? = null,
    val dimensionItemType: String? = null,
    val totalAggregationType: String? = null,
    val parent: String? = null,
    val level: Int? = null
)

// ========================================
// ENROLLMENT ANALYTICS MODELS
// ========================================

@Serializable
data class EnrollmentAnalyticsResponse(
    val headers: List<EnrollmentAnalyticsHeader> = emptyList(),
    val metaData: EnrollmentAnalyticsMetadata? = null,
    val rows: List<List<String>> = emptyList(),
    val width: Int = 0,
    val height: Int = 0
)

@Serializable
data class EnrollmentAnalyticsHeader(
    val name: String,
    val column: String,
    val valueType: String,
    val type: String,
    val hidden: Boolean = false,
    val meta: Boolean = false,
    val programStage: String? = null,
    val repeatIndex: Int? = null
)

@Serializable
data class EnrollmentAnalyticsMetadata(
    val items: Map<String, EnrollmentAnalyticsMetadataItem> = emptyMap(),
    val dimensions: Map<String, List<String>> = emptyMap(),
    val pager: AnalyticsPager? = null,
    val ouHierarchy: Map<String, List<String>> = emptyMap()
)

@Serializable
data class EnrollmentAnalyticsMetadataItem(
    val uid: String? = null,
    val code: String? = null,
    val name: String? = null,
    val programStage: String? = null,
    val valueType: String? = null,
    val optionSet: String? = null,
    val dimensionItemType: String? = null,
    val totalAggregationType: String? = null,
    val parent: String? = null,
    val level: Int? = null
)

// ========================================
// OUTLIER DETECTION MODELS
// ========================================

@Serializable
data class OutlierDetectionResponse(
    val outlierValues: List<OutlierValue> = emptyList(),
    val count: Int = 0
)

@Serializable
data class OutlierValue(
    val de: String,
    val deName: String,
    val pe: String,
    val peName: String,
    val ou: String,
    val ouName: String,
    val coc: String? = null,
    val cocName: String? = null,
    val aoc: String? = null,
    val aocName: String? = null,
    val value: Double,
    val mean: Double,
    val stdDev: Double,
    val absDev: Double,
    val zScore: Double,
    val lowerBound: Double,
    val upperBound: Double,
    val followUp: Boolean = false,
    val modifiedZScore: Double? = null
)

// ========================================
// VALIDATION RESULTS ANALYTICS MODELS
// ========================================

@Serializable
data class ValidationResultsAnalyticsResponse(
    val validationRuleViolations: List<ValidationRuleViolationAnalytics> = emptyList(),
    val count: Int = 0
)

@Serializable
data class ValidationRuleViolationAnalytics(
    val validationRule: ValidationRuleAnalyticsInfo,
    val organisationUnit: OrganisationUnitAnalyticsInfo,
    val period: PeriodAnalyticsInfo,
    val attributeOptionCombo: AttributeOptionComboAnalyticsInfo? = null,
    val leftSideValue: Double? = null,
    val rightSideValue: Double? = null,
    val dayInPeriod: Int? = null,
    val created: String? = null,
    val notificationSent: Boolean = false
)

@Serializable
data class ValidationRuleAnalyticsInfo(
    val id: String,
    val name: String,
    val description: String? = null,
    val operator: String,
    val importance: String? = null
)

@Serializable
data class OrganisationUnitAnalyticsInfo(
    val id: String,
    val name: String,
    val code: String? = null,
    val level: Int? = null,
    val path: String? = null
)

@Serializable
data class PeriodAnalyticsInfo(
    val id: String,
    val name: String,
    val periodType: String,
    val startDate: String,
    val endDate: String
)

@Serializable
data class AttributeOptionComboAnalyticsInfo(
    val id: String,
    val name: String,
    val code: String? = null
)

// ========================================
// DATA STATISTICS MODELS
// ========================================

@Serializable
data class DataStatisticsResponse(
    val dataStatistics: List<DataStatistic> = emptyList()
)

@Serializable
data class DataStatistic(
    val favorite: String? = null,
    val eventType: String,
    val timestamp: String,
    val views: Int = 0,
    val averageViewTime: Double = 0.0,
    val totalViewTime: Double = 0.0
)

// ========================================
// GEO FEATURES MODELS
// ========================================

@Serializable
data class GeoFeaturesResponse(
    val type: String = "FeatureCollection",
    val features: List<GeoFeature> = emptyList()
)

@Serializable
data class GeoFeature(
    val type: String = "Feature",
    val id: String,
    val geometry: GeoGeometry? = null,
    val properties: GeoProperties
)

@Serializable
data class GeoGeometry(
    val type: String,
    val coordinates: List<Double> = emptyList()
)

@Serializable
data class GeoProperties(
    val id: String,
    val name: String,
    val hasCoordinatesDown: Boolean = false,
    val hasCoordinatesUp: Boolean = false,
    val level: Int? = null,
    val parent: String? = null,
    val parentGraph: String? = null,
    val type: String? = null
)

// ========================================
// DIMENSION MODELS
// ========================================

@Serializable
data class Dimension(
    val dimension: String,
    val items: List<DimensionItem> = emptyList()
)

@Serializable
data class DimensionItem(
    val id: String,
    val name: String? = null,
    val code: String? = null,
    val shortName: String? = null,
    val dimensionItemType: String? = null
)

// ========================================
// ANALYTICS REQUEST MODELS
// ========================================

@Serializable
data class AnalyticsRequest(
    val dimension: List<String> = emptyList(),
    val filter: List<String> = emptyList(),
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
    val hideEmptyRows: Boolean = false,
    val hideEmptyColumns: Boolean = false,
    val showHierarchy: Boolean = false,
    val includeNumDen: Boolean = false,
    val includeMetadataDetails: Boolean = false,
    val displayProperty: String = "NAME",
    val outputIdScheme: String? = null,
    val inputIdScheme: String? = null,
    val approvalLevel: String? = null,
    val relativePeriodDate: String? = null,
    val userOrgUnit: String? = null,
    val columns: List<String> = emptyList(),
    val rows: List<String> = emptyList(),
    val order: String? = null,
    val timeField: String? = null,
    val orgUnitField: String? = null
)

@Serializable
data class EventAnalyticsRequest(
    val program: String,
    val stage: String? = null,
    val startDate: String,
    val endDate: String,
    val dimension: List<String> = emptyList(),
    val filter: List<String> = emptyList(),
    val value: String? = null,
    val aggregationType: String? = null,
    val showHierarchy: Boolean = false,
    val sortOrder: String = "ASC",
    val limit: Int? = null,
    val outputType: String = "EVENT",
    val collapseDataDimensions: Boolean = false,
    val skipMeta: Boolean = false,
    val skipData: Boolean = false,
    val skipRounding: Boolean = false,
    val completedOnly: Boolean = false,
    val hierarchyMeta: Boolean = false,
    val includeMetadataDetails: Boolean = false,
    val includeNumDen: Boolean = false,
    val displayProperty: String = "NAME",
    val outputIdScheme: String? = null,
    val inputIdScheme: String? = null,
    val approvalLevel: String? = null,
    val relativePeriodDate: String? = null,
    val userOrgUnit: String? = null,
    val coordinatesOnly: Boolean = false,
    val dataIdScheme: String? = null,
    val page: Int? = null,
    val pageSize: Int? = null,
    val paging: Boolean = false,
    val totalPages: Boolean = false,
    val rowContext: Boolean = false,
    val columns: List<String> = emptyList(),
    val rows: List<String> = emptyList(),
    val order: String? = null,
    val timeField: String? = null,
    val orgUnitField: String? = null
)

@Serializable
data class EnrollmentAnalyticsRequest(
    val program: String,
    val startDate: String,
    val endDate: String,
    val dimension: List<String> = emptyList(),
    val filter: List<String> = emptyList(),
    val value: String? = null,
    val aggregationType: String? = null,
    val showHierarchy: Boolean = false,
    val sortOrder: String = "ASC",
    val limit: Int? = null,
    val outputType: String = "ENROLLMENT",
    val collapseDataDimensions: Boolean = false,
    val skipMeta: Boolean = false,
    val skipData: Boolean = false,
    val skipRounding: Boolean = false,
    val completedOnly: Boolean = false,
    val hierarchyMeta: Boolean = false,
    val includeMetadataDetails: Boolean = false,
    val includeNumDen: Boolean = false,
    val displayProperty: String = "NAME",
    val outputIdScheme: String? = null,
    val inputIdScheme: String? = null,
    val approvalLevel: String? = null,
    val relativePeriodDate: String? = null,
    val userOrgUnit: String? = null,
    val coordinatesOnly: Boolean = false,
    val dataIdScheme: String? = null,
    val page: Int? = null,
    val pageSize: Int? = null,
    val paging: Boolean = false,
    val totalPages: Boolean = false,
    val rowContext: Boolean = false,
    val columns: List<String> = emptyList(),
    val rows: List<String> = emptyList(),
    val order: String? = null,
    val timeField: String? = null,
    val orgUnitField: String? = null
)

// ========================================
// PIVOT TABLE MODELS
// ========================================

@Serializable
data class PivotTable(
    val id: String? = null,
    val name: String,
    val description: String? = null,
    val created: String? = null,
    val lastUpdated: String? = null,
    val columns: List<Dimension> = emptyList(),
    val rows: List<Dimension> = emptyList(),
    val filters: List<Dimension> = emptyList(),
    val aggregationType: String = "DEFAULT",
    val measureCriteria: String? = null,
    val skipRounding: Boolean = false,
    val showDimensionLabels: Boolean = true,
    val hideEmptyRows: Boolean = false,
    val hideEmptyColumns: Boolean = false,
    val showHierarchy: Boolean = false,
    val completedOnly: Boolean = false,
    val displayDensity: String = "NORMAL",
    val fontSize: String = "NORMAL",
    val digitGroupSeparator: String = "SPACE",
    val legendSet: LegendSet? = null,
    val legendDisplayStyle: String = "FILL",
    val legendDisplayStrategy: String = "FIXED",
    val numberType: String = "VALUE",
    val regression: Boolean = false,
    val cumulative: Boolean = false,
    val sortOrder: Int = 0,
    val topLimit: Int = 0,
    val rowTotals: Boolean = false,
    val colTotals: Boolean = false,
    val rowSubTotals: Boolean = false,
    val colSubTotals: Boolean = false,
    val hideTitle: Boolean = false,
    val hideSubtitle: Boolean = false,
    val title: String? = null,
    val subtitle: String? = null,
    val relativePeriods: RelativePeriods? = null,
    val userOrgUnit: Boolean = false,
    val userOrgUnitChildren: Boolean = false,
    val userOrgUnitGrandChildren: Boolean = false,
    val organisationUnits: List<OrganisationUnit> = emptyList(),
    val periods: List<Period> = emptyList(),
    val dataElements: List<DataElement> = emptyList(),
    val dataElementGroups: List<DataElementGroup> = emptyList(),
    val indicators: List<Indicator> = emptyList(),
    val indicatorGroups: List<IndicatorGroup> = emptyList(),
    val dataSets: List<DataSet> = emptyList(),
    val programIndicators: List<ProgramIndicator> = emptyList(),
    val categoryDimensions: List<CategoryDimension> = emptyList(),
    val dataElementGroupSetDimensions: List<DataElementGroupSetDimension> = emptyList(),
    val organisationUnitGroupSetDimensions: List<OrganisationUnitGroupSetDimension> = emptyList(),
    val categoryOptionGroupSetDimensions: List<CategoryOptionGroupSetDimension> = emptyList()
)

@Serializable
data class LegendSet(
    val id: String,
    val name: String,
    val legends: List<Legend> = emptyList()
)

@Serializable
data class Legend(
    val id: String,
    val name: String,
    val startValue: Double,
    val endValue: Double,
    val color: String
)

@Serializable
data class RelativePeriods(
    val thisDay: Boolean = false,
    val yesterday: Boolean = false,
    val last3Days: Boolean = false,
    val last7Days: Boolean = false,
    val last14Days: Boolean = false,
    val thisWeek: Boolean = false,
    val lastWeek: Boolean = false,
    val thisBiWeek: Boolean = false,
    val lastBiWeek: Boolean = false,
    val thisMonth: Boolean = false,
    val lastMonth: Boolean = false,
    val thisBimonth: Boolean = false,
    val lastBimonth: Boolean = false,
    val thisQuarter: Boolean = false,
    val lastQuarter: Boolean = false,
    val thisSixMonth: Boolean = false,
    val lastSixMonth: Boolean = false,
    val weeksThisYear: Boolean = false,
    val monthsThisYear: Boolean = false,
    val biMonthsThisYear: Boolean = false,
    val quartersThisYear: Boolean = false,
    val thisYear: Boolean = false,
    val monthsLastYear: Boolean = false,
    val quartersLastYear: Boolean = false,
    val lastYear: Boolean = false,
    val last5Years: Boolean = false,
    val last10Years: Boolean = false,
    val last12Months: Boolean = false,
    val last6Months: Boolean = false,
    val last3Months: Boolean = false,
    val last6BiMonths: Boolean = false,
    val last4Quarters: Boolean = false,
    val last2SixMonths: Boolean = false,
    val thisFinancialYear: Boolean = false,
    val lastFinancialYear: Boolean = false,
    val last5FinancialYears: Boolean = false,
    val last10FinancialYears: Boolean = false
)

@Serializable
data class Period(
    val id: String,
    val name: String,
    val periodType: String,
    val startDate: String,
    val endDate: String
)

@Serializable
data class DataElement(
    val id: String,
    val name: String,
    val shortName: String? = null,
    val code: String? = null,
    val valueType: String? = null,
    val aggregationType: String? = null
)

@Serializable
data class DataElementGroup(
    val id: String,
    val name: String,
    val shortName: String? = null,
    val code: String? = null
)

@Serializable
data class Indicator(
    val id: String,
    val name: String,
    val shortName: String? = null,
    val code: String? = null,
    val numerator: String,
    val denominator: String,
    val indicatorType: IndicatorType
)

@Serializable
data class IndicatorType(
    val id: String,
    val name: String,
    val factor: Int
)

@Serializable
data class IndicatorGroup(
    val id: String,
    val name: String,
    val shortName: String? = null,
    val code: String? = null
)

@Serializable
data class DataSet(
    val id: String,
    val name: String,
    val shortName: String? = null,
    val code: String? = null,
    val periodType: String
)

@Serializable
data class ProgramIndicator(
    val id: String,
    val name: String,
    val shortName: String? = null,
    val code: String? = null,
    val expression: String,
    val filter: String? = null,
    val aggregationType: String
)

@Serializable
data class OrganisationUnit(
    val id: String,
    val name: String,
    val shortName: String? = null,
    val code: String? = null,
    val level: Int? = null,
    val path: String? = null
)

@Serializable
data class CategoryDimension(
    val category: Category,
    val categoryOptions: List<CategoryOption> = emptyList()
)

@Serializable
data class Category(
    val id: String,
    val name: String,
    val shortName: String? = null,
    val code: String? = null
)

@Serializable
data class CategoryOption(
    val id: String,
    val name: String,
    val shortName: String? = null,
    val code: String? = null
)

@Serializable
data class DataElementGroupSetDimension(
    val dataElementGroupSet: DataElementGroupSet,
    val dataElementGroups: List<DataElementGroup> = emptyList()
)

@Serializable
data class DataElementGroupSet(
    val id: String,
    val name: String,
    val shortName: String? = null,
    val code: String? = null
)

@Serializable
data class OrganisationUnitGroupSetDimension(
    val organisationUnitGroupSet: OrganisationUnitGroupSet,
    val organisationUnitGroups: List<OrganisationUnitGroup> = emptyList()
)

@Serializable
data class OrganisationUnitGroupSet(
    val id: String,
    val name: String,
    val shortName: String? = null,
    val code: String? = null
)

@Serializable
data class OrganisationUnitGroup(
    val id: String,
    val name: String,
    val shortName: String? = null,
    val code: String? = null
)

@Serializable
data class CategoryOptionGroupSetDimension(
    val categoryOptionGroupSet: CategoryOptionGroupSet,
    val categoryOptionGroups: List<CategoryOptionGroup> = emptyList()
)

@Serializable
data class CategoryOptionGroupSet(
    val id: String,
    val name: String,
    val shortName: String? = null,
    val code: String? = null
)

@Serializable
data class CategoryOptionGroup(
    val id: String,
    val name: String,
    val shortName: String? = null,
    val code: String? = null
)

// ========================================
// VALIDATION RESULTS MODELS
// ========================================

@Serializable
data class ValidationResultsResponse(
    val validationRuleViolations: List<ValidationRuleViolation> = emptyList(),
    val pager: AnalyticsPager? = null
)

@Serializable
data class ValidationRuleViolation(
    val validationRule: ValidationRuleInfo,
    val period: PeriodInfo,
    val organisationUnit: OrganisationUnitInfo,
    val attributeOptionCombo: AttributeOptionComboInfo? = null,
    val leftSideValue: Double,
    val rightSideValue: Double,
    val dayInPeriod: Int? = null,
    val notificationSent: Boolean = false,
    val created: String? = null
)

@Serializable
data class ValidationRuleInfo(
    val uid: String,
    val name: String,
    val description: String? = null,
    val operator: String,
    val importance: String? = null
)

@Serializable
data class PeriodInfo(
    val id: String,
    val name: String,
    val periodType: String? = null,
    val startDate: String? = null,
    val endDate: String? = null
)

@Serializable
data class OrganisationUnitInfo(
    val uid: String,
    val name: String,
    val code: String? = null,
    val level: Int? = null,
    val path: String? = null
)

@Serializable
data class AttributeOptionComboInfo(
    val uid: String,
    val name: String,
    val code: String? = null
)

// ========================================
// GEOSPATIAL ANALYTICS MODELS
// ========================================

@Serializable
data class GeospatialAnalyticsResponse(
    val headers: List<AnalyticsHeader> = emptyList(),
    val metaData: GeospatialAnalyticsMetaData? = null,
    val rows: List<List<String>> = emptyList(),
    val width: Int = 0,
    val height: Int = 0
)

@Serializable
data class GeospatialAnalyticsMetaData(
    val items: Map<String, AnalyticsMetaDataItem> = emptyMap(),
    val dimensions: Map<String, List<String>> = emptyMap(),
    val spatialSupport: SpatialSupport? = null
)

@Serializable
data class SpatialSupport(
    val type: String,
    val crs: String? = null
)

// ========================================
// ANALYTICS DIMENSIONS MODELS
// ========================================

@Serializable
data class AnalyticsDimensionsResponse(
    val dimensions: List<AnalyticsDimension> = emptyList(),
    val pager: AnalyticsPager? = null
)

@Serializable
data class AnalyticsDimension(
    val uid: String,
    val name: String,
    val code: String? = null,
    val description: String? = null,
    val dimensionType: String,
    val valueType: String? = null,
    val totalAggregationType: String? = null,
    val domainType: String? = null,
    val expression: String? = null,
    val items: List<DimensionItem> = emptyList(),
    val dataDimensionType: String? = null,
    val programStage: ProgramStageReference? = null,
    val program: ProgramReference? = null,
    val legendSet: LegendSetReference? = null,
    val optionSet: OptionSetReference? = null
)

@Serializable
data class ProgramStageReference(
    val uid: String,
    val name: String
)

@Serializable
data class ProgramReference(
    val uid: String,
    val name: String
)

@Serializable
data class LegendSetReference(
    val uid: String,
    val name: String
)

@Serializable
data class OptionSetReference(
    val uid: String,
    val name: String
)

// ========================================
// ANALYTICS FAVORITES MODELS
// ========================================

@Serializable
data class AnalyticsFavoritesResponse(
    val favorites: List<AnalyticsFavorite> = emptyList(),
    val pager: AnalyticsPager? = null
)

@Serializable
data class AnalyticsFavorite(
    val uid: String,
    val name: String,
    val description: String? = null,
    val type: String,
    val created: String? = null,
    val lastUpdated: String? = null,
    val user: UserReference? = null,
    val publicAccess: String? = null,
    val externalAccess: Boolean = false,
    val userAccesses: List<UserAccess> = emptyList(),
    val userGroupAccesses: List<UserGroupAccess> = emptyList(),
    val dimensions: List<FavoriteDimension> = emptyList(),
    val filters: List<FavoriteDimension> = emptyList(),
    val columns: List<FavoriteDimension> = emptyList(),
    val rows: List<FavoriteDimension> = emptyList()
)

@Serializable
data class UserReference(
    val uid: String,
    val name: String,
    val username: String? = null
)

@Serializable
data class UserAccess(
    val id: String,
    val access: String,
    val displayName: String? = null
)

@Serializable
data class UserGroupAccess(
    val id: String,
    val access: String,
    val displayName: String? = null
)

@Serializable
data class FavoriteDimension(
    val dimension: String,
    val items: List<DimensionItem> = emptyList(),
    val legendSet: LegendSetReference? = null,
    val programStage: ProgramStageReference? = null,
    val program: ProgramReference? = null
)

// ========================================
// ANALYTICS QUERY MODELS
// ========================================

@Serializable
data class AnalyticsQuery(
    val dimensions: List<String>,
    val filters: List<String> = emptyList(),
    val aggregationType: AggregationType? = null,
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
    val hideEmptyRows: Boolean = false,
    val hideEmptyColumns: Boolean = false,
    val showHierarchy: Boolean = false,
    val includeNumDen: Boolean = false,
    val includeMetadataDetails: Boolean = false,
    val displayProperty: DisplayProperty = DisplayProperty.NAME,
    val outputIdScheme: String = "UID",
    val inputIdScheme: String = "UID",
    val approvalLevel: String? = null,
    val relativePeriodDate: String? = null,
    val userOrgUnit: String? = null,
    val columns: List<String> = emptyList(),
    val rows: List<String> = emptyList(),
    val order: String? = null,
    val timeField: String? = null,
    val orgUnitField: String? = null,
    val format: AnalyticsFormat = AnalyticsFormat.JSON
)

// Enums are defined in AnalyticsApi.kt to avoid duplication
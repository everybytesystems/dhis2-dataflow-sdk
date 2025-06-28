package com.everybytesystems.dataflow.metadata.models

import kotlinx.serialization.Serializable

/**
 * DHIS2 Metadata models
 */

@Serializable
data class DataElement(
    val id: String,
    val name: String,
    val displayName: String? = null,
    val shortName: String? = null,
    val code: String? = null,
    val description: String? = null,
    val valueType: ValueType = ValueType.TEXT,
    val aggregationType: AggregationType = AggregationType.SUM,
    val domainType: DomainType = DomainType.AGGREGATE,
    val categoryCombo: CategoryCombo? = null,
    val optionSet: OptionSet? = null,
    val legendSets: List<LegendSet> = emptyList(),
    val created: String? = null,
    val lastUpdated: String? = null
)

@Serializable
data class DataSet(
    val id: String,
    val name: String,
    val displayName: String? = null,
    val shortName: String? = null,
    val code: String? = null,
    val description: String? = null,
    val periodType: PeriodType = PeriodType.MONTHLY,
    val categoryCombo: CategoryCombo? = null,
    val dataSetElements: List<DataSetElement> = emptyList(),
    val organisationUnits: List<OrganisationUnit> = emptyList(),
    val created: String? = null,
    val lastUpdated: String? = null
)

@Serializable
data class DataSetElement(
    val dataElement: DataElement,
    val categoryCombo: CategoryCombo? = null
)

@Serializable
data class OrganisationUnit(
    val id: String,
    val name: String,
    val displayName: String? = null,
    val shortName: String? = null,
    val code: String? = null,
    val description: String? = null,
    val level: Int? = null,
    val path: String? = null,
    val parent: OrganisationUnit? = null,
    val children: List<OrganisationUnit> = emptyList(),
    val geometry: Geometry? = null,
    val created: String? = null,
    val lastUpdated: String? = null
)

@Serializable
data class CategoryCombo(
    val id: String,
    val name: String,
    val displayName: String? = null,
    val code: String? = null,
    val categories: List<Category> = emptyList(),
    val categoryOptionCombos: List<CategoryOptionCombo> = emptyList(),
    val created: String? = null,
    val lastUpdated: String? = null
)

@Serializable
data class Category(
    val id: String,
    val name: String,
    val displayName: String? = null,
    val code: String? = null,
    val categoryOptions: List<CategoryOption> = emptyList(),
    val created: String? = null,
    val lastUpdated: String? = null
)

@Serializable
data class CategoryOption(
    val id: String,
    val name: String,
    val displayName: String? = null,
    val code: String? = null,
    val shortName: String? = null,
    val created: String? = null,
    val lastUpdated: String? = null
)

@Serializable
data class CategoryOptionCombo(
    val id: String,
    val name: String,
    val displayName: String? = null,
    val code: String? = null,
    val categoryOptions: List<CategoryOption> = emptyList(),
    val created: String? = null,
    val lastUpdated: String? = null
)

@Serializable
data class OptionSet(
    val id: String,
    val name: String,
    val displayName: String? = null,
    val code: String? = null,
    val valueType: ValueType = ValueType.TEXT,
    val options: List<Option> = emptyList(),
    val created: String? = null,
    val lastUpdated: String? = null
)

@Serializable
data class Option(
    val id: String,
    val name: String,
    val displayName: String? = null,
    val code: String,
    val sortOrder: Int? = null,
    val created: String? = null,
    val lastUpdated: String? = null
)

@Serializable
data class LegendSet(
    val id: String,
    val name: String,
    val displayName: String? = null,
    val legends: List<Legend> = emptyList(),
    val created: String? = null,
    val lastUpdated: String? = null
)

@Serializable
data class Legend(
    val id: String,
    val name: String,
    val displayName: String? = null,
    val color: String? = null,
    val startValue: Double? = null,
    val endValue: Double? = null,
    val created: String? = null,
    val lastUpdated: String? = null
)

@Serializable
data class Program(
    val id: String,
    val name: String,
    val displayName: String? = null,
    val shortName: String? = null,
    val code: String? = null,
    val description: String? = null,
    val programType: ProgramType = ProgramType.WITH_REGISTRATION,
    val categoryCombo: CategoryCombo? = null,
    val programStages: List<ProgramStage> = emptyList(),
    val programTrackedEntityAttributes: List<ProgramTrackedEntityAttribute> = emptyList(),
    val organisationUnits: List<OrganisationUnit> = emptyList(),
    val created: String? = null,
    val lastUpdated: String? = null
)

@Serializable
data class ProgramStage(
    val id: String,
    val name: String,
    val displayName: String? = null,
    val code: String? = null,
    val description: String? = null,
    val sortOrder: Int? = null,
    val repeatable: Boolean = false,
    val programStageDataElements: List<ProgramStageDataElement> = emptyList(),
    val created: String? = null,
    val lastUpdated: String? = null
)

@Serializable
data class ProgramStageDataElement(
    val id: String,
    val dataElement: DataElement,
    val compulsory: Boolean = false,
    val allowProvidedElsewhere: Boolean = false,
    val sortOrder: Int? = null,
    val created: String? = null,
    val lastUpdated: String? = null
)

@Serializable
data class TrackedEntityType(
    val id: String,
    val name: String,
    val displayName: String? = null,
    val code: String? = null,
    val description: String? = null,
    val trackedEntityTypeAttributes: List<TrackedEntityTypeAttribute> = emptyList(),
    val created: String? = null,
    val lastUpdated: String? = null
)

@Serializable
data class TrackedEntityAttribute(
    val id: String,
    val name: String,
    val displayName: String? = null,
    val shortName: String? = null,
    val code: String? = null,
    val description: String? = null,
    val valueType: ValueType = ValueType.TEXT,
    val unique: Boolean = false,
    val optionSet: OptionSet? = null,
    val created: String? = null,
    val lastUpdated: String? = null
)

@Serializable
data class TrackedEntityTypeAttribute(
    val id: String,
    val trackedEntityAttribute: TrackedEntityAttribute,
    val mandatory: Boolean = false,
    val searchable: Boolean = false,
    val displayInList: Boolean = false,
    val sortOrder: Int? = null,
    val created: String? = null,
    val lastUpdated: String? = null
)

@Serializable
data class ProgramTrackedEntityAttribute(
    val id: String,
    val trackedEntityAttribute: TrackedEntityAttribute,
    val mandatory: Boolean = false,
    val allowFutureDate: Boolean = false,
    val displayInList: Boolean = false,
    val sortOrder: Int? = null,
    val created: String? = null,
    val lastUpdated: String? = null
)

@Serializable
data class Geometry(
    val type: String,
    val coordinates: List<Double>
)

// Enums
@Serializable
enum class ValueType {
    TEXT, LONG_TEXT, LETTER, PHONE_NUMBER, EMAIL, BOOLEAN, TRUE_ONLY, DATE, DATETIME, TIME,
    NUMBER, UNIT_INTERVAL, PERCENTAGE, INTEGER, INTEGER_POSITIVE, INTEGER_NEGATIVE, INTEGER_ZERO_OR_POSITIVE,
    TRACKER_ASSOCIATE, USERNAME, COORDINATE, ORGANISATION_UNIT, REFERENCE, AGE, URL, FILE_RESOURCE, IMAGE
}

@Serializable
enum class AggregationType {
    SUM, AVERAGE, AVERAGE_SUM_ORG_UNIT, LAST, LAST_AVERAGE_ORG_UNIT, LAST_IN_PERIOD, LAST_IN_PERIOD_AVERAGE_ORG_UNIT,
    FIRST, FIRST_AVERAGE_ORG_UNIT, COUNT, STDDEV, VARIANCE, MIN, MAX, NONE, CUSTOM, DEFAULT
}

@Serializable
enum class DomainType {
    AGGREGATE, TRACKER
}

@Serializable
enum class PeriodType {
    DAILY, WEEKLY, WEEKLY_WEDNESDAY, WEEKLY_THURSDAY, WEEKLY_SATURDAY, WEEKLY_SUNDAY,
    BI_WEEKLY, MONTHLY, BI_MONTHLY, QUARTERLY, SIX_MONTHLY, SIX_MONTHLY_APRIL, SIX_MONTHLY_NOV,
    YEARLY, FINANCIAL_APRIL, FINANCIAL_JULY, FINANCIAL_OCT, FINANCIAL_NOV
}

@Serializable
enum class ProgramType {
    WITH_REGISTRATION, WITHOUT_REGISTRATION
}

/**
 * Metadata response wrapper
 */
@Serializable
data class MetadataResponse(
    val system: SystemInfo? = null,
    val date: String? = null,
    val dataElements: List<DataElement> = emptyList(),
    val dataSets: List<DataSet> = emptyList(),
    val organisationUnits: List<OrganisationUnit> = emptyList(),
    val categories: List<Category> = emptyList(),
    val categoryOptions: List<CategoryOption> = emptyList(),
    val categoryCombos: List<CategoryCombo> = emptyList(),
    val categoryOptionCombos: List<CategoryOptionCombo> = emptyList(),
    val optionSets: List<OptionSet> = emptyList(),
    val options: List<Option> = emptyList(),
    val legendSets: List<LegendSet> = emptyList(),
    val legends: List<Legend> = emptyList(),
    val programs: List<Program> = emptyList(),
    val programStages: List<ProgramStage> = emptyList(),
    val trackedEntityTypes: List<TrackedEntityType> = emptyList(),
    val trackedEntityAttributes: List<TrackedEntityAttribute> = emptyList()
)

@Serializable
data class SystemInfo(
    val version: String? = null,
    val revision: String? = null,
    val buildTime: String? = null,
    val serverDate: String? = null,
    val contextPath: String? = null,
    val userAgent: String? = null
)
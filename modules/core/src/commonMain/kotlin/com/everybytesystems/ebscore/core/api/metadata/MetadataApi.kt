package com.everybytesystems.ebscore.core.api.metadata

import com.everybytesystems.ebscore.core.api.base.BaseApi
import com.everybytesystems.ebscore.core.config.DHIS2Config
import com.everybytesystems.ebscore.core.network.ApiResponse
import com.everybytesystems.ebscore.core.version.DHIS2Version
import io.ktor.client.*
import kotlinx.serialization.Serializable

/**
 * Complete Metadata API implementation for DHIS2
 */
class MetadataApi(
    httpClient: HttpClient,
    config: DHIS2Config,
    private val version: DHIS2Version
) : BaseApi(httpClient, config) {

    /**
     * Get data elements
     */
    suspend fun getDataElements(
        fields: String = "id,name,valueType,domainType,aggregationType",
        filter: List<String> = emptyList(),
        page: Int = 1,
        pageSize: Int = 50,
        totalPages: Boolean = false
    ): ApiResponse<DataElementsResponse> {
        val params = mutableMapOf<String, String>().apply {
            put("fields", fields)
            put("page", page.toString())
            put("pageSize", pageSize.toString())
            if (totalPages) put("totalPages", "true")
            filter.forEach { put("filter", it) }
        }
        
        return get("dataElements", params)
    }

    /**
     * Get data element by ID
     */
    suspend fun getDataElement(
        id: String,
        fields: String = "id,name,valueType,domainType,aggregationType,description"
    ): ApiResponse<DataElement> {
        val params = mapOf("fields" to fields)
        return get("dataElements/$id", params)
    }

    /**
     * Create data element
     */
    suspend fun createDataElement(dataElement: DataElement): ApiResponse<ImportSummary> {
        return post("dataElements", dataElement)
    }

    /**
     * Update data element
     */
    suspend fun updateDataElement(id: String, dataElement: DataElement): ApiResponse<ImportSummary> {
        return put("dataElements/$id", dataElement)
    }

    /**
     * Delete data element
     */
    suspend fun deleteDataElement(id: String): ApiResponse<Unit> {
        return delete("dataElements/$id")
    }

    /**
     * Get organisation units
     */
    suspend fun getOrganisationUnits(
        fields: String = "id,name,level,parent",
        filter: List<String> = emptyList(),
        page: Int = 1,
        pageSize: Int = 50,
        totalPages: Boolean = false
    ): ApiResponse<OrganisationUnitsResponse> {
        val params = mutableMapOf<String, String>().apply {
            put("fields", fields)
            put("page", page.toString())
            put("pageSize", pageSize.toString())
            if (totalPages) put("totalPages", "true")
            filter.forEach { put("filter", it) }
        }
        
        return get("organisationUnits", params)
    }

    /**
     * Get organisation unit by ID
     */
    suspend fun getOrganisationUnit(
        id: String,
        fields: String = "id,name,level,parent,children"
    ): ApiResponse<OrganisationUnit> {
        val params = mapOf("fields" to fields)
        return get("organisationUnits/$id", params)
    }

    /**
     * Get data sets
     */
    suspend fun getDataSets(
        fields: String = "id,name,periodType,dataSetElements",
        filter: List<String> = emptyList(),
        page: Int = 1,
        pageSize: Int = 50
    ): ApiResponse<DataSetsResponse> {
        val params = mutableMapOf<String, String>().apply {
            put("fields", fields)
            put("page", page.toString())
            put("pageSize", pageSize.toString())
            filter.forEach { put("filter", it) }
        }
        
        return get("dataSets", params)
    }

    /**
     * Get programs
     */
    suspend fun getPrograms(
        fields: String = "id,name,programType,programStages",
        filter: List<String> = emptyList(),
        page: Int = 1,
        pageSize: Int = 50
    ): ApiResponse<ProgramsResponse> {
        val params = mutableMapOf<String, String>().apply {
            put("fields", fields)
            put("page", page.toString())
            put("pageSize", pageSize.toString())
            filter.forEach { put("filter", it) }
        }
        
        return get("programs", params)
    }

    /**
     * Get indicators
     */
    suspend fun getIndicators(
        fields: String = "id,name,numerator,denominator,indicatorType",
        filter: List<String> = emptyList(),
        page: Int = 1,
        pageSize: Int = 50
    ): ApiResponse<IndicatorsResponse> {
        val params = mutableMapOf<String, String>().apply {
            put("fields", fields)
            put("page", page.toString())
            put("pageSize", pageSize.toString())
            filter.forEach { put("filter", it) }
        }
        
        return get("indicators", params)
    }

    /**
     * Get option sets
     */
    suspend fun getOptionSets(
        fields: String = "id,name,options",
        filter: List<String> = emptyList(),
        page: Int = 1,
        pageSize: Int = 50
    ): ApiResponse<OptionSetsResponse> {
        val params = mutableMapOf<String, String>().apply {
            put("fields", fields)
            put("page", page.toString())
            put("pageSize", pageSize.toString())
            filter.forEach { put("filter", it) }
        }
        
        return get("optionSets", params)
    }

    /**
     * Get categories
     */
    suspend fun getCategories(
        fields: String = "id,name,categoryOptions",
        filter: List<String> = emptyList(),
        page: Int = 1,
        pageSize: Int = 50
    ): ApiResponse<CategoriesResponse> {
        val params = mutableMapOf<String, String>().apply {
            put("fields", fields)
            put("page", page.toString())
            put("pageSize", pageSize.toString())
            filter.forEach { put("filter", it) }
        }
        
        return get("categories", params)
    }

    /**
     * Get metadata schema
     */
    suspend fun getSchema(className: String): ApiResponse<Schema> {
        return get("schemas/$className")
    }

    /**
     * Get all schemas
     */
    suspend fun getSchemas(): ApiResponse<SchemasResponse> {
        return get("schemas")
    }

    /**
     * Import metadata
     */
    suspend fun importMetadata(
        metadata: MetadataImport,
        importStrategy: ImportStrategy = ImportStrategy.CREATE_AND_UPDATE,
        atomicMode: AtomicMode = AtomicMode.ALL,
        mergeMode: MergeMode = MergeMode.REPLACE,
        flushMode: FlushMode = FlushMode.AUTO,
        skipSharing: Boolean = false,
        skipValidation: Boolean = false,
        async: Boolean = false
    ): ApiResponse<ImportSummary> {
        val params = mutableMapOf<String, String>().apply {
            put("importStrategy", importStrategy.name)
            put("atomicMode", atomicMode.name)
            put("mergeMode", mergeMode.name)
            put("flushMode", flushMode.name)
            if (skipSharing) put("skipSharing", "true")
            if (skipValidation) put("skipValidation", "true")
            if (async) put("async", "true")
        }
        
        return post("metadata", metadata, params)
    }

    /**
     * Export metadata
     */
    suspend fun exportMetadata(
        dataElements: List<String> = emptyList(),
        organisationUnits: List<String> = emptyList(),
        dataSets: List<String> = emptyList(),
        programs: List<String> = emptyList(),
        indicators: List<String> = emptyList(),
        download: Boolean = false
    ): ApiResponse<MetadataExport> {
        val params = mutableMapOf<String, String>().apply {
            if (dataElements.isNotEmpty()) put("dataElements", dataElements.joinToString(","))
            if (organisationUnits.isNotEmpty()) put("organisationUnits", organisationUnits.joinToString(","))
            if (dataSets.isNotEmpty()) put("dataSets", dataSets.joinToString(","))
            if (programs.isNotEmpty()) put("programs", programs.joinToString(","))
            if (indicators.isNotEmpty()) put("indicators", indicators.joinToString(","))
            if (download) put("download", "true")
        }
        
        return get("metadata", params)
    }

    /**
     * Get metadata dependencies
     */
    suspend fun getMetadataDependencies(
        type: String,
        id: String
    ): ApiResponse<DependenciesResponse> {
        return get("metadata/dependencies/$type/$id")
    }
}

// Enums
enum class ImportStrategy { CREATE, UPDATE, CREATE_AND_UPDATE, DELETE }
enum class AtomicMode { ALL, NONE }
enum class MergeMode { REPLACE, MERGE }
enum class FlushMode { AUTO, OBJECT }

// Data Models
@Serializable
data class DataElementsResponse(
    val dataElements: List<DataElement> = emptyList(),
    val pager: Pager? = null
)

@Serializable
data class DataElement(
    val id: String,
    val name: String,
    val code: String? = null,
    val valueType: String,
    val domainType: String,
    val aggregationType: String,
    val description: String? = null,
    val formName: String? = null,
    val shortName: String? = null,
    val optionSet: OptionSetRef? = null,
    val categoryCombo: CategoryComboRef? = null
)

@Serializable
data class OrganisationUnitsResponse(
    val organisationUnits: List<OrganisationUnit> = emptyList(),
    val pager: Pager? = null
)

@Serializable
data class OrganisationUnit(
    val id: String,
    val name: String,
    val code: String? = null,
    val level: Int,
    val parent: OrganisationUnitRef? = null,
    val children: List<OrganisationUnitRef> = emptyList(),
    val path: String? = null,
    val coordinates: String? = null
)

@Serializable
data class DataSetsResponse(
    val dataSets: List<DataSet> = emptyList(),
    val pager: Pager? = null
)

@Serializable
data class DataSet(
    val id: String,
    val name: String,
    val code: String? = null,
    val periodType: String,
    val dataSetElements: List<DataSetElement> = emptyList(),
    val organisationUnits: List<OrganisationUnitRef> = emptyList()
)

@Serializable
data class ProgramsResponse(
    val programs: List<Program> = emptyList(),
    val pager: Pager? = null
)

@Serializable
data class Program(
    val id: String,
    val name: String,
    val code: String? = null,
    val programType: String,
    val programStages: List<ProgramStage> = emptyList(),
    val organisationUnits: List<OrganisationUnitRef> = emptyList()
)

@Serializable
data class IndicatorsResponse(
    val indicators: List<Indicator> = emptyList(),
    val pager: Pager? = null
)

@Serializable
data class Indicator(
    val id: String,
    val name: String,
    val code: String? = null,
    val numerator: String,
    val denominator: String,
    val indicatorType: IndicatorTypeRef
)

@Serializable
data class OptionSetsResponse(
    val optionSets: List<OptionSet> = emptyList(),
    val pager: Pager? = null
)

@Serializable
data class OptionSet(
    val id: String,
    val name: String,
    val code: String? = null,
    val options: List<Option> = emptyList()
)

@Serializable
data class CategoriesResponse(
    val categories: List<Category> = emptyList(),
    val pager: Pager? = null
)

@Serializable
data class Category(
    val id: String,
    val name: String,
    val code: String? = null,
    val categoryOptions: List<CategoryOptionRef> = emptyList()
)

@Serializable
data class SchemasResponse(
    val schemas: List<Schema> = emptyList()
)

@Serializable
data class Schema(
    val klass: String,
    val singular: String,
    val plural: String,
    val properties: List<Property> = emptyList()
)

@Serializable
data class Property(
    val name: String,
    val fieldName: String,
    val propertyType: String,
    val required: Boolean = false,
    val unique: Boolean = false
)

@Serializable
data class MetadataImport(
    val dataElements: List<DataElement> = emptyList(),
    val organisationUnits: List<OrganisationUnit> = emptyList(),
    val dataSets: List<DataSet> = emptyList(),
    val programs: List<Program> = emptyList(),
    val indicators: List<Indicator> = emptyList(),
    val optionSets: List<OptionSet> = emptyList(),
    val categories: List<Category> = emptyList()
)

@Serializable
data class MetadataExport(
    val dataElements: List<DataElement> = emptyList(),
    val organisationUnits: List<OrganisationUnit> = emptyList(),
    val dataSets: List<DataSet> = emptyList(),
    val programs: List<Program> = emptyList(),
    val indicators: List<Indicator> = emptyList(),
    val optionSets: List<OptionSet> = emptyList(),
    val categories: List<Category> = emptyList()
)

@Serializable
data class ImportSummary(
    val status: String,
    val description: String? = null,
    val importCount: ImportCount,
    val conflicts: List<Conflict> = emptyList(),
    val reference: String? = null
)

@Serializable
data class ImportCount(
    val imported: Int = 0,
    val updated: Int = 0,
    val deleted: Int = 0,
    val ignored: Int = 0
)

@Serializable
data class Conflict(
    val object: String,
    val value: String,
    val errorCode: String? = null
)

@Serializable
data class DependenciesResponse(
    val dependencies: List<Dependency> = emptyList()
)

@Serializable
data class Dependency(
    val type: String,
    val id: String,
    val name: String
)

@Serializable
data class Pager(
    val page: Int,
    val pageCount: Int,
    val total: Int,
    val pageSize: Int,
    val nextPage: String? = null,
    val prevPage: String? = null
)

// Reference types
@Serializable
data class OptionSetRef(val id: String, val name: String? = null)

@Serializable
data class CategoryComboRef(val id: String, val name: String? = null)

@Serializable
data class OrganisationUnitRef(val id: String, val name: String? = null)

@Serializable
data class IndicatorTypeRef(val id: String, val name: String? = null)

@Serializable
data class CategoryOptionRef(val id: String, val name: String? = null)

@Serializable
data class DataSetElement(
    val dataElement: DataElementRef,
    val categoryCombo: CategoryComboRef? = null
)

@Serializable
data class DataElementRef(val id: String, val name: String? = null)

@Serializable
data class ProgramStage(
    val id: String,
    val name: String,
    val programStageDataElements: List<ProgramStageDataElement> = emptyList()
)

@Serializable
data class ProgramStageDataElement(
    val dataElement: DataElementRef,
    val compulsory: Boolean = false,
    val allowProvidedElsewhere: Boolean = false
)

@Serializable
data class Option(
    val id: String,
    val name: String,
    val code: String? = null,
    val sortOrder: Int? = null
)
package com.everybytesystems.dataflow.core.api.metadata

import com.everybytesystems.dataflow.core.api.base.BaseApi
import com.everybytesystems.dataflow.core.config.DHIS2Config
import com.everybytesystems.dataflow.core.network.ApiResponse
import com.everybytesystems.dataflow.core.version.DHIS2Version
import io.ktor.client.*
import kotlinx.serialization.Serializable

/**
 * Metadata API for DHIS2 - Enhanced metadata operations with version support
 */
class MetadataApi(
    httpClient: HttpClient,
    config: DHIS2Config,
    private val version: DHIS2Version
) : BaseApi(httpClient, config) {
    
    suspend fun getDataElements(
        fields: String = "id,name,shortName,code,valueType",
        page: Int = 1,
        pageSize: Int = 50
    ): ApiResponse<DataElementsResponse> {
        val params = mapOf(
            "fields" to fields,
            "page" to page.toString(),
            "pageSize" to pageSize.toString(),
            "paging" to "true"
        )
        return get("dataElements", params)
    }
    
    suspend fun getDataElement(id: String, fields: String = "*"): ApiResponse<DataElement> {
        return get("dataElements/$id", mapOf("fields" to fields))
    }
    
    suspend fun createDataElement(dataElement: DataElement): ApiResponse<ImportResponse> {
        return post("dataElements", dataElement)
    }
    
    suspend fun updateDataElement(id: String, dataElement: DataElement): ApiResponse<ImportResponse> {
        return put("dataElements/$id", dataElement)
    }
    
    suspend fun deleteDataElement(id: String): ApiResponse<ImportResponse> {
        return delete("dataElements/$id")
    }
    
    suspend fun getDataSets(
        fields: String = "id,name,shortName,code,periodType",
        page: Int = 1,
        pageSize: Int = 50
    ): ApiResponse<DataSetsResponse> {
        val params = mapOf(
            "fields" to fields,
            "page" to page.toString(),
            "pageSize" to pageSize.toString(),
            "paging" to "true"
        )
        return get("dataSets", params)
    }
    
    suspend fun getDataSet(id: String, fields: String = "*"): ApiResponse<DataSet> {
        return get("dataSets/$id", mapOf("fields" to fields))
    }
    
    suspend fun getOrganisationUnits(
        fields: String = "id,name,shortName,code,level,path",
        page: Int = 1,
        pageSize: Int = 50
    ): ApiResponse<OrganisationUnitsResponse> {
        val params = mapOf(
            "fields" to fields,
            "page" to page.toString(),
            "pageSize" to pageSize.toString(),
            "paging" to "true"
        )
        return get("organisationUnits", params)
    }
    
    suspend fun getOrganisationUnit(id: String, fields: String = "*"): ApiResponse<OrganisationUnit> {
        return get("organisationUnits/$id", mapOf("fields" to fields))
    }
    
    suspend fun getPrograms(
        fields: String = "id,name,shortName,code,programType",
        page: Int = 1,
        pageSize: Int = 50
    ): ApiResponse<ProgramsResponse> {
        val params = mapOf(
            "fields" to fields,
            "page" to page.toString(),
            "pageSize" to pageSize.toString(),
            "paging" to "true"
        )
        return get("programs", params)
    }
    
    suspend fun getProgram(id: String, fields: String = "*"): ApiResponse<Program> {
        return get("programs/$id", mapOf("fields" to fields))
    }
    
    // ========================================
    // VERSION-AWARE METHODS
    // ========================================
    
    /**
     * Get metadata with version-aware features
     */
    suspend fun getMetadata(
        fields: String? = null,
        filter: List<String> = emptyList(),
        defaults: String = "INCLUDE"
    ): ApiResponse<MetadataResponse> {
        val params = buildMap {
            fields?.let { put("fields", it) }
            if (filter.isNotEmpty()) put("filter", filter.joinToString(","))
            put("defaults", defaults)
        }
        return get("metadata", params)
    }
    
    /**
     * Import metadata with version-aware validation
     */
    suspend fun importMetadata(
        metadata: MetadataImport,
        importStrategy: String = "CREATE_AND_UPDATE",
        atomicMode: String = "ALL",
        mergeMode: String = "REPLACE"
    ): ApiResponse<MetadataImportResponse> {
        val params = mapOf(
            "importStrategy" to importStrategy,
            "atomicMode" to atomicMode,
            "mergeMode" to mergeMode
        )
        return post("metadata", metadata, params)
    }
    
    /**
     * Get metadata gist (2.37+)
     */
    suspend fun getMetadataGist(
        filter: List<String> = emptyList(),
        fields: String = "id,name,displayName"
    ): ApiResponse<MetadataGistResponse> {
        if (!version.supportsMetadataGist()) {
            return ApiResponse.Error(UnsupportedOperationException("Metadata gist not supported in version ${version.versionString}"))
        }
        
        val params = buildMap {
            if (filter.isNotEmpty()) put("filter", filter.joinToString(","))
            put("fields", fields)
        }
        return get("metadata/gist", params)
    }
    
    /**
     * Get metadata dependencies (2.37+)
     */
    suspend fun getMetadataDependencies(
        uid: String,
        type: String
    ): ApiResponse<MetadataDependenciesResponse> {
        if (!version.supportsMetadataDependencies()) {
            return ApiResponse.Error(UnsupportedOperationException("Metadata dependencies not supported in version ${version.versionString}"))
        }
        
        val params = mapOf("uid" to uid, "type" to type)
        return get("metadata/dependencies", params)
    }
    
    /**
     * Validate metadata
     */
    suspend fun validateMetadata(metadata: MetadataImport): ApiResponse<MetadataValidationResponse> {
        return post("metadata/validate", metadata)
    }
    
    /**
     * Get sharing settings for metadata object (2.36+)
     */
    suspend fun getSharing(
        type: String,
        id: String
    ): ApiResponse<SharingResponse> {
        if (!version.supportsMetadataSharing()) {
            return ApiResponse.Error(UnsupportedOperationException("Metadata sharing not supported in version ${version.versionString}"))
        }
        
        return get("sharing", mapOf("type" to type, "id" to id))
    }
    
    /**
     * Update sharing settings for metadata object (2.36+)
     */
    suspend fun updateSharing(
        type: String,
        id: String,
        sharing: SharingSettings
    ): ApiResponse<SharingUpdateResponse> {
        if (!version.supportsMetadataSharing()) {
            return ApiResponse.Error(UnsupportedOperationException("Metadata sharing not supported in version ${version.versionString}"))
        }
        
        val params = mapOf("type" to type, "id" to id)
        return post("sharing", sharing, params)
    }
}

// Data Models
@Serializable
data class DataElement(
    val id: String? = null,
    val name: String,
    val shortName: String? = null,
    val code: String? = null,
    val description: String? = null,
    val valueType: String? = null,
    val aggregationType: String? = null,
    val domainType: String? = null,
    val created: String? = null,
    val lastUpdated: String? = null
)

@Serializable
data class DataElementsResponse(
    val dataElements: List<DataElement> = emptyList(),
    val pager: Pager? = null
)

@Serializable
data class DataSet(
    val id: String? = null,
    val name: String,
    val shortName: String? = null,
    val code: String? = null,
    val description: String? = null,
    val periodType: String? = null,
    val created: String? = null,
    val lastUpdated: String? = null
)

@Serializable
data class DataSetsResponse(
    val dataSets: List<DataSet> = emptyList(),
    val pager: Pager? = null
)

@Serializable
data class OrganisationUnit(
    val id: String? = null,
    val name: String,
    val shortName: String? = null,
    val code: String? = null,
    val description: String? = null,
    val level: Int? = null,
    val path: String? = null,
    val parent: OrganisationUnit? = null,
    val created: String? = null,
    val lastUpdated: String? = null
)

@Serializable
data class OrganisationUnitsResponse(
    val organisationUnits: List<OrganisationUnit> = emptyList(),
    val pager: Pager? = null
)

@Serializable
data class Program(
    val id: String? = null,
    val name: String,
    val shortName: String? = null,
    val code: String? = null,
    val description: String? = null,
    val programType: String? = null,
    val created: String? = null,
    val lastUpdated: String? = null
)

@Serializable
data class ProgramsResponse(
    val programs: List<Program> = emptyList(),
    val pager: Pager? = null
)

@Serializable
data class ImportResponse(
    val status: String,
    val importCount: ImportCount? = null,
    val conflicts: List<ImportConflict> = emptyList(),
    val reference: String? = null
)

@Serializable
data class ImportCount(
    val imported: Int = 0,
    val updated: Int = 0,
    val ignored: Int = 0,
    val deleted: Int = 0
)

@Serializable
data class ImportConflict(
    val `object`: String,
    val value: String,
    val errorCode: String? = null
)

@Serializable
data class Pager(
    val page: Int = 1,
    val pageCount: Int = 1,
    val total: Int = 0,
    val pageSize: Int = 50
)

// ========================================
// VERSION-AWARE MODELS
// ========================================

@Serializable
data class MetadataResponse(
    val system: SystemInfo? = null,
    val date: String? = null,
    val dataElements: List<DataElement> = emptyList(),
    val dataSets: List<DataSet> = emptyList(),
    val organisationUnits: List<OrganisationUnit> = emptyList(),
    val programs: List<Program> = emptyList()
)

@Serializable
data class MetadataImport(
    val dataElements: List<DataElement> = emptyList(),
    val dataSets: List<DataSet> = emptyList(),
    val organisationUnits: List<OrganisationUnit> = emptyList(),
    val programs: List<Program> = emptyList()
)

@Serializable
data class MetadataImportResponse(
    val status: String,
    val stats: ImportStats? = null,
    val typeReports: List<TypeReport> = emptyList(),
    val message: String? = null
)

@Serializable
data class ImportStats(
    val created: Int = 0,
    val updated: Int = 0,
    val deleted: Int = 0,
    val ignored: Int = 0,
    val total: Int = 0
)

@Serializable
data class TypeReport(
    val klass: String,
    val stats: ImportStats,
    val objectReports: List<ObjectReport> = emptyList()
)

@Serializable
data class ObjectReport(
    val klass: String? = null,
    val index: Int? = null,
    val uid: String? = null,
    val errorReports: List<ErrorReport> = emptyList()
)

@Serializable
data class ErrorReport(
    val message: String,
    val mainKlass: String? = null,
    val errorCode: String? = null,
    val errorProperty: String? = null
)

@Serializable
data class SystemInfo(
    val version: String,
    val revision: String? = null,
    val buildTime: String? = null,
    val serverDate: String? = null,
    val contextPath: String? = null
)

@Serializable
data class MetadataGistResponse(
    val gist: List<MetadataGistItem> = emptyList(),
    val pager: Pager? = null
)

@Serializable
data class MetadataGistItem(
    val id: String,
    val name: String,
    val displayName: String? = null,
    val href: String? = null
)

@Serializable
data class MetadataDependenciesResponse(
    val dependencies: List<MetadataDependency> = emptyList()
)

@Serializable
data class MetadataDependency(
    val type: String,
    val id: String,
    val name: String,
    val dependsOn: List<MetadataDependencyReference> = emptyList()
)

@Serializable
data class MetadataDependencyReference(
    val type: String,
    val id: String,
    val name: String
)

@Serializable
data class MetadataValidationResponse(
    val status: String,
    val validationReports: List<ValidationReport> = emptyList()
)

@Serializable
data class ValidationReport(
    val klass: String,
    val errorReports: List<ErrorReport> = emptyList()
)

@Serializable
data class SharingResponse(
    val meta: SharingMeta,
    val `object`: SharingObject
)

@Serializable
data class SharingMeta(
    val allowPublicAccess: Boolean = false,
    val allowExternalAccess: Boolean = false
)

@Serializable
data class SharingObject(
    val id: String,
    val name: String,
    val displayName: String? = null,
    val publicAccess: String? = null,
    val externalAccess: Boolean = false,
    val userAccesses: List<UserAccess> = emptyList(),
    val userGroupAccesses: List<UserGroupAccess> = emptyList()
)

@Serializable
data class SharingSettings(
    val publicAccess: String? = null,
    val externalAccess: Boolean = false,
    val userAccesses: List<UserAccess> = emptyList(),
    val userGroupAccesses: List<UserGroupAccess> = emptyList()
)

@Serializable
data class SharingUpdateResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null
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
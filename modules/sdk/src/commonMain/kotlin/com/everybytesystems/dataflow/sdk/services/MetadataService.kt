package com.everybytesystems.dataflow.sdk.services

import com.everybytesystems.dataflow.core.api.metadata.MetadataApi
import com.everybytesystems.dataflow.core.database.DataCache
import com.everybytesystems.dataflow.core.network.ApiResponse
import com.everybytesystems.dataflow.metadata.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Simplified metadata service without database operations
 * TODO: Replace with full MetadataService when database is implemented
 */
class MetadataService(
    private val metadataApi: MetadataApi,
    private val dataCache: DataCache
) {
    
    /**
     * Get data elements from server
     */
    suspend fun getDataElements(): ApiResponse<List<DataElement>> {
        return when (val response = metadataApi.getDataElements()) {
            is ApiResponse.Success -> {
                ApiResponse.Success(response.data.dataElements ?: emptyList())
            }
            is ApiResponse.Error -> response
            is ApiResponse.Loading -> response
        }
    }
    
    /**
     * Get organisation units from server
     */
    suspend fun getOrganisationUnits(): ApiResponse<List<OrganisationUnit>> {
        return when (val response = metadataApi.getOrganisationUnits()) {
            is ApiResponse.Success -> {
                ApiResponse.Success(response.data.organisationUnits ?: emptyList())
            }
            is ApiResponse.Error -> response
            is ApiResponse.Loading -> response
        }
    }
    
    /**
     * Get data sets from server
     */
    suspend fun getDataSets(): ApiResponse<List<DataSet>> {
        return when (val response = metadataApi.getDataSets()) {
            is ApiResponse.Success -> {
                ApiResponse.Success(response.data.dataSets ?: emptyList())
            }
            is ApiResponse.Error -> response
            is ApiResponse.Loading -> response
        }
    }
    
    /**
     * Get programs from server
     */
    suspend fun getPrograms(): ApiResponse<List<Program>> {
        return when (val response = metadataApi.getPrograms()) {
            is ApiResponse.Success -> {
                ApiResponse.Success(response.data.programs ?: emptyList())
            }
            is ApiResponse.Error -> response
            is ApiResponse.Loading -> response
        }
    }
    
    /**
     * Get program stages from server
     */
    suspend fun getProgramStages(): ApiResponse<List<ProgramStage>> {
        return when (val response = metadataApi.getProgramStages()) {
            is ApiResponse.Success -> {
                ApiResponse.Success(response.data.programStages ?: emptyList())
            }
            is ApiResponse.Error -> response
            is ApiResponse.Loading -> response
        }
    }
    
    /**
     * Get tracked entity types from server
     */
    suspend fun getTrackedEntityTypes(): ApiResponse<List<TrackedEntityType>> {
        return when (val response = metadataApi.getTrackedEntityTypes()) {
            is ApiResponse.Success -> {
                ApiResponse.Success(response.data.trackedEntityTypes ?: emptyList())
            }
            is ApiResponse.Error -> response
            is ApiResponse.Loading -> response
        }
    }
    
    /**
     * Sync all metadata from server
     */
    suspend fun syncAllMetadata(): Flow<MetadataSyncProgress> = flow {
        emit(MetadataSyncProgress("Starting metadata sync...", 0))
        
        emit(MetadataSyncProgress("Syncing data elements...", 20))
        getDataElements()
        
        emit(MetadataSyncProgress("Syncing organisation units...", 40))
        getOrganisationUnits()
        
        emit(MetadataSyncProgress("Syncing data sets...", 60))
        getDataSets()
        
        emit(MetadataSyncProgress("Syncing programs...", 80))
        getPrograms()
        
        emit(MetadataSyncProgress("Metadata sync complete", 100))
    }
    
    /**
     * Get metadata statistics
     */
    suspend fun getMetadataStats(): MetadataStats {
        return MetadataStats(
            dataElementsCount = 0,
            organisationUnitsCount = 0,
            dataSetsCount = 0,
            programsCount = 0,
            lastSyncTime = null
        )
    }
    
    /**
     * Clear all metadata
     */
    suspend fun clearAllMetadata() {
        dataCache.clear()
    }
}

/**
 * Metadata sync progress
 */
data class MetadataSyncProgress(
    val message: String,
    val progress: Int
)

/**
 * Metadata statistics
 */
data class MetadataStats(
    val dataElementsCount: Int,
    val organisationUnitsCount: Int,
    val dataSetsCount: Int,
    val programsCount: Int,
    val lastSyncTime: String?
)
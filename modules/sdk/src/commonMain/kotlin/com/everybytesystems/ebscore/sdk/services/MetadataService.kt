package com.everybytesystems.ebscore.sdk.services

import com.everybytesystems.ebscore.core.api.metadata.MetadataApi
import com.everybytesystems.ebscore.core.api.metadata.*
import com.everybytesystems.ebscore.core.database.DataCache
import com.everybytesystems.ebscore.core.network.ApiResponse
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
                ApiResponse.Success(response.data.dataElements)
            }
            is ApiResponse.Error -> response
            is ApiResponse.Loading -> response
        }
    }
    
    /**
     * Search data elements by name or code
     */
    suspend fun searchDataElements(query: String): ApiResponse<List<DataElement>> {
        return when (val response = getDataElements()) {
            is ApiResponse.Success -> {
                val filtered = response.data.filter { 
                    it.name.contains(query, ignoreCase = true) || 
                    it.id.contains(query, ignoreCase = true) 
                }
                ApiResponse.Success(filtered)
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
                ApiResponse.Success(response.data.organisationUnits)
            }
            is ApiResponse.Error -> response
            is ApiResponse.Loading -> response
        }
    }
    
    /**
     * Get organisation units by level
     */
    suspend fun getOrganisationUnitsByLevel(level: Int): ApiResponse<List<OrganisationUnit>> {
        return when (val response = getOrganisationUnits()) {
            is ApiResponse.Success -> {
                val filtered = response.data.filter { it.level == level }
                ApiResponse.Success(filtered)
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
                ApiResponse.Success(response.data.dataSets)
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
                ApiResponse.Success(response.data.programs)
            }
            is ApiResponse.Error -> response
            is ApiResponse.Loading -> response
        }
    }
    
    /**
     * Get indicators from server
     */
    suspend fun getIndicators(): ApiResponse<List<Indicator>> {
        return when (val response = metadataApi.getIndicators()) {
            is ApiResponse.Success -> {
                ApiResponse.Success(response.data.indicators)
            }
            is ApiResponse.Error -> response
            is ApiResponse.Loading -> response
        }
    }
    
    /**
     * Get option sets from server
     */
    suspend fun getOptionSets(): ApiResponse<List<OptionSet>> {
        return when (val response = metadataApi.getOptionSets()) {
            is ApiResponse.Success -> {
                ApiResponse.Success(response.data.optionSets)
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
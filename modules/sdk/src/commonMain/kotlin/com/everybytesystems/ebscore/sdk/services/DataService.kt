package com.everybytesystems.ebscore.sdk.services

import com.everybytesystems.ebscore.core.api.system.SystemApi
import com.everybytesystems.ebscore.core.api.system.*
import com.everybytesystems.ebscore.core.database.DataCache
import com.everybytesystems.ebscore.core.network.ApiResponse
import com.everybytesystems.ebscore.dhis2.DHIS2Client
import com.everybytesystems.ebscore.dhis2.DHIS2DataValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

/**
 * High-level data service for DHIS2 data operations
 */
class DataService(
    private val systemApi: SystemApi,
    private val dataCache: DataCache,
    private val dhis2Client: DHIS2Client
) {
    
    /**
     * Get system information
     */
    suspend fun getSystemInfo(): ApiResponse<SystemInfo> {
        return systemApi.getSystemInfo()
    }
    
    /**
     * Test connection
     */
    suspend fun ping(): ApiResponse<String> {
        return systemApi.ping()
    }
    
    /**
     * Submit data values to DHIS2
     */
    suspend fun submitDataValues(dataValues: List<DataValue>): ApiResponse<String> {
        return try {
            // Convert to DHIS2 format
            val dhis2DataValues = dataValues.map { dataValue ->
                DHIS2DataValue(
                    dataElement = dataValue.dataElement,
                    value = dataValue.value,
                    orgUnit = dataValue.orgUnit,
                    period = dataValue.period
                )
            }
            
            // Submit to DHIS2 using POST to dataValueSets endpoint
            // Create a simple JSON string manually for now
            val dataValuesJson = dhis2DataValues.joinToString(",", "[", "]") { dataValue ->
                """{"dataElement":"${dataValue.dataElement}","value":"${dataValue.value}","orgUnit":"${dataValue.orgUnit}","period":"${dataValue.period}"}"""
            }
            val jsonData = """{"dataValues":$dataValuesJson}"""
            val result = dhis2Client.post("dataValueSets", jsonData)
            
            when (result) {
                is com.everybytesystems.ebscore.network.NetworkResult.Success -> {
                    // Cache submitted data
                    dataValues.forEach { dataCache.cacheDataValue(it) }
                    ApiResponse.Success("Data values submitted successfully")
                }
                is com.everybytesystems.ebscore.network.NetworkResult.Error -> {
                    ApiResponse.Error(result.exception)
                }
                else -> ApiResponse.Error(Exception("Unknown error occurred"))
            }
        } catch (e: Exception) {
            ApiResponse.Error(Exception("Failed to submit data values: ${e.message}", e))
        }
    }
    
    /**
     * Get data values from DHIS2
     */
    suspend fun getDataValues(
        dataElement: String? = null,
        period: String? = null,
        orgUnit: String? = null
    ): ApiResponse<List<DataValue>> {
        return try {
            // First check cache
            val cachedValues = dataCache.getCachedDataValues(dataElement, period, orgUnit)
                .filterIsInstance<DataValue>()
            if (cachedValues.isNotEmpty()) {
                return ApiResponse.Success(cachedValues)
            }
            
            // Build query parameters
            val params = mutableListOf<String>()
            dataElement?.let { params.add("dataElement=$it") }
            period?.let { params.add("period=$it") }
            orgUnit?.let { params.add("orgUnit=$it") }
            
            val queryString = if (params.isNotEmpty()) "?" + params.joinToString("&") else ""
            val result = dhis2Client.get("dataValueSets$queryString")
            
            when (result) {
                is com.everybytesystems.ebscore.network.NetworkResult.Success -> {
                    // Parse JSON response and convert to DataValue objects
                    // For now, return empty list as a placeholder
                    val sdkDataValues: List<DataValue> = emptyList()
                    
                    // Cache results
                    sdkDataValues.forEach { dataCache.cacheDataValue(it) }
                    
                    ApiResponse.Success(sdkDataValues)
                }
                is com.everybytesystems.ebscore.network.NetworkResult.Error -> {
                    ApiResponse.Error(result.exception)
                }
                else -> ApiResponse.Error(Exception("Unknown error occurred"))
            }
        } catch (e: Exception) {
            ApiResponse.Error(Exception("Failed to get data values: ${e.message}", e))
        }
    }
    
    /**
     * Complete a data set
     */
    suspend fun completeDataSet(
        dataSet: String,
        period: String,
        orgUnit: String
    ): ApiResponse<String> {
        return try {
            val jsonData = """{"dataSet":"$dataSet","period":"$period","organisationUnit":"$orgUnit","completed":true}"""
            val result = dhis2Client.post("completeDataSetRegistrations", jsonData)
            
            when (result) {
                is com.everybytesystems.ebscore.network.NetworkResult.Success -> {
                    // Cache completion status
                    dataCache.cacheDataSetCompletion(dataSet, period, orgUnit)
                    ApiResponse.Success("Data set completed successfully")
                }
                is com.everybytesystems.ebscore.network.NetworkResult.Error -> {
                    ApiResponse.Error(result.exception)
                }
                else -> ApiResponse.Error(Exception("Unknown error occurred"))
            }
        } catch (e: Exception) {
            ApiResponse.Error(Exception("Failed to complete data set: ${e.message}", e))
        }
    }
    
    /**
     * Get system statistics
     */
    suspend fun getStatistics(): ApiResponse<SystemInfo> {
        return systemApi.getSystemInfo()
    }
    
    /**
     * Get database information
     */
    suspend fun getDatabaseInfo(): ApiResponse<SystemInfo> {
        return systemApi.getSystemInfo()
    }
    
    /**
     * Get system settings
     */
    suspend fun getSystemSettings(): ApiResponse<SystemSettings> {
        return systemApi.getSystemSettings()
    }
    
    /**
     * Set system setting
     */
    suspend fun setSystemSetting(key: String, value: String): ApiResponse<Unit> {
        return systemApi.setSystemSetting(key, value)
    }
    
    /**
     * Get user settings
     */
    suspend fun getUserSettings(): ApiResponse<Map<String, Any>> {
        return systemApi.getUserSettings()
    }
    
    /**
     * Set user setting
     */
    suspend fun setUserSetting(key: String, value: String): ApiResponse<Unit> {
        return systemApi.setUserSetting(key, value)
    }
    
    /**
     * Clear cache
     */
    suspend fun clearCache(): ApiResponse<Unit> {
        return systemApi.clearCache()
    }
    
    /**
     * Generate analytics tables
     */
    suspend fun generateAnalyticsTables(
        skipResourceTables: Boolean = false,
        skipAggregate: Boolean = false,
        skipEvents: Boolean = false,
        skipEnrollments: Boolean = false,
        lastYears: Int? = null
    ): ApiResponse<TaskSummary> {
        return systemApi.generateAnalyticsTables(skipResourceTables, skipAggregate, skipEvents, skipEnrollments, lastYears)
    }
    
    /**
     * Perform system maintenance
     */
    suspend fun performMaintenance(
        dataPruning: Boolean = false,
        zeroDataValueRemoval: Boolean = false,
        cacheClear: Boolean = false,
        appReload: Boolean = false
    ): ApiResponse<Unit> {
        return systemApi.performMaintenance(dataPruning, zeroDataValueRemoval, cacheClear, appReload)
    }
    
    /**
     * Get tasks
     */
    suspend fun getTasks(
        category: String? = null,
        page: Int = 1,
        pageSize: Int = 50
    ): ApiResponse<TasksResponse> {
        return systemApi.getTasks(category, page, pageSize)
    }
    
    /**
     * Monitor system health
     */
    fun monitorSystemHealth(): Flow<SystemHealthStatus> = flow {
        while (true) {
            val pingResult = ping()
            val systemInfoResult = getSystemInfo()
            
            val status = when {
                pingResult is ApiResponse.Success && systemInfoResult is ApiResponse.Success -> {
                    SystemHealthStatus.Healthy(systemInfoResult.data)
                }
                pingResult is ApiResponse.Error -> {
                    SystemHealthStatus.Unhealthy("Ping failed: ${pingResult.message}")
                }
                systemInfoResult is ApiResponse.Error -> {
                    SystemHealthStatus.Unhealthy("System info failed: ${systemInfoResult.message}")
                }
                else -> {
                    SystemHealthStatus.Unknown
                }
            }
            
            emit(status)
            kotlinx.coroutines.delay(30_000) // Check every 30 seconds
        }
    }
}

/**
 * System health status
 */
sealed class SystemHealthStatus {
    data class Healthy(val systemInfo: SystemInfo) : SystemHealthStatus()
    data class Unhealthy(val reason: String) : SystemHealthStatus()
    object Unknown : SystemHealthStatus()
}
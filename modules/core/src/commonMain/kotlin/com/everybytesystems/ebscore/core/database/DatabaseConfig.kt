package com.everybytesystems.ebscore.core.database

/**
 * Database configuration for DHIS2 DataFlow SDK
 */
data class DatabaseConfig(
    val databaseName: String = "dhis2_dataflow.db",
    val enableWAL: Boolean = true,
    val enableForeignKeys: Boolean = true
)

/**
 * Simple cache interface for basic data storage
 * This replaces SQLDelight temporarily to avoid compilation issues
 */
interface DataCache {
    suspend fun put(key: String, value: String)
    suspend fun get(key: String): String?
    suspend fun remove(key: String)
    suspend fun clear()
    
    // Extended methods for SDK services
    suspend fun cacheTrackedEntity(trackedEntity: Any)
    suspend fun getCachedTrackedEntities(program: String?, orgUnit: String?, attributes: Map<String, String>): List<Any>
    suspend fun cacheEnrollment(enrollment: Any)
    suspend fun cacheEvent(event: Any)
    suspend fun cacheDataValue(dataValue: Any)
    suspend fun getCachedDataValues(dataElement: String?, period: String?, orgUnit: String?): List<Any>
    suspend fun cacheDataSetCompletion(dataSet: String, period: String, orgUnit: String)
    suspend fun cacheOrganisationUnit(orgUnit: Any)
    suspend fun getCachedOrganisationUnits(level: Int?, parent: String?): List<Any>
    suspend fun cacheDataElement(dataElement: Any)
    suspend fun getCachedDataElements(): List<Any>
    suspend fun cacheProgram(program: Any)
    suspend fun getCachedPrograms(): List<Any>
    suspend fun cacheDataSet(dataSet: Any)
    suspend fun getCachedDataSets(): List<Any>
}

/**
 * In-memory implementation of DataCache
 */
class InMemoryDataCache : DataCache {
    private val cache = mutableMapOf<String, String>()
    private val trackedEntities = mutableListOf<Any>()
    private val enrollments = mutableListOf<Any>()
    private val events = mutableListOf<Any>()
    private val dataValues = mutableListOf<Any>()
    private val organisationUnits = mutableListOf<Any>()
    private val dataElements = mutableListOf<Any>()
    private val programs = mutableListOf<Any>()
    private val dataSets = mutableListOf<Any>()
    private val dataSetCompletions = mutableSetOf<String>()
    
    override suspend fun put(key: String, value: String) {
        cache[key] = value
    }
    
    override suspend fun get(key: String): String? {
        return cache[key]
    }
    
    override suspend fun remove(key: String) {
        cache.remove(key)
    }
    
    override suspend fun clear() {
        cache.clear()
        trackedEntities.clear()
        enrollments.clear()
        events.clear()
        dataValues.clear()
        organisationUnits.clear()
        dataElements.clear()
        programs.clear()
        dataSets.clear()
        dataSetCompletions.clear()
    }
    
    // Extended methods implementation
    override suspend fun cacheTrackedEntity(trackedEntity: Any) {
        trackedEntities.add(trackedEntity)
    }
    
    override suspend fun getCachedTrackedEntities(
        program: String?, 
        orgUnit: String?, 
        attributes: Map<String, String>
    ): List<Any> {
        // In a real implementation, this would filter based on parameters
        return trackedEntities.toList()
    }
    
    override suspend fun cacheEnrollment(enrollment: Any) {
        enrollments.add(enrollment)
    }
    
    override suspend fun cacheEvent(event: Any) {
        events.add(event)
    }
    
    override suspend fun cacheDataValue(dataValue: Any) {
        dataValues.add(dataValue)
    }
    
    override suspend fun getCachedDataValues(
        dataElement: String?, 
        period: String?, 
        orgUnit: String?
    ): List<Any> {
        // In a real implementation, this would filter based on parameters
        return dataValues.toList()
    }
    
    override suspend fun cacheDataSetCompletion(dataSet: String, period: String, orgUnit: String) {
        dataSetCompletions.add("$dataSet-$period-$orgUnit")
    }
    
    override suspend fun cacheOrganisationUnit(orgUnit: Any) {
        organisationUnits.add(orgUnit)
    }
    
    override suspend fun getCachedOrganisationUnits(level: Int?, parent: String?): List<Any> {
        // In a real implementation, this would filter based on parameters
        return organisationUnits.toList()
    }
    
    override suspend fun cacheDataElement(dataElement: Any) {
        dataElements.add(dataElement)
    }
    
    override suspend fun getCachedDataElements(): List<Any> {
        return dataElements.toList()
    }
    
    override suspend fun cacheProgram(program: Any) {
        programs.add(program)
    }
    
    override suspend fun getCachedPrograms(): List<Any> {
        return programs.toList()
    }
    
    override suspend fun cacheDataSet(dataSet: Any) {
        dataSets.add(dataSet)
    }
    
    override suspend fun getCachedDataSets(): List<Any> {
        return dataSets.toList()
    }
}
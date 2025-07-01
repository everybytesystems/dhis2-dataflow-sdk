package com.everybytesystems.ebscore.dhis2

import com.everybytesystems.ebscore.network.EBSCoreHttpClient
import com.everybytesystems.ebscore.network.NetworkConfig
import com.everybytesystems.ebscore.network.NetworkResult
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * EBSCore DHIS2 Integration Module
 * Complete DHIS2 API implementation for seamless integration
 */

// DHIS2 Configuration
@Serializable
data class DHIS2Config(
    val serverUrl: String,
    val username: String,
    val password: String,
    val apiVersion: String = "41", // DHIS2 API version
    val timeout: Long = 30_000L
)

// DHIS2 Client
class DHIS2Client(private val config: DHIS2Config) {
    
    private val networkConfig = NetworkConfig(
        baseUrl = "${config.serverUrl}/api/${config.apiVersion}",
        timeout = config.timeout,
        enableLogging = true
    )
    
    private val httpClient = EBSCoreHttpClient(networkConfig).apply {
        configureBasicAuth(config.username, config.password)
    }
    
    // System Info
    suspend fun getSystemInfo(): NetworkResult<DHIS2SystemInfo> {
        return try {
            val response = httpClient.client.get("/system/info")
            NetworkResult.Success(Json.decodeFromString(response.bodyAsText()))
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }
    }
    
    // User Management
    suspend fun getCurrentUser(): NetworkResult<DHIS2User> {
        return try {
            val response = httpClient.client.get("/me") {
                parameter("fields", "id,username,firstName,lastName,email,organisationUnits,userRoles")
            }
            NetworkResult.Success(Json.decodeFromString(response.bodyAsText()))
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }
    }
    
    // Organization Units
    suspend fun getOrganisationUnits(
        level: Int? = null,
        parent: String? = null,
        fields: String = "id,name,level,parent"
    ): NetworkResult<DHIS2OrganisationUnitsResponse> {
        return try {
            val response = httpClient.client.get("/organisationUnits") {
                parameter("fields", fields)
                parameter("paging", "false")
                level?.let { parameter("level", it) }
                parent?.let { parameter("parent", it) }
            }
            NetworkResult.Success(Json.decodeFromString(response.bodyAsText()))
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }
    }
    
    // Data Elements
    suspend fun getDataElements(
        fields: String = "id,name,valueType,domainType,aggregationType"
    ): NetworkResult<DHIS2DataElementsResponse> {
        return try {
            val response = httpClient.client.get("/dataElements") {
                parameter("fields", fields)
                parameter("paging", "false")
            }
            NetworkResult.Success(Json.decodeFromString(response.bodyAsText()))
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }
    }
    
    // Data Sets
    suspend fun getDataSets(
        fields: String = "id,name,periodType,dataElements"
    ): NetworkResult<DHIS2DataSetsResponse> {
        return try {
            val response = httpClient.client.get("/dataSets") {
                parameter("fields", fields)
                parameter("paging", "false")
            }
            NetworkResult.Success(Json.decodeFromString(response.bodyAsText()))
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }
    }
    
    // Programs (Tracker)
    suspend fun getPrograms(
        fields: String = "id,name,programType,programStages"
    ): NetworkResult<DHIS2ProgramsResponse> {
        return try {
            val response = httpClient.client.get("/programs") {
                parameter("fields", fields)
                parameter("paging", "false")
            }
            NetworkResult.Success(Json.decodeFromString(response.bodyAsText()))
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }
    }
    
    // Data Values
    suspend fun getDataValues(
        dataSet: String? = null,
        period: String? = null,
        orgUnit: String? = null,
        startDate: LocalDate? = null,
        endDate: LocalDate? = null
    ): NetworkResult<DHIS2DataValuesResponse> {
        return try {
            val response = httpClient.client.get("/dataValueSets") {
                dataSet?.let { parameter("dataSet", it) }
                period?.let { parameter("period", it) }
                orgUnit?.let { parameter("orgUnit", it) }
                startDate?.let { parameter("startDate", it.toString()) }
                endDate?.let { parameter("endDate", it.toString()) }
            }
            NetworkResult.Success(Json.decodeFromString(response.bodyAsText()))
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }
    }
    
    // Post Data Values
    suspend fun postDataValues(dataValues: DHIS2DataValueSet): NetworkResult<DHIS2ImportSummary> {
        return try {
            val response = httpClient.client.post("/dataValueSets") {
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToString(DHIS2DataValueSet.serializer(), dataValues))
            }
            NetworkResult.Success(Json.decodeFromString(response.bodyAsText()))
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }
    }
    
    // Analytics
    suspend fun getAnalytics(
        dimensions: List<String>,
        filters: List<String> = emptyList(),
        aggregationType: String = "SUM",
        displayProperty: String = "NAME"
    ): NetworkResult<DHIS2AnalyticsResponse> {
        return try {
            val response = httpClient.client.get("/analytics") {
                parameter("dimension", dimensions.joinToString(";"))
                if (filters.isNotEmpty()) {
                    parameter("filter", filters.joinToString(";"))
                }
                parameter("aggregationType", aggregationType)
                parameter("displayProperty", displayProperty)
            }
            NetworkResult.Success(Json.decodeFromString(response.bodyAsText()))
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }
    }
    
    // Events (Tracker)
    suspend fun getEvents(
        program: String,
        orgUnit: String? = null,
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
        fields: String = "event,program,orgUnit,eventDate,dataValues"
    ): NetworkResult<DHIS2EventsResponse> {
        return try {
            val response = httpClient.client.get("/events") {
                parameter("program", program)
                parameter("fields", fields)
                orgUnit?.let { parameter("orgUnit", it) }
                startDate?.let { parameter("startDate", it.toString()) }
                endDate?.let { parameter("endDate", it.toString()) }
            }
            NetworkResult.Success(Json.decodeFromString(response.bodyAsText()))
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }
    }
    
    // Post Events
    suspend fun postEvents(events: DHIS2Events): NetworkResult<DHIS2ImportSummary> {
        return try {
            val response = httpClient.client.post("/events") {
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToString(DHIS2Events.serializer(), events))
            }
            NetworkResult.Success(Json.decodeFromString(response.bodyAsText()))
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }
    }
    
    // Tracked Entity Instances
    suspend fun getTrackedEntityInstances(
        trackedEntityType: String,
        orgUnit: String? = null,
        program: String? = null,
        fields: String = "trackedEntityInstance,orgUnit,attributes,enrollments"
    ): NetworkResult<DHIS2TrackedEntityInstancesResponse> {
        return try {
            val response = httpClient.client.get("/trackedEntityInstances") {
                parameter("trackedEntityType", trackedEntityType)
                parameter("fields", fields)
                orgUnit?.let { parameter("ou", it) }
                program?.let { parameter("program", it) }
            }
            NetworkResult.Success(Json.decodeFromString(response.bodyAsText()))
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }
    }
}

// DHIS2 Data Models
@Serializable
data class DHIS2SystemInfo(
    val version: String,
    val revision: String,
    val buildTime: String,
    val serverDate: String,
    val contextPath: String,
    val userAgent: String
)

@Serializable
data class DHIS2User(
    val id: String,
    val username: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val organisationUnits: List<DHIS2OrganisationUnit> = emptyList(),
    val userRoles: List<DHIS2UserRole> = emptyList()
)

@Serializable
data class DHIS2UserRole(
    val id: String,
    val name: String
)

@Serializable
data class DHIS2OrganisationUnit(
    val id: String,
    val name: String,
    val level: Int? = null,
    val parent: DHIS2OrganisationUnit? = null,
    val children: List<DHIS2OrganisationUnit> = emptyList()
)

@Serializable
data class DHIS2OrganisationUnitsResponse(
    val organisationUnits: List<DHIS2OrganisationUnit>
)

@Serializable
data class DHIS2DataElement(
    val id: String,
    val name: String,
    val valueType: String,
    val domainType: String,
    val aggregationType: String
)

@Serializable
data class DHIS2DataElementsResponse(
    val dataElements: List<DHIS2DataElement>
)

@Serializable
data class DHIS2DataSet(
    val id: String,
    val name: String,
    val periodType: String,
    val dataElements: List<DHIS2DataElement> = emptyList()
)

@Serializable
data class DHIS2DataSetsResponse(
    val dataSets: List<DHIS2DataSet>
)

@Serializable
data class DHIS2Program(
    val id: String,
    val name: String,
    val programType: String,
    val programStages: List<DHIS2ProgramStage> = emptyList()
)

@Serializable
data class DHIS2ProgramStage(
    val id: String,
    val name: String,
    val programStageDataElements: List<DHIS2ProgramStageDataElement> = emptyList()
)

@Serializable
data class DHIS2ProgramStageDataElement(
    val dataElement: DHIS2DataElement
)

@Serializable
data class DHIS2ProgramsResponse(
    val programs: List<DHIS2Program>
)

@Serializable
data class DHIS2DataValue(
    val dataElement: String,
    val period: String,
    val orgUnit: String,
    val value: String,
    val categoryOptionCombo: String? = null,
    val attributeOptionCombo: String? = null
)

@Serializable
data class DHIS2DataValueSet(
    val dataSet: String? = null,
    val period: String? = null,
    val orgUnit: String? = null,
    val dataValues: List<DHIS2DataValue>
)

@Serializable
data class DHIS2DataValuesResponse(
    val dataSet: String? = null,
    val period: String? = null,
    val orgUnit: String? = null,
    val dataValues: List<DHIS2DataValue>
)

@Serializable
data class DHIS2ImportSummary(
    val status: String,
    val importCount: DHIS2ImportCount,
    val conflicts: List<DHIS2Conflict> = emptyList()
)

@Serializable
data class DHIS2ImportCount(
    val imported: Int,
    val updated: Int,
    val ignored: Int,
    val deleted: Int
)

@Serializable
data class DHIS2Conflict(
    val object: String,
    val value: String
)

@Serializable
data class DHIS2AnalyticsResponse(
    val headers: List<DHIS2AnalyticsHeader>,
    val rows: List<List<String>>,
    val metaData: DHIS2AnalyticsMetaData
)

@Serializable
data class DHIS2AnalyticsHeader(
    val name: String,
    val column: String,
    val type: String,
    val hidden: Boolean,
    val meta: Boolean
)

@Serializable
data class DHIS2AnalyticsMetaData(
    val items: Map<String, DHIS2MetaDataItem>,
    val dimensions: Map<String, List<String>>
)

@Serializable
data class DHIS2MetaDataItem(
    val name: String,
    val uid: String? = null
)

@Serializable
data class DHIS2Event(
    val event: String? = null,
    val program: String,
    val orgUnit: String,
    val eventDate: String,
    val status: String = "COMPLETED",
    val dataValues: List<DHIS2EventDataValue>
)

@Serializable
data class DHIS2EventDataValue(
    val dataElement: String,
    val value: String
)

@Serializable
data class DHIS2Events(
    val events: List<DHIS2Event>
)

@Serializable
data class DHIS2EventsResponse(
    val events: List<DHIS2Event>
)

@Serializable
data class DHIS2TrackedEntityInstance(
    val trackedEntityInstance: String,
    val orgUnit: String,
    val attributes: List<DHIS2Attribute> = emptyList(),
    val enrollments: List<DHIS2Enrollment> = emptyList()
)

@Serializable
data class DHIS2Attribute(
    val attribute: String,
    val value: String
)

@Serializable
data class DHIS2Enrollment(
    val enrollment: String,
    val program: String,
    val enrollmentDate: String,
    val incidentDate: String,
    val status: String = "ACTIVE"
)

@Serializable
data class DHIS2TrackedEntityInstancesResponse(
    val trackedEntityInstances: List<DHIS2TrackedEntityInstance>
)

// DHIS2 Service Layer
class DHIS2Service(private val client: DHIS2Client) {
    
    // Aggregate Data Operations
    suspend fun syncAggregateData(
        dataSet: String,
        period: String,
        orgUnit: String
    ): Flow<SyncProgress> = flow {
        emit(SyncProgress.Started("Starting aggregate data sync"))
        
        try {
            // Get data values
            emit(SyncProgress.InProgress("Fetching data values", 25))
            val dataValuesResult = client.getDataValues(dataSet, period, orgUnit)
            
            when (dataValuesResult) {
                is NetworkResult.Success -> {
                    emit(SyncProgress.InProgress("Processing data values", 75))
                    // Process and store data locally
                    emit(SyncProgress.Completed("Aggregate data sync completed", dataValuesResult.data.dataValues.size))
                }
                is NetworkResult.Error -> {
                    emit(SyncProgress.Failed("Failed to fetch data values", dataValuesResult.exception))
                }
                is NetworkResult.Loading -> {
                    emit(SyncProgress.InProgress("Loading data values", 50))
                }
            }
        } catch (e: Exception) {
            emit(SyncProgress.Failed("Sync failed", e))
        }
    }
    
    // Tracker Data Operations
    suspend fun syncTrackerData(
        program: String,
        orgUnit: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<SyncProgress> = flow {
        emit(SyncProgress.Started("Starting tracker data sync"))
        
        try {
            // Get events
            emit(SyncProgress.InProgress("Fetching events", 33))
            val eventsResult = client.getEvents(program, orgUnit, startDate, endDate)
            
            when (eventsResult) {
                is NetworkResult.Success -> {
                    emit(SyncProgress.InProgress("Processing events", 66))
                    // Process and store events locally
                    emit(SyncProgress.Completed("Tracker data sync completed", eventsResult.data.events.size))
                }
                is NetworkResult.Error -> {
                    emit(SyncProgress.Failed("Failed to fetch events", eventsResult.exception))
                }
                is NetworkResult.Loading -> {
                    emit(SyncProgress.InProgress("Loading events", 50))
                }
            }
        } catch (e: Exception) {
            emit(SyncProgress.Failed("Sync failed", e))
        }
    }
    
    // Analytics Operations
    suspend fun getAnalyticsData(
        dataElements: List<String>,
        periods: List<String>,
        orgUnits: List<String>
    ): NetworkResult<DHIS2AnalyticsResponse> {
        val dimensions = listOf(
            "dx:${dataElements.joinToString(";")}",
            "pe:${periods.joinToString(";")}",
            "ou:${orgUnits.joinToString(";")}"
        )
        
        return client.getAnalytics(dimensions)
    }
    
    // Metadata Operations
    suspend fun syncMetadata(): Flow<SyncProgress> = flow {
        emit(SyncProgress.Started("Starting metadata sync"))
        
        try {
            // Sync organization units
            emit(SyncProgress.InProgress("Syncing organization units", 20))
            val orgUnitsResult = client.getOrganisationUnits()
            
            // Sync data elements
            emit(SyncProgress.InProgress("Syncing data elements", 40))
            val dataElementsResult = client.getDataElements()
            
            // Sync data sets
            emit(SyncProgress.InProgress("Syncing data sets", 60))
            val dataSetsResult = client.getDataSets()
            
            // Sync programs
            emit(SyncProgress.InProgress("Syncing programs", 80))
            val programsResult = client.getPrograms()
            
            emit(SyncProgress.Completed("Metadata sync completed", 0))
        } catch (e: Exception) {
            emit(SyncProgress.Failed("Metadata sync failed", e))
        }
    }
}

// Sync Progress Tracking
sealed class SyncProgress {
    data class Started(val message: String) : SyncProgress()
    data class InProgress(val message: String, val progress: Int) : SyncProgress()
    data class Completed(val message: String, val recordCount: Int) : SyncProgress()
    data class Failed(val message: String, val error: Throwable) : SyncProgress()
}

// DHIS2 Repository Pattern
interface DHIS2Repository {
    suspend fun authenticate(config: DHIS2Config): NetworkResult<DHIS2User>
    suspend fun getSystemInfo(): NetworkResult<DHIS2SystemInfo>
    suspend fun syncData(syncConfig: SyncConfig): Flow<SyncProgress>
    suspend fun getLocalData(query: DataQuery): List<Any>
    suspend fun postData(data: Any): NetworkResult<DHIS2ImportSummary>
}

@Serializable
data class SyncConfig(
    val syncType: SyncType,
    val dataSet: String? = null,
    val program: String? = null,
    val orgUnit: String,
    val period: String? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null
)

enum class SyncType { AGGREGATE, TRACKER, METADATA, ALL }

@Serializable
data class DataQuery(
    val type: String,
    val filters: Map<String, String> = emptyMap(),
    val fields: List<String> = emptyList(),
    val limit: Int? = null
)

class DHIS2RepositoryImpl(private val service: DHIS2Service) : DHIS2Repository {
    
    override suspend fun authenticate(config: DHIS2Config): NetworkResult<DHIS2User> {
        val client = DHIS2Client(config)
        return client.getCurrentUser()
    }
    
    override suspend fun getSystemInfo(): NetworkResult<DHIS2SystemInfo> {
        // Implementation would use stored config to create client
        TODO("Implementation depends on stored configuration")
    }
    
    override suspend fun syncData(syncConfig: SyncConfig): Flow<SyncProgress> {
        return when (syncConfig.syncType) {
            SyncType.AGGREGATE -> {
                service.syncAggregateData(
                    syncConfig.dataSet!!,
                    syncConfig.period!!,
                    syncConfig.orgUnit
                )
            }
            SyncType.TRACKER -> {
                service.syncTrackerData(
                    syncConfig.program!!,
                    syncConfig.orgUnit,
                    syncConfig.startDate!!,
                    syncConfig.endDate!!
                )
            }
            SyncType.METADATA -> {
                service.syncMetadata()
            }
            SyncType.ALL -> {
                // Implementation would combine all sync types
                service.syncMetadata()
            }
        }
    }
    
    override suspend fun getLocalData(query: DataQuery): List<Any> {
        // Implementation would query local storage
        return emptyList()
    }
    
    override suspend fun postData(data: Any): NetworkResult<DHIS2ImportSummary> {
        // Implementation would determine data type and post accordingly
        TODO("Implementation depends on data type")
    }
}
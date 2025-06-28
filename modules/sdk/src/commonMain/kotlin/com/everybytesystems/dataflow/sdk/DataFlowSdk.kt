package com.everybytesystems.dataflow.sdk

import com.everybytesystems.dataflow.auth.AuthConfig
import com.everybytesystems.dataflow.auth.AuthManager
import com.everybytesystems.dataflow.sdk.platform.PlatformFactory
import com.everybytesystems.dataflow.core.config.DHIS2Config
import com.everybytesystems.dataflow.core.database.DataCache
import com.everybytesystems.dataflow.core.database.InMemoryDataCache
import com.everybytesystems.dataflow.core.api.metadata.MetadataApi
import com.everybytesystems.dataflow.core.api.system.SystemApi
import com.everybytesystems.dataflow.core.version.DHIS2Version
import com.everybytesystems.dataflow.core.network.ApiResponse
import com.everybytesystems.dataflow.core.DHIS2Client
import com.everybytesystems.dataflow.sdk.services.MetadataService
import com.everybytesystems.dataflow.sdk.services.DataService
import com.everybytesystems.dataflow.sdk.services.TrackerService
import io.ktor.client.*

/**
 * Main SDK class that provides access to all DHIS2 DataFlow services
 * 
 * This is the primary entry point for the DHIS2 DataFlow SDK, providing
 * access to DHIS2 Web API endpoints through organized service classes.
 */
class DataFlowSdk private constructor(
    val config: DHIS2Config,
    val authManager: AuthManager,
    private val dhis2Client: DHIS2Client,
    private val dataCache: DataCache
) {
    
    // ========================================
    // API ACCESS
    // ========================================
    
    /**
     * System API - Access to DHIS2 system information and health checks
     */
    val systemApi: SystemApi get() = dhis2Client.system
    
    /**
     * Metadata API - Access to DHIS2 metadata endpoints
     * 
     * Provides access to:
     * - Data Elements and Groups
     * - Organisation Units and Groups
     * - Data Sets and Sections
     * - Programs and Program Stages
     * - And more...
     */
    val metadataApi: MetadataApi get() = dhis2Client.metadata
    
    // ========================================
    // HIGH-LEVEL SERVICES
    // ========================================
    
    /**
     * Metadata Service - High-level metadata operations with caching
     * 
     * Provides:
     * - Metadata synchronization
     * - Cached metadata access
     * - Search and filtering
     * - Offline support
     */
    val metadataService: MetadataService by lazy { 
        MetadataService(metadataApi, dataCache) 
    }
    
    /**
     * Data Service - High-level data value operations
     * 
     * Provides:
     * - Data value submission
     * - Data set completion
     * - Data validation
     * - Quality checks
     */
    val dataService: DataService by lazy { 
        DataService(systemApi, dataCache) 
    }
    
    /**
     * Tracker Service - High-level tracker operations
     * 
     * Provides:
     * - Tracked entity management
     * - Enrollment handling
     * - Event management
     * - Relationship tracking
     */
    val trackerService: TrackerService by lazy { 
        TrackerService(dataCache) 
    }
    
    // ========================================
    // INITIALIZATION AND LIFECYCLE
    // ========================================
    
    /**
     * Initialize the SDK and restore authentication state
     */
    suspend fun initialize() {
        println("🚀 Initializing DHIS2 DataFlow SDK...")
        
        // Initialize authentication
        authManager.initialize()
        
        val detectedVersion = dhis2Client.getVersion()
        println("✅ DHIS2 Version: ${detectedVersion.versionString} (API: ${detectedVersion.minor})")
        
        println("✅ SDK initialization complete")
    }
    
    /**
     * Authenticate with DHIS2 server
     */
    suspend fun authenticate(authConfig: AuthConfig) = authManager.authenticate(authConfig)
    
    /**
     * Logout and clear all cached data
     */
    suspend fun logout() {
        authManager.logout()
        dataCache.clear()
    }
    
    /**
     * Close the SDK and release resources
     */
    fun close() {
        dhis2Client.close()
    }
    
    // ========================================
    // VERSION INFORMATION
    // ========================================
    
    /**
     * Get the detected DHIS2 version
     */
    fun getDetectedVersion(): DHIS2Version = dhis2Client.getVersion()
    
    /**
     * Check if specific features are supported
     */
    fun supportsTrackerApi(): Boolean = dhis2Client.supportsTrackerApi()
    fun supportsNewAnalytics(): Boolean = dhis2Client.supportsNewAnalytics()
    fun supportsEnhancedMetadata(): Boolean = dhis2Client.supportsEnhancedMetadata()
    fun supportsAdvancedSync(): Boolean = dhis2Client.supportsAdvancedSync()
    fun supportsModernAuth(): Boolean = dhis2Client.supportsModernAuth()
    
    // ========================================
    // CONVENIENCE METHODS
    // ========================================
    
    /**
     * Quick access to system information
     */
    suspend fun getSystemInfo() = dhis2Client.getSystemInfo()
    
    /**
     * Test connection to DHIS2 instance
     */
    suspend fun ping() = dhis2Client.ping()
    
    /**
     * Sync all metadata from server
     */
    suspend fun syncMetadata(forceRefresh: Boolean = false) = 
        metadataService.syncAll(forceRefresh)
    
    /**
     * Quick access to cached data elements
     */
    fun getDataElements() = metadataService.getDataElements()
    
    /**
     * Search data elements by name or code
     */
    fun searchDataElements(query: String) = metadataService.searchDataElements(query)
    
    /**
     * Quick access to cached organisation units
     */
    fun getOrganisationUnits() = metadataService.getOrganisationUnits()
    
    /**
     * Get organisation units by level
     */
    fun getOrganisationUnitsByLevel(level: Int) = 
        metadataService.getOrganisationUnitsByLevel(level)
    
    /**
     * Quick access to cached data sets
     */
    fun getDataSets() = metadataService.getDataSets()
    
    /**
     * Quick access to cached programs
     */
    fun getPrograms() = metadataService.getPrograms()
    
    /**
     * Quick access to cached indicators
     */
    fun getIndicators() = metadataService.getIndicators()
    
    /**
     * Submit data values
     */
    suspend fun submitDataValues(dataValues: List<com.everybytesystems.dataflow.sdk.services.DataValue>) =
        dataService.submitDataValues(dataValues)
    
    /**
     * Get data values
     */
    suspend fun getDataValues(
        dataElement: String? = null,
        period: String? = null,
        orgUnit: String? = null
    ) = dataService.getDataValues(dataElement, period, orgUnit)
    
    /**
     * Complete data set
     */
    suspend fun completeDataSet(
        dataSet: String,
        period: String,
        orgUnit: String
    ) = dataService.completeDataSet(dataSet, period, orgUnit)
    
    /**
     * Create tracked entity
     */
    suspend fun createTrackedEntity(trackedEntity: com.everybytesystems.dataflow.sdk.services.TrackedEntity) =
        trackerService.createTrackedEntity(trackedEntity)
    
    /**
     * Search tracked entities
     */
    suspend fun searchTrackedEntities(
        program: String? = null,
        orgUnit: String? = null,
        attributes: Map<String, String> = emptyMap()
    ) = trackerService.searchTrackedEntities(program = program, orgUnit = orgUnit, attributes = attributes)
    
    /**
     * Create enrollment
     */
    suspend fun createEnrollment(enrollment: com.everybytesystems.dataflow.sdk.services.Enrollment) =
        trackerService.createEnrollment(enrollment)
    
    /**
     * Create event
     */
    suspend fun createEvent(event: com.everybytesystems.dataflow.sdk.services.Event) =
        trackerService.createEvent(event)
    
    companion object {
        
        /**
         * Create DataFlowSdk instance with automatic setup
         */
        suspend fun create(config: DHIS2Config): ApiResponse<DataFlowSdk> {
            return try {
                // Create DHIS2 client
                when (val clientResult = DHIS2Client.create(config)) {
                    is ApiResponse.Success -> {
                        val dhis2Client = clientResult.data
                        
                        // Create auth manager
                        val secureStorage = PlatformFactory.createSecureStorage()
                        val authManager = AuthManager(
                            config = config,
                            httpClient = HttpClient(), // Simple client for auth
                            secureStorage = secureStorage
                        )
                        
                        // Create data cache
                        val dataCache = InMemoryDataCache()
                        
                        // Create SDK instance
                        val sdk = DataFlowSdk(
                            config = config,
                            authManager = authManager,
                            dhis2Client = dhis2Client,
                            dataCache = dataCache
                        )
                        
                        ApiResponse.Success(sdk)
                    }
                    is ApiResponse.Error -> clientResult
                    is ApiResponse.Loading -> ApiResponse.Error(RuntimeException("Unexpected loading state"))
                }
            } catch (e: Exception) {
                ApiResponse.Error(e, "Failed to create DataFlowSdk: ${e.message}")
            }
        }
        
        /**
         * Create DataFlowSdk instance with known version (skip version detection)
         */
        fun createWithVersion(config: DHIS2Config, version: DHIS2Version): DataFlowSdk {
            val dhis2Client = DHIS2Client.createWithVersion(config, version)
            val secureStorage = PlatformFactory.createSecureStorage()
            val authManager = AuthManager(
                config = config,
                httpClient = HttpClient(),
                secureStorage = secureStorage
            )
            val dataCache = InMemoryDataCache()
            
            return DataFlowSdk(
                config = config,
                authManager = authManager,
                dhis2Client = dhis2Client,
                dataCache = dataCache
            )
        }
    }
}

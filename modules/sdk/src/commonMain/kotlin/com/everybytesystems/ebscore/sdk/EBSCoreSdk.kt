package com.everybytesystems.ebscore.sdk

import com.everybytesystems.ebscore.auth.AuthConfig
import com.everybytesystems.ebscore.auth.AuthManager
import com.everybytesystems.ebscore.auth.AuthType
import com.everybytesystems.ebscore.auth.UserInfo
import com.everybytesystems.ebscore.sdk.platform.PlatformFactory
import com.everybytesystems.ebscore.core.config.DHIS2Config
import com.everybytesystems.ebscore.core.database.DataCache
import com.everybytesystems.ebscore.core.database.InMemoryDataCache
import com.everybytesystems.ebscore.core.api.metadata.MetadataApi
import com.everybytesystems.ebscore.core.api.system.SystemApi
import com.everybytesystems.ebscore.core.version.DHIS2Version
import com.everybytesystems.ebscore.core.network.ApiResponse
import com.everybytesystems.ebscore.core.DHIS2Client
import com.everybytesystems.ebscore.sdk.services.MetadataService
import com.everybytesystems.ebscore.sdk.services.DataService
import com.everybytesystems.ebscore.sdk.services.TrackerService
import com.everybytesystems.ebscore.sdk.services.DataValue
import com.everybytesystems.ebscore.sdk.services.TrackedEntity
import com.everybytesystems.ebscore.sdk.services.Enrollment
import com.everybytesystems.ebscore.sdk.services.Event
import com.everybytesystems.ebscore.dhis2.DHIS2Client as DHIS2ClientLegacy
import com.everybytesystems.ebscore.network.EBSCoreHttpClient
import com.everybytesystems.ebscore.network.NetworkConfig
import io.ktor.client.*

/**
 * Main SDK class that provides access to all EBSCore DHIS2 services
 * 
 * This is the primary entry point for the EBSCore SDK, providing
 * access to DHIS2 Web API endpoints through organized service classes.
 */
class EBSCoreSdk private constructor(
    val config: DHIS2Config,
    val authManager: AuthManager,
    private val dhis2Client: DHIS2Client,
    private val dhis2ClientLegacy: DHIS2ClientLegacy,
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
        DataService(systemApi, dataCache, dhis2ClientLegacy) 
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
        TrackerService(dhis2ClientLegacy, dataCache) 
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
    suspend fun authenticate(authConfig: AuthConfig): ApiResponse<UserInfo> {
        return when (authConfig.authType) {
            AuthType.BASIC -> {
                val username = authConfig.username
                val password = authConfig.password
                if (username != null && password != null) {
                    authManager.authenticate(username, password)
                } else {
                    ApiResponse.Error(IllegalArgumentException("Username and password are required for basic authentication"))
                }
            }
            AuthType.BEARER -> {
                val bearerToken = authConfig.bearerToken
                if (bearerToken != null) {
                    authManager.authenticateWithToken(bearerToken)
                } else {
                    ApiResponse.Error(IllegalArgumentException("Bearer token is required for bearer authentication"))
                }
            }
            else -> ApiResponse.Error(IllegalArgumentException("Unsupported authentication type: ${authConfig.authType}"))
        }
    }
    
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
        metadataService.syncAllMetadata()
    
    /**
     * Quick access to cached data elements
     */
    suspend fun getDataElements() = metadataService.getDataElements()
    
    /**
     * Search data elements by name or code
     */
    suspend fun searchDataElements(query: String) = metadataService.searchDataElements(query)
    
    /**
     * Quick access to cached organisation units
     */
    suspend fun getOrganisationUnits() = metadataService.getOrganisationUnits()
    
    /**
     * Get organisation units by level
     */
    suspend fun getOrganisationUnitsByLevel(level: Int) = 
        metadataService.getOrganisationUnitsByLevel(level)
    
    /**
     * Quick access to cached data sets
     */
    suspend fun getDataSets() = metadataService.getDataSets()
    
    /**
     * Quick access to cached programs
     */
    suspend fun getPrograms() = metadataService.getPrograms()
    
    /**
     * Quick access to cached indicators
     */
    suspend fun getIndicators() = metadataService.getIndicators()
    
    /**
     * Submit data values
     */
    suspend fun submitDataValues(dataValues: List<DataValue>) =
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
    suspend fun createTrackedEntity(trackedEntity: TrackedEntity) =
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
    suspend fun createEnrollment(enrollment: Enrollment) =
        trackerService.createEnrollmentSdk(enrollment)
    
    /**
     * Create event
     */
    suspend fun createEvent(event: Event) =
        trackerService.createEventSdk(event)
    
    companion object {
        
        /**
         * Create EBSCoreSdk instance with automatic setup
         */
        suspend fun create(config: DHIS2Config): ApiResponse<EBSCoreSdk> {
            return try {
                // Create DHIS2 client
                when (val clientResult = DHIS2Client.create(config)) {
                    is ApiResponse.Success -> {
                        val dhis2Client = clientResult.data
                        
                        // Create legacy DHIS2 client for tracker operations
                        val legacyConfig = com.everybytesystems.ebscore.dhis2.DHIS2Config(
                            serverUrl = config.baseUrl,
                            username = config.username ?: "",
                            password = config.password ?: "",
                            apiVersion = config.apiVersion,
                            timeout = config.connectTimeout
                        )
                        val dhis2HttpClient = EBSCoreHttpClient(
                            NetworkConfig(
                                baseUrl = config.baseUrl,
                                timeout = config.connectTimeout,
                                enableLogging = true
                            )
                        )
                        val dhis2ClientLegacy = DHIS2ClientLegacy(legacyConfig, dhis2HttpClient)
                        
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
                        val sdk = EBSCoreSdk(
                            config = config,
                            authManager = authManager,
                            dhis2Client = dhis2Client,
                            dhis2ClientLegacy = dhis2ClientLegacy,
                            dataCache = dataCache
                        )
                        
                        ApiResponse.Success(sdk)
                    }
                    is ApiResponse.Error -> clientResult
                    is ApiResponse.Loading -> ApiResponse.Error(RuntimeException("Unexpected loading state"))
                }
            } catch (e: Exception) {
                ApiResponse.Error(e, "Failed to create EBSCoreSdk: ${e.message}")
            }
        }
        
        /**
         * Create EBSCoreSdk instance with known version (skip version detection)
         */
        fun createWithVersion(config: DHIS2Config, version: DHIS2Version): EBSCoreSdk {
            val dhis2Client = DHIS2Client.createWithVersion(config, version)
            val legacyConfig = com.everybytesystems.ebscore.dhis2.DHIS2Config(
                serverUrl = config.baseUrl,
                username = config.username ?: "",
                password = config.password ?: "",
                apiVersion = config.apiVersion,
                timeout = config.connectTimeout
            )
            val dhis2HttpClient = EBSCoreHttpClient(
                NetworkConfig(
                    baseUrl = config.baseUrl,
                    timeout = config.connectTimeout,
                    enableLogging = true
                )
            )
            val dhis2ClientLegacy = DHIS2ClientLegacy(legacyConfig, dhis2HttpClient)
            val secureStorage = PlatformFactory.createSecureStorage()
            val authManager = AuthManager(
                config = config,
                httpClient = HttpClient(),
                secureStorage = secureStorage
            )
            val dataCache = InMemoryDataCache()
            
            return EBSCoreSdk(
                config = config,
                authManager = authManager,
                dhis2Client = dhis2Client,
                dhis2ClientLegacy = dhis2ClientLegacy,
                dataCache = dataCache
            )
        }
    }
}

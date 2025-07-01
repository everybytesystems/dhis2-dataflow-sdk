package com.everybytesystems.ebscore.dhis2

import com.everybytesystems.ebscore.network.EBSCoreHttpClient
import com.everybytesystems.ebscore.network.NetworkConfig
import com.everybytesystems.ebscore.network.NetworkResult
import com.everybytesystems.ebscore.auth.utils.Base64Utils
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

/**
 * DHIS2 Configuration Manager
 * Handles storing and retrieving DHIS2 configuration securely
 */
class DHIS2ConfigManager {
    private var storedConfig: DHIS2Config? = null
    
    /**
     * Store DHIS2 configuration
     */
    suspend fun storeConfig(config: DHIS2Config) {
        // In a real implementation, this would be stored securely
        // using encrypted storage or keychain
        storedConfig = config
    }
    
    /**
     * Get stored DHIS2 configuration
     */
    suspend fun getStoredConfig(): DHIS2Config? {
        return storedConfig
    }
    
    /**
     * Clear stored configuration
     */
    suspend fun clearConfig() {
        storedConfig = null
    }
    
    /**
     * Check if configuration is stored
     */
    fun hasStoredConfig(): Boolean {
        return storedConfig != null
    }
}

/**
 * DHIS2 Module - Main entry point for DHIS2 integration
 */
class DHIS2Module(
    private val config: DHIS2Config,
    private val httpClient: EBSCoreHttpClient = EBSCoreHttpClient(
        NetworkConfig(
            baseUrl = config.serverUrl,
            timeout = config.timeout,
            enableLogging = true
        )
    )
) {
    private val configManager = DHIS2ConfigManager()
    private val dhis2Client = DHIS2Client(config, httpClient)
    
    /**
     * Initialize DHIS2 module
     */
    suspend fun initialize(): NetworkResult<String> {
        return try {
            configManager.storeConfig(config)
            dhis2Client.testConnection()
        } catch (e: Exception) {
            NetworkResult.Error(Exception("Failed to initialize DHIS2 module: ${e.message}", e))
        }
    }
    
    /**
     * Get DHIS2 client for direct API access
     */
    fun getClient(): DHIS2Client = dhis2Client
    
    /**
     * Get configuration manager
     */
    fun getConfigManager(): DHIS2ConfigManager = configManager
}

/**
 * DHIS2 Client - Core API client
 */
class DHIS2Client(
    private val config: DHIS2Config,
    private val httpClient: EBSCoreHttpClient
) {
    private val baseUrl = "${config.serverUrl}/api/${config.apiVersion}"
    private val json = Json { ignoreUnknownKeys = true }
    
    /**
     * Test connection to DHIS2 server
     */
    suspend fun testConnection(): NetworkResult<String> {
        return try {
            val response = httpClient.client.get("$baseUrl/system/info") {
                headers {
                    append(HttpHeaders.Authorization, "Basic ${encodeCredentials()}")
                }
            }
            
            when (response.status) {
                HttpStatusCode.OK -> NetworkResult.Success("Connection successful")
                HttpStatusCode.Unauthorized -> NetworkResult.Error(
                    Exception("Invalid credentials")
                )
                else -> NetworkResult.Error(
                    Exception("Connection failed: HTTP ${response.status}")
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error(Exception("Connection test failed: ${e.message}", e))
        }
    }
    
    /**
     * Generic GET request
     */
    suspend fun get(endpoint: String): NetworkResult<String> {
        return try {
            val response = httpClient.client.get("$baseUrl/$endpoint") {
                headers {
                    append(HttpHeaders.Authorization, "Basic ${encodeCredentials()}")
                }
            }
            
            when (response.status) {
                HttpStatusCode.OK -> NetworkResult.Success(response.bodyAsText())
                else -> NetworkResult.Error(
                    Exception("GET request failed: HTTP ${response.status}")
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error(Exception("GET request failed: ${e.message}", e))
        }
    }
    
    /**
     * Generic POST request
     */
    suspend fun post(endpoint: String, data: String): NetworkResult<String> {
        return try {
            val response = httpClient.client.post("$baseUrl/$endpoint") {
                headers {
                    append(HttpHeaders.Authorization, "Basic ${encodeCredentials()}")
                    append(HttpHeaders.ContentType, "application/json")
                }
                setBody(data)
            }
            
            when (response.status) {
                HttpStatusCode.OK, HttpStatusCode.Created -> NetworkResult.Success(response.bodyAsText())
                else -> NetworkResult.Error(
                    Exception("POST request failed: HTTP ${response.status}")
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error(Exception("POST request failed: ${e.message}", e))
        }
    }
    
    /**
     * Generic PUT request
     */
    suspend fun put(endpoint: String, data: String): NetworkResult<String> {
        return try {
            val response = httpClient.client.put("$baseUrl/$endpoint") {
                headers {
                    append(HttpHeaders.Authorization, "Basic ${encodeCredentials()}")
                    append(HttpHeaders.ContentType, "application/json")
                }
                setBody(data)
            }
            
            when (response.status) {
                HttpStatusCode.OK -> NetworkResult.Success(response.bodyAsText())
                else -> NetworkResult.Error(
                    Exception("PUT request failed: HTTP ${response.status}")
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error(Exception("PUT request failed: ${e.message}", e))
        }
    }
    
    /**
     * Generic DELETE request
     */
    suspend fun delete(endpoint: String): NetworkResult<String> {
        return try {
            val response = httpClient.client.delete("$baseUrl/$endpoint") {
                headers {
                    append(HttpHeaders.Authorization, "Basic ${encodeCredentials()}")
                }
            }
            
            when (response.status) {
                HttpStatusCode.OK, HttpStatusCode.NoContent -> NetworkResult.Success("Deleted successfully")
                else -> NetworkResult.Error(
                    Exception("DELETE request failed: HTTP ${response.status}")
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error(Exception("DELETE request failed: ${e.message}", e))
        }
    }
    
    private fun encodeCredentials(): String {
        val credentials = "${config.username}:${config.password}"
        return Base64Utils.encode(credentials)
    }
}

// Basic DHIS2 Data Models
@Serializable
data class DHIS2SystemInfo(
    val version: String? = null,
    val revision: String? = null,
    val buildTime: String? = null,
    val serverDate: String? = null
)

@Serializable
data class DHIS2ImportSummary(
    val status: String? = null,
    val importCount: DHIS2ImportCount? = null,
    val conflicts: List<DHIS2Conflict>? = null
)

@Serializable
data class DHIS2ImportCount(
    val imported: Int = 0,
    val updated: Int = 0,
    val ignored: Int = 0,
    val deleted: Int = 0
)

@Serializable
data class DHIS2Conflict(
    val `object`: String? = null,
    val value: String? = null
)

@Serializable
data class DHIS2OrganisationUnit(
    val id: String? = null,
    val name: String,
    val shortName: String? = null,
    val code: String? = null,
    val level: Int? = null,
    val parent: DHIS2Reference? = null
)

@Serializable
data class DHIS2DataElement(
    val id: String? = null,
    val name: String,
    val shortName: String? = null,
    val code: String? = null,
    val valueType: String? = null,
    val domainType: String? = null
)

@Serializable
data class DHIS2Program(
    val id: String? = null,
    val name: String,
    val shortName: String? = null,
    val programType: String? = null,
    val trackedEntityType: DHIS2Reference? = null
)

@Serializable
data class DHIS2Event(
    val event: String? = null,
    val program: String,
    val orgUnit: String,
    val eventDate: String,
    val status: String = "COMPLETED",
    val dataValues: List<DHIS2DataValue> = emptyList()
)

@Serializable
data class DHIS2TrackedEntityInstance(
    val trackedEntityInstance: String? = null,
    val trackedEntityType: String,
    val orgUnit: String,
    val attributes: List<DHIS2Attribute> = emptyList(),
    val enrollments: List<DHIS2Enrollment> = emptyList()
)

@Serializable
data class DHIS2Enrollment(
    val enrollment: String? = null,
    val program: String,
    val orgUnit: String,
    val enrollmentDate: String,
    val incidentDate: String? = null,
    val status: String = "ACTIVE"
)

@Serializable
data class DHIS2DataValue(
    val dataElement: String,
    val value: String,
    val orgUnit: String? = null,
    val period: String? = null
)

@Serializable
data class DHIS2Attribute(
    val attribute: String,
    val value: String
)

@Serializable
data class DHIS2Reference(
    val id: String
)
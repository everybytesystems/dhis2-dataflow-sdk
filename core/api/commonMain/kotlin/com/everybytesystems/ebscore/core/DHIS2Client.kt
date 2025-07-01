package com.everybytesystems.ebscore.core

import com.everybytesystems.ebscore.core.api.analytics.AnalyticsApi
import com.everybytesystems.ebscore.core.api.metadata.MetadataApi
import com.everybytesystems.ebscore.core.api.system.SystemApi
import com.everybytesystems.ebscore.core.config.DHIS2Config
import com.everybytesystems.ebscore.core.network.ApiResponse
import com.everybytesystems.ebscore.core.version.DHIS2Version
import com.everybytesystems.ebscore.core.version.VersionDetector
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * Main DHIS2 SDK Client
 */
class DHIS2Client private constructor(
    private val config: DHIS2Config,
    private val httpClient: HttpClient,
    private val detectedVersion: DHIS2Version
) {
    
    // API instances
    val analytics: AnalyticsApi by lazy { AnalyticsApi(httpClient, config, detectedVersion) }
    val metadata: MetadataApi by lazy { MetadataApi(httpClient, config, detectedVersion) }
    val system: SystemApi by lazy { SystemApi(httpClient, config, detectedVersion) }
    
    /**
     * Get the detected DHIS2 version
     */
    fun getVersion(): DHIS2Version = detectedVersion
    
    /**
     * Get the configuration
     */
    fun getConfig(): DHIS2Config = config
    
    /**
     * Check if a specific feature is supported
     */
    fun supportsTrackerApi(): Boolean = detectedVersion.supportsTrackerApi()
    fun supportsNewAnalytics(): Boolean = detectedVersion.supportsNewAnalytics()
    fun supportsEnhancedMetadata(): Boolean = detectedVersion.supportsEnhancedMetadata()
    fun supportsAdvancedSync(): Boolean = detectedVersion.supportsAdvancedSync()
    fun supportsModernAuth(): Boolean = detectedVersion.supportsModernAuth()
    
    /**
     * Get system information
     */
    suspend fun getSystemInfo(): ApiResponse<com.everybytesystems.ebscore.core.api.system.SystemInfo> {
        return system.getSystemInfo()
    }
    
    /**
     * Test connection to DHIS2 instance
     */
    suspend fun ping(): ApiResponse<String> {
        return system.ping()
    }
    
    /**
     * Close the HTTP client
     */
    fun close() {
        httpClient.close()
    }
    
    companion object {
        
        /**
         * Create DHIS2 client with automatic version detection
         */
        suspend fun create(config: DHIS2Config): ApiResponse<DHIS2Client> {
            return try {
                val httpClient = createHttpClient(config)
                val versionDetector = VersionDetector(httpClient)
                
                when (val versionResponse = versionDetector.detectVersion(config)) {
                    is ApiResponse.Success -> {
                        val client = DHIS2Client(config, httpClient, versionResponse.data)
                        ApiResponse.Success(client)
                    }
                    is ApiResponse.Error -> {
                        httpClient.close()
                        ApiResponse.Error(versionResponse.exception, "Failed to detect DHIS2 version: ${versionResponse.message}")
                    }
                    is ApiResponse.Loading -> {
                        httpClient.close()
                        ApiResponse.Error(RuntimeException("Unexpected loading state during version detection"))
                    }
                }
            } catch (e: Exception) {
                ApiResponse.Error(e, "Failed to create DHIS2 client: ${e.message}")
            }
        }
        
        /**
         * Create DHIS2 client with known version (skip version detection)
         */
        fun createWithVersion(config: DHIS2Config, version: DHIS2Version): DHIS2Client {
            val httpClient = createHttpClient(config)
            return DHIS2Client(config, httpClient, version)
        }
        
        /**
         * Create HTTP client with configuration
         */
        private fun createHttpClient(config: DHIS2Config): HttpClient {
            return HttpClient {
                // Content negotiation
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                        encodeDefaults = false
                    })
                }
                
                // Logging
                if (config.enableLogging) {
                    install(Logging) {
                        logger = Logger.DEFAULT
                        level = when (config.logLevel) {
                            DHIS2Config.LogLevel.NONE -> LogLevel.NONE
                            DHIS2Config.LogLevel.INFO -> LogLevel.INFO
                            DHIS2Config.LogLevel.HEADERS -> LogLevel.HEADERS
                            DHIS2Config.LogLevel.BODY -> LogLevel.BODY
                            DHIS2Config.LogLevel.ALL -> LogLevel.ALL
                        }
                    }
                }
                
                // Timeouts
                install(HttpTimeout) {
                    requestTimeoutMillis = config.requestTimeout
                    connectTimeoutMillis = config.connectTimeout
                    socketTimeoutMillis = config.socketTimeout
                }
                
                // Authentication
                if (config.username != null && config.password != null) {
                    install(Auth) {
                        basic {
                            credentials {
                                BasicAuthCredentials(
                                    username = config.username,
                                    password = config.password
                                )
                            }
                            realm = "DHIS2"
                        }
                    }
                }
                
                // Default request configuration
                defaultRequest {
                    headers {
                        append("Accept", "application/json")
                        append("Content-Type", "application/json")
                        append("User-Agent", "DHIS2-EBSCore-SDK/1.0")
                    }
                    
                    // Add bearer token if available
                    config.bearerToken?.let { token ->
                        headers {
                            append("Authorization", "Bearer $token")
                        }
                    }
                    
                    // Add API key if available
                    config.apiKey?.let { apiKey ->
                        headers {
                            append("X-API-Key", apiKey)
                        }
                    }
                }
                
                // Retry configuration
                if (config.enableRetry) {
                    install(HttpRequestRetry) {
                        retryOnServerErrors(maxRetries = config.maxRetries)
                        exponentialDelay()
                    }
                }
            }
        }
    }
}
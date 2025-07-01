package com.everybytesystems.ebscore.core.config

import kotlinx.serialization.Serializable

/**
 * Configuration for DHIS2 server connection
 */
@Serializable
class DHIS2Config(
    val baseUrl: String,
    val username: String? = null,
    val password: String? = null,
    val bearerToken: String? = null,
    val apiKey: String? = null,
    val apiVersion: String = "41",
    val connectTimeout: Long = 30_000,
    val requestTimeout: Long = 60_000,
    val socketTimeout: Long = 60_000,
    val enableLogging: Boolean = false,
    val logLevel: LogLevel = LogLevel.NONE,
    val maxRetries: Int = 3,
    val retryDelayMs: Long = 1000,
    val enableRetry: Boolean = true,
    val autoDetectVersion: Boolean = true,
    val versionCacheTimeoutMs: Long = 5 * 60 * 1000L // 5 minutes
) {
    
    enum class LogLevel {
        NONE, INFO, HEADERS, BODY, ALL
    }
    
    fun validate(): Result<Unit> {
        return when {
            baseUrl.isBlank() -> Result.failure(IllegalArgumentException("Base URL cannot be blank"))
            !baseUrl.startsWith("http") -> Result.failure(IllegalArgumentException("Base URL must start with http or https"))
            apiVersion.isBlank() -> Result.failure(IllegalArgumentException("API version cannot be blank"))
            connectTimeout <= 0 -> Result.failure(IllegalArgumentException("Connect timeout must be positive"))
            requestTimeout <= 0 -> Result.failure(IllegalArgumentException("Request timeout must be positive"))
            socketTimeout <= 0 -> Result.failure(IllegalArgumentException("Socket timeout must be positive"))
            maxRetries < 0 -> Result.failure(IllegalArgumentException("Max retries cannot be negative"))
            retryDelayMs <= 0 -> Result.failure(IllegalArgumentException("Retry delay must be positive"))
            else -> Result.success(Unit)
        }
    }
    
    val apiBaseUrl: String
        get() = "${baseUrl.trimEnd('/')}/api/${apiVersion}"
    
    val metadataUrl: String
        get() = "$apiBaseUrl/metadata"
    
    val analyticsUrl: String
        get() = "$apiBaseUrl/analytics"
    
    val trackerUrl: String
        get() = "$apiBaseUrl/tracker"
    
    val dataValueSetsUrl: String
        get() = "$apiBaseUrl/dataValueSets"
    
    val visualizationsUrl: String
        get() = "$apiBaseUrl/visualizations"
}
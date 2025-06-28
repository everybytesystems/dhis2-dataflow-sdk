package com.everybytesystems.dataflow.core.config

import kotlinx.serialization.Serializable

/**
 * Configuration for DHIS2 server connection
 */
@Serializable
data class DHIS2Config(
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
    
    class Builder {
        private var baseUrl: String = ""
        private var username: String? = null
        private var password: String? = null
        private var bearerToken: String? = null
        private var apiKey: String? = null
        private var apiVersion: String = "41"
        private var connectTimeout: Long = 30_000
        private var requestTimeout: Long = 60_000
        private var socketTimeout: Long = 60_000
        private var enableLogging: Boolean = false
        private var logLevel: LogLevel = LogLevel.NONE
        private var maxRetries: Int = 3
        private var retryDelayMs: Long = 1000
        private var enableRetry: Boolean = true
        private var autoDetectVersion: Boolean = true
        private var versionCacheTimeoutMs: Long = 5 * 60 * 1000L
        
        fun baseUrl(baseUrl: String) = apply { this.baseUrl = baseUrl }
        fun username(username: String) = apply { this.username = username }
        fun password(password: String) = apply { this.password = password }
        fun bearerToken(token: String) = apply { this.bearerToken = token }
        fun apiKey(apiKey: String) = apply { this.apiKey = apiKey }
        fun apiVersion(version: String) = apply { this.apiVersion = version }
        fun connectTimeout(timeout: Long) = apply { this.connectTimeout = timeout }
        fun requestTimeout(timeout: Long) = apply { this.requestTimeout = timeout }
        fun socketTimeout(timeout: Long) = apply { this.socketTimeout = timeout }
        fun enableLogging(enable: Boolean) = apply { this.enableLogging = enable }
        fun logLevel(level: LogLevel) = apply { this.logLevel = level }
        fun maxRetries(retries: Int) = apply { this.maxRetries = retries }
        fun retryDelayMs(delay: Long) = apply { this.retryDelayMs = delay }
        fun enableRetry(enable: Boolean) = apply { this.enableRetry = enable }
        fun autoDetectVersion(enable: Boolean) = apply { this.autoDetectVersion = enable }
        fun versionCacheTimeoutMs(timeout: Long) = apply { this.versionCacheTimeoutMs = timeout }
        
        fun build() = DHIS2Config(
            baseUrl = baseUrl,
            username = username,
            password = password,
            bearerToken = bearerToken,
            apiKey = apiKey,
            apiVersion = apiVersion,
            connectTimeout = connectTimeout,
            requestTimeout = requestTimeout,
            socketTimeout = socketTimeout,
            enableLogging = enableLogging,
            logLevel = logLevel,
            maxRetries = maxRetries,
            retryDelayMs = retryDelayMs,
            enableRetry = enableRetry,
            autoDetectVersion = autoDetectVersion,
            versionCacheTimeoutMs = versionCacheTimeoutMs
        )
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
}
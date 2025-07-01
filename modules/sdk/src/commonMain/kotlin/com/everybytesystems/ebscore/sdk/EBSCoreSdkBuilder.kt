package com.everybytesystems.ebscore.sdk

import com.everybytesystems.ebscore.core.config.DHIS2Config
import com.everybytesystems.ebscore.core.version.DHIS2Version
import com.everybytesystems.ebscore.core.network.ApiResponse

/**
 * Builder for creating EBSCoreSdk instances with fluent configuration
 */
class EBSCoreSdkBuilder {
    private var baseUrl: String? = null
    private var apiVersion: String = "41"
    private var username: String? = null
    private var password: String? = null
    private var bearerToken: String? = null
    private var apiKey: String? = null
    private var enableLogging: Boolean = false
    private var logLevel: DHIS2Config.LogLevel = DHIS2Config.LogLevel.INFO
    private var connectTimeout: Long = 30_000L
    private var requestTimeout: Long = 60_000L
    private var socketTimeout: Long = 60_000L
    private var enableRetry: Boolean = true
    private var maxRetries: Int = 3
    private var autoDetectVersion: Boolean = true
    private var enableCaching: Boolean = true
    private var cacheSize: Long = 50 * 1024 * 1024L // 50MB
    private var enableCompression: Boolean = true
    private var userAgent: String = "DHIS2-EBSCore-SDK/1.0"
    
    /**
     * Set the base URL of the DHIS2 instance
     */
    fun baseUrl(url: String) = apply {
        this.baseUrl = url.trimEnd('/')
    }
    
    /**
     * Set the API version to use
     */
    fun apiVersion(version: String) = apply {
        this.apiVersion = version
    }
    
    /**
     * Set basic authentication credentials
     */
    fun basicAuth(username: String, password: String) = apply {
        this.username = username
        this.password = password
        this.bearerToken = null
        this.apiKey = null
    }
    
    /**
     * Set bearer token authentication
     */
    fun bearerToken(token: String) = apply {
        this.bearerToken = token
        this.username = null
        this.password = null
        this.apiKey = null
    }
    
    /**
     * Set API key authentication
     */
    fun apiKey(key: String) = apply {
        this.apiKey = key
        this.username = null
        this.password = null
        this.bearerToken = null
    }
    
    /**
     * Enable or disable logging
     */
    fun enableLogging(enabled: Boolean) = apply {
        this.enableLogging = enabled
    }
    
    /**
     * Set logging level
     */
    fun logLevel(level: DHIS2Config.LogLevel) = apply {
        this.logLevel = level
    }
    
    /**
     * Set connection timeout in milliseconds
     */
    fun connectTimeout(timeout: Long) = apply {
        this.connectTimeout = timeout
    }
    
    /**
     * Set request timeout in milliseconds
     */
    fun requestTimeout(timeout: Long) = apply {
        this.requestTimeout = timeout
    }
    
    /**
     * Set socket timeout in milliseconds
     */
    fun socketTimeout(timeout: Long) = apply {
        this.socketTimeout = timeout
    }
    
    /**
     * Enable or disable automatic retries
     */
    fun enableRetry(enabled: Boolean) = apply {
        this.enableRetry = enabled
    }
    
    /**
     * Set maximum number of retries
     */
    fun maxRetries(retries: Int) = apply {
        this.maxRetries = retries
    }
    
    /**
     * Enable or disable automatic version detection
     */
    fun autoDetectVersion(enabled: Boolean) = apply {
        this.autoDetectVersion = enabled
    }
    
    /**
     * Enable or disable caching
     */
    fun enableCaching(enabled: Boolean) = apply {
        this.enableCaching = enabled
    }
    
    /**
     * Set cache size in bytes
     */
    fun cacheSize(size: Long) = apply {
        this.cacheSize = size
    }
    
    /**
     * Enable or disable compression
     */
    fun enableCompression(enabled: Boolean) = apply {
        this.enableCompression = enabled
    }
    
    /**
     * Set custom user agent
     */
    fun userAgent(agent: String) = apply {
        this.userAgent = agent
    }
    
    /**
     * Build the EBSCoreSdk instance
     */
    suspend fun build(): ApiResponse<EBSCoreSdk> {
        val url = baseUrl ?: throw IllegalStateException("Base URL is required")
        
        val config = DHIS2Config(
            baseUrl = url,
            apiVersion = apiVersion,
            username = username,
            password = password,
            bearerToken = bearerToken,
            apiKey = apiKey,
            enableLogging = enableLogging,
            logLevel = logLevel,
            connectTimeout = connectTimeout,
            requestTimeout = requestTimeout,
            socketTimeout = socketTimeout,
            enableRetry = enableRetry,
            maxRetries = maxRetries,
            autoDetectVersion = autoDetectVersion
        )
        
        return EBSCoreSdk.create(config)
    }
    
    /**
     * Build the EBSCoreSdk instance with known version (skip version detection)
     */
    fun buildWithVersion(version: DHIS2Version): EBSCoreSdk {
        val url = baseUrl ?: throw IllegalStateException("Base URL is required")
        
        val config = DHIS2Config(
            baseUrl = url,
            apiVersion = apiVersion,
            username = username,
            password = password,
            bearerToken = bearerToken,
            apiKey = apiKey,
            enableLogging = enableLogging,
            logLevel = logLevel,
            connectTimeout = connectTimeout,
            requestTimeout = requestTimeout,
            socketTimeout = socketTimeout,
            enableRetry = enableRetry,
            maxRetries = maxRetries,
            autoDetectVersion = false // Skip version detection
        )
        
        return EBSCoreSdk.createWithVersion(config, version)
    }
    
    companion object {
        /**
         * Create a new builder instance
         */
        fun newBuilder() = EBSCoreSdkBuilder()
        
        /**
         * Create a builder with basic configuration
         */
        fun basic(baseUrl: String, username: String, password: String) = 
            EBSCoreSdkBuilder()
                .baseUrl(baseUrl)
                .basicAuth(username, password)
        
        /**
         * Create a builder with PAT configuration
         */
        fun withToken(baseUrl: String, token: String) = 
            EBSCoreSdkBuilder()
                .baseUrl(baseUrl)
                .bearerToken(token)
        
        /**
         * Create a builder with API key configuration
         */
        fun withApiKey(baseUrl: String, apiKey: String) = 
            EBSCoreSdkBuilder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
    }
}
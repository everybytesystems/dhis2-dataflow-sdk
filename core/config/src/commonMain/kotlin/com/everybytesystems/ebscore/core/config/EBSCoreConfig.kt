package com.everybytesystems.ebscore.core.config

import kotlinx.serialization.Serializable

/**
 * Main configuration for EBSCore SDK
 */
@Serializable
data class EBSCoreConfig(
    val baseUrl: String,
    val userAgent: String = "EBSCore-SDK/1.0.0",
    val timeoutMillis: Long = 30_000,
    val connectTimeoutMillis: Long = 10_000,
    val enableLogging: Boolean = false,
    val enableCaching: Boolean = true,
    val cacheSize: Long = 100 * 1024 * 1024, // 100MB
    val enableCompression: Boolean = true,
    val retryPolicy: RetryPolicy = RetryPolicy.DEFAULT,
    val environment: Environment = Environment.PRODUCTION
) {
    
    companion object {
        /**
         * Create configuration for DHIS2 instance
         */
        fun forDHIS2(
            baseUrl: String,
            enableLogging: Boolean = false
        ) = EBSCoreConfig(
            baseUrl = baseUrl.removeSuffix("/"),
            userAgent = "EBSCore-SDK-DHIS2/1.0.0",
            enableLogging = enableLogging,
            timeoutMillis = 60_000, // DHIS2 can be slow
            retryPolicy = RetryPolicy.EXPONENTIAL_BACKOFF
        )
        
        /**
         * Create configuration for FHIR server
         */
        fun forFHIR(
            baseUrl: String,
            enableLogging: Boolean = false
        ) = EBSCoreConfig(
            baseUrl = baseUrl.removeSuffix("/"),
            userAgent = "EBSCore-SDK-FHIR/1.0.0",
            enableLogging = enableLogging,
            timeoutMillis = 30_000,
            retryPolicy = RetryPolicy.LINEAR_BACKOFF
        )
        
        /**
         * Create configuration for development/testing
         */
        fun development(
            baseUrl: String
        ) = EBSCoreConfig(
            baseUrl = baseUrl.removeSuffix("/"),
            userAgent = "EBSCore-SDK-Dev/1.0.0",
            enableLogging = true,
            environment = Environment.DEVELOPMENT,
            retryPolicy = RetryPolicy.NONE
        )
    }
}

/**
 * Retry policy configuration
 */
@Serializable
data class RetryPolicy(
    val maxRetries: Int,
    val initialDelayMillis: Long,
    val maxDelayMillis: Long,
    val multiplier: Double,
    val retryOnConnectionFailure: Boolean = true,
    val retryOnTimeout: Boolean = true
) {
    companion object {
        val NONE = RetryPolicy(
            maxRetries = 0,
            initialDelayMillis = 0,
            maxDelayMillis = 0,
            multiplier = 1.0
        )
        
        val DEFAULT = RetryPolicy(
            maxRetries = 3,
            initialDelayMillis = 1000,
            maxDelayMillis = 10_000,
            multiplier = 2.0
        )
        
        val EXPONENTIAL_BACKOFF = RetryPolicy(
            maxRetries = 5,
            initialDelayMillis = 1000,
            maxDelayMillis = 30_000,
            multiplier = 2.0
        )
        
        val LINEAR_BACKOFF = RetryPolicy(
            maxRetries = 3,
            initialDelayMillis = 2000,
            maxDelayMillis = 10_000,
            multiplier = 1.0
        )
    }
}

/**
 * Environment configuration
 */
enum class Environment {
    DEVELOPMENT,
    STAGING,
    PRODUCTION
}

/**
 * Configuration builder for fluent API
 */
class EBSCoreConfigBuilder {
    private var baseUrl: String = ""
    private var userAgent: String = "EBSCore-SDK/1.0.0"
    private var timeoutMillis: Long = 30_000
    private var connectTimeoutMillis: Long = 10_000
    private var enableLogging: Boolean = false
    private var enableCaching: Boolean = true
    private var cacheSize: Long = 100 * 1024 * 1024
    private var enableCompression: Boolean = true
    private var retryPolicy: RetryPolicy = RetryPolicy.DEFAULT
    private var environment: Environment = Environment.PRODUCTION
    
    fun baseUrl(url: String) = apply { this.baseUrl = url.removeSuffix("/") }
    fun userAgent(agent: String) = apply { this.userAgent = agent }
    fun timeout(millis: Long) = apply { this.timeoutMillis = millis }
    fun connectTimeout(millis: Long) = apply { this.connectTimeoutMillis = millis }
    fun enableLogging(enable: Boolean = true) = apply { this.enableLogging = enable }
    fun enableCaching(enable: Boolean = true) = apply { this.enableCaching = enable }
    fun cacheSize(size: Long) = apply { this.cacheSize = size }
    fun enableCompression(enable: Boolean = true) = apply { this.enableCompression = enable }
    fun retryPolicy(policy: RetryPolicy) = apply { this.retryPolicy = policy }
    fun environment(env: Environment) = apply { this.environment = env }
    
    fun build(): EBSCoreConfig {
        require(baseUrl.isNotBlank()) { "Base URL is required" }
        
        return EBSCoreConfig(
            baseUrl = baseUrl,
            userAgent = userAgent,
            timeoutMillis = timeoutMillis,
            connectTimeoutMillis = connectTimeoutMillis,
            enableLogging = enableLogging,
            enableCaching = enableCaching,
            cacheSize = cacheSize,
            enableCompression = enableCompression,
            retryPolicy = retryPolicy,
            environment = environment
        )
    }
}

/**
 * DSL function for creating configuration
 */
fun ebsCoreConfig(block: EBSCoreConfigBuilder.() -> Unit): EBSCoreConfig {
    return EBSCoreConfigBuilder().apply(block).build()
}
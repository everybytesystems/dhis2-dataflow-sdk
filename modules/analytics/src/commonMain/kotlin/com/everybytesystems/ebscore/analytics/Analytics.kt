package com.everybytesystems.ebscore.analytics

/**
 * Generic analytics interface for EBSCore SDK
 * This provides basic analytics functionality that can be used across different platforms
 */
interface Analytics {
    /**
     * Track an event with optional parameters
     */
    suspend fun trackEvent(eventName: String, parameters: Map<String, Any> = emptyMap())
    
    /**
     * Set user properties
     */
    suspend fun setUserProperties(properties: Map<String, Any>)
    
    /**
     * Track screen view
     */
    suspend fun trackScreenView(screenName: String, parameters: Map<String, Any> = emptyMap())
}

/**
 * Default implementation of Analytics interface
 */
class DefaultAnalytics : Analytics {
    override suspend fun trackEvent(eventName: String, parameters: Map<String, Any>) {
        // Default implementation - can be overridden by platform-specific implementations
        println("Analytics Event: $eventName with parameters: $parameters")
    }
    
    override suspend fun setUserProperties(properties: Map<String, Any>) {
        println("Analytics User Properties: $properties")
    }
    
    override suspend fun trackScreenView(screenName: String, parameters: Map<String, Any>) {
        println("Analytics Screen View: $screenName with parameters: $parameters")
    }
}
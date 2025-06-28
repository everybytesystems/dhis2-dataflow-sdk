import com.everybytesystems.dataflow.sdk.DataFlowSdkBuilder
import com.everybytesystems.dataflow.core.version.DHIS2Feature
import com.everybytesystems.dataflow.auth.BasicAuthConfig
import kotlinx.coroutines.runBlocking

/**
 * Comprehensive example demonstrating DHIS2 version-aware SDK capabilities
 * 
 * This example shows how the SDK automatically:
 * 1. Detects DHIS2 version on connection
 * 2. Adapts API calls based on version
 * 3. Provides fallback mechanisms for unsupported features
 * 4. Handles parameter transformations automatically
 */
fun main() = runBlocking {
    
    // ========================================
    // 1. CREATE VERSION-AWARE SDK
    // ========================================
    
    val sdk = DataFlowSdkBuilder()
        .baseUrl("https://play.dhis2.org/2.42.0") // DHIS2 2.42 demo server
        .autoDetectVersion(true) // Enable automatic version detection
        .enableLogging(true) // Enable logging to see version detection
        .build()
    
    // ========================================
    // 2. INITIALIZE WITH VERSION DETECTION
    // ========================================
    
    println("🚀 Initializing SDK with automatic version detection...")
    sdk.initialize()
    
    // ========================================
    // 3. AUTHENTICATE
    // ========================================
    
    val authResult = sdk.authenticate(
        BasicAuthConfig(
            username = "admin",
            password = "district"
        )
    )
    
    if (authResult.isSuccess) {
        println("✅ Authentication successful")
        
        // ========================================
        // 4. CHECK DETECTED VERSION
        // ========================================
        
        val detectedVersion = sdk.getDetectedVersion()
        if (detectedVersion != null) {
            println("📋 Detected DHIS2 Version: ${detectedVersion.fullVersion}")
            println("📋 API Version: ${detectedVersion.apiVersion}")
            println("📋 Short Version: ${detectedVersion.shortVersion}")
            
            // ========================================
            // 5. CHECK FEATURE SUPPORT
            // ========================================
            
            println("\n🔍 Feature Support Analysis:")
            
            val features = listOf(
                DHIS2Feature.TRACKER_API to "Tracker API",
                DHIS2Feature.NEW_TRACKER_API to "New Tracker API (2.38+)",
                DHIS2Feature.ANALYTICS_API to "Analytics API",
                DHIS2Feature.VISUALIZATIONS_API to "Visualizations API (2.37+)",
                DHIS2Feature.EVENT_VISUALIZATIONS to "Event Visualizations (2.38+)",
                DHIS2Feature.DATA_EXCHANGE to "Data Exchange API (2.39+)",
                DHIS2Feature.AGGREGATE_DATA_EXCHANGE to "Aggregate Data Exchange (2.40+)",
                DHIS2Feature.OAUTH2_SUPPORT to "OAuth2 Support (2.38+)",
                DHIS2Feature.TWO_FACTOR_AUTH to "Two-Factor Authentication (2.37+)",
                DHIS2Feature.WEBHOOKS to "Webhooks (2.40+)"
            )
            
            features.forEach { (feature, description) ->
                val supported = sdk.isFeatureSupported(feature)
                val icon = if (supported) "✅" else "❌"
                println("  $icon $description")
            }
            
            // ========================================
            // 6. VERSION-AWARE API CALLS
            // ========================================
            
            println("\n🌐 Making version-aware API calls...")
            
            // System info call (works on all versions)
            val systemInfo = sdk.getSystemInfo()
            if (systemInfo.isSuccess) {
                println("✅ System info retrieved successfully")
                println("   Server version: ${systemInfo.data?.version}")
            }
            
            // Tracker API call (automatically uses correct endpoint)
            if (sdk.isFeatureSupported(DHIS2Feature.TRACKER_API)) {
                println("🎯 Tracker API is supported - making tracker call...")
                
                // The SDK will automatically:
                // - Use /api/42/tracker/trackedEntities for DHIS2 2.42
                // - Use /api/41/tracker/trackedEntities for DHIS2 2.38-2.41
                // - Use /api/36/trackedEntityInstances for DHIS2 2.36-2.37
                // - Transform parameters as needed
                
                try {
                    // This call will be automatically adapted based on version
                    val trackedEntities = sdk.versionAwareHttpClient.getResource<Any>(
                        resource = "trackedentityinstances",
                        params = mapOf(
                            "orgUnit" to "ImspTQPwCqd", // Will be transformed to "orgUnits" in 2.41+
                            "pageSize" to 5
                        )
                    )
                    
                    if (trackedEntities.isSuccess) {
                        println("✅ Tracker data retrieved successfully")
                    } else {
                        println("⚠️ Tracker call failed: ${trackedEntities.message}")
                    }
                } catch (e: Exception) {
                    println("⚠️ Tracker call error: ${e.message}")
                }
            } else {
                println("❌ Tracker API not supported in this version")
            }
            
            // Analytics API call
            if (sdk.isFeatureSupported(DHIS2Feature.ANALYTICS_API)) {
                println("📊 Analytics API is supported - making analytics call...")
                
                try {
                    val analytics = sdk.versionAwareHttpClient.getResource<Any>(
                        resource = "analytics",
                        params = mapOf(
                            "dimension" to "dx:FnYCr2EAzWS;sTzgSOXpzBN",
                            "dimension" to "pe:LAST_12_MONTHS",
                            "dimension" to "ou:ImspTQPwCqd",
                            "displayProperty" to "NAME"
                        )
                    )
                    
                    if (analytics.isSuccess) {
                        println("✅ Analytics data retrieved successfully")
                    } else {
                        println("⚠️ Analytics call failed: ${analytics.message}")
                    }
                } catch (e: Exception) {
                    println("⚠️ Analytics call error: ${e.message}")
                }
            }
            
            // Data Exchange API call (only available in 2.39+)
            if (sdk.isFeatureSupported(DHIS2Feature.DATA_EXCHANGE)) {
                println("🔄 Data Exchange API is supported")
            } else {
                println("❌ Data Exchange API not available (requires DHIS2 2.39+)")
            }
            
            // ========================================
            // 7. VERSION-SPECIFIC BEHAVIOR EXAMPLES
            // ========================================
            
            println("\n🔧 Version-specific behavior examples:")
            
            when {
                detectedVersion.isAtLeast(2, 42) -> {
                    println("🆕 DHIS2 2.42+ detected:")
                    println("   - Using new tracker API endpoints only")
                    println("   - User credentials merged into user object")
                    println("   - Old audit endpoints removed")
                    println("   - Analytics table columns renamed")
                }
                
                detectedVersion.isAtLeast(2, 41) -> {
                    println("🔄 DHIS2 2.41 detected:")
                    println("   - Using new tracker API with fallback support")
                    println("   - Parameter names updated (semicolon to comma)")
                    println("   - Sharing API changes applied")
                    println("   - Database table/column renames handled")
                }
                
                detectedVersion.isAtLeast(2, 38) -> {
                    println("📱 DHIS2 2.38+ detected:")
                    println("   - New tracker API available")
                    println("   - Event visualizations supported")
                    println("   - OAuth2 support available")
                }
                
                detectedVersion.isAtLeast(2, 37) -> {
                    println("📊 DHIS2 2.37+ detected:")
                    println("   - Visualizations API available")
                    println("   - Outlier detection supported")
                    println("   - Two-factor authentication available")
                }
                
                else -> {
                    println("📦 DHIS2 2.36 detected:")
                    println("   - Using legacy API endpoints")
                    println("   - Limited feature set available")
                }
            }
            
        } else {
            println("⚠️ Could not detect DHIS2 version")
        }
        
        // ========================================
        // 8. MANUAL VERSION REFRESH
        // ========================================
        
        println("\n🔄 Refreshing version detection...")
        val refreshedVersion = sdk.refreshVersionDetection()
        if (refreshedVersion != null) {
            println("✅ Version refreshed: ${refreshedVersion.fullVersion}")
        }
        
    } else {
        println("❌ Authentication failed: ${authResult.message}")
    }
    
    // ========================================
    // 9. CLEANUP
    // ========================================
    
    sdk.close()
    println("\n✅ SDK closed successfully")
}

/**
 * Example of version-aware feature usage
 */
suspend fun demonstrateVersionAwareFeatures(sdk: com.everybytesystems.dataflow.sdk.DataFlowSdk) {
    val version = sdk.getDetectedVersion() ?: return
    
    println("🎯 Demonstrating version-aware features for DHIS2 ${version.fullVersion}")
    
    // Example 1: Tracker API usage
    if (sdk.isFeatureSupported(DHIS2Feature.NEW_TRACKER_API)) {
        println("✅ Using new tracker API (2.38+)")
        // Use new tracker endpoints
    } else if (sdk.isFeatureSupported(DHIS2Feature.TRACKER_API)) {
        println("⚠️ Using legacy tracker API (2.36-2.37)")
        // Use legacy tracker endpoints
    }
    
    // Example 2: Visualization API usage
    if (sdk.isFeatureSupported(DHIS2Feature.VISUALIZATIONS_API)) {
        println("✅ Using visualizations API (2.37+)")
        // Use visualizations endpoints
    } else {
        println("⚠️ Using legacy charts API (2.36)")
        // Use legacy charts endpoints
    }
    
    // Example 3: Data Exchange usage
    if (sdk.isFeatureSupported(DHIS2Feature.DATA_EXCHANGE)) {
        println("✅ Data Exchange API available (2.39+)")
        // Use data exchange features
    } else {
        println("❌ Data Exchange not available - using alternative sync methods")
        // Use alternative data synchronization methods
    }
}

/**
 * Example of handling version-specific parameter transformations
 */
fun demonstrateParameterTransformations() {
    println("🔧 Parameter transformation examples:")
    
    // DHIS2 2.41+: Semicolon to comma separation
    println("  2.41+: assignedUser -> assignedUsers (comma-separated)")
    println("  2.41+: orgUnit -> orgUnits (comma-separated)")
    println("  2.41+: skipPaging -> paging (inverted logic)")
    
    // DHIS2 2.42+: Parameter deprecations
    println("  2.42+: programStatus -> status (enrollments)")
    println("  2.42+: programStatus -> enrollmentStatus (events)")
    println("  2.42+: ou -> orgUnit (ownership transfer)")
    
    // User object changes in 2.42
    println("  2.42+: userCredentials merged into user object")
}
import com.everybytesystems.dataflow.core.DHIS2Client
import com.everybytesystems.dataflow.core.config.DHIS2Config
import com.everybytesystems.dataflow.core.network.ApiResponse
import com.everybytesystems.dataflow.core.version.DHIS2Version
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    println("🚀 DHIS2 EBSCore SDK Demo")
    println("=" * 50)
    
    // Demo 1: Version Detection and Feature Support
    println("\n📋 Demo 1: Version Detection and Feature Support")
    demoVersionDetection()
    
    // Demo 2: Configuration Builder
    println("\n⚙️ Demo 2: Configuration Builder")
    demoConfigurationBuilder()
    
    // Demo 3: Client Creation with Known Version
    println("\n🔧 Demo 3: Client Creation with Known Version")
    demoClientWithKnownVersion()
    
    println("\n✅ Demo completed successfully!")
}

suspend fun demoVersionDetection() {
    // Test version parsing
    val versions = listOf("2.35.0", "2.36.5", "2.40.4", "2.41.0-SNAPSHOT")
    
    versions.forEach { versionString ->
        val version = DHIS2Version.parse(versionString)
        if (version != null) {
            println("📦 Version: ${version.versionString}")
            println("   - Tracker API: ${version.supportsTrackerApi()}")
            println("   - New Analytics: ${version.supportsNewAnalytics()}")
            println("   - Enhanced Metadata: ${version.supportsEnhancedMetadata()}")
            println("   - Advanced Sync: ${version.supportsAdvancedSync()}")
            println("   - Modern Auth: ${version.supportsModernAuth()}")
        }
    }
}

fun demoConfigurationBuilder() {
    // Demo different configuration options
    val configs = listOf(
        // Basic auth config
        DHIS2Config.Builder()
            .baseUrl("https://play.dhis2.org/2.40.4")
            .username("admin")
            .password("district")
            .enableLogging(true)
            .logLevel(DHIS2Config.LogLevel.INFO)
            .build(),
        
        // Bearer token config
        DHIS2Config.Builder()
            .baseUrl("https://api.dhis2.org")
            .bearerToken("your-bearer-token-here")
            .requestTimeout(30000)
            .maxRetries(5)
            .build(),
        
        // API key config
        DHIS2Config.Builder()
            .baseUrl("https://custom.dhis2.instance.com")
            .apiKey("your-api-key-here")
            .enableRetry(false)
            .autoDetectVersion(false)
            .build()
    )
    
    configs.forEachIndexed { index, config ->
        println("🔧 Configuration ${index + 1}:")
        println("   - Base URL: ${config.baseUrl}")
        println("   - Auth Type: ${getAuthType(config)}")
        println("   - Logging: ${config.enableLogging}")
        println("   - Request Timeout: ${config.requestTimeout}ms")
        println("   - Max Retries: ${config.maxRetries}")
        println("   - Auto Detect Version: ${config.autoDetectVersion}")
        
        // Validate configuration
        val validation = config.validate()
        println("   - Valid: ${validation.isSuccess}")
        if (validation.isFailure) {
            println("   - Error: ${validation.exceptionOrNull()?.message}")
        }
        println()
    }
}

fun demoClientWithKnownVersion() {
    val config = DHIS2Config.Builder()
        .baseUrl("https://play.dhis2.org/2.40.4")
        .username("admin")
        .password("district")
        .build()
    
    // Create client with known version (no network call)
    val client = DHIS2Client.createWithVersion(config, DHIS2Version.V2_40)
    
    println("🔧 Client created with version: ${client.getVersion().versionString}")
    println("   - Supports Tracker API: ${client.supportsTrackerApi()}")
    println("   - Supports New Analytics: ${client.supportsNewAnalytics()}")
    println("   - Supports Enhanced Metadata: ${client.supportsEnhancedMetadata()}")
    println("   - Supports Advanced Sync: ${client.supportsAdvancedSync()}")
    println("   - Supports Modern Auth: ${client.supportsModernAuth()}")
    
    // Clean up
    client.close()
}

private fun getAuthType(config: DHIS2Config): String {
    return when {
        config.username != null && config.password != null -> "Basic Auth"
        config.bearerToken != null -> "Bearer Token"
        config.apiKey != null -> "API Key"
        else -> "None"
    }
}

private operator fun String.times(n: Int): String = this.repeat(n)
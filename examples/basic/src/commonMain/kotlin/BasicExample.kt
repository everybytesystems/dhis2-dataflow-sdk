import com.everybytesystems.dataflow.core.DHIS2Client
import com.everybytesystems.dataflow.core.config.DHIS2Config
import com.everybytesystems.dataflow.core.network.ApiResponse

/**
 * Basic example demonstrating DHIS2 SDK usage
 */
suspend fun basicExample() {
    // Configure DHIS2 connection
    val config = DHIS2Config.Builder()
        .baseUrl("https://play.dhis2.org/2.40.4")
        .username("admin")
        .password("district")
        .enableLogging(true)
        .logLevel(DHIS2Config.LogLevel.INFO)
        .build()
    
    // Create client with automatic version detection
    when (val clientResult = DHIS2Client.create(config)) {
        is ApiResponse.Success -> {
            val client = clientResult.data
            
            println("Connected to DHIS2 ${client.getVersion().versionString}")
            
            // Test system info
            when (val systemInfoResult = client.system.getSystemInfo()) {
                is ApiResponse.Success -> {
                    val systemInfo = systemInfoResult.data
                    println("System: ${systemInfo.systemName}")
                    println("Version: ${systemInfo.version}")
                    println("Build Time: ${systemInfo.buildTime}")
                }
                is ApiResponse.Error -> {
                    println("Failed to get system info: ${systemInfoResult.message}")
                }
                is ApiResponse.Loading -> {
                    println("Loading system info...")
                }
            }
            
            // Test ping
            when (val pingResult = client.ping()) {
                is ApiResponse.Success -> {
                    println("Ping successful: ${pingResult.data}")
                }
                is ApiResponse.Error -> {
                    println("Ping failed: ${pingResult.message}")
                }
                is ApiResponse.Loading -> {
                    println("Pinging...")
                }
            }
            
            // Get data elements
            when (val dataElementsResult = client.metadata.getDataElements(pageSize = 5)) {
                is ApiResponse.Success -> {
                    val response = dataElementsResult.data
                    println("Found ${response.pager?.total ?: 0} data elements")
                    response.dataElements.forEach { dataElement ->
                        println("- ${dataElement.name} (${dataElement.id})")
                    }
                }
                is ApiResponse.Error -> {
                    println("Failed to get data elements: ${dataElementsResult.message}")
                }
                is ApiResponse.Loading -> {
                    println("Loading data elements...")
                }
            }
            
            // Get organisation units
            when (val orgUnitsResult = client.metadata.getOrganisationUnits(pageSize = 5)) {
                is ApiResponse.Success -> {
                    val response = orgUnitsResult.data
                    println("Found ${response.pager?.total ?: 0} organisation units")
                    response.organisationUnits.forEach { orgUnit ->
                        println("- ${orgUnit.name} (Level: ${orgUnit.level})")
                    }
                }
                is ApiResponse.Error -> {
                    println("Failed to get organisation units: ${orgUnitsResult.message}")
                }
                is ApiResponse.Loading -> {
                    println("Loading organisation units...")
                }
            }
            
            // Check feature support
            println("\nFeature Support:")
            println("- Tracker API: ${client.supportsTrackerApi()}")
            println("- New Analytics: ${client.supportsNewAnalytics()}")
            println("- Enhanced Metadata: ${client.supportsEnhancedMetadata()}")
            println("- Advanced Sync: ${client.supportsAdvancedSync()}")
            println("- Modern Auth: ${client.supportsModernAuth()}")
            
            // Close the client
            client.close()
        }
        is ApiResponse.Error -> {
            println("Failed to create DHIS2 client: ${clientResult.message}")
        }
        is ApiResponse.Loading -> {
            println("Creating DHIS2 client...")
        }
    }
}

/**
 * Example with known version (skip version detection)
 */
fun quickExample() {
    val config = DHIS2Config.Builder()
        .baseUrl("https://play.dhis2.org/2.40.4")
        .username("admin")
        .password("district")
        .build()
    
    // Create client with known version
    val client = DHIS2Client.createWithVersion(config, com.everybytesystems.dataflow.core.version.DHIS2Version.V2_40)
    
    println("Created DHIS2 client for version ${client.getVersion().versionString}")
    
    // Use the client...
    
    client.close()
}
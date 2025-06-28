package com.everybytesystems.dataflow.examples.enhanced

import com.everybytesystems.dataflow.core.DHIS2Client
import com.everybytesystems.dataflow.core.config.DHIS2Config
import com.everybytesystems.dataflow.core.network.ApiResponse
import kotlinx.coroutines.runBlocking

/**
 * Example demonstrating enhanced APIs with version-aware features
 */
fun main() = runBlocking {
    // Configure DHIS2 connection
    val config = DHIS2Config(
        baseUrl = "https://play.dhis2.org/2.40.4",
        username = "admin",
        password = "district"
    )
    
    // Create client
    val client = DHIS2Client.create(config)
    
    println("=== Enhanced DHIS2 SDK Example ===")
    println("Detected Version: ${client.detectedVersion.versionString}")
    println()
    
    // Demonstrate System API enhancements
    demonstrateSystemApi(client)
    
    // Demonstrate Metadata API enhancements
    demonstrateMetadataApi(client)
    
    client.close()
}

suspend fun demonstrateSystemApi(client: DHIS2Client) {
    println("=== System API Enhancements ===")
    
    // Get system information
    when (val response = client.system.getSystemInfo()) {
        is ApiResponse.Success -> {
            val systemInfo = response.data
            println("System Version: ${systemInfo.version}")
            println("Server Date: ${systemInfo.serverDate}")
            println("System ID: ${systemInfo.systemId}")
        }
        is ApiResponse.Error -> {
            println("Error getting system info: ${response.exception.message}")
        }
    }
    
    // Version-aware system statistics (2.37+)
    if (client.detectedVersion.supportsSystemStatistics()) {
        println("\n--- System Statistics (2.37+) ---")
        when (val response = client.system.getSystemStatistics()) {
            is ApiResponse.Success -> {
                val stats = response.data.statistics
                println("Data Values: ${stats.dataValueCount}")
                println("Events: ${stats.eventCount}")
                println("Enrollments: ${stats.enrollmentCount}")
            }
            is ApiResponse.Error -> {
                println("Error getting system statistics: ${response.exception.message}")
            }
        }
    } else {
        println("System statistics not supported in version ${client.detectedVersion.versionString}")
    }
    
    // Version-aware system health (2.38+)
    if (client.detectedVersion.supportsSystemHealth()) {
        println("\n--- System Health (2.38+) ---")
        when (val response = client.system.getSystemHealth()) {
            is ApiResponse.Success -> {
                val health = response.data
                println("Health Status: ${health.status}")
                println("Health Checks: ${health.checks.size}")
                health.checks.forEach { check ->
                    println("  - ${check.name}: ${check.status}")
                }
            }
            is ApiResponse.Error -> {
                println("Error getting system health: ${response.exception.message}")
            }
        }
    } else {
        println("System health not supported in version ${client.detectedVersion.versionString}")
    }
    
    // Cache management
    println("\n--- Cache Management ---")
    when (val response = client.system.getCacheInfo()) {
        is ApiResponse.Success -> {
            val cacheInfo = response.data
            println("Available Caches: ${cacheInfo.caches.size}")
            cacheInfo.caches.forEach { cache ->
                println("  - ${cache.name}: ${cache.size} items, ${cache.hitRate}% hit rate")
            }
        }
        is ApiResponse.Error -> {
            println("Error getting cache info: ${response.exception.message}")
        }
    }
    
    println()
}

suspend fun demonstrateMetadataApi(client: DHIS2Client) {
    println("=== Metadata API Enhancements ===")
    
    // Get data elements with enhanced filtering
    println("--- Data Elements ---")
    when (val response = client.metadata.getDataElements(
        fields = "id,name,shortName,code,valueType,aggregationType",
        page = 1,
        pageSize = 5
    )) {
        is ApiResponse.Success -> {
            val dataElements = response.data.dataElements
            println("Found ${dataElements.size} data elements:")
            dataElements.forEach { element ->
                println("  - ${element.name} (${element.code}) - ${element.valueType}")
            }
        }
        is ApiResponse.Error -> {
            println("Error getting data elements: ${response.exception.message}")
        }
    }
    
    // Version-aware metadata gist (2.37+)
    if (client.detectedVersion.supportsMetadataGist()) {
        println("\n--- Metadata Gist (2.37+) ---")
        when (val response = client.metadata.getMetadataGist(
            filter = listOf("name:like:ANC"),
            fields = "id,name,displayName"
        )) {
            is ApiResponse.Success -> {
                val gist = response.data.gist
                println("Found ${gist.size} metadata objects matching 'ANC':")
                gist.forEach { item ->
                    println("  - ${item.name} (${item.id})")
                }
            }
            is ApiResponse.Error -> {
                println("Error getting metadata gist: ${response.exception.message}")
            }
        }
    } else {
        println("Metadata gist not supported in version ${client.detectedVersion.versionString}")
    }
    
    // Version-aware metadata dependencies (2.37+)
    if (client.detectedVersion.supportsMetadataDependencies()) {
        println("\n--- Metadata Dependencies (2.37+) ---")
        // Get first data element to check dependencies
        when (val elementsResponse = client.metadata.getDataElements(pageSize = 1)) {
            is ApiResponse.Success -> {
                val firstElement = elementsResponse.data.dataElements.firstOrNull()
                if (firstElement?.id != null) {
                    when (val depResponse = client.metadata.getMetadataDependencies(
                        uid = firstElement.id!!,
                        type = "dataElement"
                    )) {
                        is ApiResponse.Success -> {
                            val dependencies = depResponse.data.dependencies
                            println("Dependencies for ${firstElement.name}: ${dependencies.size}")
                            dependencies.forEach { dep ->
                                println("  - ${dep.name} (${dep.type})")
                            }
                        }
                        is ApiResponse.Error -> {
                            println("Error getting dependencies: ${depResponse.exception.message}")
                        }
                    }
                }
            }
            is ApiResponse.Error -> {
                println("Error getting data elements for dependency check: ${elementsResponse.exception.message}")
            }
        }
    } else {
        println("Metadata dependencies not supported in version ${client.detectedVersion.versionString}")
    }
    
    // Version-aware sharing (2.36+)
    if (client.detectedVersion.supportsMetadataSharing()) {
        println("\n--- Metadata Sharing (2.36+) ---")
        // Get first data element to check sharing
        when (val elementsResponse = client.metadata.getDataElements(pageSize = 1)) {
            is ApiResponse.Success -> {
                val firstElement = elementsResponse.data.dataElements.firstOrNull()
                if (firstElement?.id != null) {
                    when (val sharingResponse = client.metadata.getSharing(
                        type = "dataElement",
                        id = firstElement.id!!
                    )) {
                        is ApiResponse.Success -> {
                            val sharing = sharingResponse.data.`object`
                            println("Sharing for ${sharing.name}:")
                            println("  - Public Access: ${sharing.publicAccess}")
                            println("  - External Access: ${sharing.externalAccess}")
                            println("  - User Accesses: ${sharing.userAccesses.size}")
                            println("  - User Group Accesses: ${sharing.userGroupAccesses.size}")
                        }
                        is ApiResponse.Error -> {
                            println("Error getting sharing: ${sharingResponse.exception.message}")
                        }
                    }
                }
            }
            is ApiResponse.Error -> {
                println("Error getting data elements for sharing check: ${elementsResponse.exception.message}")
            }
        }
    } else {
        println("Metadata sharing not supported in version ${client.detectedVersion.versionString}")
    }
    
    println()
}package com.everybytesystems.dataflow.examples.enhanced

import com.everybytesystems.dataflow.core.DHIS2Client
import com.everybytesystems.dataflow.core.config.DHIS2Config
import com.everybytesystems.dataflow.core.network.ApiResponse
import kotlinx.coroutines.runBlocking

/**
 * Example demonstrating enhanced APIs with version-aware features
 */
fun main() = runBlocking {
    // Configure DHIS2 connection
    val config = DHIS2Config(
        baseUrl = "https://play.dhis2.org/2.40.4",
        username = "admin",
        password = "district"
    )
    
    // Create client
    val client = DHIS2Client.create(config)
    
    println("=== Enhanced DHIS2 SDK Example ===")
    println("Detected Version: ${client.detectedVersion.versionString}")
    println()
    
    // Demonstrate System API enhancements
    demonstrateSystemApi(client)
    
    // Demonstrate Metadata API enhancements
    demonstrateMetadataApi(client)
    
    client.close()
}

suspend fun demonstrateSystemApi(client: DHIS2Client) {
    println("=== System API Enhancements ===")
    
    // Get system information
    when (val response = client.system.getSystemInfo()) {
        is ApiResponse.Success -> {
            val systemInfo = response.data
            println("System Version: ${systemInfo.version}")
            println("Server Date: ${systemInfo.serverDate}")
            println("System ID: ${systemInfo.systemId}")
        }
        is ApiResponse.Error -> {
            println("Error getting system info: ${response.exception.message}")
        }
    }
    
    // Version-aware system statistics (2.37+)
    if (client.detectedVersion.supportsSystemStatistics()) {
        println("\n--- System Statistics (2.37+) ---")
        when (val response = client.system.getSystemStatistics()) {
            is ApiResponse.Success -> {
                val stats = response.data.statistics
                println("Data Values: ${stats.dataValueCount}")
                println("Events: ${stats.eventCount}")
                println("Enrollments: ${stats.enrollmentCount}")
            }
            is ApiResponse.Error -> {
                println("Error getting system statistics: ${response.exception.message}")
            }
        }
    } else {
        println("System statistics not supported in version ${client.detectedVersion.versionString}")
    }
    
    // Version-aware system health (2.38+)
    if (client.detectedVersion.supportsSystemHealth()) {
        println("\n--- System Health (2.38+) ---")
        when (val response = client.system.getSystemHealth()) {
            is ApiResponse.Success -> {
                val health = response.data
                println("Health Status: ${health.status}")
                println("Health Checks: ${health.checks.size}")
                health.checks.forEach { check ->
                    println("  - ${check.name}: ${check.status}")
                }
            }
            is ApiResponse.Error -> {
                println("Error getting system health: ${response.exception.message}")
            }
        }
    } else {
        println("System health not supported in version ${client.detectedVersion.versionString}")
    }
    
    // Cache management
    println("\n--- Cache Management ---")
    when (val response = client.system.getCacheInfo()) {
        is ApiResponse.Success -> {
            val cacheInfo = response.data
            println("Available Caches: ${cacheInfo.caches.size}")
            cacheInfo.caches.forEach { cache ->
                println("  - ${cache.name}: ${cache.size} items, ${cache.hitRate}% hit rate")
            }
        }
        is ApiResponse.Error -> {
            println("Error getting cache info: ${response.exception.message}")
        }
    }
    
    println()
}

suspend fun demonstrateMetadataApi(client: DHIS2Client) {
    println("=== Metadata API Enhancements ===")
    
    // Get data elements with enhanced filtering
    println("--- Data Elements ---")
    when (val response = client.metadata.getDataElements(
        fields = "id,name,shortName,code,valueType,aggregationType",
        page = 1,
        pageSize = 5
    )) {
        is ApiResponse.Success -> {
            val dataElements = response.data.dataElements
            println("Found ${dataElements.size} data elements:")
            dataElements.forEach { element ->
                println("  - ${element.name} (${element.code}) - ${element.valueType}")
            }
        }
        is ApiResponse.Error -> {
            println("Error getting data elements: ${response.exception.message}")
        }
    }
    
    // Version-aware metadata gist (2.37+)
    if (client.detectedVersion.supportsMetadataGist()) {
        println("\n--- Metadata Gist (2.37+) ---")
        when (val response = client.metadata.getMetadataGist(
            filter = listOf("name:like:ANC"),
            fields = "id,name,displayName"
        )) {
            is ApiResponse.Success -> {
                val gist = response.data.gist
                println("Found ${gist.size} metadata objects matching 'ANC':")
                gist.forEach { item ->
                    println("  - ${item.name} (${item.id})")
                }
            }
            is ApiResponse.Error -> {
                println("Error getting metadata gist: ${response.exception.message}")
            }
        }
    } else {
        println("Metadata gist not supported in version ${client.detectedVersion.versionString}")
    }
    
    // Version-aware metadata dependencies (2.37+)
    if (client.detectedVersion.supportsMetadataDependencies()) {
        println("\n--- Metadata Dependencies (2.37+) ---")
        // Get first data element to check dependencies
        when (val elementsResponse = client.metadata.getDataElements(pageSize = 1)) {
            is ApiResponse.Success -> {
                val firstElement = elementsResponse.data.dataElements.firstOrNull()
                if (firstElement?.id != null) {
                    when (val depResponse = client.metadata.getMetadataDependencies(
                        uid = firstElement.id!!,
                        type = "dataElement"
                    )) {
                        is ApiResponse.Success -> {
                            val dependencies = depResponse.data.dependencies
                            println("Dependencies for ${firstElement.name}: ${dependencies.size}")
                            dependencies.forEach { dep ->
                                println("  - ${dep.name} (${dep.type})")
                            }
                        }
                        is ApiResponse.Error -> {
                            println("Error getting dependencies: ${depResponse.exception.message}")
                        }
                    }
                }
            }
            is ApiResponse.Error -> {
                println("Error getting data elements for dependency check: ${elementsResponse.exception.message}")
            }
        }
    } else {
        println("Metadata dependencies not supported in version ${client.detectedVersion.versionString}")
    }
    
    // Version-aware sharing (2.36+)
    if (client.detectedVersion.supportsMetadataSharing()) {
        println("\n--- Metadata Sharing (2.36+) ---")
        // Get first data element to check sharing
        when (val elementsResponse = client.metadata.getDataElements(pageSize = 1)) {
            is ApiResponse.Success -> {
                val firstElement = elementsResponse.data.dataElements.firstOrNull()
                if (firstElement?.id != null) {
                    when (val sharingResponse = client.metadata.getSharing(
                        type = "dataElement",
                        id = firstElement.id!!
                    )) {
                        is ApiResponse.Success -> {
                            val sharing = sharingResponse.data.`object`
                            println("Sharing for ${sharing.name}:")
                            println("  - Public Access: ${sharing.publicAccess}")
                            println("  - External Access: ${sharing.externalAccess}")
                            println("  - User Accesses: ${sharing.userAccesses.size}")
                            println("  - User Group Accesses: ${sharing.userGroupAccesses.size}")
                        }
                        is ApiResponse.Error -> {
                            println("Error getting sharing: ${sharingResponse.exception.message}")
                        }
                    }
                }
            }
            is ApiResponse.Error -> {
                println("Error getting data elements for sharing check: ${elementsResponse.exception.message}")
            }
        }
    } else {
        println("Metadata sharing not supported in version ${client.detectedVersion.versionString}")
    }
    
    println()
}
# Enhanced APIs Documentation

This document describes the enhanced APIs in the DHIS2 DataFlow SDK that provide version-aware functionality and comprehensive feature support.

## Overview

The enhanced APIs automatically detect the DHIS2 version and provide appropriate functionality based on what's supported in that version. This ensures compatibility across different DHIS2 versions while enabling access to the latest features when available.

## Version Detection

The SDK automatically detects the DHIS2 version during client initialization:

```kotlin
val client = DHIS2Client.create(config)
println("Detected Version: ${client.detectedVersion.versionString}")
```

## Enhanced System API

The System API provides comprehensive system management and monitoring capabilities.

### Basic System Information

```kotlin
// Get system information
val systemInfo = client.system.getSystemInfo()

// Ping system for health check
val pingResponse = client.system.ping()

// Get system ID
val systemId = client.system.getSystemId()
```

### Version-Aware Features

#### System Statistics (2.37+)
```kotlin
if (client.detectedVersion.supportsSystemStatistics()) {
    val stats = client.system.getSystemStatistics()
    println("Data Values: ${stats.data.statistics.dataValueCount}")
    println("Events: ${stats.data.statistics.eventCount}")
}
```

#### System Health Monitoring (2.38+)
```kotlin
if (client.detectedVersion.supportsSystemHealth()) {
    val health = client.system.getSystemHealth()
    println("Health Status: ${health.data.status}")
    health.data.checks.forEach { check ->
        println("${check.name}: ${check.status}")
    }
}
```

#### System Performance Metrics (2.38+)
```kotlin
if (client.detectedVersion.supportsSystemPerformance()) {
    val performance = client.system.getSystemPerformance()
    println("CPU Usage: ${performance.data.performance.cpuUsage}%")
    println("Memory Usage: ${performance.data.performance.memoryUsage}%")
}
```

### Cache Management

```kotlin
// Get cache information
val cacheInfo = client.system.getCacheInfo()

// Clear specific cache
client.system.clearCache("metadata")

// Clear all caches
client.system.clearAllCaches()

// Reload configuration
client.system.reloadConfiguration()
```

### Maintenance Mode (2.37+)

```kotlin
if (client.detectedVersion.supportsMaintenanceMode()) {
    // Check maintenance status
    val status = client.system.getMaintenanceStatus()
    
    // Enable maintenance mode
    client.system.enableMaintenanceMode("System maintenance in progress")
    
    // Disable maintenance mode
    client.system.disableMaintenanceMode()
}
```

### System Settings

```kotlin
// Get all system settings
val settings = client.system.getSystemSettings()

// Get specific setting
val setting = client.system.getSystemSetting("keyAnalyticsMaxLimit")

// Set system setting with validation
client.system.setSystemSetting("keyAnalyticsMaxLimit", 50000, validate = true)

// Delete system setting
client.system.deleteSystemSetting("customSetting")
```

## Enhanced Metadata API

The Metadata API provides comprehensive metadata management with version-aware features.

### Basic Metadata Operations

```kotlin
// Get data elements with enhanced filtering
val dataElements = client.metadata.getDataElements(
    fields = "id,name,shortName,code,valueType,aggregationType",
    page = 1,
    pageSize = 50
)

// Get single data element
val dataElement = client.metadata.getDataElement("dataElementId", fields = "*")

// Create data element
val newElement = DataElement(name = "New Data Element", ...)
client.metadata.createDataElement(newElement)

// Update data element
client.metadata.updateDataElement("id", updatedElement)

// Delete data element
client.metadata.deleteDataElement("id")
```

### Version-Aware Features

#### Metadata Gist (2.37+)
```kotlin
if (client.detectedVersion.supportsMetadataGist()) {
    val gist = client.metadata.getMetadataGist(
        filter = listOf("name:like:ANC"),
        fields = "id,name,displayName"
    )
    gist.data.gist.forEach { item ->
        println("${item.name} (${item.id})")
    }
}
```

#### Metadata Dependencies (2.37+)
```kotlin
if (client.detectedVersion.supportsMetadataDependencies()) {
    val dependencies = client.metadata.getMetadataDependencies(
        uid = "dataElementId",
        type = "dataElement"
    )
    dependencies.data.dependencies.forEach { dep ->
        println("${dep.name} (${dep.type})")
    }
}
```

#### Metadata Sharing (2.36+)
```kotlin
if (client.detectedVersion.supportsMetadataSharing()) {
    // Get sharing settings
    val sharing = client.metadata.getSharing("dataElement", "elementId")
    
    // Update sharing settings
    val newSharing = SharingSettings(
        publicAccess = "r-------",
        externalAccess = false,
        userAccesses = listOf(
            UserAccess(id = "userId", access = "rw------")
        )
    )
    client.metadata.updateSharing("dataElement", "elementId", newSharing)
}
```

### Metadata Import/Export

```kotlin
// Export metadata
val metadata = client.metadata.getMetadata(
    fields = "id,name,shortName",
    filter = listOf("name:like:ANC"),
    defaults = "INCLUDE"
)

// Import metadata with validation
val importData = MetadataImport(
    dataElements = listOf(dataElement1, dataElement2)
)
val importResult = client.metadata.importMetadata(
    metadata = importData,
    importStrategy = "CREATE_AND_UPDATE",
    atomicMode = "ALL",
    mergeMode = "REPLACE"
)

// Validate metadata before import
val validation = client.metadata.validateMetadata(importData)
```

### Organization Units

```kotlin
// Get organization units with hierarchy
val orgUnits = client.metadata.getOrganisationUnits(
    fields = "id,name,shortName,code,level,path,parent,children",
    page = 1,
    pageSize = 50
)

// Get single organization unit
val orgUnit = client.metadata.getOrganisationUnit("orgUnitId")

// Create organization unit
val newOrgUnit = OrganisationUnit(name = "New Health Facility", ...)
client.metadata.createOrganisationUnit(newOrgUnit)
```

### Programs

```kotlin
// Get programs
val programs = client.metadata.getPrograms(
    fields = "id,name,shortName,code,programType,trackedEntityType",
    page = 1,
    pageSize = 50
)

// Get single program
val program = client.metadata.getProgram("programId")
```

### Data Sets

```kotlin
// Get data sets
val dataSets = client.metadata.getDataSets(
    fields = "id,name,shortName,code,periodType,categoryCombo",
    page = 1,
    pageSize = 50
)

// Get single data set
val dataSet = client.metadata.getDataSet("dataSetId")
```

## Feature Support Detection

The SDK provides comprehensive feature support detection:

```kotlin
val version = client.detectedVersion

// Metadata features
if (version.supportsMetadataGist()) { /* Use gist API */ }
if (version.supportsMetadataVersioning()) { /* Use versioning */ }
if (version.supportsMetadataSharing()) { /* Use sharing */ }
if (version.supportsMetadataDependencies()) { /* Use dependencies */ }

// System features
if (version.supportsSystemStatistics()) { /* Use statistics */ }
if (version.supportsSystemHealth()) { /* Use health monitoring */ }
if (version.supportsMaintenanceMode()) { /* Use maintenance mode */ }

// Tracker features
if (version.supportsTrackerApi()) { /* Use new tracker API */ }
if (version.supportsTrackerWorkingLists()) { /* Use working lists */ }

// Analytics features
if (version.supportsNewAnalytics()) { /* Use new analytics */ }
if (version.supportsAnalyticsOutlierDetection()) { /* Use outlier detection */ }
```

## Error Handling

The enhanced APIs provide comprehensive error handling:

```kotlin
when (val response = client.metadata.getDataElements()) {
    is ApiResponse.Success -> {
        val dataElements = response.data.dataElements
        // Process data elements
    }
    is ApiResponse.Error -> {
        when (response.exception) {
            is UnsupportedOperationException -> {
                println("Feature not supported in this version")
            }
            else -> {
                println("API error: ${response.exception.message}")
            }
        }
    }
}
```

## Best Practices

1. **Always check version support** before using version-specific features
2. **Use appropriate error handling** for both API errors and version compatibility
3. **Leverage enhanced filtering** for better performance
4. **Use pagination** for large datasets
5. **Validate metadata** before importing
6. **Monitor system health** in production environments

## Example Usage

See the complete example in `examples/enhanced-apis/src/main/kotlin/EnhancedApisExample.kt` for a comprehensive demonstration of the enhanced APIs.

```bash
# Run the enhanced APIs example
./gradlew :examples:enhanced-apis:run
```

## Supported DHIS2 Versions

- **2.36+**: Basic enhanced features, metadata sharing, translations
- **2.37+**: Metadata gist, dependencies, system statistics, maintenance mode
- **2.38+**: System health, performance monitoring, metadata versioning
- **2.39+**: Advanced features, event hooks
- **2.40+**: Latest features, offline sync, advanced caching

The SDK automatically adapts to the detected version and provides appropriate functionality.# Enhanced APIs Documentation

This document describes the enhanced APIs in the DHIS2 DataFlow SDK that provide version-aware functionality and comprehensive feature support.

## Overview

The enhanced APIs automatically detect the DHIS2 version and provide appropriate functionality based on what's supported in that version. This ensures compatibility across different DHIS2 versions while enabling access to the latest features when available.

## Version Detection

The SDK automatically detects the DHIS2 version during client initialization:

```kotlin
val client = DHIS2Client.create(config)
println("Detected Version: ${client.detectedVersion.versionString}")
```

## Enhanced System API

The System API provides comprehensive system management and monitoring capabilities.

### Basic System Information

```kotlin
// Get system information
val systemInfo = client.system.getSystemInfo()

// Ping system for health check
val pingResponse = client.system.ping()

// Get system ID
val systemId = client.system.getSystemId()
```

### Version-Aware Features

#### System Statistics (2.37+)
```kotlin
if (client.detectedVersion.supportsSystemStatistics()) {
    val stats = client.system.getSystemStatistics()
    println("Data Values: ${stats.data.statistics.dataValueCount}")
    println("Events: ${stats.data.statistics.eventCount}")
}
```

#### System Health Monitoring (2.38+)
```kotlin
if (client.detectedVersion.supportsSystemHealth()) {
    val health = client.system.getSystemHealth()
    println("Health Status: ${health.data.status}")
    health.data.checks.forEach { check ->
        println("${check.name}: ${check.status}")
    }
}
```

#### System Performance Metrics (2.38+)
```kotlin
if (client.detectedVersion.supportsSystemPerformance()) {
    val performance = client.system.getSystemPerformance()
    println("CPU Usage: ${performance.data.performance.cpuUsage}%")
    println("Memory Usage: ${performance.data.performance.memoryUsage}%")
}
```

### Cache Management

```kotlin
// Get cache information
val cacheInfo = client.system.getCacheInfo()

// Clear specific cache
client.system.clearCache("metadata")

// Clear all caches
client.system.clearAllCaches()

// Reload configuration
client.system.reloadConfiguration()
```

### Maintenance Mode (2.37+)

```kotlin
if (client.detectedVersion.supportsMaintenanceMode()) {
    // Check maintenance status
    val status = client.system.getMaintenanceStatus()
    
    // Enable maintenance mode
    client.system.enableMaintenanceMode("System maintenance in progress")
    
    // Disable maintenance mode
    client.system.disableMaintenanceMode()
}
```

### System Settings

```kotlin
// Get all system settings
val settings = client.system.getSystemSettings()

// Get specific setting
val setting = client.system.getSystemSetting("keyAnalyticsMaxLimit")

// Set system setting with validation
client.system.setSystemSetting("keyAnalyticsMaxLimit", 50000, validate = true)

// Delete system setting
client.system.deleteSystemSetting("customSetting")
```

## Enhanced Metadata API

The Metadata API provides comprehensive metadata management with version-aware features.

### Basic Metadata Operations

```kotlin
// Get data elements with enhanced filtering
val dataElements = client.metadata.getDataElements(
    fields = "id,name,shortName,code,valueType,aggregationType",
    page = 1,
    pageSize = 50
)

// Get single data element
val dataElement = client.metadata.getDataElement("dataElementId", fields = "*")

// Create data element
val newElement = DataElement(name = "New Data Element", ...)
client.metadata.createDataElement(newElement)

// Update data element
client.metadata.updateDataElement("id", updatedElement)

// Delete data element
client.metadata.deleteDataElement("id")
```

### Version-Aware Features

#### Metadata Gist (2.37+)
```kotlin
if (client.detectedVersion.supportsMetadataGist()) {
    val gist = client.metadata.getMetadataGist(
        filter = listOf("name:like:ANC"),
        fields = "id,name,displayName"
    )
    gist.data.gist.forEach { item ->
        println("${item.name} (${item.id})")
    }
}
```

#### Metadata Dependencies (2.37+)
```kotlin
if (client.detectedVersion.supportsMetadataDependencies()) {
    val dependencies = client.metadata.getMetadataDependencies(
        uid = "dataElementId",
        type = "dataElement"
    )
    dependencies.data.dependencies.forEach { dep ->
        println("${dep.name} (${dep.type})")
    }
}
```

#### Metadata Sharing (2.36+)
```kotlin
if (client.detectedVersion.supportsMetadataSharing()) {
    // Get sharing settings
    val sharing = client.metadata.getSharing("dataElement", "elementId")
    
    // Update sharing settings
    val newSharing = SharingSettings(
        publicAccess = "r-------",
        externalAccess = false,
        userAccesses = listOf(
            UserAccess(id = "userId", access = "rw------")
        )
    )
    client.metadata.updateSharing("dataElement", "elementId", newSharing)
}
```

### Metadata Import/Export

```kotlin
// Export metadata
val metadata = client.metadata.getMetadata(
    fields = "id,name,shortName",
    filter = listOf("name:like:ANC"),
    defaults = "INCLUDE"
)

// Import metadata with validation
val importData = MetadataImport(
    dataElements = listOf(dataElement1, dataElement2)
)
val importResult = client.metadata.importMetadata(
    metadata = importData,
    importStrategy = "CREATE_AND_UPDATE",
    atomicMode = "ALL",
    mergeMode = "REPLACE"
)

// Validate metadata before import
val validation = client.metadata.validateMetadata(importData)
```

### Organization Units

```kotlin
// Get organization units with hierarchy
val orgUnits = client.metadata.getOrganisationUnits(
    fields = "id,name,shortName,code,level,path,parent,children",
    page = 1,
    pageSize = 50
)

// Get single organization unit
val orgUnit = client.metadata.getOrganisationUnit("orgUnitId")

// Create organization unit
val newOrgUnit = OrganisationUnit(name = "New Health Facility", ...)
client.metadata.createOrganisationUnit(newOrgUnit)
```

### Programs

```kotlin
// Get programs
val programs = client.metadata.getPrograms(
    fields = "id,name,shortName,code,programType,trackedEntityType",
    page = 1,
    pageSize = 50
)

// Get single program
val program = client.metadata.getProgram("programId")
```

### Data Sets

```kotlin
// Get data sets
val dataSets = client.metadata.getDataSets(
    fields = "id,name,shortName,code,periodType,categoryCombo",
    page = 1,
    pageSize = 50
)

// Get single data set
val dataSet = client.metadata.getDataSet("dataSetId")
```

## Feature Support Detection

The SDK provides comprehensive feature support detection:

```kotlin
val version = client.detectedVersion

// Metadata features
if (version.supportsMetadataGist()) { /* Use gist API */ }
if (version.supportsMetadataVersioning()) { /* Use versioning */ }
if (version.supportsMetadataSharing()) { /* Use sharing */ }
if (version.supportsMetadataDependencies()) { /* Use dependencies */ }

// System features
if (version.supportsSystemStatistics()) { /* Use statistics */ }
if (version.supportsSystemHealth()) { /* Use health monitoring */ }
if (version.supportsMaintenanceMode()) { /* Use maintenance mode */ }

// Tracker features
if (version.supportsTrackerApi()) { /* Use new tracker API */ }
if (version.supportsTrackerWorkingLists()) { /* Use working lists */ }

// Analytics features
if (version.supportsNewAnalytics()) { /* Use new analytics */ }
if (version.supportsAnalyticsOutlierDetection()) { /* Use outlier detection */ }
```

## Error Handling

The enhanced APIs provide comprehensive error handling:

```kotlin
when (val response = client.metadata.getDataElements()) {
    is ApiResponse.Success -> {
        val dataElements = response.data.dataElements
        // Process data elements
    }
    is ApiResponse.Error -> {
        when (response.exception) {
            is UnsupportedOperationException -> {
                println("Feature not supported in this version")
            }
            else -> {
                println("API error: ${response.exception.message}")
            }
        }
    }
}
```

## Best Practices

1. **Always check version support** before using version-specific features
2. **Use appropriate error handling** for both API errors and version compatibility
3. **Leverage enhanced filtering** for better performance
4. **Use pagination** for large datasets
5. **Validate metadata** before importing
6. **Monitor system health** in production environments

## Example Usage

See the complete example in `examples/enhanced-apis/src/main/kotlin/EnhancedApisExample.kt` for a comprehensive demonstration of the enhanced APIs.

```bash
# Run the enhanced APIs example
./gradlew :examples:enhanced-apis:run
```

## Supported DHIS2 Versions

- **2.36+**: Basic enhanced features, metadata sharing, translations
- **2.37+**: Metadata gist, dependencies, system statistics, maintenance mode
- **2.38+**: System health, performance monitoring, metadata versioning
- **2.39+**: Advanced features, event hooks
- **2.40+**: Latest features, offline sync, advanced caching

The SDK automatically adapts to the detected version and provides appropriate functionality.
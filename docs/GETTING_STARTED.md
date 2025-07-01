# Getting Started with EBSCore SDK

This guide will help you get started with the EBSCore SDK for DHIS2 integration.

## 📋 Prerequisites

- **Kotlin Multiplatform** project setup
- **DHIS2 instance** (version 2.36 or higher)
- **Valid DHIS2 credentials** (username/password or OAuth2)

## 📦 Installation

### 1. Add Repository

Add JitPack repository to your `build.gradle.kts`:

```kotlin
repositories {
    mavenCentral()
    maven("https://jitpack.io")
}
```

### 2. Add Dependencies

```kotlin
dependencies {
    // Core SDK (required)
    implementation("com.github.everybytesystems.ebscore-sdk:ebscore-sdk-core:1.0.0")
    
    // Authentication (required)
    implementation("com.github.everybytesystems.ebscore-sdk:ebscore-sdk-auth:1.0.0")
    
    // Optional modules
    implementation("com.github.everybytesystems.ebscore-sdk:ebscore-sdk-metadata:1.0.0")
    implementation("com.github.everybytesystems.ebscore-sdk:ebscore-sdk-data:1.0.0")
    implementation("com.github.everybytesystems.ebscore-sdk:ebscore-sdk-tracker:1.0.0")
    implementation("com.github.everybytesystems.ebscore-sdk:ebscore-sdk-analytics:1.0.0")
}
```

## 🚀 Basic Setup

### 1. Create SDK Instance

```kotlin
import com.everybytesystems.ebscore.sdk.EBSCoreSdkBuilder
import com.everybytesystems.ebscore.auth.BasicAuthConfig

suspend fun createSdk(): EBSCoreSdk {
    return EBSCoreSdkBuilder()
        .baseUrl("https://play.dhis2.org/2.42.0")
        .autoDetectVersion(true)
        .enableLogging(true)
        .build()
}
```

### 2. Initialize and Authenticate

```kotlin
suspend fun main() {
    val sdk = createSdk()
    
    // Initialize SDK
    sdk.initialize()
    
    // Authenticate with basic auth
    val authResult = sdk.authenticate(
        BasicAuthConfig(
            username = "admin",
            password = "district"
        )
    )
    
    when {
        authResult.isSuccess -> {
            println("✅ Authentication successful!")
            // Use the SDK
            useSDK(sdk)
        }
        authResult.isError -> {
            println("❌ Authentication failed: ${authResult.error}")
        }
    }
    
    // Always close the SDK
    sdk.close()
}
```

## 🔐 Authentication Options

### Basic Authentication

```kotlin
val basicAuth = BasicAuthConfig(
    username = "your-username",
    password = "your-password"
)

sdk.authenticate(basicAuth)
```

### OAuth2 Authentication

```kotlin
val oauth2Config = OAuth2Config(
    clientId = "your-client-id",
    clientSecret = "your-client-secret",
    redirectUri = "your-app://callback",
    scopes = listOf("read", "write")
)

sdk.authenticate(oauth2Config)
```

### Personal Access Token

```kotlin
val tokenConfig = PersonalAccessTokenConfig(
    token = "your-personal-access-token"
)

sdk.authenticate(tokenConfig)
```

## 📊 Using the APIs

### System Information

```kotlin
suspend fun getSystemInfo(sdk: EBSCoreSdk) {
    val systemInfo = sdk.systemApi.getSystemInfo()
    
    when {
        systemInfo.isSuccess -> {
            val info = systemInfo.data!!
            println("DHIS2 Version: ${info.version}")
            println("Server Date: ${info.serverDate}")
            println("Database: ${info.databaseInfo?.name}")
        }
        systemInfo.isError -> {
            println("Error: ${systemInfo.error}")
        }
    }
}
```

### Metadata Operations

```kotlin
suspend fun fetchMetadata(sdk: EBSCoreSdk) {
    // Get data elements
    val dataElements = sdk.metadataApi.getDataElements(
        fields = "id,name,valueType,domainType",
        filter = "domainType:eq:AGGREGATE"
    )
    
    // Get organization units
    val orgUnits = sdk.metadataApi.getOrganisationUnits(
        fields = "id,name,level,parent",
        filter = "level:le:3"
    )
    
    // Get programs
    val programs = sdk.metadataApi.getPrograms(
        fields = "id,name,programType,trackedEntityType"
    )
}
```

### Data Operations

```kotlin
suspend fun workWithData(sdk: EBSCoreSdk) {
    // Submit data values
    val dataValueSet = DataValueSet(
        dataSet = "BfMAe6Itzgt",
        completeDate = "2024-01-15",
        period = "202401",
        orgUnit = "DiszpKrYNg8",
        dataValues = listOf(
            DataValue(
                dataElement = "FTRrcoaog83",
                value = "100",
                categoryOptionCombo = "HllvX50cXC0"
            )
        )
    )
    
    val result = sdk.dataApi.postDataValueSet(dataValueSet)
    
    // Get analytics data
    val analytics = sdk.analyticsApi.getAnalytics(
        dimension = listOf(
            "dx:FTRrcoaog83;Jtf34kNZhzP",
            "pe:LAST_12_MONTHS",
            "ou:USER_ORGUNIT"
        ),
        displayProperty = "NAME"
    )
}
```

### Tracker Operations

```kotlin
suspend fun workWithTracker(sdk: EBSCoreSdk) {
    // Get tracked entity instances
    val trackedEntities = sdk.trackerApi.getTrackedEntityInstances(
        ou = "DiszpKrYNg8",
        program = "IpHINAT79UW",
        fields = "trackedEntityInstance,attributes,enrollments"
    )
    
    // Create new tracked entity
    val newEntity = TrackedEntityInstance(
        trackedEntityType = "nEenWmSyUEp",
        orgUnit = "DiszpKrYNg8",
        attributes = listOf(
            Attribute(
                attribute = "w75KJ2mc4zz",
                value = "John Doe"
            )
        )
    )
    
    val createResult = sdk.trackerApi.postTrackedEntityInstances(
        listOf(newEntity)
    )
}
```

## ⚙️ Configuration Options

### SDK Builder Options

```kotlin
val sdk = EBSCoreSdkBuilder()
    .baseUrl("https://your-dhis2-instance.org")
    .autoDetectVersion(true)           // Auto-detect DHIS2 version
    .enableLogging(true)               // Enable debug logging
    .enableCaching(true)               // Enable response caching
    .cacheSize(50 * 1024 * 1024)      // 50MB cache size
    .enableCompression(true)           // Enable GZIP compression
    .connectTimeout(30_000)            // 30 second connect timeout
    .readTimeout(60_000)               // 60 second read timeout
    .userAgent("MyApp/1.0")            // Custom user agent
    .build()
```

### Version-Specific Features

```kotlin
// Check DHIS2 version and use appropriate features
val version = sdk.getDetectedVersion()

when {
    version >= DHIS2Version.V2_40 -> {
        // Use features available in 2.40+
        val result = sdk.trackerApi.getTrackerObjects()
    }
    version >= DHIS2Version.V2_38 -> {
        // Use features available in 2.38+
        val result = sdk.analyticsApi.getEventAnalytics()
    }
    else -> {
        // Fallback for older versions
        val result = sdk.analyticsApi.getAnalytics()
    }
}
```

## 🔄 Error Handling

### Handling API Responses

```kotlin
suspend fun handleApiResponse(sdk: EBSCoreSdk) {
    val result = sdk.metadataApi.getDataElements()
    
    when {
        result.isSuccess -> {
            val dataElements = result.data!!
            println("Retrieved ${dataElements.size} data elements")
        }
        
        result.isError -> {
            when (val error = result.error!!) {
                is NetworkError -> {
                    println("Network error: ${error.message}")
                    // Handle network issues
                }
                
                is AuthenticationError -> {
                    println("Authentication failed: ${error.message}")
                    // Re-authenticate or show login
                }
                
                is ValidationError -> {
                    println("Validation error: ${error.message}")
                    // Handle validation issues
                }
                
                is ServerError -> {
                    println("Server error: ${error.httpCode} - ${error.message}")
                    // Handle server errors
                }
                
                else -> {
                    println("Unknown error: ${error.message}")
                }
            }
        }
    }
}
```

### Retry Logic

```kotlin
suspend fun withRetry(
    maxRetries: Int = 3,
    delayMs: Long = 1000,
    operation: suspend () -> ApiResponse<T>
): ApiResponse<T> {
    repeat(maxRetries) { attempt ->
        val result = operation()
        
        if (result.isSuccess || !result.error!!.isRetryable()) {
            return result
        }
        
        if (attempt < maxRetries - 1) {
            delay(delayMs * (attempt + 1))
        }
    }
    
    return operation() // Final attempt
}
```

## 🎯 Best Practices

### 1. Resource Management

```kotlin
// Always close the SDK when done
try {
    val sdk = EBSCoreSdkBuilder().build()
    // Use SDK
} finally {
    sdk.close()
}

// Or use with automatic resource management
EBSCoreSdkBuilder().build().use { sdk ->
    // Use SDK - automatically closed
}
```

### 2. Caching Strategy

```kotlin
// Enable caching for better performance
val sdk = EBSCoreSdkBuilder()
    .enableCaching(true)
    .cacheSize(100 * 1024 * 1024) // 100MB
    .build()

// Use cache-aware methods
val dataElements = sdk.metadataApi.getDataElements(
    useCache = true,
    maxAge = Duration.ofHours(1)
)
```

### 3. Batch Operations

```kotlin
// Batch multiple operations for better performance
val batch = sdk.createBatch()
    .addDataValues(dataValueSet1)
    .addDataValues(dataValueSet2)
    .addTrackedEntities(trackedEntities)

val results = batch.execute()
```

## 🐛 Troubleshooting

### Common Issues

1. **Connection Timeout**
   ```kotlin
   // Increase timeout values
   .connectTimeout(60_000)
   .readTimeout(120_000)
   ```

2. **Authentication Failures**
   ```kotlin
   // Check credentials and server URL
   // Enable logging to see detailed error messages
   .enableLogging(true)
   ```

3. **Version Compatibility**
   ```kotlin
   // Check detected version
   val version = sdk.getDetectedVersion()
   println("Detected DHIS2 version: $version")
   ```

### Debug Logging

```kotlin
// Enable detailed logging
val sdk = EBSCoreSdkBuilder()
    .enableLogging(true)
    .logLevel(LogLevel.DEBUG)
    .build()
```

## 📚 Next Steps

- [API Reference](API_REFERENCE.md) - Complete API documentation
- [Examples](EXAMPLES.md) - More code examples and use cases
- [Publishing Guide](PUBLISHING.md) - How to publish your app

---

**Need help?** Check our [GitHub Discussions](https://github.com/everybytesystems/ebscore-sdk/discussions) or create an [issue](https://github.com/everybytesystems/ebscore-sdk/issues).
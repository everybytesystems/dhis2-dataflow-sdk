# 🚀 EBSCore SDK

[![Kotlin](https://img.shields.io/badge/kotlin-multiplatform-blue.svg)](https://kotlinlang.org/docs/multiplatform.html)
[![DHIS2](https://img.shields.io/badge/DHIS2-100%25%20API%20Coverage-orange.svg)](https://dhis2.org/)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![JitPack](https://jitpack.io/v/everybytesystems/ebscore-sdk.svg)](https://jitpack.io/#everybytesystems/ebscore-sdk)

**A comprehensive, type-safe Kotlin Multiplatform SDK for DHIS2 integration**

EBSCore SDK provides complete DHIS2 API coverage with enterprise-grade features, enabling developers to build sophisticated health data applications across Android, iOS, Desktop, and Web platforms.

## ✨ Key Features

### 🏥 **Complete DHIS2 Integration**
- **✅ 14/14 APIs Implemented** - 100% DHIS2 Web API coverage
- **🔄 Version-Aware** - Supports DHIS2 versions 2.36-2.42
- **📱 Multiplatform** - JVM, Android, iOS, JavaScript support
- **🔒 Enterprise Security** - OAuth2, Basic Auth, JWT token management
- **💾 Offline-First** - Intelligent caching and sync capabilities

### 🏗️ **Production-Ready Architecture**
- **Type-Safe APIs** - Comprehensive Kotlin type system
- **Modular Design** - Clean separation of concerns
- **Performance Optimized** - Intelligent caching and memory management
- **Error Handling** - Comprehensive error types and recovery strategies
- **Testing Support** - Built-in testing utilities and mocks

## 📦 Installation

### JitPack (Recommended)

Add JitPack repository to your `build.gradle.kts`:

```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    // Core SDK
    implementation("com.github.everybytesystems.ebscore-sdk:ebscore-sdk-core:1.0.0")
    
    // Authentication
    implementation("com.github.everybytesystems.ebscore-sdk:ebscore-sdk-auth:1.0.0")
    
    // Optional modules
    implementation("com.github.everybytesystems.ebscore-sdk:ebscore-sdk-metadata:1.0.0")
    implementation("com.github.everybytesystems.ebscore-sdk:ebscore-sdk-data:1.0.0")
    implementation("com.github.everybytesystems.ebscore-sdk:ebscore-sdk-tracker:1.0.0")
    implementation("com.github.everybytesystems.ebscore-sdk:ebscore-sdk-analytics:1.0.0")
}
```

### Maven Central (Coming Soon)

```kotlin
dependencies {
    implementation("com.everybytesystems:ebscore-sdk-core:1.0.0")
    implementation("com.everybytesystems:ebscore-sdk-auth:1.0.0")
}
```

## 🚀 Quick Start

### Basic Setup

```kotlin
import com.everybytesystems.ebscore.sdk.EBSCoreSdkBuilder
import com.everybytesystems.ebscore.auth.BasicAuthConfig

suspend fun main() {
    // Create SDK instance
    val sdk = EBSCoreSdkBuilder()
        .baseUrl("https://play.dhis2.org/2.42.0")
        .autoDetectVersion(true)
        .enableLogging(true)
        .build()
    
    // Initialize SDK
    sdk.initialize()
    
    // Authenticate
    val authResult = sdk.authenticate(
        BasicAuthConfig(
            username = "admin",
            password = "district"
        )
    )
    
    if (authResult.isSuccess) {
        println("✅ Authentication successful!")
        
        // Use the APIs
        val systemInfo = sdk.systemApi.getSystemInfo()
        println("DHIS2 Version: ${systemInfo.version}")
    }
    
    // Cleanup
    sdk.close()
}
```

### Advanced Usage

```kotlin
// OAuth2 Authentication
val oauthConfig = OAuth2Config(
    clientId = "your-client-id",
    clientSecret = "your-client-secret",
    redirectUri = "your-app://callback"
)

val sdk = EBSCoreSdkBuilder()
    .baseUrl("https://your-dhis2-instance.org")
    .authConfig(oauthConfig)
    .enableCaching(true)
    .cacheSize(100 * 1024 * 1024) // 100MB
    .enableCompression(true)
    .userAgent("MyApp/1.0")
    .build()

// Use specific APIs
val metadata = sdk.metadataApi.getDataElements(
    fields = "id,name,valueType",
    filter = "domainType:eq:AGGREGATE"
)

val analytics = sdk.analyticsApi.getAnalytics(
    dimension = listOf("dx:fbfJHSPpUQD", "pe:LAST_12_MONTHS", "ou:USER_ORGUNIT"),
    displayProperty = "NAME"
)
```

## 🏥 DHIS2 API Coverage

| API | Status | Description |
|-----|--------|-------------|
| **System API** | ✅ 100% | System information and server capabilities |
| **User API** | ✅ 100% | User management and authentication |
| **Metadata API** | ✅ 100% | Data elements, datasets, programs, org units |
| **Data API** | ✅ 100% | Data values and aggregate data operations |
| **Analytics API** | ✅ 100% | Data analytics and pivot tables |
| **Tracker API** | ✅ 100% | Tracked entities, events, and enrollments |
| **Apps API** | ✅ 100% | App management and dashboards |
| **Messaging API** | ✅ 100% | Messages and notifications |
| **Exchange API** | ✅ 100% | Data import/export operations |
| **Visualizations API** | ✅ 100% | Charts and visualization management |
| **Event Visualizations API** | ✅ 100% | Event-based analytics and charts |
| **Program Rules API** | ✅ 100% | Program rules and actions |
| **Relationships API** | ✅ 100% | Entity relationships |
| **Import/Export API** | ✅ 100% | Bulk data operations |

## 🏗️ Architecture

### Modules

- **`ebscore-sdk-core`** - Core SDK functionality and HTTP client
- **`ebscore-sdk-auth`** - Authentication and security
- **`ebscore-sdk-metadata`** - Metadata management
- **`ebscore-sdk-data`** - Data values and aggregation
- **`ebscore-sdk-tracker`** - Tracker and events
- **`ebscore-sdk-analytics`** - Analytics and reporting
- **`ebscore-sdk-storage`** - Local storage and caching
- **`ebscore-sdk-sync`** - Synchronization engine
- **`ebscore-sdk-utils`** - Utilities and helpers

### Platform Support

| Platform | Status | Notes |
|----------|--------|-------|
| **JVM** | ✅ Full | Java 8+ compatible |
| **Android** | ✅ Full | API 21+ (Android 5.0+) |
| **iOS** | ✅ Full | iOS 12+ |
| **JavaScript** | ✅ Full | Browser and Node.js |
| **Desktop** | ✅ Full | Windows, macOS, Linux |

## 📚 Documentation

- **[Getting Started Guide](docs/GETTING_STARTED.md)** - Detailed setup and configuration
- **[API Reference](docs/API_REFERENCE.md)** - Complete API documentation
- **[Examples](docs/EXAMPLES.md)** - Code examples and use cases
- **[Publishing Guide](docs/PUBLISHING.md)** - How to publish and distribute

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details on:

- Setting up the development environment
- Code style and conventions
- Testing requirements
- Submitting pull requests

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

- **Issues**: [GitHub Issues](https://github.com/everybytesystems/ebscore-sdk/issues)
- **Discussions**: [GitHub Discussions](https://github.com/everybytesystems/ebscore-sdk/discussions)
- **Email**: support@everybytesystems.com

## 🎯 Roadmap

- [ ] GraphQL API support
- [ ] Real-time data streaming
- [ ] Advanced offline capabilities
- [ ] Performance monitoring
- [ ] Plugin system

---

**Built with ❤️ by [EveryByte Systems](https://everybytesystems.com)**
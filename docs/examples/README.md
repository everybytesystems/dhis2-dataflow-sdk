# 📚 EBSCore SDK Examples

Complete examples and tutorials for all EBSCore SDK modules across Android, iOS, Desktop, and Web platforms.

## 🎯 Overview

This directory contains comprehensive examples demonstrating how to use EBSCore SDK in real-world applications. Each platform example shows best practices, common patterns, and advanced usage scenarios.

## 📁 Directory Structure

```
examples/
├── README.md                    # This file - overview and getting started
├── shared/                      # Shared multiplatform code examples
│   └── README.md               # Common patterns and business logic
├── android/                     # Android-specific examples
│   └── README.md               # Native Android implementation
├── ios/                        # iOS-specific examples
│   └── README.md               # Native iOS implementation
├── desktop/                    # Desktop-specific examples
│   └── README.md               # JVM Desktop implementation
├── web/                        # Web-specific examples
│   └── README.md               # Compose for Web implementation
├── tutorials/                  # Step-by-step tutorials
│   ├── getting-started.md      # Basic setup and first app
│   ├── authentication.md       # Authentication patterns
│   ├── data-management.md      # CRUD operations
│   ├── offline-sync.md         # Offline-first architecture
│   ├── analytics.md            # Analytics and reporting
│   └── charts.md              # Data visualization
└── samples/                    # Complete sample applications
    ├── health-tracker/         # Patient tracking app
    ├── analytics-dashboard/    # Analytics dashboard
    ├── clinic-management/      # Clinic management system
    └── mobile-health/          # Mobile health worker app
```

## 🚀 Quick Start Guide

### 1. Choose Your Platform

Select the platform that matches your development needs:

- **🤖 Android**: Native Android applications with Jetpack Compose
- **🍎 iOS**: Native iOS applications with SwiftUI integration
- **🖥️ Desktop**: Cross-platform desktop applications (Windows, macOS, Linux)
- **🌐 Web**: Browser-based applications with Compose for Web
- **🔄 Shared**: Multiplatform shared code for all platforms

### 2. Basic Setup

All examples require the EBSCore SDK dependency:

```kotlin
dependencies {
    implementation("com.everybytesystems.ebscore:ebscore-sdk:1.0.0")
}
```

### 3. Initialize the SDK

```kotlin
val sdk = EBSCoreSdk.Builder()
    .baseUrl("https://your-dhis2-instance.org")
    .credentials("username", "password")
    .enableOfflineMode(true)
    .enableAnalytics(true)
    .build()
```

## 📱 Platform Examples Overview

### Android Examples
- **Native UI**: Jetpack Compose integration
- **Material Design**: Material 3 components
- **Navigation**: Navigation Compose patterns
- **Offline Support**: Room database integration
- **Background Sync**: WorkManager implementation

### iOS Examples
- **SwiftUI Integration**: Native iOS UI patterns
- **Core Data**: Local storage implementation
- **Background Tasks**: iOS background processing
- **Push Notifications**: APNs integration
- **Biometric Auth**: Face ID/Touch ID support

### Desktop Examples
- **Compose Desktop**: Cross-platform desktop UI
- **File Management**: Local file operations
- **System Integration**: OS-specific features
- **Multi-window**: Desktop window management
- **Printing**: Report generation and printing

### Web Examples
- **Compose for Web**: Browser-based applications
- **PWA Support**: Progressive Web App features
- **Local Storage**: Browser storage APIs
- **Real-time Updates**: WebSocket integration
- **Responsive Design**: Mobile-first web design

## 🎓 Learning Path

### Beginner Level
1. **Getting Started** - Basic SDK setup and configuration
2. **Authentication** - User login and session management
3. **Data Fetching** - Basic DHIS2 data retrieval
4. **Simple UI** - Display data in lists and forms

### Intermediate Level
1. **Data Management** - CRUD operations and validation
2. **Offline Support** - Local storage and sync
3. **Charts & Analytics** - Data visualization
4. **Error Handling** - Robust error management

### Advanced Level
1. **Custom Sync** - Advanced synchronization patterns
2. **Performance** - Optimization and caching strategies
3. **Security** - Advanced authentication and encryption
4. **Architecture** - Clean architecture patterns

## 🏗️ Module Examples

### Core SDK Module
```kotlin
// Basic SDK usage
val systemInfo = sdk.getSystemInfo()
val orgUnits = sdk.getOrganizationUnits()
val dataElements = sdk.getDataElements()
```

### DHIS2 Integration
```kotlin
// Complete DHIS2 API coverage
val dataValues = sdk.getDataValues("dataElement", "period", "orgUnit")
val trackedEntities = sdk.getTrackedEntities("program", "orgUnit")
val events = sdk.getEvents("program", "orgUnit")
```

### Analytics Module
```kotlin
// Advanced analytics
val query = AnalyticsQuery.builder()
    .dimension("dx", listOf("dataElement1", "dataElement2"))
    .dimension("pe", listOf("202401", "202402"))
    .dimension("ou", listOf("orgUnit1"))
    .build()

val analyticsData = sdk.getAnalytics(query)
```

### Chart Library (68+ Types)
```kotlin
// Interactive charts
val lineChart = LineChart(
    data = analyticsData,
    title = "Monthly Trends",
    interactive = true,
    exportable = true
)

val heatmap = Heatmap(
    data = geographicData,
    colorScheme = ColorScheme.VIRIDIS,
    interactive = true
)
```

### Sync Engine
```kotlin
// Intelligent synchronization
val syncResult = sdk.sync(
    options = SyncOptions(
        syncMetadata = true,
        syncData = true,
        conflictResolution = ConflictResolutionStrategy.SERVER_WINS
    )
)
```

## 🎯 Use Case Examples

### 1. Patient Registration System
```kotlin
// Create new patient
val patient = TrackedEntity(
    trackedEntityType = "person",
    orgUnit = "healthFacilityId",
    attributes = listOf(
        TrackedEntityAttribute("firstName", "John"),
        TrackedEntityAttribute("lastName", "Doe"),
        TrackedEntityAttribute("dateOfBirth", "1990-01-01"),
        TrackedEntityAttribute("gender", "male")
    )
)

val result = sdk.createTrackedEntity(patient)
```

### 2. Data Collection Forms
```kotlin
// Submit data values
val dataValues = listOf(
    DataValue(
        dataElement = "weight",
        period = "202401",
        orgUnit = "facilityId",
        value = "70.5"
    ),
    DataValue(
        dataElement = "height",
        period = "202401",
        orgUnit = "facilityId",
        value = "175"
    )
)

sdk.saveDataValues(dataValues)
```

### 3. Analytics Dashboard
```kotlin
// Generate dashboard data
val dashboardData = sdk.getDashboardData(
    orgUnit = "facilityId",
    period = "202401"
)

// Create visualizations
val charts = listOf(
    BarChart(dashboardData.indicators),
    LineChart(dashboardData.trends),
    PieChart(dashboardData.distribution)
)
```

### 4. Offline-First Mobile App
```kotlin
// Enable offline mode
val sdk = EBSCoreSdk.Builder()
    .baseUrl("https://dhis2-instance.org")
    .enableOfflineMode(true)
    .syncInterval(Duration.ofHours(6))
    .build()

// Data works offline automatically
val patients = sdk.getTrackedEntities("program", "orgUnit") // Works offline
```

## 🔧 Development Tools

### Code Generation
```bash
# Generate model classes from DHIS2 metadata
./gradlew generateModels --metadata-url=https://your-instance.org/api/metadata
```

### Testing Utilities
```kotlin
// Mock SDK for testing
val mockSdk = MockEBSCoreSdk.builder()
    .withMockData("patients.json")
    .withMockAnalytics("analytics.json")
    .build()
```

### Debug Tools
```kotlin
// Enable debug logging
val sdk = EBSCoreSdk.Builder()
    .enableDebugLogging(true)
    .logLevel(LogLevel.VERBOSE)
    .build()
```

## 📊 Performance Benchmarks

### Data Loading Performance
- **Small datasets** (< 1000 records): ~100ms
- **Medium datasets** (1000-10000 records): ~500ms
- **Large datasets** (> 10000 records): ~2s with pagination

### Chart Rendering Performance
- **Basic charts** (< 1000 points): ~50ms
- **Complex charts** (1000-10000 points): ~200ms
- **Interactive charts** with animations: ~300ms

### Sync Performance
- **Metadata sync**: ~30s for complete metadata
- **Data sync**: ~1s per 1000 data values
- **Incremental sync**: ~5s for daily changes

## 🔒 Security Examples

### Authentication Patterns
```kotlin
// OAuth 2.0 authentication
val sdk = EBSCoreSdk.Builder()
    .baseUrl("https://dhis2-instance.org")
    .oauth2(
        clientId = "your-client-id",
        clientSecret = "your-client-secret",
        redirectUri = "your-app://callback"
    )
    .build()

// Token-based authentication
val sdk = EBSCoreSdk.Builder()
    .baseUrl("https://dhis2-instance.org")
    .bearerToken("your-access-token")
    .build()
```

### Data Encryption
```kotlin
// Enable data encryption
val sdk = EBSCoreSdk.Builder()
    .enableEncryption(true)
    .encryptionKey("your-encryption-key")
    .build()
```

## 🌍 Internationalization

### Multi-language Support
```kotlin
// Set locale for SDK
val sdk = EBSCoreSdk.Builder()
    .locale(Locale.FRENCH)
    .build()

// Get localized metadata
val localizedDataElements = sdk.getDataElements(locale = "fr")
```

## 📱 Platform-Specific Features

### Android-Specific
- **Biometric Authentication**: Fingerprint/Face unlock
- **Background Sync**: WorkManager integration
- **Push Notifications**: Firebase Cloud Messaging
- **Camera Integration**: QR code scanning
- **Location Services**: GPS tracking

### iOS-Specific
- **Face ID/Touch ID**: Biometric authentication
- **Background App Refresh**: iOS background tasks
- **Push Notifications**: Apple Push Notification service
- **Core Location**: Location tracking
- **HealthKit Integration**: Health data integration

### Desktop-Specific
- **File System Access**: Local file operations
- **System Tray**: Background operation
- **Multi-monitor Support**: Window management
- **Printing**: Report generation
- **System Integration**: OS notifications

### Web-Specific
- **Progressive Web App**: Offline capabilities
- **Web Workers**: Background processing
- **Local Storage**: Browser storage APIs
- **WebRTC**: Real-time communication
- **Service Workers**: Caching strategies

## 🚀 Deployment Examples

### Android Deployment
```gradle
android {
    signingConfigs {
        release {
            storeFile file('keystore.jks')
            storePassword 'password'
            keyAlias 'key'
            keyPassword 'password'
        }
    }
    buildTypes {
        release {
            signingConfig signingConfigs.release
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt')
        }
    }
}
```

### iOS Deployment
```swift
// iOS deployment configuration
let config = EBSCoreConfig(
    baseUrl: "https://production-instance.org",
    environment: .production,
    enableCrashReporting: true
)
```

### Web Deployment
```kotlin
// Web deployment with service worker
val sdk = EBSCoreSdk.Builder()
    .baseUrl("https://api.example.com")
    .enableServiceWorker(true)
    .cacheStrategy(CacheStrategy.CACHE_FIRST)
    .build()
```

## 📈 Monitoring and Analytics

### Performance Monitoring
```kotlin
// Enable performance monitoring
val sdk = EBSCoreSdk.Builder()
    .enablePerformanceMonitoring(true)
    .performanceCallback { metrics ->
        // Send metrics to analytics service
        analyticsService.track(metrics)
    }
    .build()
```

### Error Tracking
```kotlin
// Enable error tracking
val sdk = EBSCoreSdk.Builder()
    .enableErrorTracking(true)
    .errorCallback { error ->
        // Send error to crash reporting service
        crashlytics.recordException(error)
    }
    .build()
```

## 🤝 Contributing to Examples

### Adding New Examples
1. Fork the repository
2. Create a new example in the appropriate platform directory
3. Follow the existing code style and documentation format
4. Include comprehensive comments and README
5. Add tests for your example
6. Submit a pull request

### Example Template
```kotlin
/**
 * Example: [Brief Description]
 * 
 * This example demonstrates:
 * - Feature 1
 * - Feature 2
 * - Feature 3
 * 
 * Prerequisites:
 * - DHIS2 instance with demo data
 * - Valid user credentials
 * 
 * Usage:
 * 1. Configure your DHIS2 instance URL
 * 2. Set up authentication
 * 3. Run the example
 */
class ExampleClass {
    // Implementation
}
```

## 📞 Support and Resources

- **📖 Full Documentation**: [API Documentation](https://everybytesystems.github.io/ebscore-sdk/)
- **💬 Community**: [GitHub Discussions](https://github.com/everybytesystems/ebscore-sdk/discussions)
- **🐛 Issues**: [GitHub Issues](https://github.com/everybytesystems/ebscore-sdk/issues)
- **📧 Email**: support@everybytesystems.com
- **🎓 Tutorials**: [Video Tutorials](https://youtube.com/everybytesystems)

---

**Ready to build amazing health data applications? Start with the [Getting Started Tutorial](tutorials/getting-started.md)!**
# DHIS2 EBSCore SDK

A comprehensive Kotlin Multiplatform SDK for interacting with DHIS2 systems, providing high-level APIs for metadata management, data synchronization, authentication, and more.

## 🚀 Features

### Core Functionality
- **Authentication Management**: Support for Basic Auth, Personal Access Tokens, and OAuth2/OIDC
- **Metadata Services**: Comprehensive metadata synchronization and caching
- **Data Services**: Data value submission, validation, and quality checks
- **Offline Support**: Local caching and offline-first architecture
- **Multi-platform**: Supports JVM, Android, and iOS (iOS support in development)

### High-Level Services

#### 🔐 Authentication (`auth` module)
- Multiple authentication methods (Basic, PAT, OAuth2)
- Secure credential storage
- Authentication state management
- Automatic token refresh

#### 📊 Metadata Management (`metadata` module)
- Data elements, organisation units, data sets
- Programs, indicators, option sets
- Hierarchical organisation unit support
- Search and filtering capabilities
- Offline caching with sync

#### 💾 Data Management (`data` module)
- Data value submission and retrieval
- Data set completion tracking
- Data validation and quality checks
- Bulk operations support
- Conflict resolution

#### 🎨 Visualization (`visual` module)
- Chart and graph generation
- Dashboard components
- Data visualization utilities
- Export capabilities

## 📁 Project Structure

```
dhis2-ebscore-sdk/
├── modules/
│   ├── core/           # Core networking, models, and utilities
│   ├── auth/           # Authentication and security
│   ├── metadata/       # Metadata management
│   ├── data/           # Data value operations
│   ├── visual/         # Visualization components
│   └── sdk/            # Main SDK facade and high-level APIs
├── examples/           # Usage examples and demos
└── docs/              # Documentation
```

## 🛠 Quick Start

### 1. Basic Setup

```kotlin
// Initialize the SDK
val sdk = EBSCoreSdkBuilder()
    .baseUrl("https://play.dhis2.org/40.2.2")
    .basicAuth("admin", "district")
    .enableLogging(true)
    .build()

// Initialize and authenticate
sdk.initialize()
val authResult = sdk.authenticate(
    AuthConfig.Basic("admin", "district")
)
```

### 2. Metadata Operations

```kotlin
// Sync all metadata
val syncResult = sdk.syncMetadata()

// Get cached data elements
val dataElements = sdk.getDataElements()

// Search for specific elements
val ancElements = sdk.searchDataElements("ANC")

// Get organisation units by level
val rootOrgUnits = sdk.getOrganisationUnitsByLevel(1)
```

### 3. Data Operations

```kotlin
// Submit data values
val dataValues = listOf(
    DataValue(
        dataElement = "dataElementId",
        period = "202401",
        orgUnit = "orgUnitId",
        value = "100"
    )
)
val submitResult = sdk.submitDataValues(dataValues)

// Complete a data set
sdk.completeDataSet("dataSetId", "202401", "orgUnitId")
```

## 🔧 Configuration Options

### SDK Builder Options

```kotlin
val sdk = EBSCoreSdkBuilder()
    .baseUrl("https://dhis2.example.org")
    .apiVersion("41")
    .enableLogging(true)
    .connectTimeout(30_000)
    .requestTimeout(60_000)
    .maxRetries(3)
    .enableCaching(true)
    .cacheSize(50 * 1024 * 1024) // 50MB
    .build()
```

### Authentication Methods

```kotlin
// Basic Authentication
AuthConfig.Basic("username", "password")

// Personal Access Token
AuthConfig.PersonalAccessToken("d2pat_...")

// OAuth2/OIDC
AuthConfig.OAuth2(
    clientId = "clientId",
    clientSecret = "secret",
    redirectUri = "redirect://uri"
)
```

## 📚 Advanced Usage

### Custom Error Handling

```kotlin
when (val result = sdk.syncMetadata()) {
    is ApiResponse.Success -> {
        println("Sync successful")
    }
    is ApiResponse.Error -> {
        println("Sync failed: ${result.message}")
        // Handle error, possibly use cached data
    }
    is ApiResponse.Loading -> {
        println("Sync in progress...")
    }
}
```

### Offline Support

```kotlin
// The SDK automatically caches data for offline use
val cachedDataElements = sdk.getDataElements() // Works offline

// Force refresh from server
val syncResult = sdk.syncMetadata(forceRefresh = true)
```

### Authentication State Monitoring

```kotlin
// Monitor authentication state changes
sdk.authManager.authState.collect { state ->
    when (state) {
        is AuthState.Authenticated -> {
            println("User: ${state.user.displayName}")
        }
        is AuthState.Unauthenticated -> {
            println("Please login")
        }
        is AuthState.Error -> {
            println("Auth error: ${state.message}")
        }
    }
}
```

## 🏗 Architecture

### Core Components

1. **DHIS2Client**: Low-level HTTP client with authentication
2. **DataCache**: Local storage and caching layer
3. **MetadataService**: High-level metadata operations
4. **DataService**: Data value management
5. **AuthManager**: Authentication state management

### Design Principles

- **Offline-First**: All data is cached locally for offline access
- **Type-Safe**: Comprehensive Kotlin type system usage
- **Reactive**: Flow-based APIs for real-time updates
- **Modular**: Clean separation of concerns across modules
- **Testable**: Dependency injection and mockable interfaces

## 🧪 Testing

```bash
# Run all tests
./gradlew test

# Run specific module tests
./gradlew :dhis2-ebscore-sdk-core:test
./gradlew :dhis2-ebscore-sdk-auth:test
```

## 📦 Dependencies

### Core Dependencies
- **Kotlin Multiplatform**: 2.0.21
- **Ktor**: 3.0.1 (HTTP client)
- **kotlinx.serialization**: 1.7.3 (JSON serialization)
- **kotlinx.coroutines**: 1.9.0 (Async operations)
- **kotlinx.datetime**: 0.6.1 (Date/time handling)

### Platform-Specific
- **Android**: OkHttp, AndroidX Security
- **JVM**: OkHttp
- **iOS**: Darwin HTTP client (planned)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🔗 Related Projects

- [DHIS2 Core](https://github.com/dhis2/dhis2-core)
- [DHIS2 Android SDK](https://github.com/dhis2/dhis2-android-sdk)
- [DHIS2 Web API](https://docs.dhis2.org/en/develop/using-the-api/dhis-core-version-master/web-api.html)

## 📞 Support

- [DHIS2 Community](https://community.dhis2.org/)
- [Documentation](https://docs.dhis2.org/)
- [GitHub Issues](https://github.com/your-org/dhis2-ebscore-sdk/issues)

---

**Note**: This SDK is currently in development. Some features may not be fully implemented yet. iOS support is planned for future releases.

## 🔧 Current Status

### ✅ Completed
- Project structure and module organization
- Core networking infrastructure with Ktor
- Authentication framework (Basic, PAT, OAuth2)
- Comprehensive DHIS2 model definitions
- Metadata API implementations
- Data value operations
- Error handling and response wrappers
- Offline caching architecture
- Version detection and compatibility

### 🚧 In Progress
- Build system optimization (addressing Kotlin compiler issues)
- iOS target support
- SQLDelight integration for local storage
- Comprehensive testing suite

### 📋 Planned
- Complete tracker operations
- Advanced visualization components
- Performance optimizations
- Documentation and examples
- CI/CD pipeline setup

## 🛠 Development Notes

The project is currently experiencing some build issues related to Kotlin compiler compatibility. We've temporarily disabled iOS targets and SQLDelight integration to focus on core JVM and Android functionality. These will be re-enabled once the build issues are resolved.

To work with the current codebase:
1. Focus on JVM and Android targets
2. Use the core networking and authentication modules
3. Implement business logic using the provided APIs
4. Test using the JVM target for now
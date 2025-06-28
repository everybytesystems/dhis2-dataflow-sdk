# DHIS2 DataFlow SDK

A powerful Kotlin Multiplatform SDK for Android and iOS that provides seamless integration with DHIS2 systems, featuring offline-first architecture, real-time data synchronization, and AI-powered insights.

## 🚀 Features

- **🔐 Multi-Auth Support**: Basic, Personal Access Token (PAT), OAuth2/OIDC, and Cookie authentication
- **📊 Complete DHIS2 Integration**: Metadata, Analytics, Tracker, DataValueSets, and Visualizations
- **💾 Offline-First Architecture**: SQLDelight-powered local storage with intelligent sync
- **🔄 Real-time Sync**: Background synchronization with conflict resolution
- **🎨 Compose Multiplatform UI**: Shared UI components for charts, tables, and dashboards
- **🤖 AI Insights**: Natural language commentary and trend analysis
- **🛡️ Secure Storage**: Platform-specific encrypted credential storage
- **📱 Cross-Platform**: Single codebase for Android and iOS

## 📦 Installation

### Gradle (Android/KMP)

```kotlin
dependencies {
    implementation("com.everybytesystems.dataflow-sdk:sdk:1.0.0")
}
```

### CocoaPods (iOS)

```ruby
pod 'DHIS2DataFlowSDK', '~> 1.0.0'
```

### Swift Package Manager (iOS)

```swift
dependencies: [
    .package(url: "https://github.com/everybytesystems/dhis2-dataflow-sdk", from: "1.0.0")
]
```

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    UI Layer (Compose MP)                    │
├─────────────────────────────────────────────────────────────┤
│  📊 Charts  │  📋 Tables  │  🗺️ Maps  │  🤖 AI Insights  │
├─────────────────────────────────────────────────────────────┤
│                      Service Layer                          │
├─────────────────────────────────────────────────────────────┤
│ 🔐 Auth │ 📈 Analytics │ 🏥 Tracker │ 📊 DataValueSets │
├─────────────────────────────────────────────────────────────┤
│                      Core Layer                             │
├─────────────────────────────────────────────────────────────┤
│  🌐 Ktor HTTP  │  💾 SQLDelight  │  🔄 Sync Manager  │
├─────────────────────────────────────────────────────────────┤
│                   Platform Bridge                           │
├─────────────────────────────────────────────────────────────┤
│      🤖 Android (Jetpack)    │    🍎 iOS (SwiftUI)        │
└─────────────────────────────────────────────────────────────┘
```

## 🚀 Quick Start

### 1. Initialize the SDK

```kotlin
// Android
val databaseDriverFactory = DatabaseDriverFactory(context)
val secureStorageFactory = SecureStorageFactory(context)

// iOS
val databaseDriverFactory = DatabaseDriverFactory()
val secureStorageFactory = SecureStorageFactory()

// Common initialization
val sdk = DataFlowSdkBuilder()
    .baseUrl("https://your-dhis2-instance.org")
    .apiVersion("41")
    .enableLogging(true)
    .databaseDriverFactory(databaseDriverFactory)
    .secureStorageFactory(secureStorageFactory)
    .build()

// Initialize the SDK
sdk.initialize()
```

### 2. Authenticate

```kotlin
// Basic Authentication
val authResult = sdk.authenticate(
    AuthConfig.Basic(
        username = "admin",
        password = "district"
    )
)

// Personal Access Token
val authResult = sdk.authenticate(
    AuthConfig.PersonalAccessToken(
        token = "d2pat_5xVA12xyUbWNedQxy4ohH77WlxRGVvZZ1151814092"
    )
)

// OAuth2/OIDC
val authResult = sdk.authenticate(
    AuthConfig.OAuth2(
        clientId = "your-client-id",
        redirectUri = "your-app://callback",
        authorizationUrl = "https://your-idp.org/auth",
        tokenUrl = "https://your-idp.org/token"
    )
)
```

### 3. Sync and Access Data

```kotlin
// Sync all metadata
sdk.metadataService.syncAll()

// Get data elements
val dataElements = sdk.metadataService.getDataElements()
println("Found ${dataElements.size} data elements")

// Search for specific data
val ancElements = sdk.metadataService.searchDataElements("ANC")

// Get organisation units by level
val districts = sdk.metadataService.getOrganisationUnitsByLevel(3)

// Get programs and stages
val programs = sdk.metadataService.getPrograms()
programs.forEach { program ->
    val stages = sdk.metadataService.getProgramStages(program.id)
    println("Program: ${program.name} has ${stages.size} stages")
}
```

### 4. Handle Authentication State

```kotlin
// Listen to authentication state changes
sdk.authManager.authState.collect { state ->
    when (state) {
        is AuthState.Authenticated -> {
            println("Welcome ${state.user.displayName}!")
            // Start using the SDK services
        }
        is AuthState.Error -> {
            println("Auth error: ${state.message}")
        }
        is AuthState.Unauthenticated -> {
            println("Please login")
        }
        is AuthState.Authenticating -> {
            println("Logging in...")
        }
    }
}
```

## 📊 Working with Data

### Metadata Management

```kotlin
// Get all data sets
val dataSets = sdk.metadataService.getDataSets()

// Get data elements for a specific data set
val dataElements = sdk.metadataService.getDataElementsForDataSet(dataSetId)

// Get organisation unit hierarchy
val rootUnits = sdk.metadataService.getOrganisationUnitsByLevel(1)
val children = sdk.metadataService.getChildOrganisationUnits(parentId)

// Work with option sets
val optionSets = sdk.metadataService.getOptionSets()
val options = sdk.metadataService.getOptions(optionSetId)
```

### Analytics Queries

```kotlin
// Create analytics request
val analyticsRequest = AnalyticsRequest(
    dimensions = listOf("pe:LAST_12_MONTHS", "ou:USER_ORGUNIT"),
    filters = listOf("dx:dataElementId1;dataElementId2")
)

// Execute analytics query
val analyticsResult = sdk.analyticsService.query(analyticsRequest)
when (analyticsResult) {
    is ApiResponse.Success -> {
        val data = analyticsResult.data
        println("Analytics data: ${data.rows.size} rows")
    }
    is ApiResponse.Error -> {
        println("Analytics error: ${analyticsResult.message}")
    }
}
```

### DataValueSets

```kotlin
// Fetch data value set
val dataValueSet = sdk.dataValueSetService.fetch(
    dataSet = "dataSetId",
    period = "202401",
    orgUnit = "orgUnitId"
)

// Create and post data values
val dataValueSet = DataValueSet(
    dataSet = "dataSetId",
    period = "202401",
    orgUnit = "orgUnitId",
    dataValues = listOf(
        DataValue(
            dataElement = "dataElementId",
            value = "100",
            categoryOptionCombo = "categoryOptionComboId"
        )
    )
)

val postResult = sdk.dataValueSetService.post(dataValueSet)
```

## 🔄 Offline Support

The SDK provides robust offline capabilities:

- **Automatic Caching**: All API responses are cached locally
- **Background Sync**: Periodic synchronization when online
- **Conflict Resolution**: Smart merging of local and remote changes
- **Offline Queue**: Operations are queued when offline and executed when online
- **Delta Sync**: Only sync changed data to minimize bandwidth

```kotlin
// Check sync status
sdk.syncManager.syncStatus.collect { status ->
    when (status) {
        is SyncStatus.Syncing -> showProgressIndicator()
        is SyncStatus.Success -> hideProgressIndicator()
        is SyncStatus.Error -> showErrorMessage(status.message)
    }
}

// Force sync
sdk.syncManager.syncAll()

// Handle offline scenarios
try {
    val data = sdk.metadataService.getDataElements()
    // This will return cached data if offline
} catch (e: Exception) {
    // Handle error
}
```

## 🛡️ Security

- **Encrypted Storage**: Credentials stored using platform-specific encryption
  - Android: EncryptedSharedPreferences with AES-256
  - iOS: Keychain Services with hardware security module
- **Token Management**: Automatic token refresh and secure storage
- **Certificate Pinning**: Optional SSL certificate pinning
- **Request Signing**: Support for request signing and verification

## 🎨 UI Components (Compose Multiplatform)

```kotlin
@Composable
fun DashboardScreen(sdk: DataFlowSdk) {
    val dataElements by sdk.metadataService.getDataElementsFlow().collectAsState()
    
    LazyColumn {
        items(dataElements) { element ->
            DataElementCard(
                dataElement = element,
                onClick = { /* Handle click */ }
            )
        }
    }
}

@Composable
fun ChartView(
    analyticsData: AnalyticsResponse,
    chartType: ChartType = ChartType.LINE
) {
    // Chart implementation using KoalaPlot or similar
    when (chartType) {
        ChartType.LINE -> LineChart(data = analyticsData)
        ChartType.BAR -> BarChart(data = analyticsData)
        ChartType.PIE -> PieChart(data = analyticsData)
    }
}
```

## 🧪 Testing

The SDK includes comprehensive testing utilities:

```kotlin
// Unit tests with MockK
@Test
fun testMetadataService() = runTest {
    val mockHttpClient = mockk<HttpClient>()
    val service = MetadataService(config, mockHttpClient, database, cache, sync)
    
    coEvery { mockHttpClient.get(any()) } returns mockResponse
    
    val result = service.getDataElements()
    assertEquals(expectedElements, result)
}

// Integration tests with test database
@Test
fun testOfflineSync() = runTest {
    val testDb = createInMemoryDatabase()
    val sdk = createTestSdk(database = testDb)
    
    // Test offline scenarios
    sdk.metadataService.syncAll()
    // Verify data is cached
}
```

## 📱 Platform-Specific Features

### Android

```kotlin
// Android-specific initialization
class MainActivity : ComponentActivity() {
    private lateinit var sdk: DataFlowSdk
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        sdk = DataFlowSdkBuilder()
            .baseUrl("https://dhis2.example.org")
            .databaseDriverFactory(DatabaseDriverFactory(this))
            .secureStorageFactory(SecureStorageFactory(this))
            .build()
        
        lifecycleScope.launch {
            sdk.initialize()
        }
    }
}
```

### iOS

```swift
// iOS-specific initialization
class AppDelegate: UIResponder, UIApplicationDelegate {
    var sdk: DataFlowSdk?
    
    func application(_ application: UIApplication, 
                    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        
        sdk = DataFlowSdkBuilder()
            .baseUrl("https://dhis2.example.org")
            .databaseDriverFactory(DatabaseDriverFactory())
            .secureStorageFactory(SecureStorageFactory())
            .build()
        
        Task {
            await sdk?.initialize()
        }
        
        return true
    }
}
```

## 🔧 Configuration

### Advanced Configuration

```kotlin
val sdk = DataFlowSdkBuilder()
    .baseUrl("https://dhis2.example.org")
    .apiVersion("41")
    .enableLogging(true)
    .connectTimeout(30_000)
    .requestTimeout(60_000)
    .maxRetries(3)
    .retryDelay(1000)
    .databaseDriverFactory(databaseDriverFactory)
    .secureStorageFactory(secureStorageFactory)
    .coroutineScope(customScope)
    .build()
```

## 📚 Documentation

- [Technical Documentation](dataflowskd-docs.md) - Detailed implementation guide
- [API Reference](docs/api-reference.md)
- [Architecture Guide](docs/architecture.md)
- [Migration Guide](docs/migration.md)
- [Troubleshooting](docs/troubleshooting.md)
- [Contributing](CONTRIBUTING.md)

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details.

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

- 📧 Email: support@everybytesystems.com
- 💬 Discord: [Join our community](https://discord.gg/dhis2-dataflow)
- 🐛 Issues: [GitHub Issues](https://github.com/everybytesystems/dhis2-dataflow-sdk/issues)
- 📖 Documentation: [Full Documentation](https://docs.dhis2-dataflow.com)

## 🗺️ Roadmap

- [ ] **v1.1**: Enhanced AI insights with offline LLM support
- [ ] **v1.2**: Real-time push notifications (FCM/APNS)
- [ ] **v1.3**: Advanced geospatial features with Mapbox integration
- [ ] **v1.4**: Program Rules Engine support
- [ ] **v1.5**: Flutter wrapper for cross-platform development

---

Made with ❤️ by [EveryByte Systems](https://everybytesystems.com)
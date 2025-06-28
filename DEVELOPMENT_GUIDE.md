# DHIS2 DataFlow SDK - Development Guide

## 🎯 Getting Started

This guide will help you understand how to work with and extend the DHIS2 DataFlow SDK.

## 📋 Prerequisites

- **JDK 17+**: Required for Kotlin 2.0.21
- **Android SDK**: For Android target development
- **IntelliJ IDEA** or **Android Studio**: Recommended IDEs
- **Git**: For version control

## 🏗 Project Setup

### 1. Clone and Build

```bash
git clone <repository-url>
cd dhis2-dataflow-sdk

# Build JVM and Android targets (iOS temporarily disabled)
./gradlew build
```

### 2. IDE Setup

#### IntelliJ IDEA
1. Open the project root directory
2. Import as Gradle project
3. Enable Kotlin Multiplatform plugin
4. Sync Gradle project

#### Android Studio
1. Open the project
2. Sync Gradle files
3. Ensure Kotlin plugin is updated

## 🔧 Current Build Status

### Working Targets
- ✅ **JVM**: Fully functional
- ✅ **Android**: Fully functional

### Temporarily Disabled
- ❌ **iOS**: Disabled due to build issues
- ❌ **SQLDelight**: Disabled due to compatibility issues

### Build Issues
The project currently has Kotlin compiler compatibility issues with the newer versions. To work around this:

1. **Focus on JVM/Android**: These targets work correctly
2. **Avoid iOS for now**: Will be re-enabled after fixes
3. **Use in-memory caching**: SQLDelight will be added back later

## 📁 Module Structure Deep Dive

### Core Module (`modules/core/`)

**Purpose**: Foundation layer with networking, models, and utilities

**Key Files**:
```
core/src/commonMain/kotlin/
├── api/                    # API interfaces and implementations
│   ├── auth/              # Authentication APIs
│   ├── data/              # Data value APIs
│   ├── metadata/          # Metadata APIs
│   └── system/            # System info APIs
├── config/                # Configuration classes
├── models/                # DHIS2 data models
│   ├── auth/              # Authentication models
│   ├── data/              # Data models
│   ├── metadata/          # Metadata models
│   └── system/            # System models
├── network/               # Networking utilities
└── version/               # Version detection
```

**Development Tips**:
- Add new DHIS2 models in `models/` package
- Extend APIs in `api/` package
- Use `DHIS2Client` for HTTP operations
- Follow existing serialization patterns

### Authentication Module (`modules/auth/`)

**Purpose**: Authentication and security management

**Key Components**:
- `AuthManager`: Central authentication coordinator
- `AuthConfig`: Configuration for different auth methods
- `AuthState`: Authentication state management
- `CredentialStorage`: Secure credential persistence

**Development Tips**:
- Extend `AuthConfig` for new authentication methods
- Implement platform-specific credential storage
- Use `AuthState` flow for reactive authentication

### Metadata Module (`modules/metadata/`)

**Purpose**: Metadata synchronization and management

**Key Components**:
- `MetadataService`: High-level metadata operations
- `MetadataCache`: Local metadata storage
- `MetadataSync`: Synchronization logic

**Development Tips**:
- Add new metadata types in core models first
- Implement caching strategies in `MetadataCache`
- Use `MetadataService` for business logic

### Data Module (`modules/data/`)

**Purpose**: Data value operations and validation

**Key Components**:
- `DataService`: Data value management
- `DataValueValidator`: Data validation logic
- `DataSetService`: Data set operations

**Development Tips**:
- Implement validation rules in `DataValueValidator`
- Use bulk operations for performance
- Handle conflicts in data synchronization

### Visual Module (`modules/visual/`)

**Purpose**: Data visualization components

**Key Components**:
- `ChartGenerator`: Chart creation utilities
- `DashboardComponent`: Dashboard building blocks
- `DataVisualization`: Visualization helpers

**Development Tips**:
- Use platform-specific chart libraries
- Implement common visualization patterns
- Support data export functionality

### SDK Module (`modules/sdk/`)

**Purpose**: Main SDK facade and high-level APIs

**Key Components**:
- `DataFlowSdk`: Main SDK interface
- `DataFlowSdkBuilder`: Builder pattern for configuration

**Development Tips**:
- Keep the API surface clean and intuitive
- Delegate to appropriate service modules
- Maintain backward compatibility

## 🔨 Development Workflow

### 1. Adding New Features

#### Step 1: Define Models (if needed)
```kotlin
// In modules/core/src/commonMain/kotlin/models/
@Serializable
data class NewDHIS2Model(
    val id: String,
    val name: String,
    val description: String? = null
)
```

#### Step 2: Create API Interface
```kotlin
// In modules/core/src/commonMain/kotlin/api/
interface NewModelApi {
    suspend fun getNewModels(): ApiResponse<List<NewDHIS2Model>>
    suspend fun createNewModel(model: NewDHIS2Model): ApiResponse<NewDHIS2Model>
}
```

#### Step 3: Implement API
```kotlin
class NewModelApiImpl(private val client: DHIS2Client) : NewModelApi {
    override suspend fun getNewModels(): ApiResponse<List<NewDHIS2Model>> {
        return client.get("/api/newModels")
    }
    
    override suspend fun createNewModel(model: NewDHIS2Model): ApiResponse<NewDHIS2Model> {
        return client.post("/api/newModels", model)
    }
}
```

#### Step 4: Add to Service Layer
```kotlin
// In appropriate service module
class NewModelService(private val api: NewModelApi) {
    suspend fun syncNewModels(): Flow<SyncProgress> = flow {
        emit(SyncProgress.Loading)
        when (val result = api.getNewModels()) {
            is ApiResponse.Success -> {
                // Cache and process
                emit(SyncProgress.Success)
            }
            is ApiResponse.Error -> {
                emit(SyncProgress.Error(result.message))
            }
        }
    }
}
```

#### Step 5: Expose in SDK
```kotlin
// In modules/sdk/
class DataFlowSdkImpl : DataFlowSdk {
    override suspend fun syncNewModels(): Flow<SyncProgress> {
        return newModelService.syncNewModels()
    }
}
```

### 2. Testing Strategy

#### Unit Tests
```kotlin
class NewModelServiceTest {
    @Test
    fun `syncNewModels should return success when API succeeds`() = runTest {
        // Given
        val mockApi = mockk<NewModelApi>()
        every { mockApi.getNewModels() } returns ApiResponse.Success(emptyList())
        
        val service = NewModelService(mockApi)
        
        // When
        val result = service.syncNewModels().first()
        
        // Then
        assertTrue(result is SyncProgress.Success)
    }
}
```

#### Integration Tests
```kotlin
class NewModelIntegrationTest {
    @Test
    fun `should sync new models end to end`() = runTest {
        // Test with real HTTP client against test server
    }
}
```

### 3. Error Handling Patterns

#### API Level
```kotlin
suspend fun apiCall(): ApiResponse<Data> {
    return try {
        val response = httpClient.get("/api/endpoint")
        ApiResponse.Success(response.body())
    } catch (e: Exception) {
        ApiResponse.Error(e.message ?: "Unknown error")
    }
}
```

#### Service Level
```kotlin
suspend fun serviceOperation(): Flow<Result<Data>> = flow {
    emit(Result.Loading)
    when (val apiResult = api.getData()) {
        is ApiResponse.Success -> {
            // Process and cache
            emit(Result.Success(processedData))
        }
        is ApiResponse.Error -> {
            // Try to use cached data
            val cachedData = cache.getData()
            if (cachedData != null) {
                emit(Result.Success(cachedData))
            } else {
                emit(Result.Error(apiResult.message))
            }
        }
    }
}
```

## 🧪 Testing Guidelines

### Test Structure
```
src/
├── commonTest/           # Common tests
├── jvmTest/             # JVM-specific tests
└── androidTest/         # Android-specific tests
```

### Testing Best Practices

1. **Mock External Dependencies**
```kotlin
val mockClient = mockk<DHIS2Client>()
val service = MetadataService(mockClient)
```

2. **Test Error Scenarios**
```kotlin
@Test
fun `should handle network errors gracefully`() {
    // Test network failure scenarios
}
```

3. **Test Caching Behavior**
```kotlin
@Test
fun `should return cached data when offline`() {
    // Test offline scenarios
}
```

## 🔧 Build Configuration

### Gradle Tasks

```bash
# Build all targets
./gradlew build

# Run tests
./gradlew test

# Build specific module
./gradlew :dhis2-dataflow-sdk-core:build

# Run specific module tests
./gradlew :dhis2-dataflow-sdk-auth:test
```

### Common Build Issues

#### Issue: Kotlin Compiler Error
```
Internal compiler error. See log for more details
```
**Solution**: Currently using Kotlin 2.0.21 with compatibility issues. Consider downgrading to 1.9.x temporarily.

#### Issue: iOS Build Fails
```
No such module 'Foundation'
```
**Solution**: iOS targets are temporarily disabled. Focus on JVM/Android.

#### Issue: SQLDelight Errors
```
SQLDelight plugin compatibility issues
```
**Solution**: SQLDelight is temporarily disabled. Use in-memory caching.

## 📚 Code Style Guidelines

### Kotlin Conventions
- Use `camelCase` for functions and properties
- Use `PascalCase` for classes and interfaces
- Use meaningful names
- Prefer `val` over `var`
- Use trailing commas in multi-line structures

### Architecture Patterns
- **Repository Pattern**: For data access
- **Builder Pattern**: For configuration
- **Observer Pattern**: For reactive updates
- **Dependency Injection**: For testability

### Documentation
- Document public APIs with KDoc
- Include usage examples
- Document complex business logic
- Keep README files updated

## 🚀 Deployment & Publishing

### Version Management
```kotlin
// gradle.properties
version=1.0.0-SNAPSHOT
```

### Publishing Configuration
```kotlin
// build.gradle.kts
publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["kotlin"])
        }
    }
}
```

## 🔮 Future Roadmap

### Short Term (Next Sprint)
1. Fix Kotlin compiler compatibility issues
2. Re-enable iOS targets
3. Add SQLDelight integration
4. Comprehensive testing suite

### Medium Term (Next Month)
1. Complete tracker operations
2. Advanced visualization components
3. Performance optimizations
4. Documentation improvements

### Long Term (Next Quarter)
1. CI/CD pipeline
2. Example applications
3. Performance benchmarking
4. Community contributions

## 🤝 Contributing Guidelines

### Pull Request Process
1. Fork the repository
2. Create feature branch: `git checkout -b feature/amazing-feature`
3. Make changes and add tests
4. Ensure all tests pass: `./gradlew test`
5. Commit changes: `git commit -m 'Add amazing feature'`
6. Push to branch: `git push origin feature/amazing-feature`
7. Open Pull Request

### Code Review Checklist
- [ ] Code follows style guidelines
- [ ] Tests are included and passing
- [ ] Documentation is updated
- [ ] No breaking changes (or properly documented)
- [ ] Performance impact considered

## 📞 Getting Help

### Resources
- **DHIS2 Documentation**: https://docs.dhis2.org/
- **Kotlin Multiplatform**: https://kotlinlang.org/docs/multiplatform.html
- **Ktor Documentation**: https://ktor.io/docs/

### Community
- **DHIS2 Community**: https://community.dhis2.org/
- **Kotlin Slack**: https://kotlinlang.slack.com/
- **Stack Overflow**: Tag questions with `dhis2` and `kotlin-multiplatform`

---


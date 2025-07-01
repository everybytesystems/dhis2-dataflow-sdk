# API Reference

Complete reference for all EBSCore SDK APIs.

## 🏗️ Core SDK

### EBSCoreSdk

Main SDK class providing access to all DHIS2 APIs.

```kotlin
class EBSCoreSdk {
    // API instances
    val systemApi: SystemApi
    val userApi: UserApi
    val metadataApi: MetadataApi
    val dataApi: DataApi
    val analyticsApi: AnalyticsApi
    val trackerApi: TrackerApi
    val appsApi: AppsApi
    val messagingApi: MessagingApi
    val exchangeApi: ExchangeApi
    val visualizationsApi: VisualizationsApi
    val eventVisualizationsApi: EventVisualizationsApi
    val programRulesApi: ProgramRulesApi
    val relationshipsApi: RelationshipsApi
    val importExportApi: ImportExportApi
    
    // Core methods
    suspend fun initialize()
    suspend fun authenticate(config: AuthConfig): ApiResponse<AuthResult>
    suspend fun close()
    fun getDetectedVersion(): DHIS2Version
}
```

### EBSCoreSdkBuilder

Builder for creating SDK instances.

```kotlin
class EBSCoreSdkBuilder {
    fun baseUrl(url: String): EBSCoreSdkBuilder
    fun autoDetectVersion(enabled: Boolean): EBSCoreSdkBuilder
    fun enableLogging(enabled: Boolean): EBSCoreSdkBuilder
    fun enableCaching(enabled: Boolean): EBSCoreSdkBuilder
    fun cacheSize(bytes: Long): EBSCoreSdkBuilder
    fun enableCompression(enabled: Boolean): EBSCoreSdkBuilder
    fun connectTimeout(millis: Long): EBSCoreSdkBuilder
    fun readTimeout(millis: Long): EBSCoreSdkBuilder
    fun userAgent(agent: String): EBSCoreSdkBuilder
    suspend fun build(): EBSCoreSdk
}
```

## 🔐 Authentication API

### AuthConfig Types

```kotlin
// Basic Authentication
data class BasicAuthConfig(
    val username: String,
    val password: String
) : AuthConfig

// OAuth2 Authentication
data class OAuth2Config(
    val clientId: String,
    val clientSecret: String,
    val redirectUri: String,
    val scopes: List<String> = emptyList()
) : AuthConfig

// Personal Access Token
data class PersonalAccessTokenConfig(
    val token: String
) : AuthConfig
```

## 🏥 System API

### SystemApi

```kotlin
interface SystemApi {
    suspend fun getSystemInfo(): ApiResponse<SystemInfo>
    suspend fun getSystemSettings(): ApiResponse<SystemSettings>
    suspend fun ping(): ApiResponse<String>
}
```

### Models

```kotlin
data class SystemInfo(
    val version: String,
    val revision: String,
    val buildTime: String,
    val serverDate: String,
    val databaseInfo: DatabaseInfo?
)

data class DatabaseInfo(
    val name: String,
    val user: String,
    val url: String
)
```

## 👤 User API

### UserApi

```kotlin
interface UserApi {
    suspend fun getCurrentUser(): ApiResponse<User>
    suspend fun getUsers(
        fields: String? = null,
        filter: String? = null,
        paging: PagingParams? = null
    ): ApiResponse<List<User>>
    
    suspend fun getUserById(id: String, fields: String? = null): ApiResponse<User>
    suspend fun createUser(user: User): ApiResponse<ImportSummary>
    suspend fun updateUser(id: String, user: User): ApiResponse<ImportSummary>
    suspend fun deleteUser(id: String): ApiResponse<ImportSummary>
}
```

## 📊 Metadata API

### MetadataApi

```kotlin
interface MetadataApi {
    // Data Elements
    suspend fun getDataElements(
        fields: String? = null,
        filter: String? = null,
        paging: PagingParams? = null
    ): ApiResponse<List<DataElement>>
    
    // Data Sets
    suspend fun getDataSets(
        fields: String? = null,
        filter: String? = null,
        paging: PagingParams? = null
    ): ApiResponse<List<DataSet>>
    
    // Organization Units
    suspend fun getOrganisationUnits(
        fields: String? = null,
        filter: String? = null,
        paging: PagingParams? = null
    ): ApiResponse<List<OrganisationUnit>>
    
    // Programs
    suspend fun getPrograms(
        fields: String? = null,
        filter: String? = null,
        paging: PagingParams? = null
    ): ApiResponse<List<Program>>
    
    // Categories
    suspend fun getCategories(
        fields: String? = null,
        filter: String? = null,
        paging: PagingParams? = null
    ): ApiResponse<List<Category>>
    
    // Option Sets
    suspend fun getOptionSets(
        fields: String? = null,
        filter: String? = null,
        paging: PagingParams? = null
    ): ApiResponse<List<OptionSet>>
}
```

## 📈 Data API

### DataApi

```kotlin
interface DataApi {
    // Data Values
    suspend fun getDataValueSets(
        dataSet: String? = null,
        dataElementGroup: String? = null,
        period: String? = null,
        startDate: String? = null,
        endDate: String? = null,
        orgUnit: String? = null,
        children: Boolean = false
    ): ApiResponse<DataValueSet>
    
    suspend fun postDataValueSet(
        dataValueSet: DataValueSet,
        dryRun: Boolean = false,
        strategy: ImportStrategy = ImportStrategy.CREATE_AND_UPDATE
    ): ApiResponse<ImportSummary>
    
    // Complete Data Set Registrations
    suspend fun getCompleteDataSetRegistrations(
        dataSet: String,
        period: String,
        orgUnit: String
    ): ApiResponse<List<CompleteDataSetRegistration>>
    
    suspend fun postCompleteDataSetRegistration(
        registration: CompleteDataSetRegistration
    ): ApiResponse<ImportSummary>
}
```

## 📊 Analytics API

### AnalyticsApi

```kotlin
interface AnalyticsApi {
    suspend fun getAnalytics(
        dimension: List<String>,
        filter: List<String> = emptyList(),
        aggregationType: String? = null,
        measureCriteria: String? = null,
        preAggregationMeasureCriteria: String? = null,
        startDate: String? = null,
        endDate: String? = null,
        skipMeta: Boolean = false,
        skipData: Boolean = false,
        skipRounding: Boolean = false,
        completedOnly: Boolean = false,
        hierarchyMeta: Boolean = false,
        ignoreLimit: Boolean = false,
        hideEmptyRows: Boolean = false,
        hideEmptyColumns: Boolean = false,
        showHierarchy: Boolean = false,
        includeNumDen: Boolean = false,
        includeMetadataDetails: Boolean = false,
        displayProperty: String? = null,
        outputIdScheme: String? = null,
        inputIdScheme: String? = null,
        approvalLevel: String? = null,
        relativePeriodDate: String? = null,
        userOrgUnit: String? = null,
        columns: List<String> = emptyList(),
        rows: List<String> = emptyList()
    ): ApiResponse<AnalyticsResponse>
    
    suspend fun getRawData(
        dimension: List<String>,
        filter: List<String> = emptyList(),
        startDate: String? = null,
        endDate: String? = null
    ): ApiResponse<AnalyticsResponse>
}
```

## 🎯 Tracker API

### TrackerApi

```kotlin
interface TrackerApi {
    // Tracked Entity Instances
    suspend fun getTrackedEntityInstances(
        ou: String? = null,
        ouMode: String? = null,
        program: String? = null,
        programStatus: String? = null,
        followUp: Boolean? = null,
        lastUpdatedStartDate: String? = null,
        lastUpdatedEndDate: String? = null,
        programStartDate: String? = null,
        programEndDate: String? = null,
        trackedEntityType: String? = null,
        trackedEntityInstance: String? = null,
        fields: String? = null,
        page: Int? = null,
        pageSize: Int? = null,
        totalPages: Boolean = false,
        skipPaging: Boolean = false,
        includeDeleted: Boolean = false,
        includeAllAttributes: Boolean = false,
        attribute: List<String> = emptyList(),
        filter: List<String> = emptyList(),
        assignedUserMode: String? = null,
        assignedUser: List<String> = emptyList(),
        order: String? = null
    ): ApiResponse<TrackedEntityInstanceResponse>
    
    suspend fun postTrackedEntityInstances(
        trackedEntityInstances: List<TrackedEntityInstance>,
        strategy: ImportStrategy = ImportStrategy.CREATE_AND_UPDATE
    ): ApiResponse<ImportSummary>
    
    // Events
    suspend fun getEvents(
        program: String? = null,
        programStage: String? = null,
        programStatus: String? = null,
        followUp: Boolean? = null,
        trackedEntityInstance: String? = null,
        orgUnit: String? = null,
        ouMode: String? = null,
        startDate: String? = null,
        endDate: String? = null,
        dueDateStart: String? = null,
        dueDateEnd: String? = null,
        lastUpdatedStartDate: String? = null,
        lastUpdatedEndDate: String? = null,
        lastUpdatedDuration: String? = null,
        status: String? = null,
        attributeCc: String? = null,
        attributeCos: String? = null,
        skipMeta: Boolean = false,
        page: Int? = null,
        pageSize: Int? = null,
        totalPages: Boolean = false,
        skipPaging: Boolean = false,
        includeDeleted: Boolean = false,
        dataElementIdScheme: String? = null,
        categoryOptionComboIdScheme: String? = null,
        orgUnitIdScheme: String? = null,
        programIdScheme: String? = null,
        programStageIdScheme: String? = null,
        idScheme: String? = null,
        order: String? = null,
        event: String? = null,
        assignedUserMode: String? = null,
        assignedUser: List<String> = emptyList(),
        filter: List<String> = emptyList(),
        filterAttributes: List<String> = emptyList(),
        fields: String? = null
    ): ApiResponse<EventResponse>
    
    suspend fun postEvents(
        events: List<Event>,
        strategy: ImportStrategy = ImportStrategy.CREATE_AND_UPDATE
    ): ApiResponse<ImportSummary>
    
    // Enrollments
    suspend fun getEnrollments(
        ou: String? = null,
        ouMode: String? = null,
        program: String? = null,
        programStatus: String? = null,
        followUp: Boolean? = null,
        lastUpdatedStartDate: String? = null,
        lastUpdatedEndDate: String? = null,
        programStartDate: String? = null,
        programEndDate: String? = null,
        trackedEntityType: String? = null,
        trackedEntityInstance: String? = null,
        enrollment: String? = null,
        fields: String? = null,
        page: Int? = null,
        pageSize: Int? = null,
        totalPages: Boolean = false,
        skipPaging: Boolean = false,
        includeDeleted: Boolean = false
    ): ApiResponse<EnrollmentResponse>
    
    suspend fun postEnrollments(
        enrollments: List<Enrollment>,
        strategy: ImportStrategy = ImportStrategy.CREATE_AND_UPDATE
    ): ApiResponse<ImportSummary>
}
```

## 📱 Apps API

### AppsApi

```kotlin
interface AppsApi {
    suspend fun getApps(): ApiResponse<List<App>>
    suspend fun getAppById(id: String): ApiResponse<App>
    suspend fun installApp(appBundle: ByteArray): ApiResponse<ImportSummary>
    suspend fun uninstallApp(id: String): ApiResponse<ImportSummary>
    suspend fun reloadApps(): ApiResponse<String>
}
```

## 💬 Messaging API

### MessagingApi

```kotlin
interface MessagingApi {
    suspend fun getMessages(
        fields: String? = null,
        filter: String? = null,
        paging: PagingParams? = null
    ): ApiResponse<List<Message>>
    
    suspend fun sendMessage(
        subject: String,
        text: String,
        users: List<String> = emptyList(),
        userGroups: List<String> = emptyList(),
        organisationUnits: List<String> = emptyList(),
        messageType: String = "PRIVATE"
    ): ApiResponse<ImportSummary>
    
    suspend fun getConversations(
        fields: String? = null,
        filter: String? = null,
        paging: PagingParams? = null
    ): ApiResponse<List<MessageConversation>>
    
    suspend fun markConversationRead(id: String): ApiResponse<String>
    suspend fun markConversationUnread(id: String): ApiResponse<String>
}
```

## 🔄 Common Types

### ApiResponse

```kotlin
sealed class ApiResponse<out T> {
    data class Success<T>(val data: T) : ApiResponse<T>()
    data class Error(val error: ApiError) : ApiResponse<Nothing>()
    
    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
}
```

### ApiError

```kotlin
sealed class ApiError(val message: String) {
    class NetworkError(message: String) : ApiError(message)
    class AuthenticationError(message: String) : ApiError(message)
    class ValidationError(message: String, val violations: List<String>) : ApiError(message)
    class ServerError(val httpCode: Int, message: String) : ApiError(message)
    class UnknownError(message: String) : ApiError(message)
    
    fun isRetryable(): Boolean = when (this) {
        is NetworkError -> true
        is ServerError -> httpCode >= 500
        else -> false
    }
}
```

### PagingParams

```kotlin
data class PagingParams(
    val page: Int = 1,
    val pageSize: Int = 50,
    val totalPages: Boolean = false,
    val skipPaging: Boolean = false
)
```

### ImportStrategy

```kotlin
enum class ImportStrategy {
    CREATE,
    UPDATE,
    CREATE_AND_UPDATE,
    DELETE
}
```

### ImportSummary

```kotlin
data class ImportSummary(
    val status: String,
    val importCount: ImportCount,
    val conflicts: List<ImportConflict> = emptyList(),
    val reference: String? = null
)

data class ImportCount(
    val imported: Int = 0,
    val updated: Int = 0,
    val ignored: Int = 0,
    val deleted: Int = 0
)
```

## 🎯 Usage Examples

### Basic API Usage

```kotlin
// Get system information
val systemInfo = sdk.systemApi.getSystemInfo()
if (systemInfo.isSuccess) {
    println("DHIS2 Version: ${systemInfo.data!!.version}")
}

// Get data elements with filtering
val dataElements = sdk.metadataApi.getDataElements(
    fields = "id,name,valueType",
    filter = "domainType:eq:AGGREGATE"
)

// Submit data values
val dataValueSet = DataValueSet(
    dataSet = "BfMAe6Itzgt",
    period = "202401",
    orgUnit = "DiszpKrYNg8",
    dataValues = listOf(
        DataValue(
            dataElement = "FTRrcoaog83",
            value = "100"
        )
    )
)

val result = sdk.dataApi.postDataValueSet(dataValueSet)
```

### Error Handling

```kotlin
val result = sdk.metadataApi.getDataElements()

when {
    result.isSuccess -> {
        val elements = result.data!!
        // Process data elements
    }
    result.isError -> {
        when (val error = result.error!!) {
            is NetworkError -> {
                // Handle network issues
                println("Network error: ${error.message}")
            }
            is AuthenticationError -> {
                // Re-authenticate
                sdk.authenticate(authConfig)
            }
            is ValidationError -> {
                // Handle validation errors
                error.violations.forEach { violation ->
                    println("Validation error: $violation")
                }
            }
            is ServerError -> {
                // Handle server errors
                println("Server error ${error.httpCode}: ${error.message}")
            }
        }
    }
}
```

---

For more examples and detailed usage, see [Getting Started](GETTING_STARTED.md) and [Examples](EXAMPLES.md).
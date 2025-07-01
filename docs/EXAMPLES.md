# Examples

Comprehensive examples for using the EBSCore SDK.

## 🚀 Quick Examples

### Basic Setup and Authentication

```kotlin
import com.everybytesystems.ebscore.sdk.EBSCoreSdkBuilder
import com.everybytesystems.ebscore.auth.BasicAuthConfig

suspend fun basicExample() {
    val sdk = EBSCoreSdkBuilder()
        .baseUrl("https://play.dhis2.org/2.42.0")
        .build()
    
    sdk.initialize()
    
    val authResult = sdk.authenticate(
        BasicAuthConfig("admin", "district")
    )
    
    if (authResult.isSuccess) {
        val systemInfo = sdk.systemApi.getSystemInfo()
        println("Connected to DHIS2 ${systemInfo.data?.version}")
    }
    
    sdk.close()
}
```

### Working with Metadata

```kotlin
suspend fun metadataExample(sdk: EBSCoreSdk) {
    // Get all data elements
    val dataElements = sdk.metadataApi.getDataElements(
        fields = "id,name,valueType,domainType"
    )
    
    // Filter aggregate data elements
    val aggregateElements = sdk.metadataApi.getDataElements(
        fields = "id,name,valueType",
        filter = "domainType:eq:AGGREGATE"
    )
    
    // Get organization units hierarchy
    val orgUnits = sdk.metadataApi.getOrganisationUnits(
        fields = "id,name,level,parent[id,name]",
        filter = "level:le:3"
    )
    
    // Get programs with tracked entity types
    val programs = sdk.metadataApi.getPrograms(
        fields = "id,name,programType,trackedEntityType[id,name]"
    )
}
```

### Data Operations

```kotlin
suspend fun dataExample(sdk: EBSCoreSdk) {
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
            ),
            DataValue(
                dataElement = "Jtf34kNZhzP",
                value = "200",
                categoryOptionCombo = "HllvX50cXC0"
            )
        )
    )
    
    val result = sdk.dataApi.postDataValueSet(dataValueSet)
    
    when {
        result.isSuccess -> {
            val summary = result.data!!
            println("Imported: ${summary.importCount.imported}")
            println("Updated: ${summary.importCount.updated}")
        }
        result.isError -> {
            println("Error: ${result.error}")
        }
    }
}
```

### Analytics

```kotlin
suspend fun analyticsExample(sdk: EBSCoreSdk) {
    // Get analytics data
    val analytics = sdk.analyticsApi.getAnalytics(
        dimension = listOf(
            "dx:FTRrcoaog83;Jtf34kNZhzP", // Data elements
            "pe:LAST_12_MONTHS",           // Periods
            "ou:USER_ORGUNIT"              // Org units
        ),
        displayProperty = "NAME"
    )
    
    if (analytics.isSuccess) {
        val response = analytics.data!!
        
        // Process headers
        response.headers.forEach { header ->
            println("Dimension: ${header.name} (${header.column})")
        }
        
        // Process data rows
        response.rows.forEach { row ->
            println("Data: ${row.joinToString(", ")}")
        }
    }
}
```

### Tracker Operations

```kotlin
suspend fun trackerExample(sdk: EBSCoreSdk) {
    // Get tracked entity instances
    val trackedEntities = sdk.trackerApi.getTrackedEntityInstances(
        ou = "DiszpKrYNg8",
        program = "IpHINAT79UW",
        fields = "trackedEntityInstance,attributes,enrollments[enrollment,events]"
    )
    
    // Create new tracked entity
    val newEntity = TrackedEntityInstance(
        trackedEntityType = "nEenWmSyUEp",
        orgUnit = "DiszpKrYNg8",
        attributes = listOf(
            Attribute(
                attribute = "w75KJ2mc4zz", // First name
                value = "John"
            ),
            Attribute(
                attribute = "zDhUuAYrxNC", // Last name
                value = "Doe"
            )
        )
    )
    
    val createResult = sdk.trackerApi.postTrackedEntityInstances(
        listOf(newEntity)
    )
    
    // Create enrollment
    if (createResult.isSuccess) {
        val teiId = createResult.data!!.reference
        
        val enrollment = Enrollment(
            trackedEntityInstance = teiId,
            program = "IpHINAT79UW",
            orgUnit = "DiszpKrYNg8",
            enrollmentDate = "2024-01-15",
            incidentDate = "2024-01-15"
        )
        
        sdk.trackerApi.postEnrollments(listOf(enrollment))
    }
}
```

## 🔄 Advanced Examples

### Error Handling and Retry Logic

```kotlin
suspend fun robustApiCall(sdk: EBSCoreSdk) {
    val maxRetries = 3
    var attempt = 0
    
    while (attempt < maxRetries) {
        val result = sdk.metadataApi.getDataElements()
        
        when {
            result.isSuccess -> {
                println("Success: ${result.data!!.size} data elements")
                return
            }
            
            result.isError -> {
                val error = result.error!!
                
                when {
                    error.isRetryable() && attempt < maxRetries - 1 -> {
                        attempt++
                        println("Retrying... (attempt $attempt)")
                        delay(1000 * attempt) // Exponential backoff
                    }
                    
                    error is AuthenticationError -> {
                        println("Re-authenticating...")
                        sdk.authenticate(BasicAuthConfig("admin", "district"))
                        attempt++
                    }
                    
                    else -> {
                        println("Failed: ${error.message}")
                        return
                    }
                }
            }
        }
    }
}
```

### Batch Operations

```kotlin
suspend fun batchOperations(sdk: EBSCoreSdk) {
    // Prepare multiple data value sets
    val dataValueSets = listOf(
        DataValueSet(
            dataSet = "BfMAe6Itzgt",
            period = "202401",
            orgUnit = "DiszpKrYNg8",
            dataValues = listOf(
                DataValue("FTRrcoaog83", "100"),
                DataValue("Jtf34kNZhzP", "200")
            )
        ),
        DataValueSet(
            dataSet = "BfMAe6Itzgt",
            period = "202402",
            orgUnit = "DiszpKrYNg8",
            dataValues = listOf(
                DataValue("FTRrcoaog83", "150"),
                DataValue("Jtf34kNZhzP", "250")
            )
        )
    )
    
    // Submit in batch
    val results = dataValueSets.map { dataValueSet ->
        sdk.dataApi.postDataValueSet(dataValueSet)
    }
    
    // Process results
    results.forEachIndexed { index, result ->
        when {
            result.isSuccess -> {
                println("Batch $index: Success")
            }
            result.isError -> {
                println("Batch $index: Error - ${result.error}")
            }
        }
    }
}
```

### Caching Strategy

```kotlin
suspend fun cachingExample() {
    val sdk = EBSCoreSdkBuilder()
        .baseUrl("https://play.dhis2.org/2.42.0")
        .enableCaching(true)
        .cacheSize(50 * 1024 * 1024) // 50MB
        .build()
    
    // First call - fetches from server
    val dataElements1 = sdk.metadataApi.getDataElements()
    
    // Second call - uses cache (much faster)
    val dataElements2 = sdk.metadataApi.getDataElements()
    
    // Force refresh from server
    val dataElements3 = sdk.metadataApi.getDataElements(
        useCache = false
    )
}
```

## 📱 Platform-Specific Examples

### Android Example

```kotlin
class MainActivity : ComponentActivity() {
    private lateinit var sdk: EBSCoreSdk
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        lifecycleScope.launch {
            initializeSDK()
        }
    }
    
    private suspend fun initializeSDK() {
        sdk = EBSCoreSdkBuilder()
            .baseUrl("https://your-dhis2-instance.org")
            .enableCaching(true)
            .build()
        
        sdk.initialize()
        
        // Authenticate
        val authResult = sdk.authenticate(
            BasicAuthConfig("username", "password")
        )
        
        if (authResult.isSuccess) {
            loadData()
        }
    }
    
    private suspend fun loadData() {
        val dataElements = sdk.metadataApi.getDataElements()
        
        // Update UI on main thread
        withContext(Dispatchers.Main) {
            if (dataElements.isSuccess) {
                // Update RecyclerView or Compose UI
                updateUI(dataElements.data!!)
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        lifecycleScope.launch {
            sdk.close()
        }
    }
}
```

### Desktop Example

```kotlin
fun main() = application {
    var dataElements by remember { mutableStateOf<List<DataElement>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        isLoading = true
        
        val sdk = EBSCoreSdkBuilder()
            .baseUrl("https://play.dhis2.org/2.42.0")
            .build()
        
        sdk.initialize()
        sdk.authenticate(BasicAuthConfig("admin", "district"))
        
        val result = sdk.metadataApi.getDataElements()
        if (result.isSuccess) {
            dataElements = result.data!!
        }
        
        isLoading = false
        sdk.close()
    }
    
    Window(onCloseRequest = ::exitApplication, title = "DHIS2 Desktop App") {
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn {
                items(dataElements) { element ->
                    Card(modifier = Modifier.padding(8.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(element.name, style = MaterialTheme.typography.h6)
                            Text("Type: ${element.valueType}")
                        }
                    }
                }
            }
        }
    }
}
```

## 🧪 Testing Examples

### Unit Testing

```kotlin
class DataElementApiTest {
    
    @Test
    fun `should fetch data elements successfully`() = runTest {
        // Given
        val mockClient = MockHttpClient()
        val api = DataElementApi(mockClient)
        
        mockClient.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("""
                    {
                        "dataElements": [
                            {
                                "id": "FTRrcoaog83",
                                "name": "Accute Flaccid Paralysis (AFP) cases",
                                "valueType": "INTEGER"
                            }
                        ]
                    }
                """.trimIndent())
        )
        
        // When
        val result = api.getDataElements()
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals(1, result.data?.size)
        assertEquals("FTRrcoaog83", result.data?.first()?.id)
    }
}
```

### Integration Testing

```kotlin
class SDKIntegrationTest {
    
    @Test
    fun `should authenticate and fetch system info`() = runTest {
        val sdk = EBSCoreSdkBuilder()
            .baseUrl("https://play.dhis2.org/2.42.0")
            .build()
        
        try {
            sdk.initialize()
            
            val authResult = sdk.authenticate(
                BasicAuthConfig("admin", "district")
            )
            
            assertTrue(authResult.isSuccess)
            
            val systemInfo = sdk.systemApi.getSystemInfo()
            assertTrue(systemInfo.isSuccess)
            assertNotNull(systemInfo.data?.version)
            
        } finally {
            sdk.close()
        }
    }
}
```

---

For more detailed documentation, see:
- [Getting Started](GETTING_STARTED.md)
- [API Reference](API_REFERENCE.md)

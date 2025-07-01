# 🖥️ Desktop Examples - EBSCore SDK

Complete Desktop examples demonstrating cross-platform desktop development with EBSCore SDK using Compose Desktop, targeting Windows, macOS, and Linux platforms.

## 🎯 Overview

These examples show how to build production-ready desktop applications using EBSCore SDK with:
- **Compose Desktop** for cross-platform UI development
- **Material Design 3** adapted for desktop interfaces
- **MVVM Architecture** with ViewModels and StateFlow
- **SQLDelight** for local database management
- **File System Integration** for data import/export
- **Multi-window Support** for complex workflows
- **System Integration** with OS-specific features

## 🖥️ Setup

### Dependencies (build.gradle.kts)

```kotlin
plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("app.cash.sqldelight")
}

kotlin {
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()
    }
    
    sourceSets {
        val desktopMain by getting {
            dependencies {
                // EBSCore SDK
                implementation("com.everybytesystems.ebscore:ebscore-sdk:1.0.0")
                
                // Compose Desktop
                implementation(compose.desktop.currentOs)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                
                // Coroutines
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.7.3")
                
                // SQLDelight
                implementation("app.cash.sqldelight:sqlite-driver:2.0.1")
                implementation("app.cash.sqldelight:coroutines-extensions:2.0.1")
                
                // Networking
                implementation("io.ktor:ktor-client-cio:2.3.7")
                implementation("io.ktor:ktor-client-content-negotiation:2.3.7")
                implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.7")
                
                // File operations
                implementation("org.apache.poi:poi:5.2.4")
                implementation("org.apache.poi:poi-ooxml:5.2.4")
                
                // Charts and visualization
                implementation("org.jetbrains.lets-plot:lets-plot-kotlin-jvm:4.4.1")
                implementation("org.jetbrains.lets-plot:lets-plot-image-export:4.1.0")
                
                // System integration
                implementation("net.java.dev.jna:jna:5.13.0")
                implementation("net.java.dev.jna:jna-platform:5.13.0")
            }
        }
        
        val desktopTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        
        nativeDistributions {
            targetFormats(
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb
            )
            
            packageName = "EBSCore Desktop"
            packageVersion = "1.0.0"
            description = "EBSCore SDK Desktop Application"
            copyright = "© 2024 EveryByte Systems. All rights reserved."
            vendor = "EveryByte Systems"
            
            windows {
                iconFile.set(project.file("src/desktopMain/resources/icon.ico"))
                menuGroup = "EBSCore"
                upgradeUuid = "12345678-1234-1234-1234-123456789012"
            }
            
            macOS {
                iconFile.set(project.file("src/desktopMain/resources/icon.icns"))
                bundleID = "com.everybytesystems.ebscore.desktop"
                appCategory = "public.app-category.healthcare-fitness"
            }
            
            linux {
                iconFile.set(project.file("src/desktopMain/resources/icon.png"))
                debMaintainer = "support@everybytesystems.com"
                menuGroup = "Office"
                appCategory = "Office"
            }
        }
    }
}

sqldelight {
    databases {
        create("EBSCoreDatabase") {
            packageName.set("com.everybytesystems.ebscore.desktop.database")
            srcDirs.setFrom("src/desktopMain/sqldelight")
        }
    }
}
```

## 🏗️ Architecture Components

### 1. Main Application Entry Point

```kotlin
// Main.kt
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import com.everybytesystems.ebscore.sdk.EBSCoreSdk
import kotlinx.coroutines.runBlocking
import java.awt.Dimension

fun main() = application {
    // Initialize SDK
    val sdk = remember {
        EBSCoreSdk.Builder()
            .baseUrl("https://play.dhis2.org/2.40.1")
            .enableOfflineMode(true)
            .enableAnalytics(true)
            .build()
    }
    
    // Application state
    var isAuthenticated by remember { mutableStateOf(false) }
    var currentUser by remember { mutableStateOf<User?>(null) }
    
    // Main window
    Window(
        onCloseRequest = ::exitApplication,
        title = "EBSCore Desktop",
        state = WindowState(
            position = WindowPosition(Alignment.Center),
            size = androidx.compose.ui.unit.DpSize(1200.dp, 800.dp)
        )
    ) {
        // Set minimum window size
        window.minimumSize = Dimension(800, 600)
        
        EBSCoreDesktopTheme {
            if (isAuthenticated) {
                MainDesktopApp(
                    sdk = sdk,
                    currentUser = currentUser,
                    onLogout = { 
                        isAuthenticated = false
                        currentUser = null
                    }
                )
            } else {
                LoginScreen(
                    sdk = sdk,
                    onLoginSuccess = { user ->
                        isAuthenticated = true
                        currentUser = user
                    }
                )
            }
        }
    }
}

// DesktopTheme.kt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1976D2),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE3F2FD),
    onPrimaryContainer = Color(0xFF0D47A1),
    secondary = Color(0xFF03DAC6),
    onSecondary = Color.Black,
    tertiary = Color(0xFF018786),
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF90CAF9),
    onPrimary = Color(0xFF0D47A1),
    primaryContainer = Color(0xFF1565C0),
    onPrimaryContainer = Color(0xFFE3F2FD),
    secondary = Color(0xFF03DAC6),
    onSecondary = Color.Black,
    tertiary = Color(0xFF018786),
    background = Color(0xFF1C1B1F),
    surface = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E5),
    onSurface = Color(0xFFE6E1E5),
)

@Composable
fun EBSCoreDesktopTheme(
    darkTheme: Boolean = false, // Desktop apps often default to light theme
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
```

### 2. SQLDelight Database Setup

```sql
-- src/desktopMain/sqldelight/com/everybytesystems/ebscore/desktop/database/Patient.sq

CREATE TABLE PatientEntity (
    id TEXT NOT NULL PRIMARY KEY,
    firstName TEXT NOT NULL,
    lastName TEXT NOT NULL,
    dateOfBirth TEXT NOT NULL,
    gender TEXT NOT NULL,
    phoneNumber TEXT,
    address TEXT,
    facilityId TEXT NOT NULL,
    lastUpdated INTEGER NOT NULL,
    syncStatus TEXT NOT NULL DEFAULT 'PENDING'
);

-- Queries
selectAll:
SELECT * FROM PatientEntity ORDER BY lastName, firstName;

selectByFacility:
SELECT * FROM PatientEntity 
WHERE facilityId = ? 
ORDER BY lastName, firstName;

selectById:
SELECT * FROM PatientEntity WHERE id = ?;

searchPatients:
SELECT * FROM PatientEntity 
WHERE facilityId = ? 
AND (firstName LIKE '%' || ? || '%' OR lastName LIKE '%' || ? || '%')
ORDER BY lastName, firstName;

selectBySyncStatus:
SELECT * FROM PatientEntity WHERE syncStatus = ?;

insertPatient:
INSERT OR REPLACE INTO PatientEntity(
    id, firstName, lastName, dateOfBirth, gender, 
    phoneNumber, address, facilityId, lastUpdated, syncStatus
) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);

updateSyncStatus:
UPDATE PatientEntity SET syncStatus = ? WHERE id = ?;

deletePatient:
DELETE FROM PatientEntity WHERE id = ?;

deleteByFacility:
DELETE FROM PatientEntity WHERE facilityId = ?;
```

```kotlin
// database/DatabaseManager.kt
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.everybytesystems.ebscore.desktop.database.EBSCoreDatabase
import java.io.File

class DatabaseManager {
    companion object {
        private var instance: EBSCoreDatabase? = null
        
        fun getInstance(): EBSCoreDatabase {
            return instance ?: synchronized(this) {
                instance ?: createDatabase().also { instance = it }
            }
        }
        
        private fun createDatabase(): EBSCoreDatabase {
            val databasePath = getDatabasePath()
            val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:$databasePath")
            
            // Create tables if they don't exist
            EBSCoreDatabase.Schema.create(driver)
            
            return EBSCoreDatabase(driver)
        }
        
        private fun getDatabasePath(): String {
            val userHome = System.getProperty("user.home")
            val appDir = File(userHome, ".ebscore")
            
            if (!appDir.exists()) {
                appDir.mkdirs()
            }
            
            return File(appDir, "ebscore.db").absolutePath
        }
    }
}
```

### 3. Repository Implementation

```kotlin
// repository/PatientRepository.kt
import com.everybytesystems.ebscore.desktop.database.EBSCoreDatabase
import com.everybytesystems.ebscore.desktop.database.PatientEntity
import com.everybytesystems.ebscore.sdk.EBSCoreSdk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface PatientRepository {
    suspend fun getPatientsByFacility(facilityId: String): List<Patient>
    suspend fun getPatientById(patientId: String): Patient?
    suspend fun savePatient(patient: Patient): Result<Patient>
    suspend fun deletePatient(patientId: String): Result<Unit>
    suspend fun searchPatients(facilityId: String, query: String): List<Patient>
    suspend fun syncPatients(facilityId: String): Result<Int>
    fun observePatients(facilityId: String): Flow<List<Patient>>
}

class PatientRepositoryImpl(
    private val database: EBSCoreDatabase,
    private val sdk: EBSCoreSdk
) : PatientRepository {
    
    private val patientQueries = database.patientQueries
    
    override suspend fun getPatientsByFacility(facilityId: String): List<Patient> {
        return withContext(Dispatchers.IO) {
            patientQueries.selectByFacility(facilityId)
                .executeAsList()
                .map { it.toPatient() }
        }
    }
    
    override suspend fun getPatientById(patientId: String): Patient? {
        return withContext(Dispatchers.IO) {
            patientQueries.selectById(patientId)
                .executeAsOneOrNull()
                ?.toPatient()
        }
    }
    
    override suspend fun savePatient(patient: Patient): Result<Patient> {
        return withContext(Dispatchers.IO) {
            try {
                // Save locally first
                val entity = patient.toEntity()
                patientQueries.insertPatient(
                    id = entity.id,
                    firstName = entity.firstName,
                    lastName = entity.lastName,
                    dateOfBirth = entity.dateOfBirth,
                    gender = entity.gender,
                    phoneNumber = entity.phoneNumber,
                    address = entity.address,
                    facilityId = entity.facilityId,
                    lastUpdated = entity.lastUpdated,
                    syncStatus = entity.syncStatus
                )
                
                // Try to sync to server
                try {
                    val trackedEntity = patient.toTrackedEntity()
                    val result = sdk.createTrackedEntity(trackedEntity)
                    
                    if (result.isSuccess) {
                        // Update with server ID and mark as synced
                        val syncedPatient = patient.copy(
                            id = result.uid,
                            syncStatus = SyncStatus.SYNCED
                        )
                        
                        val syncedEntity = syncedPatient.toEntity()
                        patientQueries.insertPatient(
                            id = syncedEntity.id,
                            firstName = syncedEntity.firstName,
                            lastName = syncedEntity.lastName,
                            dateOfBirth = syncedEntity.dateOfBirth,
                            gender = syncedEntity.gender,
                            phoneNumber = syncedEntity.phoneNumber,
                            address = syncedEntity.address,
                            facilityId = syncedEntity.facilityId,
                            lastUpdated = syncedEntity.lastUpdated,
                            syncStatus = syncedEntity.syncStatus
                        )
                        
                        Result.success(syncedPatient)
                    } else {
                        Result.success(patient)
                    }
                } catch (e: Exception) {
                    // Keep local copy even if sync fails
                    Result.success(patient)
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    override suspend fun deletePatient(patientId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val patient = patientQueries.selectById(patientId).executeAsOneOrNull()
                if (patient != null) {
                    patientQueries.deletePatient(patientId)
                    
                    // Try to delete from server if synced
                    if (patient.syncStatus == SyncStatus.SYNCED.name) {
                        try {
                            sdk.deleteTrackedEntity(patientId)
                        } catch (e: Exception) {
                            // Log error but don't fail the operation
                            println("Failed to delete from server: ${e.message}")
                        }
                    }
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    override suspend fun searchPatients(facilityId: String, query: String): List<Patient> {
        return withContext(Dispatchers.IO) {
            patientQueries.searchPatients(facilityId, query, query)
                .executeAsList()
                .map { it.toPatient() }
        }
    }
    
    override suspend fun syncPatients(facilityId: String): Result<Int> {
        return withContext(Dispatchers.IO) {
            try {
                // Sync pending local changes to server
                val pendingPatients = patientQueries.selectBySyncStatus(SyncStatus.PENDING.name)
                    .executeAsList()
                    .map { it.toPatient() }
                
                var syncedCount = 0
                
                pendingPatients.forEach { patient ->
                    try {
                        val trackedEntity = patient.toTrackedEntity()
                        val result = sdk.createTrackedEntity(trackedEntity)
                        
                        if (result.isSuccess) {
                            patientQueries.updateSyncStatus(SyncStatus.SYNCED.name, result.uid)
                            syncedCount++
                        } else {
                            patientQueries.updateSyncStatus(SyncStatus.FAILED.name, patient.id)
                        }
                    } catch (e: Exception) {
                        patientQueries.updateSyncStatus(SyncStatus.FAILED.name, patient.id)
                    }
                }
                
                // Fetch latest data from server
                try {
                    val serverPatients = sdk.getTrackedEntities("patient_program", facilityId)
                    serverPatients.forEach { trackedEntity ->
                        val patient = trackedEntity.toPatient(facilityId)
                        val entity = patient.toEntity()
                        
                        patientQueries.insertPatient(
                            id = entity.id,
                            firstName = entity.firstName,
                            lastName = entity.lastName,
                            dateOfBirth = entity.dateOfBirth,
                            gender = entity.gender,
                            phoneNumber = entity.phoneNumber,
                            address = entity.address,
                            facilityId = entity.facilityId,
                            lastUpdated = entity.lastUpdated,
                            syncStatus = entity.syncStatus
                        )
                    }
                } catch (e: Exception) {
                    // Continue with local data if server fetch fails
                    println("Failed to fetch from server: ${e.message}")
                }
                
                Result.success(syncedCount)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    override fun observePatients(facilityId: String): Flow<List<Patient>> = flow {
        while (true) {
            val patients = getPatientsByFacility(facilityId)
            emit(patients)
            kotlinx.coroutines.delay(5000) // Refresh every 5 seconds
        }
    }
}

// Extension functions for mapping
fun PatientEntity.toPatient(): Patient {
    return Patient(
        id = id,
        firstName = firstName,
        lastName = lastName,
        dateOfBirth = dateOfBirth,
        gender = Gender.valueOf(gender.uppercase()),
        phoneNumber = phoneNumber,
        address = address,
        facilityId = facilityId,
        lastUpdated = lastUpdated,
        syncStatus = SyncStatus.valueOf(syncStatus.uppercase())
    )
}

fun Patient.toEntity(): PatientEntity {
    return PatientEntity(
        id = id,
        firstName = firstName,
        lastName = lastName,
        dateOfBirth = dateOfBirth,
        gender = gender.name,
        phoneNumber = phoneNumber,
        address = address,
        facilityId = facilityId,
        lastUpdated = lastUpdated,
        syncStatus = syncStatus.name
    )
}
```

### 4. ViewModels for Desktop

```kotlin
// viewmodel/PatientListViewModel.kt
import androidx.compose.runtime.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class PatientListViewModel(
    private val repository: PatientRepository,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
) {
    private val _uiState = MutableStateFlow(PatientListUiState())
    val uiState: StateFlow<PatientListUiState> = _uiState.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private var currentFacilityId: String? = null
    
    init {
        // Setup search debouncing
        searchQuery
            .debounce(300)
            .distinctUntilChanged()
            .onEach { query ->
                currentFacilityId?.let { facilityId ->
                    if (query.isBlank()) {
                        loadPatients(facilityId)
                    } else {
                        searchPatients(facilityId, query)
                    }
                }
            }
            .launchIn(scope)
    }
    
    fun loadPatients(facilityId: String) {
        currentFacilityId = facilityId
        _uiState.update { it.copy(isLoading = true, error = null) }
        
        scope.launch {
            try {
                val patients = repository.getPatientsByFacility(facilityId)
                _uiState.update { 
                    it.copy(
                        patients = patients,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }
    
    fun searchPatients(query: String) {
        _searchQuery.value = query
    }
    
    private fun searchPatients(facilityId: String, query: String) {
        scope.launch {
            try {
                val patients = repository.searchPatients(facilityId, query)
                _uiState.update { 
                    it.copy(
                        patients = patients,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }
    
    fun refreshPatients() {
        currentFacilityId?.let { facilityId ->
            _uiState.update { it.copy(isRefreshing = true) }
            
            scope.launch {
                try {
                    val syncedCount = repository.syncPatients(facilityId).getOrThrow()
                    loadPatients(facilityId)
                    _uiState.update { 
                        it.copy(
                            isRefreshing = false,
                            syncMessage = "Synced $syncedCount patients"
                        )
                    }
                } catch (e: Exception) {
                    _uiState.update { 
                        it.copy(
                            isRefreshing = false,
                            error = e.message
                        )
                    }
                }
            }
        }
    }
    
    fun deletePatient(patient: Patient) {
        scope.launch {
            try {
                repository.deletePatient(patient.id).getOrThrow()
                currentFacilityId?.let { loadPatients(it) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    fun clearSyncMessage() {
        _uiState.update { it.copy(syncMessage = null) }
    }
    
    fun dispose() {
        scope.cancel()
    }
}

data class PatientListUiState(
    val patients: List<Patient> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val syncMessage: String? = null
)
```

### 5. Desktop UI Components

```kotlin
// ui/MainDesktopApp.kt
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDesktopApp(
    sdk: EBSCoreSdk,
    currentUser: User?,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(DesktopTab.PATIENTS) }
    var selectedFacility by remember { mutableStateOf<HealthFacility?>(null) }
    
    Row(modifier = Modifier.fillMaxSize()) {
        // Navigation Rail
        NavigationRail(
            modifier = Modifier.fillMaxHeight()
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            DesktopTab.values().forEach { tab ->
                NavigationRailItem(
                    icon = { Icon(tab.icon, contentDescription = tab.title) },
                    label = { Text(tab.title) },
                    selected = selectedTab == tab,
                    onClick = { selectedTab = tab }
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // User menu
            NavigationRailItem(
                icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                label = { Text("Profile") },
                selected = false,
                onClick = { /* Show profile menu */ }
            )
            
            NavigationRailItem(
                icon = { Icon(Icons.Default.ExitToApp, contentDescription = "Logout") },
                label = { Text("Logout") },
                selected = false,
                onClick = onLogout
            )
        }
        
        // Main content area
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Top bar with facility selector
            TopAppBar(
                title = { 
                    Text("EBSCore Desktop - ${selectedTab.title}")
                },
                actions = {
                    // Facility selector
                    selectedFacility?.let { facility ->
                        AssistChip(
                            onClick = { /* Show facility selector */ },
                            label = { Text(facility.name) },
                            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) }
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Sync button
                    IconButton(onClick = { /* Trigger sync */ }) {
                        Icon(Icons.Default.Sync, contentDescription = "Sync")
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Tab content
            when (selectedTab) {
                DesktopTab.PATIENTS -> {
                    selectedFacility?.let { facility ->
                        PatientListScreen(
                            facilityId = facility.id,
                            sdk = sdk
                        )
                    } ?: FacilitySelectionPrompt(
                        onFacilitySelected = { selectedFacility = it }
                    )
                }
                DesktopTab.ANALYTICS -> {
                    AnalyticsScreen(sdk = sdk)
                }
                DesktopTab.REPORTS -> {
                    ReportsScreen(sdk = sdk)
                }
                DesktopTab.SYNC -> {
                    SyncScreen(sdk = sdk)
                }
                DesktopTab.SETTINGS -> {
                    SettingsScreen()
                }
            }
        }
    }
}

enum class DesktopTab(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    PATIENTS("Patients", Icons.Default.People),
    ANALYTICS("Analytics", Icons.Default.Analytics),
    REPORTS("Reports", Icons.Default.Assessment),
    SYNC("Sync", Icons.Default.Sync),
    SETTINGS("Settings", Icons.Default.Settings)
}

@Composable
fun FacilitySelectionPrompt(
    onFacilitySelected: (HealthFacility) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Select a Health Facility",
                style = MaterialTheme.typography.headlineSmall
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Choose a health facility to view and manage patient data.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = { 
                    // Show facility selection dialog
                    val demoFacility = HealthFacility(
                        id = "demo_facility",
                        name = "Demo Health Center",
                        code = "DHC001",
                        level = 3,
                        coordinates = null,
                        parentId = null,
                        lastUpdated = System.currentTimeMillis()
                    )
                    onFacilitySelected(demoFacility)
                }
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Select Facility")
            }
        }
    }
}
```

### 6. Patient Management Screen

```kotlin
// ui/PatientListScreen.kt
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientListScreen(
    facilityId: String,
    sdk: EBSCoreSdk
) {
    val repository = remember { PatientRepositoryImpl(DatabaseManager.getInstance(), sdk) }
    val viewModel = remember { PatientListViewModel(repository) }
    
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    
    var showAddPatientDialog by remember { mutableStateOf(false) }
    var selectedPatient by remember { mutableStateOf<Patient?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf<Patient?>(null) }
    
    LaunchedEffect(facilityId) {
        viewModel.loadPatients(facilityId)
    }
    
    // Show sync message
    uiState.syncMessage?.let { message ->
        LaunchedEffect(message) {
            // In a real app, you might show a snackbar
            kotlinx.coroutines.delay(3000)
            viewModel.clearSyncMessage()
        }
    }
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Search and actions bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::searchPatients,
                placeholder = { Text("Search patients...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.weight(1f)
            )
            
            Button(
                onClick = { showAddPatientDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Patient")
            }
            
            Button(
                onClick = viewModel::refreshPatients,
                enabled = !uiState.isRefreshing
            ) {
                if (uiState.isRefreshing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sync")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Content
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Loading patients...")
                    }
                }
            }
            
            uiState.error != null -> {
                ErrorCard(
                    message = uiState.error,
                    onRetry = { viewModel.loadPatients(facilityId) },
                    onDismiss = viewModel::clearError
                )
            }
            
            uiState.patients.isEmpty() -> {
                EmptyStateCard(
                    message = if (searchQuery.isBlank()) {
                        "No patients found"
                    } else {
                        "No patients match your search"
                    },
                    actionText = "Add Patient",
                    onAction = { showAddPatientDialog = true }
                )
            }
            
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = uiState.patients,
                        key = { it.id }
                    ) { patient ->
                        PatientCard(
                            patient = patient,
                            onClick = { selectedPatient = patient },
                            onDelete = { showDeleteConfirmation = patient }
                        )
                    }
                }
            }
        }
    }
    
    // Dialogs
    if (showAddPatientDialog) {
        AddPatientDialog(
            facilityId = facilityId,
            onDismiss = { showAddPatientDialog = false },
            onPatientAdded = {
                showAddPatientDialog = false
                viewModel.loadPatients(facilityId)
            }
        )
    }
    
    selectedPatient?.let { patient ->
        PatientDetailDialog(
            patient = patient,
            onDismiss = { selectedPatient = null },
            onEdit = { /* Navigate to edit */ },
            onDelete = { 
                selectedPatient = null
                showDeleteConfirmation = patient
            }
        )
    }
    
    showDeleteConfirmation?.let { patient ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = null },
            title = { Text("Delete Patient") },
            text = { 
                Text("Are you sure you want to delete ${patient.fullName}? This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deletePatient(patient)
                        showDeleteConfirmation = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirmation = null }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientCard(
    patient: Patient,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = patient.fullName,
                    style = MaterialTheme.typography.titleMedium
                )
                
                Text(
                    text = "DOB: ${patient.dateOfBirth}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                patient.phoneNumber?.let { phone ->
                    Text(
                        text = "Phone: $phone",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sync status indicator
                Icon(
                    imageVector = when (patient.syncStatus) {
                        SyncStatus.SYNCED -> Icons.Default.CheckCircle
                        SyncStatus.PENDING -> Icons.Default.Schedule
                        SyncStatus.FAILED -> Icons.Default.Error
                    },
                    contentDescription = patient.syncStatus.name,
                    tint = when (patient.syncStatus) {
                        SyncStatus.SYNCED -> MaterialTheme.colorScheme.primary
                        SyncStatus.PENDING -> MaterialTheme.colorScheme.secondary
                        SyncStatus.FAILED -> MaterialTheme.colorScheme.error
                    }
                )
                
                // Gender icon
                Icon(
                    imageVector = when (patient.gender) {
                        Gender.MALE -> Icons.Default.Person
                        Gender.FEMALE -> Icons.Default.Person
                        else -> Icons.Default.Person
                    },
                    contentDescription = patient.gender.name,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // Delete button
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
```

### 7. File Operations and Export

```kotlin
// utils/FileOperations.kt
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

object FileOperations {
    
    fun exportPatientsToExcel(patients: List<Patient>): File? {
        val fileChooser = JFileChooser().apply {
            dialogTitle = "Export Patients to Excel"
            fileFilter = FileNameExtensionFilter("Excel files (*.xlsx)", "xlsx")
            selectedFile = File("patients_${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))}.xlsx")
        }
        
        return if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            val file = fileChooser.selectedFile
            createExcelFile(patients, file)
            file
        } else {
            null
        }
    }
    
    private fun createExcelFile(patients: List<Patient>, file: File) {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Patients")
        
        // Create header row
        val headerRow = sheet.createRow(0)
        val headers = listOf(
            "ID", "First Name", "Last Name", "Date of Birth", 
            "Gender", "Phone Number", "Address", "Facility ID", 
            "Last Updated", "Sync Status"
        )
        
        headers.forEachIndexed { index, header ->
            val cell = headerRow.createCell(index)
            cell.setCellValue(header)
            
            // Style header
            val cellStyle = workbook.createCellStyle()
            val font = workbook.createFont()
            font.bold = true
            cellStyle.setFont(font)
            cell.cellStyle = cellStyle
        }
        
        // Add patient data
        patients.forEachIndexed { rowIndex, patient ->
            val row = sheet.createRow(rowIndex + 1)
            
            row.createCell(0).setCellValue(patient.id)
            row.createCell(1).setCellValue(patient.firstName)
            row.createCell(2).setCellValue(patient.lastName)
            row.createCell(3).setCellValue(patient.dateOfBirth)
            row.createCell(4).setCellValue(patient.gender.name)
            row.createCell(5).setCellValue(patient.phoneNumber ?: "")
            row.createCell(6).setCellValue(patient.address ?: "")
            row.createCell(7).setCellValue(patient.facilityId)
            row.createCell(8).setCellValue(patient.lastUpdated.toString())
            row.createCell(9).setCellValue(patient.syncStatus.name)
        }
        
        // Auto-size columns
        headers.indices.forEach { sheet.autoSizeColumn(it) }
        
        // Write to file
        FileOutputStream(file).use { outputStream ->
            workbook.write(outputStream)
        }
        
        workbook.close()
    }
    
    fun importPatientsFromExcel(): List<Patient>? {
        val fileChooser = JFileChooser().apply {
            dialogTitle = "Import Patients from Excel"
            fileFilter = FileNameExtensionFilter("Excel files (*.xlsx)", "xlsx")
        }
        
        return if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            val file = fileChooser.selectedFile
            readExcelFile(file)
        } else {
            null
        }
    }
    
    private fun readExcelFile(file: File): List<Patient> {
        val patients = mutableListOf<Patient>()
        val workbook = XSSFWorkbook(file.inputStream())
        val sheet = workbook.getSheetAt(0)
        
        // Skip header row
        for (rowIndex in 1..sheet.lastRowNum) {
            val row = sheet.getRow(rowIndex) ?: continue
            
            try {
                val patient = Patient(
                    id = row.getCell(0)?.stringCellValue ?: "",
                    firstName = row.getCell(1)?.stringCellValue ?: "",
                    lastName = row.getCell(2)?.stringCellValue ?: "",
                    dateOfBirth = row.getCell(3)?.stringCellValue ?: "",
                    gender = Gender.valueOf(row.getCell(4)?.stringCellValue?.uppercase() ?: "UNKNOWN"),
                    phoneNumber = row.getCell(5)?.stringCellValue?.takeIf { it.isNotBlank() },
                    address = row.getCell(6)?.stringCellValue?.takeIf { it.isNotBlank() },
                    facilityId = row.getCell(7)?.stringCellValue ?: "",
                    lastUpdated = System.currentTimeMillis(),
                    syncStatus = SyncStatus.PENDING
                )
                patients.add(patient)
            } catch (e: Exception) {
                println("Error parsing row $rowIndex: ${e.message}")
            }
        }
        
        workbook.close()
        return patients
    }
    
    fun exportReportToPDF(reportData: ReportData): File? {
        val fileChooser = JFileChooser().apply {
            dialogTitle = "Export Report to PDF"
            fileFilter = FileNameExtensionFilter("PDF files (*.pdf)", "pdf")
            selectedFile = File("report_${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))}.pdf")
        }
        
        return if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            val file = fileChooser.selectedFile
            // PDF generation would be implemented here
            // using libraries like iText or Apache PDFBox
            file
        } else {
            null
        }
    }
}

data class ReportData(
    val title: String,
    val generatedDate: LocalDateTime,
    val data: Map<String, Any>
)
```

### 8. System Integration

```kotlin
// utils/SystemIntegration.kt
import com.sun.jna.Platform
import com.sun.jna.platform.win32.Advapi32Util
import com.sun.jna.platform.win32.WinReg
import java.awt.Desktop
import java.awt.SystemTray
import java.awt.TrayIcon
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.io.File
import java.net.URI
import javax.imageio.ImageIO

object SystemIntegration {
    
    fun openFileInDefaultApplication(file: File) {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(file)
            } catch (e: Exception) {
                println("Failed to open file: ${e.message}")
            }
        }
    }
    
    fun openUrlInBrowser(url: String) {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(URI(url))
            } catch (e: Exception) {
                println("Failed to open URL: ${e.message}")
            }
        }
    }
    
    fun setupSystemTray(onShow: () -> Unit, onExit: () -> Unit) {
        if (!SystemTray.isSupported()) {
            println("System tray is not supported")
            return
        }
        
        try {
            val tray = SystemTray.getSystemTray()
            val image = ImageIO.read(
                SystemIntegration::class.java.getResourceAsStream("/icon.png")
            )
            
            val trayIcon = TrayIcon(image, "EBSCore Desktop")
            trayIcon.isImageAutoSize = true
            
            // Create popup menu
            val popup = java.awt.PopupMenu()
            
            val showItem = java.awt.MenuItem("Show")
            showItem.addActionListener { onShow() }
            popup.add(showItem)
            
            popup.addSeparator()
            
            val exitItem = java.awt.MenuItem("Exit")
            exitItem.addActionListener { onExit() }
            popup.add(exitItem)
            
            trayIcon.popupMenu = popup
            tray.add(trayIcon)
        } catch (e: Exception) {
            println("Failed to setup system tray: ${e.message}")
        }
    }
    
    fun getSystemInfo(): SystemInfo {
        return SystemInfo(
            osName = System.getProperty("os.name"),
            osVersion = System.getProperty("os.version"),
            osArch = System.getProperty("os.arch"),
            javaVersion = System.getProperty("java.version"),
            userHome = System.getProperty("user.home"),
            userName = System.getProperty("user.name"),
            totalMemory = Runtime.getRuntime().totalMemory(),
            freeMemory = Runtime.getRuntime().freeMemory(),
            maxMemory = Runtime.getRuntime().maxMemory()
        )
    }
    
    fun registerFileAssociation(extension: String, applicationPath: String) {
        if (Platform.isWindows()) {
            try {
                // Register file association on Windows
                val keyPath = "Software\\Classes\\.$extension"
                Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, keyPath)
                Advapi32Util.registrySetStringValue(
                    WinReg.HKEY_CURRENT_USER, 
                    keyPath, 
                    "", 
                    "EBSCoreFile"
                )
                
                val commandKeyPath = "Software\\Classes\\EBSCoreFile\\shell\\open\\command"
                Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, commandKeyPath)
                Advapi32Util.registrySetStringValue(
                    WinReg.HKEY_CURRENT_USER,
                    commandKeyPath,
                    "",
                    "\"$applicationPath\" \"%1\""
                )
            } catch (e: Exception) {
                println("Failed to register file association: ${e.message}")
            }
        }
    }
}

data class SystemInfo(
    val osName: String,
    val osVersion: String,
    val osArch: String,
    val javaVersion: String,
    val userHome: String,
    val userName: String,
    val totalMemory: Long,
    val freeMemory: Long,
    val maxMemory: Long
)
```

This comprehensive Desktop example demonstrates production-ready patterns for building cross-platform desktop applications with EBSCore SDK, including modern Compose Desktop UI, SQLDelight database integration, file operations, system integration, and advanced desktop-specific features.
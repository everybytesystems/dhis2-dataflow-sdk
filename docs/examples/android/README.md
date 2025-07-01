# 🤖 Android Examples - EBSCore SDK

Complete Android examples demonstrating native Android development with EBSCore SDK using Jetpack Compose, Material Design 3, and modern Android architecture patterns.

## 🎯 Overview

These examples show how to build production-ready Android applications using EBSCore SDK with:
- **Jetpack Compose** for modern UI development
- **Material Design 3** for consistent design language
- **MVVM Architecture** with ViewModels and StateFlow
- **Offline-First** approach with Room database
- **Background Sync** using WorkManager
- **Dependency Injection** with Hilt

## 📱 Setup

### Dependencies (build.gradle.kts)

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("kotlin-parcelize")
}

android {
    namespace = "com.everybytesystems.ebscore.android.example"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.everybytesystems.ebscore.android.example"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    
    kotlinOptions {
        jvmTarget = "1.8"
    }
    
    buildFeatures {
        compose = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // EBSCore SDK
    implementation("com.everybytesystems.ebscore:ebscore-sdk:1.0.0")
    
    // Android Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    
    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.6")
    
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    
    // Hilt
    implementation("com.google.dagger:hilt-android:2.48")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    kapt("com.google.dagger:hilt-compiler:2.48")
    
    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    
    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("androidx.hilt:hilt-work:1.1.0")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // Image Loading
    implementation("io.coil-kt:coil-compose:2.5.0")
    
    // Permissions
    implementation("com.google.accompanist:accompanist-permissions:0.32.0")
    
    // Biometric
    implementation("androidx.biometric:biometric:1.1.0")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.02.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
```

### Application Class

```kotlin
// Application.kt
import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class EBSCoreApplication : Application(), Configuration.Provider {
    
    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    
    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }
}
```

## 🏗️ Architecture Components

### 1. Dependency Injection with Hilt

```kotlin
// di/AppModule.kt
import com.everybytesystems.ebscore.sdk.EBSCoreSdk
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideEBSCoreSdk(): EBSCoreSdk {
        return EBSCoreSdk.Builder()
            .baseUrl("https://play.dhis2.org/2.40.1")
            .enableOfflineMode(true)
            .enableAnalytics(true)
            .build()
    }
    
    @Provides
    @Singleton
    fun providePatientRepository(
        sdk: EBSCoreSdk,
        patientDao: PatientDao
    ): PatientRepository {
        return PatientRepositoryImpl(sdk, patientDao)
    }
}

// di/DatabaseModule.kt
import androidx.room.Room
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "ebscore_database"
        )
        .fallbackToDestructiveMigration()
        .build()
    }
    
    @Provides
    fun providePatientDao(database: AppDatabase): PatientDao {
        return database.patientDao()
    }
    
    @Provides
    fun provideDataValueDao(database: AppDatabase): DataValueDao {
        return database.dataValueDao()
    }
}
```

### 2. Room Database for Offline Storage

```kotlin
// data/local/entities/PatientEntity.kt
import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Entity(tableName = "patients")
@Parcelize
data class PatientEntity(
    @PrimaryKey
    val id: String,
    val firstName: String,
    val lastName: String,
    val dateOfBirth: String,
    val gender: String,
    val phoneNumber: String?,
    val address: String?,
    val facilityId: String,
    val lastUpdated: Long,
    val syncStatus: SyncStatus = SyncStatus.PENDING
) : Parcelable

enum class SyncStatus {
    SYNCED, PENDING, FAILED
}

// data/local/dao/PatientDao.kt
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {
    
    @Query("SELECT * FROM patients WHERE facilityId = :facilityId ORDER BY lastName, firstName")
    fun getPatientsByFacility(facilityId: String): Flow<List<PatientEntity>>
    
    @Query("SELECT * FROM patients WHERE id = :patientId")
    suspend fun getPatientById(patientId: String): PatientEntity?
    
    @Query("SELECT * FROM patients WHERE syncStatus = :status")
    suspend fun getPatientsByStatus(status: SyncStatus): List<PatientEntity>
    
    @Query("""
        SELECT * FROM patients 
        WHERE facilityId = :facilityId 
        AND (firstName LIKE '%' || :query || '%' OR lastName LIKE '%' || :query || '%')
        ORDER BY lastName, firstName
    """)
    fun searchPatients(facilityId: String, query: String): Flow<List<PatientEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatient(patient: PatientEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatients(patients: List<PatientEntity>)
    
    @Update
    suspend fun updatePatient(patient: PatientEntity)
    
    @Delete
    suspend fun deletePatient(patient: PatientEntity)
    
    @Query("DELETE FROM patients WHERE facilityId = :facilityId")
    suspend fun deletePatientsByFacility(facilityId: String)
}

// data/local/database/AppDatabase.kt
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [PatientEntity::class, DataValueEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun patientDao(): PatientDao
    abstract fun dataValueDao(): DataValueDao
}

// data/local/converters/Converters.kt
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return Gson().toJson(value)
    }
    
    @TypeConverter
    fun toStringList(value: String): List<String> {
        return Gson().fromJson(value, object : TypeToken<List<String>>() {}.type)
    }
}
```

### 3. Repository Pattern Implementation

```kotlin
// data/repository/PatientRepository.kt
import com.everybytesystems.ebscore.sdk.EBSCoreSdk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface PatientRepository {
    fun getPatientsByFacility(facilityId: String): Flow<List<Patient>>
    suspend fun getPatientById(patientId: String): Patient?
    suspend fun savePatient(patient: Patient): Result<Patient>
    suspend fun deletePatient(patientId: String): Result<Unit>
    fun searchPatients(facilityId: String, query: String): Flow<List<Patient>>
    suspend fun syncPatients(facilityId: String): Result<Int>
}

@Singleton
class PatientRepositoryImpl @Inject constructor(
    private val sdk: EBSCoreSdk,
    private val patientDao: PatientDao
) : PatientRepository {
    
    override fun getPatientsByFacility(facilityId: String): Flow<List<Patient>> {
        return patientDao.getPatientsByFacility(facilityId)
            .map { entities -> entities.map { it.toPatient() } }
    }
    
    override suspend fun getPatientById(patientId: String): Patient? {
        return patientDao.getPatientById(patientId)?.toPatient()
    }
    
    override suspend fun savePatient(patient: Patient): Result<Patient> {
        return try {
            // Save locally first
            val entity = patient.toEntity().copy(
                syncStatus = SyncStatus.PENDING,
                lastUpdated = System.currentTimeMillis()
            )
            patientDao.insertPatient(entity)
            
            // Try to sync to server
            try {
                val trackedEntity = patient.toTrackedEntity()
                val result = sdk.createTrackedEntity(trackedEntity)
                
                if (result.isSuccess) {
                    // Update with server ID and mark as synced
                    val syncedEntity = entity.copy(
                        id = result.uid,
                        syncStatus = SyncStatus.SYNCED
                    )
                    patientDao.updatePatient(syncedEntity)
                    Result.success(syncedEntity.toPatient())
                } else {
                    Result.success(entity.toPatient())
                }
            } catch (e: Exception) {
                // Keep local copy even if sync fails
                Result.success(entity.toPatient())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deletePatient(patientId: String): Result<Unit> {
        return try {
            val patient = patientDao.getPatientById(patientId)
            if (patient != null) {
                patientDao.deletePatient(patient)
                
                // Try to delete from server if synced
                if (patient.syncStatus == SyncStatus.SYNCED) {
                    try {
                        sdk.deleteTrackedEntity(patientId)
                    } catch (e: Exception) {
                        // Log error but don't fail the operation
                    }
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun searchPatients(facilityId: String, query: String): Flow<List<Patient>> {
        return patientDao.searchPatients(facilityId, query)
            .map { entities -> entities.map { it.toPatient() } }
    }
    
    override suspend fun syncPatients(facilityId: String): Result<Int> {
        return try {
            // Sync pending local changes to server
            val pendingPatients = patientDao.getPatientsByStatus(SyncStatus.PENDING)
            var syncedCount = 0
            
            pendingPatients.forEach { patient ->
                try {
                    val trackedEntity = patient.toPatient().toTrackedEntity()
                    val result = sdk.createTrackedEntity(trackedEntity)
                    
                    if (result.isSuccess) {
                        patientDao.updatePatient(
                            patient.copy(
                                id = result.uid,
                                syncStatus = SyncStatus.SYNCED
                            )
                        )
                        syncedCount++
                    }
                } catch (e: Exception) {
                    patientDao.updatePatient(
                        patient.copy(syncStatus = SyncStatus.FAILED)
                    )
                }
            }
            
            // Fetch latest data from server
            try {
                val serverPatients = sdk.getTrackedEntities("patient_program", facilityId)
                val entities = serverPatients.map { it.toPatientEntity(facilityId) }
                patientDao.insertPatients(entities)
            } catch (e: Exception) {
                // Continue with local data if server fetch fails
            }
            
            Result.success(syncedCount)
        } catch (e: Exception) {
            Result.failure(e)
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
        facilityId = facilityId
    )
}

fun Patient.toEntity(): PatientEntity {
    return PatientEntity(
        id = id,
        firstName = firstName,
        lastName = lastName,
        dateOfBirth = dateOfBirth,
        gender = gender.name.lowercase(),
        phoneNumber = phoneNumber,
        address = address,
        facilityId = facilityId,
        lastUpdated = System.currentTimeMillis()
    )
}
```

### 4. ViewModels with StateFlow

```kotlin
// presentation/viewmodel/PatientListViewModel.kt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PatientListViewModel @Inject constructor(
    private val patientRepository: PatientRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(PatientListUiState())
    val uiState: StateFlow<PatientListUiState> = _uiState.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _selectedFacility = MutableStateFlow<String?>(null)
    
    init {
        // Combine search query and facility to get filtered patients
        combine(
            _selectedFacility.filterNotNull(),
            _searchQuery
        ) { facilityId, query ->
            if (query.isBlank()) {
                patientRepository.getPatientsByFacility(facilityId)
            } else {
                patientRepository.searchPatients(facilityId, query)
            }
        }.flatMapLatest { it }
            .catch { error ->
                _uiState.update { it.copy(error = error.message) }
            }
            .onEach { patients ->
                _uiState.update { 
                    it.copy(
                        patients = patients,
                        isLoading = false,
                        error = null
                    )
                }
            }
            .launchIn(viewModelScope)
    }
    
    fun loadPatients(facilityId: String) {
        _selectedFacility.value = facilityId
        _uiState.update { it.copy(isLoading = true) }
    }
    
    fun searchPatients(query: String) {
        _searchQuery.value = query
    }
    
    fun refreshPatients() {
        _selectedFacility.value?.let { facilityId ->
            viewModelScope.launch {
                _uiState.update { it.copy(isRefreshing = true) }
                
                patientRepository.syncPatients(facilityId)
                    .onSuccess { syncedCount ->
                        _uiState.update { 
                            it.copy(
                                isRefreshing = false,
                                syncMessage = "Synced $syncedCount patients"
                            )
                        }
                    }
                    .onFailure { error ->
                        _uiState.update { 
                            it.copy(
                                isRefreshing = false,
                                error = error.message
                            )
                        }
                    }
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    fun clearSyncMessage() {
        _uiState.update { it.copy(syncMessage = null) }
    }
}

data class PatientListUiState(
    val patients: List<Patient> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val syncMessage: String? = null
)

// presentation/viewmodel/PatientDetailViewModel.kt
@HiltViewModel
class PatientDetailViewModel @Inject constructor(
    private val patientRepository: PatientRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val patientId: String = checkNotNull(savedStateHandle["patientId"])
    
    private val _uiState = MutableStateFlow(PatientDetailUiState())
    val uiState: StateFlow<PatientDetailUiState> = _uiState.asStateFlow()
    
    init {
        loadPatientDetails()
    }
    
    private fun loadPatientDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val patient = patientRepository.getPatientById(patientId)
                _uiState.update { 
                    it.copy(
                        patient = patient,
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
    
    fun deletePatient() {
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true) }
            
            patientRepository.deletePatient(patientId)
                .onSuccess {
                    _uiState.update { 
                        it.copy(
                            isDeleting = false,
                            isDeleted = true
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { 
                        it.copy(
                            isDeleting = false,
                            error = error.message
                        )
                    }
                }
        }
    }
}

data class PatientDetailUiState(
    val patient: Patient? = null,
    val isLoading: Boolean = false,
    val isDeleting: Boolean = false,
    val isDeleted: Boolean = false,
    val error: String? = null
)
```

### 5. Jetpack Compose UI Components

```kotlin
// presentation/ui/screen/PatientListScreen.kt
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientListScreen(
    facilityId: String,
    onPatientClick: (String) -> Unit,
    onAddPatientClick: () -> Unit,
    viewModel: PatientListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    
    LaunchedEffect(facilityId) {
        viewModel.loadPatients(facilityId)
    }
    
    // Show sync message
    uiState.syncMessage?.let { message ->
        LaunchedEffect(message) {
            // Show snackbar or toast
            viewModel.clearSyncMessage()
        }
    }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Search Bar
        SearchBar(
            query = searchQuery,
            onQueryChange = viewModel::searchPatients,
            placeholder = { Text("Search patients...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Search suggestions could go here
        }
        
        // Content
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                uiState.error != null -> {
                    ErrorMessage(
                        message = uiState.error,
                        onRetry = { viewModel.loadPatients(facilityId) },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                uiState.patients.isEmpty() -> {
                    EmptyState(
                        message = if (searchQuery.isBlank()) {
                            "No patients found"
                        } else {
                            "No patients match your search"
                        },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = uiState.patients,
                            key = { it.id }
                        ) { patient ->
                            PatientCard(
                                patient = patient,
                                onClick = { onPatientClick(patient.id) }
                            )
                        }
                    }
                }
            }
            
            // Floating Action Button
            FloatingActionButton(
                onClick = onAddPatientClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Patient")
            }
            
            // Pull to refresh indicator
            if (uiState.isRefreshing) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientCard(
    patient: Patient,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${patient.firstName} ${patient.lastName}",
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
                
                Icon(
                    imageVector = when (patient.gender) {
                        Gender.MALE -> Icons.Default.Person
                        Gender.FEMALE -> Icons.Default.Person
                        else -> Icons.Default.Person
                    },
                    contentDescription = patient.gender.name,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun ErrorMessage(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
fun EmptyState(
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.PersonOff,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(48.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
```

### 6. Background Sync with WorkManager

```kotlin
// work/SyncWorker.kt
import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val patientRepository: PatientRepository
) : CoroutineWorker(context, workerParams) {
    
    override suspend fun doWork(): Result {
        return try {
            val facilityId = inputData.getString(KEY_FACILITY_ID) ?: return Result.failure()
            
            patientRepository.syncPatients(facilityId)
                .fold(
                    onSuccess = { syncedCount ->
                        val outputData = workDataOf(KEY_SYNCED_COUNT to syncedCount)
                        Result.success(outputData)
                    },
                    onFailure = { 
                        Result.retry()
                    }
                )
        } catch (e: Exception) {
            Result.failure()
        }
    }
    
    companion object {
        const val KEY_FACILITY_ID = "facility_id"
        const val KEY_SYNCED_COUNT = "synced_count"
        const val WORK_NAME = "sync_patients"
        
        fun schedulePeriodicSync(context: Context, facilityId: String) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()
            
            val inputData = workDataOf(KEY_FACILITY_ID to facilityId)
            
            val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
                repeatInterval = 6,
                repeatIntervalTimeUnit = TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .setInputData(inputData)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .build()
            
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    syncRequest
                )
        }
    }
}
```

### 7. Navigation with Compose Navigation

```kotlin
// navigation/AppNavigation.kt
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { facilityId ->
                    navController.navigate("patient_list/$facilityId") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        
        composable("patient_list/{facilityId}") { backStackEntry ->
            val facilityId = backStackEntry.arguments?.getString("facilityId") ?: ""
            PatientListScreen(
                facilityId = facilityId,
                onPatientClick = { patientId ->
                    navController.navigate("patient_detail/$patientId")
                },
                onAddPatientClick = {
                    navController.navigate("add_patient/$facilityId")
                }
            )
        }
        
        composable("patient_detail/{patientId}") { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId") ?: ""
            PatientDetailScreen(
                patientId = patientId,
                onNavigateBack = { navController.popBackStack() },
                onEditPatient = { 
                    navController.navigate("edit_patient/$patientId")
                }
            )
        }
        
        composable("add_patient/{facilityId}") { backStackEntry ->
            val facilityId = backStackEntry.arguments?.getString("facilityId") ?: ""
            AddPatientScreen(
                facilityId = facilityId,
                onNavigateBack = { navController.popBackStack() },
                onPatientSaved = { patientId ->
                    navController.navigate("patient_detail/$patientId") {
                        popUpTo("patient_list/$facilityId")
                    }
                }
            )
        }
    }
}
```

### 8. MainActivity

```kotlin
// MainActivity.kt
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            EBSCoreTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}
```

## 🔧 Advanced Features

### Biometric Authentication

```kotlin
// auth/BiometricAuthManager.kt
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

class BiometricAuthManager(private val activity: FragmentActivity) {
    
    fun authenticate(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val biometricManager = BiometricManager.from(activity)
        
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                showBiometricPrompt(onSuccess, onError)
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                onError("No biometric hardware available")
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                onError("Biometric hardware unavailable")
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                onError("No biometric credentials enrolled")
            }
        }
    }
    
    private fun showBiometricPrompt(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)
        val biometricPrompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onError(errString.toString())
                }
                
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }
                
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onError("Authentication failed")
                }
            })
        
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Authentication")
            .setSubtitle("Use your fingerprint or face to authenticate")
            .setNegativeButtonText("Cancel")
            .build()
        
        biometricPrompt.authenticate(promptInfo)
    }
}
```

### Push Notifications

```kotlin
// notification/NotificationService.kt
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EBSCoreMessagingService : FirebaseMessagingService() {
    
    @Inject
    lateinit var notificationManager: NotificationManager
    
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        remoteMessage.notification?.let { notification ->
            notificationManager.showNotification(
                title = notification.title ?: "EBSCore",
                message = notification.body ?: "",
                data = remoteMessage.data
            )
        }
    }
    
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Send token to server
    }
}
```

This comprehensive Android example demonstrates production-ready patterns for building native Android applications with EBSCore SDK, including modern architecture, offline support, background sync, and advanced Android features.
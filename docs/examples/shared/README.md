# 🔄 Shared Examples - EBSCore SDK

Shared multiplatform examples demonstrating common patterns and best practices across all platforms (Android, iOS, Desktop, Web).

## 🎯 Overview

These examples show how to write platform-agnostic code using EBSCore SDK that works seamlessly across all supported platforms. The shared code demonstrates common patterns, data models, and business logic that can be reused across different platform implementations.

## 📁 Structure

```
shared/
├── commonMain/
│   ├── data/              # Shared data models and repositories
│   ├── domain/            # Business logic and use cases
│   ├── presentation/      # Shared ViewModels and state management
│   └── utils/             # Common utilities and extensions
├── androidMain/           # Android-specific implementations
├── iosMain/              # iOS-specific implementations
├── desktopMain/          # Desktop-specific implementations
└── jsMain/               # Web-specific implementations
```

## 🎯 Shared Module Examples

### 1. Common Data Models

```kotlin
// commonMain/data/models/SharedModels.kt
import kotlinx.serialization.Serializable
import kotlinx.datetime.Instant

@Serializable
data class HealthFacility(
    val id: String,
    val name: String,
    val code: String,
    val level: Int,
    val coordinates: Coordinates?,
    val parentId: String?,
    val lastUpdated: Instant
)

@Serializable
data class Coordinates(
    val latitude: Double,
    val longitude: Double
)

@Serializable
data class Patient(
    val id: String,
    val firstName: String,
    val lastName: String,
    val dateOfBirth: String,
    val gender: Gender,
    val phoneNumber: String?,
    val address: Address?,
    val enrollments: List<ProgramEnrollment> = emptyList()
)

@Serializable
enum class Gender {
    MALE, FEMALE, OTHER, UNKNOWN
}

@Serializable
data class Address(
    val street: String?,
    val city: String?,
    val state: String?,
    val postalCode: String?,
    val country: String?
)

@Serializable
data class ProgramEnrollment(
    val id: String,
    val programId: String,
    val enrollmentDate: String,
    val status: EnrollmentStatus,
    val events: List<ProgramEvent> = emptyList()
)

@Serializable
enum class EnrollmentStatus {
    ACTIVE, COMPLETED, CANCELLED
}

@Serializable
data class ProgramEvent(
    val id: String,
    val programStageId: String,
    val eventDate: String,
    val status: EventStatus,
    val dataValues: Map<String, String> = emptyMap()
)

@Serializable
enum class EventStatus {
    ACTIVE, COMPLETED, VISITED, SCHEDULE, OVERDUE, SKIPPED
}

@Serializable
data class DataIndicator(
    val id: String,
    val name: String,
    val value: Double,
    val period: String,
    val orgUnit: String,
    val target: Double?,
    val trend: TrendDirection
)

@Serializable
enum class TrendDirection {
    UP, DOWN, STABLE, UNKNOWN
}
```

### 2. Repository Pattern Implementation

```kotlin
// commonMain/data/repository/HealthDataRepository.kt
import com.everybytesystems.ebscore.sdk.EBSCoreSdk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.catch

interface HealthDataRepository {
    suspend fun getHealthFacilities(): Result<List<HealthFacility>>
    suspend fun getPatients(facilityId: String): Result<List<Patient>>
    suspend fun getPatient(patientId: String): Result<Patient?>
    suspend fun savePatient(patient: Patient): Result<Patient>
    suspend fun getDataIndicators(period: String, orgUnit: String): Result<List<DataIndicator>>
    fun observePatients(facilityId: String): Flow<List<Patient>>
    suspend fun syncData(): Result<SyncStatus>
}

class HealthDataRepositoryImpl(
    private val sdk: EBSCoreSdk
) : HealthDataRepository {
    
    override suspend fun getHealthFacilities(): Result<List<HealthFacility>> {
        return try {
            val orgUnits = sdk.getOrganizationUnits()
            val facilities = orgUnits.map { orgUnit ->
                HealthFacility(
                    id = orgUnit.id,
                    name = orgUnit.name,
                    code = orgUnit.code,
                    level = orgUnit.level,
                    coordinates = orgUnit.coordinates?.let { 
                        Coordinates(it.latitude, it.longitude) 
                    },
                    parentId = orgUnit.parent,
                    lastUpdated = orgUnit.lastUpdated
                )
            }
            Result.success(facilities)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getPatients(facilityId: String): Result<List<Patient>> {
        return try {
            val trackedEntities = sdk.getTrackedEntities(
                program = "patient_program",
                orgUnit = facilityId
            )
            
            val patients = trackedEntities.map { entity ->
                Patient(
                    id = entity.trackedEntity,
                    firstName = entity.getAttributeValue("first_name") ?: "",
                    lastName = entity.getAttributeValue("last_name") ?: "",
                    dateOfBirth = entity.getAttributeValue("date_of_birth") ?: "",
                    gender = parseGender(entity.getAttributeValue("gender")),
                    phoneNumber = entity.getAttributeValue("phone_number"),
                    address = parseAddress(entity),
                    enrollments = entity.enrollments.map { enrollment ->
                        ProgramEnrollment(
                            id = enrollment.enrollment,
                            programId = enrollment.program,
                            enrollmentDate = enrollment.enrollmentDate,
                            status = parseEnrollmentStatus(enrollment.status),
                            events = enrollment.events.map { event ->
                                ProgramEvent(
                                    id = event.event,
                                    programStageId = event.programStage,
                                    eventDate = event.eventDate,
                                    status = parseEventStatus(event.status),
                                    dataValues = event.dataValues.associate { 
                                        it.dataElement to it.value 
                                    }
                                )
                            }
                        )
                    }
                )
            }
            Result.success(patients)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getPatient(patientId: String): Result<Patient?> {
        return try {
            val entity = sdk.getTrackedEntity(patientId)
            if (entity != null) {
                val patient = Patient(
                    id = entity.trackedEntity,
                    firstName = entity.getAttributeValue("first_name") ?: "",
                    lastName = entity.getAttributeValue("last_name") ?: "",
                    dateOfBirth = entity.getAttributeValue("date_of_birth") ?: "",
                    gender = parseGender(entity.getAttributeValue("gender")),
                    phoneNumber = entity.getAttributeValue("phone_number"),
                    address = parseAddress(entity),
                    enrollments = emptyList() // Load separately if needed
                )
                Result.success(patient)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun savePatient(patient: Patient): Result<Patient> {
        return try {
            val trackedEntity = TrackedEntity(
                trackedEntityType = "person",
                orgUnit = "default_facility", // Should be passed as parameter
                attributes = listOf(
                    TrackedEntityAttribute("first_name", patient.firstName),
                    TrackedEntityAttribute("last_name", patient.lastName),
                    TrackedEntityAttribute("date_of_birth", patient.dateOfBirth),
                    TrackedEntityAttribute("gender", patient.gender.name.lowercase()),
                    TrackedEntityAttribute("phone_number", patient.phoneNumber ?: "")
                )
            )
            
            val result = sdk.createTrackedEntity(trackedEntity)
            if (result.isSuccess) {
                Result.success(patient.copy(id = result.uid))
            } else {
                Result.failure(Exception("Failed to save patient"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getDataIndicators(period: String, orgUnit: String): Result<List<DataIndicator>> {
        return try {
            val query = AnalyticsQuery.builder()
                .dimension("dx", listOf("indicator1", "indicator2", "indicator3"))
                .dimension("pe", listOf(period))
                .dimension("ou", listOf(orgUnit))
                .build()
            
            val analyticsData = sdk.getAnalytics(query)
            val indicators = analyticsData.rows.map { row ->
                DataIndicator(
                    id = row[0],
                    name = getIndicatorName(row[0]),
                    value = row[3].toDoubleOrNull() ?: 0.0,
                    period = row[1],
                    orgUnit = row[2],
                    target = getIndicatorTarget(row[0]),
                    trend = calculateTrend(row[0], period)
                )
            }
            Result.success(indicators)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun observePatients(facilityId: String): Flow<List<Patient>> = flow {
        while (true) {
            val result = getPatients(facilityId)
            if (result.isSuccess) {
                emit(result.getOrNull() ?: emptyList())
            }
            kotlinx.coroutines.delay(30000) // Refresh every 30 seconds
        }
    }.catch { e ->
        emit(emptyList())
    }
    
    override suspend fun syncData(): Result<SyncStatus> {
        return try {
            val syncService = sdk.getSyncService()
            val result = syncService.syncAll(
                options = SyncOptions(
                    syncMetadata = true,
                    syncData = true,
                    conflictResolution = ConflictResolutionStrategy.SERVER_WINS
                )
            )
            
            when (result) {
                is SyncResult.Success -> Result.success(
                    SyncStatus.Completed(result.syncedItems, result.duration)
                )
                is SyncResult.Error -> Result.failure(
                    Exception(result.message)
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Helper functions
    private fun parseGender(value: String?): Gender {
        return when (value?.lowercase()) {
            "male", "m" -> Gender.MALE
            "female", "f" -> Gender.FEMALE
            "other" -> Gender.OTHER
            else -> Gender.UNKNOWN
        }
    }
    
    private fun parseAddress(entity: TrackedEntity): Address? {
        val street = entity.getAttributeValue("address_street")
        val city = entity.getAttributeValue("address_city")
        val state = entity.getAttributeValue("address_state")
        val postalCode = entity.getAttributeValue("address_postal_code")
        val country = entity.getAttributeValue("address_country")
        
        return if (listOf(street, city, state, postalCode, country).any { !it.isNullOrBlank() }) {
            Address(street, city, state, postalCode, country)
        } else null
    }
    
    private fun parseEnrollmentStatus(status: String): EnrollmentStatus {
        return when (status.uppercase()) {
            "ACTIVE" -> EnrollmentStatus.ACTIVE
            "COMPLETED" -> EnrollmentStatus.COMPLETED
            "CANCELLED" -> EnrollmentStatus.CANCELLED
            else -> EnrollmentStatus.ACTIVE
        }
    }
    
    private fun parseEventStatus(status: String): EventStatus {
        return when (status.uppercase()) {
            "ACTIVE" -> EventStatus.ACTIVE
            "COMPLETED" -> EventStatus.COMPLETED
            "VISITED" -> EventStatus.VISITED
            "SCHEDULE" -> EventStatus.SCHEDULE
            "OVERDUE" -> EventStatus.OVERDUE
            "SKIPPED" -> EventStatus.SKIPPED
            else -> EventStatus.ACTIVE
        }
    }
    
    private fun getIndicatorName(id: String): String {
        return when (id) {
            "indicator1" -> "Patient Visits"
            "indicator2" -> "Vaccination Rate"
            "indicator3" -> "Treatment Success"
            else -> "Unknown Indicator"
        }
    }
    
    private fun getIndicatorTarget(id: String): Double? {
        return when (id) {
            "indicator1" -> 100.0
            "indicator2" -> 95.0
            "indicator3" -> 85.0
            else -> null
        }
    }
    
    private fun calculateTrend(indicatorId: String, period: String): TrendDirection {
        // Simplified trend calculation - in real implementation,
        // you would compare with previous periods
        return TrendDirection.STABLE
    }
}

sealed class SyncStatus {
    object InProgress : SyncStatus()
    data class Completed(val itemsSynced: Int, val duration: Long) : SyncStatus()
    data class Failed(val error: String) : SyncStatus()
}
```

### 3. Use Cases (Business Logic)

```kotlin
// commonMain/domain/usecase/PatientUseCases.kt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetPatientsUseCase(
    private val repository: HealthDataRepository
) {
    suspend operator fun invoke(facilityId: String): Result<List<Patient>> {
        return repository.getPatients(facilityId)
    }
}

class SearchPatientsUseCase(
    private val repository: HealthDataRepository
) {
    suspend operator fun invoke(facilityId: String, query: String): Result<List<Patient>> {
        return repository.getPatients(facilityId).map { patients ->
            patients.filter { patient ->
                patient.firstName.contains(query, ignoreCase = true) ||
                patient.lastName.contains(query, ignoreCase = true) ||
                patient.phoneNumber?.contains(query) == true
            }
        }
    }
}

class CreatePatientUseCase(
    private val repository: HealthDataRepository
) {
    suspend operator fun invoke(patient: Patient): Result<Patient> {
        // Validate patient data
        val validationResult = validatePatient(patient)
        if (validationResult.isFailure) {
            return validationResult
        }
        
        return repository.savePatient(patient)
    }
    
    private fun validatePatient(patient: Patient): Result<Patient> {
        val errors = mutableListOf<String>()
        
        if (patient.firstName.isBlank()) {
            errors.add("First name is required")
        }
        
        if (patient.lastName.isBlank()) {
            errors.add("Last name is required")
        }
        
        if (patient.dateOfBirth.isBlank()) {
            errors.add("Date of birth is required")
        }
        
        if (patient.phoneNumber?.isNotBlank() == true && !isValidPhoneNumber(patient.phoneNumber)) {
            errors.add("Invalid phone number format")
        }
        
        return if (errors.isEmpty()) {
            Result.success(patient)
        } else {
            Result.failure(ValidationException(errors))
        }
    }
    
    private fun isValidPhoneNumber(phone: String): Boolean {
        return phone.matches(Regex("^[+]?[1-9]\\d{1,14}$"))
    }
}

class GetPatientDetailsUseCase(
    private val repository: HealthDataRepository
) {
    suspend operator fun invoke(patientId: String): Result<PatientDetails?> {
        return repository.getPatient(patientId).map { patient ->
            patient?.let { PatientDetails.fromPatient(it) }
        }
    }
}

class ObservePatientsUseCase(
    private val repository: HealthDataRepository
) {
    operator fun invoke(facilityId: String): Flow<List<Patient>> {
        return repository.observePatients(facilityId)
    }
}

class SyncDataUseCase(
    private val repository: HealthDataRepository
) {
    suspend operator fun invoke(): Result<SyncStatus> {
        return repository.syncData()
    }
}

data class PatientDetails(
    val patient: Patient,
    val totalVisits: Int,
    val lastVisitDate: String?,
    val activePrograms: List<String>,
    val upcomingAppointments: List<Appointment>
) {
    companion object {
        fun fromPatient(patient: Patient): PatientDetails {
            val totalVisits = patient.enrollments.sumOf { it.events.size }
            val lastVisitDate = patient.enrollments
                .flatMap { it.events }
                .maxByOrNull { it.eventDate }
                ?.eventDate
            
            val activePrograms = patient.enrollments
                .filter { it.status == EnrollmentStatus.ACTIVE }
                .map { it.programId }
            
            return PatientDetails(
                patient = patient,
                totalVisits = totalVisits,
                lastVisitDate = lastVisitDate,
                activePrograms = activePrograms,
                upcomingAppointments = emptyList() // Would be calculated from events
            )
        }
    }
}

data class Appointment(
    val id: String,
    val date: String,
    val type: String,
    val status: AppointmentStatus
)

enum class AppointmentStatus {
    SCHEDULED, CONFIRMED, COMPLETED, CANCELLED, NO_SHOW
}

class ValidationException(val errors: List<String>) : Exception(errors.joinToString(", "))
```

### 4. Shared ViewModels and State Management

```kotlin
// commonMain/presentation/viewmodel/SharedViewModel.kt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel(
    protected val scope: CoroutineScope
) {
    protected val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    protected val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    fun clearError() {
        _error.value = null
    }
    
    protected fun handleError(throwable: Throwable) {
        _error.value = throwable.message ?: "An unknown error occurred"
        _isLoading.value = false
    }
}

class PatientListViewModel(
    private val getPatientsUseCase: GetPatientsUseCase,
    private val searchPatientsUseCase: SearchPatientsUseCase,
    private val observePatientsUseCase: ObservePatientsUseCase,
    scope: CoroutineScope
) : BaseViewModel(scope) {
    
    private val _patients = MutableStateFlow<List<Patient>>(emptyList())
    val patients: StateFlow<List<Patient>> = _patients.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _selectedFacility = MutableStateFlow<String?>(null)
    val selectedFacility: StateFlow<String?> = _selectedFacility.asStateFlow()
    
    fun loadPatients(facilityId: String) {
        _selectedFacility.value = facilityId
        scope.launch {
            _isLoading.value = true
            try {
                val result = getPatientsUseCase(facilityId)
                result.fold(
                    onSuccess = { _patients.value = it },
                    onFailure = { handleError(it) }
                )
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun searchPatients(query: String) {
        _searchQuery.value = query
        val facilityId = _selectedFacility.value ?: return
        
        scope.launch {
            _isLoading.value = true
            try {
                val result = if (query.isBlank()) {
                    getPatientsUseCase(facilityId)
                } else {
                    searchPatientsUseCase(facilityId, query)
                }
                
                result.fold(
                    onSuccess = { _patients.value = it },
                    onFailure = { handleError(it) }
                )
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun startObservingPatients(facilityId: String) {
        scope.launch {
            observePatientsUseCase(facilityId).collect { patients ->
                _patients.value = patients
            }
        }
    }
    
    fun refreshPatients() {
        _selectedFacility.value?.let { facilityId ->
            loadPatients(facilityId)
        }
    }
}

class PatientDetailsViewModel(
    private val getPatientDetailsUseCase: GetPatientDetailsUseCase,
    scope: CoroutineScope
) : BaseViewModel(scope) {
    
    private val _patientDetails = MutableStateFlow<PatientDetails?>(null)
    val patientDetails: StateFlow<PatientDetails?> = _patientDetails.asStateFlow()
    
    fun loadPatientDetails(patientId: String) {
        scope.launch {
            _isLoading.value = true
            try {
                val result = getPatientDetailsUseCase(patientId)
                result.fold(
                    onSuccess = { _patientDetails.value = it },
                    onFailure = { handleError(it) }
                )
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class CreatePatientViewModel(
    private val createPatientUseCase: CreatePatientUseCase,
    scope: CoroutineScope
) : BaseViewModel(scope) {
    
    private val _patientForm = MutableStateFlow(PatientForm())
    val patientForm: StateFlow<PatientForm> = _patientForm.asStateFlow()
    
    private val _validationErrors = MutableStateFlow<List<String>>(emptyList())
    val validationErrors: StateFlow<List<String>> = _validationErrors.asStateFlow()
    
    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()
    
    fun updateForm(form: PatientForm) {
        _patientForm.value = form
        _validationErrors.value = emptyList()
    }
    
    fun savePatient() {
        val form = _patientForm.value
        val patient = Patient(
            id = "", // Will be generated
            firstName = form.firstName,
            lastName = form.lastName,
            dateOfBirth = form.dateOfBirth,
            gender = form.gender,
            phoneNumber = form.phoneNumber.takeIf { it.isNotBlank() },
            address = if (form.hasAddress()) form.toAddress() else null
        )
        
        scope.launch {
            _isLoading.value = true
            try {
                val result = createPatientUseCase(patient)
                result.fold(
                    onSuccess = { 
                        _saveSuccess.value = true
                        _patientForm.value = PatientForm() // Reset form
                    },
                    onFailure = { 
                        if (it is ValidationException) {
                            _validationErrors.value = it.errors
                        } else {
                            handleError(it)
                        }
                    }
                )
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun resetSaveSuccess() {
        _saveSuccess.value = false
    }
}

data class PatientForm(
    val firstName: String = "",
    val lastName: String = "",
    val dateOfBirth: String = "",
    val gender: Gender = Gender.UNKNOWN,
    val phoneNumber: String = "",
    val street: String = "",
    val city: String = "",
    val state: String = "",
    val postalCode: String = "",
    val country: String = ""
) {
    fun hasAddress(): Boolean {
        return listOf(street, city, state, postalCode, country).any { it.isNotBlank() }
    }
    
    fun toAddress(): Address {
        return Address(
            street = street.takeIf { it.isNotBlank() },
            city = city.takeIf { it.isNotBlank() },
            state = state.takeIf { it.isNotBlank() },
            postalCode = postalCode.takeIf { it.isNotBlank() },
            country = country.takeIf { it.isNotBlank() }
        )
    }
}

class SyncViewModel(
    private val syncDataUseCase: SyncDataUseCase,
    scope: CoroutineScope
) : BaseViewModel(scope) {
    
    private val _syncStatus = MutableStateFlow<SyncStatus?>(null)
    val syncStatus: StateFlow<SyncStatus?> = _syncStatus.asStateFlow()
    
    private val _lastSyncTime = MutableStateFlow<String?>(null)
    val lastSyncTime: StateFlow<String?> = _lastSyncTime.asStateFlow()
    
    fun startSync() {
        scope.launch {
            _syncStatus.value = SyncStatus.InProgress
            _isLoading.value = true
            
            try {
                val result = syncDataUseCase()
                result.fold(
                    onSuccess = { status ->
                        _syncStatus.value = status
                        if (status is SyncStatus.Completed) {
                            _lastSyncTime.value = getCurrentTimestamp()
                        }
                    },
                    onFailure = { 
                        _syncStatus.value = SyncStatus.Failed(it.message ?: "Sync failed")
                        handleError(it)
                    }
                )
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearSyncStatus() {
        _syncStatus.value = null
    }
    
    private fun getCurrentTimestamp(): String {
        // Platform-specific implementation would be provided
        return "2024-01-15 10:30:00"
    }
}
```

### 5. Shared Utilities and Extensions

```kotlin
// commonMain/utils/SharedExtensions.kt
import kotlinx.datetime.*
import kotlinx.serialization.json.Json

// Date/Time Extensions
fun String.toLocalDate(): LocalDate? {
    return try {
        LocalDate.parse(this)
    } catch (e: Exception) {
        null
    }
}

fun LocalDate.toDHIS2Format(): String {
    return this.toString()
}

fun Instant.toDisplayString(): String {
    return this.toLocalDateTime(TimeZone.currentSystemDefault())
        .toString().replace('T', ' ').substringBefore('.')
}

// Collection Extensions
fun <T> List<T>.chunkedBy(size: Int): List<List<T>> {
    return this.chunked(size)
}

fun <T> List<T>.safeGet(index: Int): T? {
    return if (index in 0 until size) this[index] else null
}

// String Extensions
fun String.isValidEmail(): Boolean {
    return this.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
}

fun String.isValidPhoneNumber(): Boolean {
    return this.matches(Regex("^[+]?[1-9]\\d{1,14}$"))
}

fun String.capitalizeWords(): String {
    return this.split(" ").joinToString(" ") { word ->
        word.lowercase().replaceFirstChar { it.uppercase() }
    }
}

// Result Extensions
inline fun <T, R> Result<T>.mapResult(transform: (T) -> R): Result<R> {
    return when {
        isSuccess -> Result.success(transform(getOrThrow()))
        else -> Result.failure(exceptionOrNull()!!)
    }
}

inline fun <T> Result<T>.onSuccessAndFailure(
    onSuccess: (T) -> Unit,
    onFailure: (Throwable) -> Unit
) {
    fold(onSuccess, onFailure)
}

// TrackedEntity Extensions
fun TrackedEntity.getAttributeValue(attributeId: String): String? {
    return attributes.find { it.attribute == attributeId }?.value
}

fun TrackedEntity.hasAttribute(attributeId: String): Boolean {
    return attributes.any { it.attribute == attributeId }
}

// JSON Extensions
val SharedJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
    encodeDefaults = false
}

inline fun <reified T> String.parseJson(): T? {
    return try {
        SharedJson.decodeFromString<T>(this)
    } catch (e: Exception) {
        null
    }
}

inline fun <reified T> T.toJsonString(): String {
    return SharedJson.encodeToString(kotlinx.serialization.serializer(), this)
}
```

### 6. Shared Validation Logic

```kotlin
// commonMain/utils/ValidationUtils.kt
object ValidationUtils {
    
    fun validatePatientData(patient: Patient): ValidationResult {
        val errors = mutableListOf<ValidationError>()
        
        // Required fields
        if (patient.firstName.isBlank()) {
            errors.add(ValidationError.FIRST_NAME_REQUIRED)
        }
        
        if (patient.lastName.isBlank()) {
            errors.add(ValidationError.LAST_NAME_REQUIRED)
        }
        
        if (patient.dateOfBirth.isBlank()) {
            errors.add(ValidationError.DATE_OF_BIRTH_REQUIRED)
        } else if (!isValidDate(patient.dateOfBirth)) {
            errors.add(ValidationError.INVALID_DATE_FORMAT)
        }
        
        // Optional field validation
        patient.phoneNumber?.let { phone ->
            if (phone.isNotBlank() && !phone.isValidPhoneNumber()) {
                errors.add(ValidationError.INVALID_PHONE_NUMBER)
            }
        }
        
        // Age validation
        patient.dateOfBirth.toLocalDate()?.let { birthDate ->
            val age = calculateAge(birthDate)
            if (age < 0 || age > 150) {
                errors.add(ValidationError.INVALID_AGE)
            }
        }
        
        return ValidationResult(errors)
    }
    
    fun validateHealthFacility(facility: HealthFacility): ValidationResult {
        val errors = mutableListOf<ValidationError>()
        
        if (facility.name.isBlank()) {
            errors.add(ValidationError.FACILITY_NAME_REQUIRED)
        }
        
        if (facility.code.isBlank()) {
            errors.add(ValidationError.FACILITY_CODE_REQUIRED)
        }
        
        if (facility.level < 1 || facility.level > 6) {
            errors.add(ValidationError.INVALID_FACILITY_LEVEL)
        }
        
        facility.coordinates?.let { coords ->
            if (coords.latitude < -90 || coords.latitude > 90) {
                errors.add(ValidationError.INVALID_LATITUDE)
            }
            if (coords.longitude < -180 || coords.longitude > 180) {
                errors.add(ValidationError.INVALID_LONGITUDE)
            }
        }
        
        return ValidationResult(errors)
    }
    
    private fun isValidDate(dateString: String): Boolean {
        return try {
            LocalDate.parse(dateString)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    private fun calculateAge(birthDate: LocalDate): Int {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        return today.year - birthDate.year - if (today.dayOfYear < birthDate.dayOfYear) 1 else 0
    }
}

data class ValidationResult(
    val errors: List<ValidationError>
) {
    val isValid: Boolean get() = errors.isEmpty()
    val errorMessages: List<String> get() = errors.map { it.message }
}

enum class ValidationError(val message: String) {
    FIRST_NAME_REQUIRED("First name is required"),
    LAST_NAME_REQUIRED("Last name is required"),
    DATE_OF_BIRTH_REQUIRED("Date of birth is required"),
    INVALID_DATE_FORMAT("Invalid date format. Use YYYY-MM-DD"),
    INVALID_PHONE_NUMBER("Invalid phone number format"),
    INVALID_AGE("Age must be between 0 and 150 years"),
    FACILITY_NAME_REQUIRED("Facility name is required"),
    FACILITY_CODE_REQUIRED("Facility code is required"),
    INVALID_FACILITY_LEVEL("Facility level must be between 1 and 6"),
    INVALID_LATITUDE("Latitude must be between -90 and 90"),
    INVALID_LONGITUDE("Longitude must be between -180 and 180")
}
```

### 7. Shared Analytics and Reporting

```kotlin
// commonMain/domain/analytics/AnalyticsService.kt
import com.everybytesystems.ebscore.sdk.EBSCoreSdk
import kotlinx.datetime.*

class AnalyticsService(
    private val sdk: EBSCoreSdk
) {
    
    suspend fun generatePatientReport(
        facilityId: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): PatientReport {
        val patients = getPatients(facilityId)
        val registrations = getRegistrations(facilityId, startDate, endDate)
        val visits = getVisits(facilityId, startDate, endDate)
        
        return PatientReport(
            facilityId = facilityId,
            reportPeriod = DateRange(startDate, endDate),
            totalPatients = patients.size,
            newRegistrations = registrations.size,
            totalVisits = visits.size,
            averageAge = calculateAverageAge(patients),
            genderDistribution = calculateGenderDistribution(patients),
            ageGroups = calculateAgeGroups(patients),
            visitTrends = calculateVisitTrends(visits, startDate, endDate)
        )
    }
    
    suspend fun generateIndicatorReport(
        orgUnit: String,
        period: String
    ): IndicatorReport {
        val query = AnalyticsQuery.builder()
            .dimension("dx", getHealthIndicators())
            .dimension("pe", listOf(period))
            .dimension("ou", listOf(orgUnit))
            .build()
        
        val analyticsData = sdk.getAnalytics(query)
        val indicators = analyticsData.rows.map { row ->
            HealthIndicator(
                id = row[0],
                name = getIndicatorName(row[0]),
                value = row[3].toDoubleOrNull() ?: 0.0,
                target = getIndicatorTarget(row[0]),
                unit = getIndicatorUnit(row[0]),
                category = getIndicatorCategory(row[0])
            )
        }
        
        return IndicatorReport(
            orgUnit = orgUnit,
            period = period,
            indicators = indicators,
            overallPerformance = calculateOverallPerformance(indicators),
            recommendations = generateRecommendations(indicators)
        )
    }
    
    suspend fun generateDashboardData(
        facilityId: String
    ): DashboardData {
        val currentMonth = Clock.System.todayIn(TimeZone.currentSystemDefault())
            .let { "${it.year}${it.monthNumber.toString().padStart(2, '0')}" }
        
        val patients = getPatients(facilityId)
        val thisMonthVisits = getVisits(facilityId, 
            Clock.System.todayIn(TimeZone.currentSystemDefault()).let { 
                it.minus(DatePeriod(days = it.dayOfMonth - 1)) 
            },
            Clock.System.todayIn(TimeZone.currentSystemDefault())
        )
        
        val indicators = generateIndicatorReport(facilityId, currentMonth)
        
        return DashboardData(
            facilityId = facilityId,
            lastUpdated = Clock.System.now(),
            totalPatients = patients.size,
            thisMonthVisits = thisMonthVisits.size,
            keyIndicators = indicators.indicators.take(5),
            alerts = generateAlerts(indicators.indicators),
            trends = calculateTrends(facilityId)
        )
    }
    
    private suspend fun getPatients(facilityId: String): List<Patient> {
        // Implementation would use repository
        return emptyList()
    }
    
    private suspend fun getRegistrations(
        facilityId: String, 
        startDate: LocalDate, 
        endDate: LocalDate
    ): List<Patient> {
        // Implementation would filter patients by registration date
        return emptyList()
    }
    
    private suspend fun getVisits(
        facilityId: String, 
        startDate: LocalDate, 
        endDate: LocalDate
    ): List<Visit> {
        // Implementation would get visits from events
        return emptyList()
    }
    
    private fun calculateAverageAge(patients: List<Patient>): Double {
        if (patients.isEmpty()) return 0.0
        
        val ages = patients.mapNotNull { patient ->
            patient.dateOfBirth.toLocalDate()?.let { birthDate ->
                val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
                today.year - birthDate.year
            }
        }
        
        return if (ages.isNotEmpty()) ages.average() else 0.0
    }
    
    private fun calculateGenderDistribution(patients: List<Patient>): Map<Gender, Int> {
        return patients.groupingBy { it.gender }.eachCount()
    }
    
    private fun calculateAgeGroups(patients: List<Patient>): Map<AgeGroup, Int> {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        
        return patients.mapNotNull { patient ->
            patient.dateOfBirth.toLocalDate()?.let { birthDate ->
                val age = today.year - birthDate.year
                when {
                    age < 5 -> AgeGroup.UNDER_5
                    age < 18 -> AgeGroup.CHILD
                    age < 65 -> AgeGroup.ADULT
                    else -> AgeGroup.ELDERLY
                }
            }
        }.groupingBy { it }.eachCount()
    }
    
    private fun calculateVisitTrends(
        visits: List<Visit>, 
        startDate: LocalDate, 
        endDate: LocalDate
    ): List<TrendPoint> {
        // Group visits by week and calculate trends
        return emptyList()
    }
    
    private fun getHealthIndicators(): List<String> {
        return listOf(
            "vaccination_rate",
            "maternal_mortality",
            "child_mortality",
            "malnutrition_rate",
            "tb_treatment_success"
        )
    }
    
    private fun getIndicatorName(id: String): String {
        return when (id) {
            "vaccination_rate" -> "Vaccination Coverage Rate"
            "maternal_mortality" -> "Maternal Mortality Ratio"
            "child_mortality" -> "Under-5 Mortality Rate"
            "malnutrition_rate" -> "Malnutrition Prevalence"
            "tb_treatment_success" -> "TB Treatment Success Rate"
            else -> "Unknown Indicator"
        }
    }
    
    private fun getIndicatorTarget(id: String): Double? {
        return when (id) {
            "vaccination_rate" -> 95.0
            "maternal_mortality" -> 70.0
            "child_mortality" -> 25.0
            "malnutrition_rate" -> 5.0
            "tb_treatment_success" -> 85.0
            else -> null
        }
    }
    
    private fun getIndicatorUnit(id: String): String {
        return when (id) {
            "vaccination_rate" -> "%"
            "maternal_mortality" -> "per 100,000 live births"
            "child_mortality" -> "per 1,000 live births"
            "malnutrition_rate" -> "%"
            "tb_treatment_success" -> "%"
            else -> ""
        }
    }
    
    private fun getIndicatorCategory(id: String): IndicatorCategory {
        return when (id) {
            "vaccination_rate" -> IndicatorCategory.IMMUNIZATION
            "maternal_mortality" -> IndicatorCategory.MATERNAL_HEALTH
            "child_mortality" -> IndicatorCategory.CHILD_HEALTH
            "malnutrition_rate" -> IndicatorCategory.NUTRITION
            "tb_treatment_success" -> IndicatorCategory.DISEASE_CONTROL
            else -> IndicatorCategory.OTHER
        }
    }
    
    private fun calculateOverallPerformance(indicators: List<HealthIndicator>): PerformanceLevel {
        val performanceScores = indicators.mapNotNull { indicator ->
            indicator.target?.let { target ->
                when {
                    indicator.value >= target * 0.9 -> 3 // Good
                    indicator.value >= target * 0.7 -> 2 // Fair
                    else -> 1 // Poor
                }
            }
        }
        
        val averageScore = if (performanceScores.isNotEmpty()) {
            performanceScores.average()
        } else 0.0
        
        return when {
            averageScore >= 2.5 -> PerformanceLevel.GOOD
            averageScore >= 1.5 -> PerformanceLevel.FAIR
            else -> PerformanceLevel.POOR
        }
    }
    
    private fun generateRecommendations(indicators: List<HealthIndicator>): List<String> {
        val recommendations = mutableListOf<String>()
        
        indicators.forEach { indicator ->
            indicator.target?.let { target ->
                if (indicator.value < target * 0.8) {
                    recommendations.add(getRecommendation(indicator.id))
                }
            }
        }
        
        return recommendations
    }
    
    private fun getRecommendation(indicatorId: String): String {
        return when (indicatorId) {
            "vaccination_rate" -> "Increase vaccination outreach programs and community education"
            "maternal_mortality" -> "Improve skilled birth attendance and emergency obstetric care"
            "child_mortality" -> "Strengthen child health services and nutrition programs"
            "malnutrition_rate" -> "Implement nutrition education and supplementation programs"
            "tb_treatment_success" -> "Improve treatment adherence monitoring and patient support"
            else -> "Review and improve service delivery for this indicator"
        }
    }
    
    private fun generateAlerts(indicators: List<HealthIndicator>): List<Alert> {
        return indicators.mapNotNull { indicator ->
            indicator.target?.let { target ->
                when {
                    indicator.value < target * 0.5 -> Alert(
                        level = AlertLevel.CRITICAL,
                        message = "${indicator.name} is critically below target (${indicator.value}/${target})",
                        indicatorId = indicator.id
                    )
                    indicator.value < target * 0.7 -> Alert(
                        level = AlertLevel.WARNING,
                        message = "${indicator.name} is below target (${indicator.value}/${target})",
                        indicatorId = indicator.id
                    )
                    else -> null
                }
            }
        }
    }
    
    private suspend fun calculateTrends(facilityId: String): List<TrendData> {
        // Implementation would calculate trends over time
        return emptyList()
    }
}

// Data classes for analytics
data class PatientReport(
    val facilityId: String,
    val reportPeriod: DateRange,
    val totalPatients: Int,
    val newRegistrations: Int,
    val totalVisits: Int,
    val averageAge: Double,
    val genderDistribution: Map<Gender, Int>,
    val ageGroups: Map<AgeGroup, Int>,
    val visitTrends: List<TrendPoint>
)

data class IndicatorReport(
    val orgUnit: String,
    val period: String,
    val indicators: List<HealthIndicator>,
    val overallPerformance: PerformanceLevel,
    val recommendations: List<String>
)

data class DashboardData(
    val facilityId: String,
    val lastUpdated: Instant,
    val totalPatients: Int,
    val thisMonthVisits: Int,
    val keyIndicators: List<HealthIndicator>,
    val alerts: List<Alert>,
    val trends: List<TrendData>
)

data class HealthIndicator(
    val id: String,
    val name: String,
    val value: Double,
    val target: Double?,
    val unit: String,
    val category: IndicatorCategory
)

data class DateRange(
    val startDate: LocalDate,
    val endDate: LocalDate
)

data class Visit(
    val id: String,
    val patientId: String,
    val date: LocalDate,
    val type: String
)

data class TrendPoint(
    val date: LocalDate,
    val value: Double
)

data class Alert(
    val level: AlertLevel,
    val message: String,
    val indicatorId: String
)

data class TrendData(
    val indicatorId: String,
    val direction: TrendDirection,
    val changePercent: Double
)

enum class AgeGroup {
    UNDER_5, CHILD, ADULT, ELDERLY
}

enum class IndicatorCategory {
    IMMUNIZATION, MATERNAL_HEALTH, CHILD_HEALTH, NUTRITION, DISEASE_CONTROL, OTHER
}

enum class PerformanceLevel {
    GOOD, FAIR, POOR
}

enum class AlertLevel {
    INFO, WARNING, CRITICAL
}
```

## 🔧 Platform-Specific Implementations

### Android-specific (androidMain)

```kotlin
// androidMain/utils/PlatformUtils.kt
import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

actual fun getCurrentTimestamp(): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return formatter.format(Date())
}

actual fun formatFileSize(bytes: Long): String {
    return android.text.format.Formatter.formatFileSize(getApplicationContext(), bytes)
}

actual fun openUrl(url: String) {
    val context = getApplicationContext()
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    context.startActivity(intent)
}
```

### iOS-specific (iosMain)

```kotlin
// iosMain/utils/PlatformUtils.kt
import platform.Foundation.*

actual fun getCurrentTimestamp(): String {
    val formatter = NSDateFormatter()
    formatter.dateFormat = "yyyy-MM-dd HH:mm:ss"
    return formatter.stringFromDate(NSDate())
}

actual fun formatFileSize(bytes: Long): String {
    return NSByteCountFormatter.stringFromByteCount(bytes, NSByteCountFormatterCountStyle.NSByteCountFormatterCountStyleFile)
}

actual fun openUrl(url: String) {
    val nsUrl = NSURL.URLWithString(url)
    nsUrl?.let {
        UIApplication.sharedApplication.openURL(it)
    }
}
```

### Desktop-specific (desktopMain)

```kotlin
// desktopMain/utils/PlatformUtils.kt
import java.awt.Desktop
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*

actual fun getCurrentTimestamp(): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    return formatter.format(Date())
}

actual fun formatFileSize(bytes: Long): String {
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    var size = bytes.toDouble()
    var unitIndex = 0
    
    while (size >= 1024 && unitIndex < units.size - 1) {
        size /= 1024
        unitIndex++
    }
    
    return "%.1f %s".format(size, units[unitIndex])
}

actual fun openUrl(url: String) {
    if (Desktop.isDesktopSupported()) {
        Desktop.getDesktop().browse(URI(url))
    }
}
```

### Web-specific (jsMain)

```kotlin
// jsMain/utils/PlatformUtils.kt
import kotlinx.browser.window

actual fun getCurrentTimestamp(): String {
    val date = js("new Date()")
    return date.toISOString().replace('T', ' ').substringBefore('.')
}

actual fun formatFileSize(bytes: Long): String {
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    var size = bytes.toDouble()
    var unitIndex = 0
    
    while (size >= 1024 && unitIndex < units.size - 1) {
        size /= 1024
        unitIndex++
    }
    
    return "${size.asDynamic().toFixed(1)} ${units[unitIndex]}"
}

actual fun openUrl(url: String) {
    window.open(url, "_blank")
}
```

## 🔧 Build Configuration

### Shared Module build.gradle.kts

```kotlin
plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.android.library")
}

kotlin {
    android()
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
        }
    }
    
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }
    
    js(IR) {
        browser()
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.everybytesystems.ebscore:ebscore-sdk:1.0.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")
            }
        }
        
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
            }
        }
        
        val androidMain by getting {
            dependencies {
                implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
            }
        }
        
        val iosMain by getting
        val desktopMain by getting
        val jsMain by getting
    }
}

android {
    namespace = "com.everybytesystems.ebscore.shared"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
}
```

This comprehensive shared module demonstrates how to write platform-agnostic code using EBSCore SDK that can be shared across Android, iOS, Desktop, and Web applications, providing a solid foundation for multiplatform health data applications.
# 🔌 Integrations - External System Adapters

The integrations module provides adapters and clients for connecting to various health information systems and data sources.

## 📁 Structure

### **🏥 dhis2/** - DHIS2 Integration
- **api/** - DHIS2 Web API client implementations
- **models/** - DHIS2 data models and entities
- **sync/** - DHIS2-specific synchronization logic
- **auth/** - DHIS2 authentication mechanisms

### **🔬 fhir/** - FHIR Support
- **client/** - FHIR R4/R5 client implementation
- **resources/** - FHIR resource models and operations
- **converters/** - Data converters between FHIR and internal models

### **🏥 openmrs/** - OpenMRS Integration
- **api/** - OpenMRS REST API client
- **models/** - OpenMRS data models
- **sync/** - OpenMRS synchronization logic

### **📊 redcap/** - REDCap/KoboToolbox Import
- **importers/** - Data importers from REDCap/KoboToolbox
- **exporters/** - Data exporters to REDCap format
- **transformers/** - Data transformation utilities

## 🎯 Key Features

### **DHIS2 Integration**
- **Complete API Coverage** - All 14 DHIS2 APIs supported
- **Version Aware** - Supports DHIS2 versions 2.36-2.42
- **Offline Sync** - Intelligent offline synchronization
- **Metadata Management** - Complete metadata handling

### **FHIR Support**
- **FHIR R4/R5** - Latest FHIR specifications
- **Resource Operations** - CRUD operations on FHIR resources
- **Search Parameters** - Advanced FHIR search capabilities
- **Validation** - FHIR resource validation

### **Multi-System Support**
- **Unified API** - Consistent interface across systems
- **Data Mapping** - Automatic data transformation
- **Conflict Resolution** - Handle data conflicts intelligently

## 📚 Usage Examples

### DHIS2 Integration
```kotlin
val dhis2Client = DHIS2Client {
    baseUrl = "https://play.dhis2.org/2.42.0"
    username = "admin"
    password = "district"
}

// Get system information
val systemInfo = dhis2Client.system.getInfo()

// Fetch data elements
val dataElements = dhis2Client.metadata.getDataElements {
    fields = "id,name,valueType"
    filter = "domainType:eq:AGGREGATE"
}

// Submit data values
val dataValueSet = DataValueSet {
    dataSet = "BfMAe6Itzgt"
    period = "202401"
    orgUnit = "DiszpKrYNg8"
    dataValues = listOf(
        DataValue("fbfJHSPpUQD", "100")
    )
}
dhis2Client.data.postDataValueSet(dataValueSet)
```

### FHIR Integration
```kotlin
val fhirClient = FHIRClient {
    baseUrl = "https://hapi.fhir.org/baseR4"
    format = FHIRFormat.JSON
}

// Create a patient
val patient = Patient {
    name = listOf(HumanName {
        family = "Doe"
        given = listOf("John")
    })
    gender = AdministrativeGender.MALE
}

val createdPatient = fhirClient.create(patient)

// Search for patients
val patients = fhirClient.search<Patient> {
    where("family").matches("Doe")
    where("gender").exactly("male")
}
```

### OpenMRS Integration
```kotlin
val openMRSClient = OpenMRSClient {
    baseUrl = "https://demo.openmrs.org/openmrs"
    username = "admin"
    password = "Admin123"
}

// Get patients
val patients = openMRSClient.patients.search("John")

// Create encounter
val encounter = Encounter {
    patient = patients.first()
    encounterType = "Initial HIV Clinic Visit"
    location = "Outpatient Clinic"
}

openMRSClient.encounters.create(encounter)
```

## 🔄 Data Flow

```
External System → Integration Adapter → Internal Models → Core SDK
     ↓                    ↓                   ↓            ↓
   DHIS2              dhis2/api         data/models    core/api
   FHIR               fhir/client       data/models    core/api
   OpenMRS            openmrs/api       data/models    core/api
```

## 🎯 Integration Patterns

### **Adapter Pattern**
Each integration implements a common interface while handling system-specific details internally.

### **Data Mapping**
Automatic transformation between external system formats and internal data models.

### **Error Handling**
Consistent error handling across all integrations with system-specific error details.

### **Authentication**
Pluggable authentication mechanisms supporting various auth methods per system.

## 📊 Supported Systems

| System | Status | Coverage | Auth Methods |
|--------|--------|----------|--------------|
| **DHIS2** | ✅ Production | 100% | Basic, OAuth2, JWT |
| **FHIR** | ✅ Production | 90% | Bearer Token, OAuth2 |
| **OpenMRS** | ⚠️ Beta | 70% | Basic, Session |
| **REDCap** | ⚠️ Beta | 60% | API Token |
| **KoboToolbox** | 🚧 Development | 40% | API Token |

## 🚀 Roadmap

- [ ] **FHIR R5** - Complete FHIR R5 support
- [ ] **HL7 v2** - HL7 v2 message processing
- [ ] **IHE Profiles** - IHE integration profiles
- [ ] **Custom Adapters** - Plugin system for custom integrations
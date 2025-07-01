# 📊 Data - Modeling and Transformation

The data module provides comprehensive data modeling, validation, and transformation capabilities for health information systems.

## 📁 Structure

### **🏥 models/** - Domain models
- **health/** - Health-specific models (Patient, Encounter, Observation)
- **forms/** - Form models and field definitions
- **users/** - User and authentication models
- **organizations/** - Organization and hierarchy models

### **📋 schema/** - JSON Schema definitions
- **dhis2/** - DHIS2 metadata schemas
- **fhir/** - FHIR resource schemas
- **custom/** - Custom schema definitions

### **🔄 mapper/** - Data mappers and transformers
- **dhis2/** - DHIS2 data mappers
- **fhir/** - FHIR data mappers
- **generic/** - Generic transformation utilities

### **✅ validators/** - Schema validation and constraints
- **rules/** - Validation rules and business logic
- **engines/** - Validation engines and processors
- **reports/** - Validation reports and error handling

## 🎯 Key Features

### **Rich Domain Models**
- **Type Safety** - Comprehensive Kotlin type system
- **Immutability** - Immutable data classes with builder patterns
- **Validation** - Built-in validation and constraints
- **Serialization** - JSON/XML serialization support

### **Schema Management**
- **JSON Schema** - Complete schema definitions
- **Validation** - Schema-based validation
- **Evolution** - Schema versioning and migration
- **Documentation** - Auto-generated documentation

### **Data Transformation**
- **Bidirectional Mapping** - Convert between different formats
- **Type Conversion** - Automatic type conversion
- **Field Mapping** - Flexible field mapping rules
- **Aggregation** - Data aggregation and summarization

## 📚 Usage Examples

### Domain Models
```kotlin
// Health models
val patient = Patient {
    id = "PAT-001"
    name = PersonName {
        given = "John"
        family = "Doe"
    }
    gender = Gender.MALE
    birthDate = LocalDate.of(1990, 1, 15)
    identifiers = listOf(
        Identifier {
            system = "MRN"
            value = "12345"
        }
    )
}

val encounter = Encounter {
    id = "ENC-001"
    patient = patient.reference()
    type = EncounterType.OUTPATIENT
    status = EncounterStatus.FINISHED
    period = Period {
        start = LocalDateTime.now().minusHours(2)
        end = LocalDateTime.now()
    }
}

// Form models
val form = Form {
    id = "HIV-SCREENING"
    title = "HIV Screening Form"
    version = "1.0"
    sections = listOf(
        FormSection {
            title = "Demographics"
            fields = listOf(
                TextField {
                    name = "firstName"
                    label = "First Name"
                    required = true
                    maxLength = 50
                },
                SelectField {
                    name = "gender"
                    label = "Gender"
                    options = listOf("Male", "Female", "Other")
                    required = true
                }
            )
        }
    )
}
```

### Schema Validation
```kotlin
// Define schema
val patientSchema = JsonSchema {
    type = "object"
    properties = mapOf(
        "id" to JsonSchema { type = "string" },
        "name" to JsonSchema {
            type = "object"
            properties = mapOf(
                "given" to JsonSchema { type = "string" },
                "family" to JsonSchema { type = "string" }
            )
            required = listOf("family")
        },
        "birthDate" to JsonSchema {
            type = "string"
            format = "date"
        }
    )
    required = listOf("id", "name")
}

// Validate data
val validator = SchemaValidator(patientSchema)
val result = validator.validate(patientData)

if (result.isValid) {
    println("Patient data is valid")
} else {
    result.errors.forEach { error ->
        println("Validation error: ${error.message} at ${error.path}")
    }
}
```

### Data Mapping
```kotlin
// DHIS2 to FHIR mapping
val dhis2ToFhirMapper = DataMapper<DHIS2TrackedEntity, FHIRPatient> {
    map { source ->
        FHIRPatient {
            id = source.trackedEntity
            name = listOf(
                HumanName {
                    given = listOf(source.getAttribute("firstName"))
                    family = source.getAttribute("lastName")
                }
            )
            gender = when (source.getAttribute("gender")) {
                "Male" -> AdministrativeGender.MALE
                "Female" -> AdministrativeGender.FEMALE
                else -> AdministrativeGender.UNKNOWN
            }
            birthDate = source.getAttribute("birthDate")?.let { 
                LocalDate.parse(it) 
            }
        }
    }
}

val fhirPatient = dhis2ToFhirMapper.map(dhis2TrackedEntity)
```

### Validation Rules
```kotlin
// Custom validation rules
val patientValidationRules = ValidationRuleSet {
    rule("age-check") {
        description = "Patient must be at least 18 years old"
        condition { patient ->
            val age = Period.between(patient.birthDate, LocalDate.now()).years
            age >= 18
        }
        errorMessage = "Patient must be at least 18 years old"
    }
    
    rule("identifier-required") {
        description = "Patient must have at least one identifier"
        condition { patient ->
            patient.identifiers.isNotEmpty()
        }
        errorMessage = "Patient must have at least one identifier"
    }
}

// Apply validation
val validationResult = patientValidationRules.validate(patient)
```

## 🏗️ Model Hierarchy

### **Health Models**
```
HealthEntity
├── Patient
├── Practitioner
├── Organization
├── Encounter
├── Observation
├── Condition
├── Procedure
├── Medication
└── DiagnosticReport
```

### **Form Models**
```
FormElement
├── Form
├── FormSection
├── FormField
│   ├── TextField
│   ├── NumberField
│   ├── DateField
│   ├── SelectField
│   ├── CheckboxField
│   ├── RadioField
│   └── FileField
└── FormValidation
```

### **User Models**
```
UserEntity
├── User
├── UserProfile
├── UserRole
├── Permission
├── Organization
└── OrganizationUnit
```

## 🔄 Data Flow

```
External Data → Schema Validation → Model Creation → Business Logic → Storage
      ↓              ↓                    ↓              ↓           ↓
   JSON/XML      validators/         models/        core/logic   core/cache
   Raw Data      engines/           health/                     
```

## 📊 Validation Levels

### **Schema Validation**
- **Structure** - JSON/XML structure validation
- **Types** - Data type validation
- **Constraints** - Field constraints (length, format, etc.)

### **Business Validation**
- **Rules** - Business logic validation
- **Relationships** - Entity relationship validation
- **Consistency** - Data consistency checks

### **Integration Validation**
- **Mapping** - Data mapping validation
- **Transformation** - Transformation result validation
- **Compatibility** - Cross-system compatibility checks

## 🎯 Best Practices

### **Model Design**
- Use immutable data classes
- Implement builder patterns for complex objects
- Include comprehensive validation
- Support serialization/deserialization

### **Schema Management**
- Version schemas properly
- Document schema changes
- Provide migration paths
- Test schema compatibility

### **Data Mapping**
- Use bidirectional mappers
- Handle null values gracefully
- Validate mapping results
- Log transformation errors

## 🚀 Roadmap

- [ ] **GraphQL Schema** - GraphQL schema generation
- [ ] **OpenAPI** - OpenAPI schema integration
- [ ] **Avro Support** - Apache Avro schema support
- [ ] **Real-time Validation** - Real-time data validation
- [ ] **ML Integration** - Machine learning model integration
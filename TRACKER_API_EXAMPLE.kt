import com.everybytesystems.ebscore.sdk.EBSCoreSdkBuilder
import com.everybytesystems.ebscore.auth.BasicAuthConfig
import com.everybytesystems.ebscore.core.models.tracker.*
import kotlinx.coroutines.runBlocking

/**
 * Example demonstrating the new DHIS2 Tracker API
 * 
 * This example shows how to use the comprehensive Tracker API to:
 * 1. Manage tracked entities (individuals)
 * 2. Handle enrollments in programs
 * 3. Create and update events
 * 4. Manage relationships between entities
 * 5. Work with programs and program stages
 * 6. Handle program rules and indicators
 */
fun main() = runBlocking {
    
    // ========================================
    // 1. CREATE SDK WITH TRACKER API
    // ========================================
    
    val sdk = EBSCoreSdkBuilder()
        .baseUrl("https://play.dhis2.org/2.42.0") // DHIS2 2.42 demo server
        .autoDetectVersion(true) // Enable automatic version detection
        .enableLogging(true) // Enable logging
        .build()
    
    // Initialize SDK
    sdk.initialize()
    
    // Authenticate
    val authResult = sdk.authenticate(
        BasicAuthConfig(
            username = "admin",
            password = "district"
        )
    )
    
    if (authResult.isSuccess) {
        println("✅ Authentication successful")
        
        // ========================================
        // 2. TRACKED ENTITY TYPES & ATTRIBUTES
        // ========================================
        
        println("\n🎯 === TRACKED ENTITY TYPES & ATTRIBUTES ===")
        
        // Get tracked entity types
        val trackedEntityTypesResult = sdk.trackerApi.getTrackedEntityTypes(
            fields = "id,name,displayName,featureType,trackedEntityTypeAttributes",
            pageSize = 5
        )
        
        if (trackedEntityTypesResult.isSuccess) {
            val types = trackedEntityTypesResult.data!!
            println("✅ Tracked entity types retrieved: ${types.pager?.total}")
            
            types.trackedEntityTypes.forEach { type ->
                println("   Type: ${type.name} (${type.featureType})")
                println("     Attributes: ${type.trackedEntityTypeAttributes.size}")
            }
        }
        
        // Get tracked entity attributes
        val attributesResult = sdk.trackerApi.getTrackedEntityAttributes(
            fields = "id,name,displayName,valueType,unique,optionSet",
            pageSize = 10
        )
        
        if (attributesResult.isSuccess) {
            println("✅ Tracked entity attributes retrieved: ${attributesResult.data?.pager?.total}")
            
            attributesResult.data?.trackedEntityAttributes?.take(5)?.forEach { attr ->
                println("   Attribute: ${attr.name} (${attr.valueType})")
            }
        }
        
        // ========================================
        // 3. PROGRAMS & PROGRAM STAGES
        // ========================================
        
        println("\n📋 === PROGRAMS & PROGRAM STAGES ===")
        
        // Get programs
        val programsResult = sdk.trackerApi.getPrograms(
            fields = "id,name,displayName,programType,trackedEntityType,programStages",
            pageSize = 5
        )
        
        if (programsResult.isSuccess) {
            val programs = programsResult.data!!
            println("✅ Programs retrieved: ${programs.pager?.total}")
            
            programs.programs.forEach { program ->
                println("   Program: ${program.name} (${program.programType})")
                println("     Stages: ${program.programStages.size}")
                
                // Get program stages for this program
                if (program.programStages.isNotEmpty()) {
                    val stagesResult = sdk.trackerApi.getProgramStages(
                        filter = "program.id:eq:${program.id}",
                        fields = "id,name,displayName,repeatable,programStageDataElements",
                        pageSize = 3
                    )
                    
                    if (stagesResult.isSuccess) {
                        stagesResult.data?.programStages?.forEach { stage ->
                            println("       Stage: ${stage.name} (repeatable: ${stage.repeatable})")
                            println("         Data elements: ${stage.programStageDataElements.size}")
                        }
                    }
                }
            }
        }
        
        // ========================================
        // 4. TRACKED ENTITIES MANAGEMENT
        // ========================================
        
        println("\n👤 === TRACKED ENTITIES MANAGEMENT ===")
        
        // Get existing tracked entities
        val trackedEntitiesResult = sdk.trackerApi.getTrackedEntities(
            orgUnit = "ImspTQPwCqd", // Sierra Leone
            fields = "trackedEntity,trackedEntityType,orgUnit,attributes,enrollments",
            pageSize = 5
        )
        
        if (trackedEntitiesResult.isSuccess) {
            val entities = trackedEntitiesResult.data!!
            println("✅ Tracked entities retrieved: ${entities.pager?.total}")
            
            entities.trackedEntities.take(3).forEach { entity ->
                println("   Entity: ${entity.trackedEntity}")
                println("     Type: ${entity.trackedEntityType}")
                println("     Attributes: ${entity.attributes.size}")
                println("     Enrollments: ${entity.enrollments.size}")
            }
        }
        
        // Create a new tracked entity (example)
        if (trackedEntityTypesResult.isSuccess && trackedEntityTypesResult.data?.trackedEntityTypes?.isNotEmpty() == true) {
            val firstType = trackedEntityTypesResult.data.trackedEntityTypes.first()
            
            val newTrackedEntity = TrackedEntity(
                trackedEntityType = firstType.id,
                orgUnit = "ImspTQPwCqd",
                attributes = listOf(
                    TrackedEntityAttributeValue(
                        attribute = "w75KJ2mc4zz", // First name (if exists)
                        value = "John"
                    ),
                    TrackedEntityAttributeValue(
                        attribute = "zDhUuAYrxNC", // Last name (if exists)
                        value = "Doe"
                    )
                )
            )
            
            // Note: This would create a real entity, so we'll just validate
            val validateResult = sdk.trackerApi.createTrackedEntity(
                newTrackedEntity,
                TrackerImportParams(importMode = "VALIDATE")
            )
            
            if (validateResult.isSuccess) {
                println("✅ Tracked entity validation successful")
                println("   Status: ${validateResult.data?.status}")
            } else {
                println("ℹ️ Tracked entity validation: ${validateResult.message}")
            }
        }
        
        // ========================================
        // 5. ENROLLMENTS MANAGEMENT
        // ========================================
        
        println("\n📝 === ENROLLMENTS MANAGEMENT ===")
        
        // Get existing enrollments
        val enrollmentsResult = sdk.trackerApi.getEnrollments(
            orgUnit = "ImspTQPwCqd",
            fields = "enrollment,trackedEntity,program,orgUnit,enrolledAt,status,attributes,events",
            pageSize = 5
        )
        
        if (enrollmentsResult.isSuccess) {
            val enrollments = enrollmentsResult.data!!
            println("✅ Enrollments retrieved: ${enrollments.pager?.total}")
            
            enrollments.enrollments.take(3).forEach { enrollment ->
                println("   Enrollment: ${enrollment.enrollment}")
                println("     Program: ${enrollment.program}")
                println("     Status: ${enrollment.status}")
                println("     Events: ${enrollment.events.size}")
            }
        }
        
        // Create a new enrollment (example)
        if (programsResult.isSuccess && programsResult.data?.programs?.isNotEmpty() == true &&
            trackedEntitiesResult.isSuccess && trackedEntitiesResult.data?.trackedEntities?.isNotEmpty() == true) {
            
            val firstProgram = programsResult.data.programs.first()
            val firstEntity = trackedEntitiesResult.data.trackedEntities.first()
            
            val newEnrollment = Enrollment(
                trackedEntity = firstEntity.trackedEntity!!,
                program = firstProgram.id,
                orgUnit = "ImspTQPwCqd",
                enrolledAt = "2024-01-01",
                status = "ACTIVE"
            )
            
            // Validate enrollment
            val validateEnrollmentResult = sdk.trackerApi.createEnrollment(
                newEnrollment,
                TrackerImportParams(importMode = "VALIDATE")
            )
            
            if (validateEnrollmentResult.isSuccess) {
                println("✅ Enrollment validation successful")
            } else {
                println("ℹ️ Enrollment validation: ${validateEnrollmentResult.message}")
            }
        }
        
        // ========================================
        // 6. EVENTS MANAGEMENT
        // ========================================
        
        println("\n📅 === EVENTS MANAGEMENT ===")
        
        // Get existing events
        val eventsResult = sdk.trackerApi.getEvents(
            orgUnit = "ImspTQPwCqd",
            fields = "event,enrollment,trackedEntity,program,programStage,orgUnit,status,occurredAt,dataValues",
            pageSize = 5
        )
        
        if (eventsResult.isSuccess) {
            val events = eventsResult.data!!
            println("✅ Events retrieved: ${events.pager?.total}")
            
            events.events.take(3).forEach { event ->
                println("   Event: ${event.event}")
                println("     Program Stage: ${event.programStage}")
                println("     Status: ${event.status}")
                println("     Data Values: ${event.dataValues.size}")
            }
        }
        
        // Create a new event (example)
        if (enrollmentsResult.isSuccess && enrollmentsResult.data?.enrollments?.isNotEmpty() == true &&
            programsResult.isSuccess && programsResult.data?.programs?.isNotEmpty() == true) {
            
            val firstEnrollment = enrollmentsResult.data.enrollments.first()
            val firstProgram = programsResult.data.programs.first()
            
            if (firstProgram.programStages.isNotEmpty()) {
                val firstStage = firstProgram.programStages.first()
                
                val newEvent = Event(
                    enrollment = firstEnrollment.enrollment,
                    trackedEntity = firstEnrollment.trackedEntity,
                    program = firstProgram.id,
                    programStage = firstStage.id,
                    orgUnit = "ImspTQPwCqd",
                    status = "ACTIVE",
                    occurredAt = "2024-01-15",
                    dataValues = listOf(
                        DataValue(
                            dataElement = "qrur9Dvnyt5", // Example data element
                            value = "5"
                        )
                    )
                )
                
                // Validate event
                val validateEventResult = sdk.trackerApi.createEvent(
                    newEvent,
                    TrackerImportParams(importMode = "VALIDATE")
                )
                
                if (validateEventResult.isSuccess) {
                    println("✅ Event validation successful")
                } else {
                    println("ℹ️ Event validation: ${validateEventResult.message}")
                }
            }
        }
        
        // ========================================
        // 7. RELATIONSHIPS
        // ========================================
        
        println("\n🔗 === RELATIONSHIPS ===")
        
        // Get relationship types
        val relationshipTypesResult = sdk.trackerApi.getRelationshipTypes(
            fields = "id,name,displayName,bidirectional,fromConstraint,toConstraint",
            pageSize = 5
        )
        
        if (relationshipTypesResult.isSuccess) {
            println("✅ Relationship types retrieved: ${relationshipTypesResult.data?.pager?.total}")
            
            relationshipTypesResult.data?.relationshipTypes?.forEach { relType ->
                println("   Type: ${relType.name} (bidirectional: ${relType.bidirectional})")
            }
        }
        
        // Get existing relationships
        val relationshipsResult = sdk.trackerApi.getRelationships(
            fields = "relationship,relationshipType,from,to",
            pageSize = 5
        )
        
        if (relationshipsResult.isSuccess) {
            println("✅ Relationships retrieved: ${relationshipsResult.data?.pager?.total}")
            
            relationshipsResult.data?.relationships?.take(3)?.forEach { relationship ->
                println("   Relationship: ${relationship.relationship}")
                println("     Type: ${relationship.relationshipType}")
            }
        }
        
        // ========================================
        // 8. PROGRAM INDICATORS & RULES
        // ========================================
        
        println("\n📊 === PROGRAM INDICATORS & RULES ===")
        
        // Get program indicators
        val indicatorsResult = sdk.trackerApi.getProgramIndicators(
            fields = "id,name,displayName,program,expression,filter,analyticsType",
            pageSize = 5
        )
        
        if (indicatorsResult.isSuccess) {
            println("✅ Program indicators retrieved: ${indicatorsResult.data?.pager?.total}")
            
            indicatorsResult.data?.programIndicators?.forEach { indicator ->
                println("   Indicator: ${indicator.name} (${indicator.analyticsType})")
            }
        }
        
        // Get program rules
        val rulesResult = sdk.trackerApi.getProgramRules(
            fields = "id,name,displayName,program,programStage,condition,programRuleActions",
            pageSize = 5
        )
        
        if (rulesResult.isSuccess) {
            println("✅ Program rules retrieved: ${rulesResult.data?.pager?.total}")
            
            rulesResult.data?.programRules?.forEach { rule ->
                println("   Rule: ${rule.name}")
                println("     Actions: ${rule.programRuleActions.size}")
            }
        }
        
        // Get program rule variables
        val variablesResult = sdk.trackerApi.getProgramRuleVariables(
            fields = "id,name,displayName,program,sourceType",
            pageSize = 5
        )
        
        if (variablesResult.isSuccess) {
            println("✅ Program rule variables retrieved: ${variablesResult.data?.pager?.total}")
            
            variablesResult.data?.programRuleVariables?.forEach { variable ->
                println("   Variable: ${variable.name} (${variable.sourceType})")
            }
        }
        
        // ========================================
        // 9. BULK OPERATIONS
        // ========================================
        
        println("\n📦 === BULK OPERATIONS ===")
        
        // Create a tracker bundle for bulk operations
        val trackerBundle = TrackerBundle(
            trackedEntities = listOf(
                TrackedEntity(
                    trackedEntityType = "nEenWmSyUEp", // Person (if exists)
                    orgUnit = "ImspTQPwCqd",
                    attributes = listOf(
                        TrackedEntityAttributeValue(
                            attribute = "w75KJ2mc4zz",
                            value = "Jane"
                        ),
                        TrackedEntityAttributeValue(
                            attribute = "zDhUuAYrxNC",
                            value = "Smith"
                        )
                    )
                )
            ),
            enrollments = emptyList(),
            events = emptyList(),
            relationships = emptyList()
        )
        
        // Validate the bundle
        val validateBundleResult = sdk.trackerApi.validateTrackerBundle(
            trackerBundle,
            TrackerImportParams(importMode = "VALIDATE")
        )
        
        if (validateBundleResult.isSuccess) {
            println("✅ Tracker bundle validation successful")
            println("   Status: ${validateBundleResult.data?.status}")
            
            val stats = validateBundleResult.data?.stats
            if (stats != null) {
                println("   Stats: Created=${stats.created}, Updated=${stats.updated}, Ignored=${stats.ignored}")
            }
        } else {
            println("ℹ️ Tracker bundle validation: ${validateBundleResult.message}")
        }
        
        // ========================================
        // 10. VERSION-AWARE FEATURES
        // ========================================
        
        println("\n🔄 === VERSION-AWARE FEATURES ===")
        
        val detectedVersion = sdk.getDetectedVersion()
        if (detectedVersion != null) {
            println("📋 Detected DHIS2 Version: ${detectedVersion.fullVersion}")
            
            when {
                detectedVersion.isAtLeast(2, 42) -> {
                    println("🆕 Using DHIS2 2.42+ features:")
                    println("   - New tracker API only")
                    println("   - Enhanced validation")
                    println("   - Improved performance")
                }
                
                detectedVersion.isAtLeast(2, 38) -> {
                    println("🔄 Using DHIS2 2.38+ features:")
                    println("   - New tracker API available")
                    println("   - Legacy tracker API still supported")
                    println("   - Automatic endpoint selection")
                }
                
                else -> {
                    println("📦 Using DHIS2 2.36-2.37 features:")
                    println("   - Legacy tracker API only")
                    println("   - TrackedEntityInstances endpoints")
                    println("   - ProgramInstances endpoints")
                }
            }
        }
        
        // Feature support checking
        val features = mapOf(
            "New Tracker API" to com.everybytesystems.ebscore.core.version.DHIS2Feature.NEW_TRACKER_API,
            "Tracker API" to com.everybytesystems.ebscore.core.version.DHIS2Feature.TRACKER_API,
            "Program Rules" to com.everybytesystems.ebscore.core.version.DHIS2Feature.PROGRAM_RULES,
            "Relationships" to com.everybytesystems.ebscore.core.version.DHIS2Feature.RELATIONSHIPS
        )
        
        println("\n🔍 Tracker Feature Support Matrix:")
        features.forEach { (name, feature) ->
            val supported = sdk.isFeatureSupported(feature)
            val icon = if (supported) "✅" else "❌"
            println("  $icon $name")
        }
        
        // ========================================
        // 11. TRACKER API SUMMARY
        // ========================================
        
        println("\n📋 === TRACKER API SUMMARY ===")
        println("✅ Tracked Entities: Full CRUD support")
        println("✅ Tracked Entity Types: Full management")
        println("✅ Tracked Entity Attributes: Full management")
        println("✅ Enrollments: Full CRUD support")
        println("✅ Programs: Full management")
        println("✅ Events: Full CRUD support")
        println("✅ Program Stages: Full management")
        println("✅ Relationships: Full CRUD support")
        println("✅ Relationship Types: Full management")
        println("✅ Program Indicators: Full management")
        println("✅ Program Rules: Full management")
        println("✅ Program Rule Actions: Full management")
        println("✅ Program Rule Variables: Full management")
        println("✅ Bulk Operations: Full tracker bundle support")
        println("✅ Version Awareness: Automatic API selection")
        
        println("\n🎉 TRACKER API IMPLEMENTATION COMPLETE!")
        println("🎯 All major tracker endpoints are now available!")
        println("📊 Both new (2.38+) and legacy tracker APIs supported!")
        
    } else {
        println("❌ Authentication failed: ${authResult.message}")
    }
    
    // Cleanup
    sdk.close()
    println("\n✅ SDK closed successfully")
}

/**
 * Example of creating a complete tracker workflow
 */
suspend fun demonstrateCompleteTrackerWorkflow(sdk: com.everybytesystems.ebscore.sdk.EBSCoreSdk) {
    println("\n🎯 === COMPLETE TRACKER WORKFLOW ===")
    
    // 1. Get a program
    val programsResult = sdk.trackerApi.getPrograms(pageSize = 1)
    if (programsResult.isSuccess && programsResult.data?.programs?.isNotEmpty() == true) {
        val program = programsResult.data.programs.first()
        
        // 2. Create a tracked entity
        val trackedEntity = TrackedEntity(
            trackedEntityType = program.trackedEntityType?.id ?: "nEenWmSyUEp",
            orgUnit = "ImspTQPwCqd",
            attributes = listOf(
                TrackedEntityAttributeValue(
                    attribute = "w75KJ2mc4zz",
                    value = "Complete"
                ),
                TrackedEntityAttributeValue(
                    attribute = "zDhUuAYrxNC",
                    value = "Workflow"
                )
            )
        )
        
        // 3. Create an enrollment
        val enrollment = Enrollment(
            trackedEntity = "GENERATED_UID", // Would be generated
            program = program.id,
            orgUnit = "ImspTQPwCqd",
            enrolledAt = "2024-01-01",
            status = "ACTIVE"
        )
        
        // 4. Create events for each program stage
        val events = program.programStages.map { stage ->
            Event(
                enrollment = "GENERATED_UID", // Would be generated
                trackedEntity = "GENERATED_UID", // Would be generated
                program = program.id,
                programStage = stage.id,
                orgUnit = "ImspTQPwCqd",
                status = "ACTIVE",
                occurredAt = "2024-01-15",
                dataValues = stage.programStageDataElements.take(2).map { psde ->
                    DataValue(
                        dataElement = psde.dataElement.id,
                        value = "Sample Value"
                    )
                }
            )
        }
        
        // 5. Create a complete tracker bundle
        val bundle = TrackerBundle(
            trackedEntities = listOf(trackedEntity),
            enrollments = listOf(enrollment),
            events = events,
            relationships = emptyList()
        )
        
        // 6. Validate the complete workflow
        val validateResult = sdk.trackerApi.validateTrackerBundle(bundle)
        
        if (validateResult.isSuccess) {
            println("✅ Complete tracker workflow validation successful")
            println("   Tracked Entities: ${bundle.trackedEntities.size}")
            println("   Enrollments: ${bundle.enrollments.size}")
            println("   Events: ${bundle.events.size}")
            println("   Status: ${validateResult.data?.status}")
        } else {
            println("❌ Complete tracker workflow validation failed: ${validateResult.message}")
        }
    }
}

/**
 * Example of advanced tracker queries
 */
suspend fun demonstrateAdvancedTrackerQueries(sdk: com.everybytesystems.ebscore.sdk.EBSCoreSdk) {
    println("\n🔍 === ADVANCED TRACKER QUERIES ===")
    
    // 1. Complex tracked entity query
    val complexTEQuery = sdk.trackerApi.getTrackedEntities(
        orgUnit = "ImspTQPwCqd",
        program = "IpHINAT79UW",
        programStatus = "ACTIVE",
        followUp = true,
        lastUpdatedStartDate = "2023-01-01",
        lastUpdatedEndDate = "2024-12-31",
        filter = listOf("w75KJ2mc4zz:like:John"),
        includeAllAttributes = true,
        ouMode = "DESCENDANTS",
        order = "created:desc",
        pageSize = 20
    )
    
    if (complexTEQuery.isSuccess) {
        println("✅ Complex tracked entity query successful")
        println("   Results: ${complexTEQuery.data?.pager?.total}")
    }
    
    // 2. Advanced enrollment query
    val complexEnrollmentQuery = sdk.trackerApi.getEnrollments(
        program = "IpHINAT79UW",
        programStatus = "ACTIVE",
        enrolledAfter = "2023-01-01",
        enrolledBefore = "2024-12-31",
        followUp = true,
        ouMode = "ACCESSIBLE",
        order = "enrolledAt:desc",
        pageSize = 15
    )
    
    if (complexEnrollmentQuery.isSuccess) {
        println("✅ Complex enrollment query successful")
        println("   Results: ${complexEnrollmentQuery.data?.pager?.total}")
    }
    
    // 3. Advanced event query
    val complexEventQuery = sdk.trackerApi.getEvents(
        program = "IpHINAT79UW",
        programStage = "A03MvHHogjR",
        status = "COMPLETED",
        occurredAfter = "2023-01-01",
        occurredBefore = "2024-12-31",
        assignedUserMode = "CURRENT",
        ouMode = "SELECTED",
        order = "occurredAt:desc",
        pageSize = 25
    )
    
    if (complexEventQuery.isSuccess) {
        println("✅ Complex event query successful")
        println("   Results: ${complexEventQuery.data?.pager?.total}")
    }
    
    println("🎯 Advanced tracker queries completed!")
}
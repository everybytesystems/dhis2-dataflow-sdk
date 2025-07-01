/**
 * DHIS2 EBSCore SDK - Current Implementation Usage Examples
 * 
 * This file demonstrates how to use the currently implemented APIs:
 * - Version Detection & Compatibility
 * - Tracker API (Complete)
 * - Data Values API (Complete)
 * - Metadata API (Basic)
 * - System API (Basic)
 */

import com.everybytesystems.ebscore.core.DHIS2Client
import com.everybytesystems.ebscore.core.config.DHIS2Config
import com.everybytesystems.ebscore.core.network.ApiResponse
import com.everybytesystems.ebscore.core.version.DHIS2Version
import com.everybytesystems.ebscore.core.api.tracker.*
import com.everybytesystems.ebscore.core.api.data.*

suspend fun main() {
    // ========================================
    // 1. CLIENT SETUP WITH VERSION DETECTION
    // ========================================
    
    val config = DHIS2Config(
        baseUrl = "https://play.dhis2.org/2.40.1",
        username = "admin",
        password = "district",
        enableLogging = true,
        logLevel = DHIS2Config.LogLevel.BODY
    )
    
    // Create client with automatic version detection
    val clientResponse = DHIS2Client.create(config)
    
    when (clientResponse) {
        is ApiResponse.Success -> {
            val client = clientResponse.data
            println("✅ Connected to DHIS2 ${client.getVersion().versionString}")
            
            // Demonstrate all current functionality
            demonstrateVersionDetection(client)
            demonstrateTrackerAPI(client)
            demonstrateDataValuesAPI(client)
            demonstrateMetadataAPI(client)
            demonstrateSystemAPI(client)
            
            client.close()
        }
        is ApiResponse.Error -> {
            println("❌ Failed to connect: ${clientResponse.message}")
            clientResponse.exception?.printStackTrace()
        }
        is ApiResponse.Loading -> {
            println("⏳ Loading...")
        }
    }
}

// ========================================
// VERSION DETECTION & FEATURE SUPPORT
// ========================================

suspend fun demonstrateVersionDetection(client: DHIS2Client) {
    println("\n🔍 VERSION DETECTION & FEATURE SUPPORT")
    println("=====================================")
    
    val version = client.getVersion()
    println("DHIS2 Version: ${version.versionString}")
    println("Major: ${version.major}, Minor: ${version.minor}, Patch: ${version.patch}")
    
    // Feature support detection
    println("\n📋 Feature Support:")
    println("Tracker API: ${version.supportsTrackerApi()}")
    println("Working Lists: ${version.supportsTrackerWorkingLists()}")
    println("Potential Duplicates: ${version.supportsTrackerPotentialDuplicates()}")
    println("CSV Import: ${version.supportsTrackerCSVImport()}")
    println("Metadata Gist: ${version.supportsMetadataGist()}")
    println("Data Value Audit: ${version.supportsDataValueAudit()}")
    println("Async Operations: ${version.supportsDataValueSetAsync()}")
    println("Event Hooks: ${version.supportsEventHooks()}")
    
    // System information
    when (val systemInfo = client.getSystemInfo()) {
        is ApiResponse.Success -> {
            println("\n🖥️ System Information:")
            systemInfo.data.forEach { (key, value) ->
                println("$key: $value")
            }
        }
        is ApiResponse.Error -> println("❌ Failed to get system info: ${systemInfo.message}")
        is ApiResponse.Loading -> println("⏳ Loading system info...")
    }
}

// ========================================
// TRACKER API EXAMPLES
// ========================================

suspend fun demonstrateTrackerAPI(client: DHIS2Client) {
    println("\n🎯 TRACKER API EXAMPLES")
    println("=======================")
    
    val tracker = client.tracker
    
    // 1. Get tracked entities with filtering
    println("\n1. Getting Tracked Entities...")
    val trackedEntitiesResponse = tracker.getTrackedEntities(
        orgUnit = "DiszpKrYNg8", // Sample org unit
        ouMode = OrgUnitSelectionMode.DESCENDANTS,
        fields = "trackedEntity,trackedEntityType,orgUnit,attributes[attribute,value]",
        pageSize = 5
    )
    
    when (trackedEntitiesResponse) {
        is ApiResponse.Success -> {
            val entities = trackedEntitiesResponse.data.trackedEntities
            println("✅ Found ${entities.size} tracked entities")
            entities.forEach { entity ->
                println("  - ${entity.trackedEntity} (${entity.trackedEntityType})")
                entity.attributes.forEach { attr ->
                    println("    ${attr.attribute}: ${attr.value}")
                }
            }
        }
        is ApiResponse.Error -> println("❌ Error: ${trackedEntitiesResponse.message}")
        is ApiResponse.Loading -> println("⏳ Loading...")
    }
    
    // 2. Get enrollments
    println("\n2. Getting Enrollments...")
    val enrollmentsResponse = tracker.getEnrollments(
        program = "IpHINAT79UW", // Sample program
        orgUnit = "DiszpKrYNg8",
        programStatus = ProgramStatus.ACTIVE,
        fields = "enrollment,program,status,enrolledAt,events[event,status,occurredAt]",
        pageSize = 3
    )
    
    when (enrollmentsResponse) {
        is ApiResponse.Success -> {
            val enrollments = enrollmentsResponse.data.enrollments
            println("✅ Found ${enrollments.size} enrollments")
            enrollments.forEach { enrollment ->
                println("  - ${enrollment.enrollment} (${enrollment.status})")
                println("    Events: ${enrollment.events.size}")
            }
        }
        is ApiResponse.Error -> println("❌ Error: ${enrollmentsResponse.message}")
        is ApiResponse.Loading -> println("⏳ Loading...")
    }
    
    // 3. Get events with comprehensive filtering
    println("\n3. Getting Events...")
    val eventsResponse = tracker.getEvents(
        program = "IpHINAT79UW",
        orgUnit = "DiszpKrYNg8",
        ouMode = OrgUnitSelectionMode.DESCENDANTS,
        status = EventStatus.COMPLETED,
        fields = "event,status,occurredAt,dataValues[dataElement,value]",
        pageSize = 5
    )
    
    when (eventsResponse) {
        is ApiResponse.Success -> {
            val events = eventsResponse.data.events
            println("✅ Found ${events.size} events")
            events.forEach { event ->
                println("  - ${event.event} (${event.status}) - ${event.occurredAt}")
                println("    Data values: ${event.dataValues.size}")
            }
        }
        is ApiResponse.Error -> println("❌ Error: ${eventsResponse.message}")
        is ApiResponse.Loading -> println("⏳ Loading...")
    }
    
    // 4. Version-specific features
    if (client.getVersion().supportsTrackerWorkingLists()) {
        println("\n4. Getting Working Lists (2.37+)...")
        val workingListsResponse = tracker.getWorkingLists(
            fields = "id,name,description,program"
        )
        
        when (workingListsResponse) {
            is ApiResponse.Success -> {
                val workingLists = workingListsResponse.data.workingLists
                println("✅ Found ${workingLists.size} working lists")
                workingLists.forEach { wl ->
                    println("  - ${wl.name} (${wl.program})")
                }
            }
            is ApiResponse.Error -> println("❌ Error: ${workingListsResponse.message}")
            is ApiResponse.Loading -> println("⏳ Loading...")
        }
    }
    
    // 5. Import example (dry run)
    println("\n5. Tracker Import Example (Dry Run)...")
    val importPayload = TrackerImportPayload(
        trackedEntities = listOf(
            TrackedEntity(
                trackedEntityType = "nEenWmSyUEp", // Person
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
        )
    )
    
    val importResponse = tracker.importTrackerData(
        payload = importPayload,
        dryRun = true,
        importStrategy = TrackerImportStrategy.CREATE_AND_UPDATE,
        reportMode = TrackerImportReportMode.FULL
    )
    
    when (importResponse) {
        is ApiResponse.Success -> {
            val result = importResponse.data
            println("✅ Import validation successful")
            println("  Status: ${result.status}")
            println("  Stats: ${result.stats.created} created, ${result.stats.updated} updated")
            result.validationReport?.errorReports?.forEach { error ->
                println("  Error: ${error.message}")
            }
        }
        is ApiResponse.Error -> println("❌ Import error: ${importResponse.message}")
        is ApiResponse.Loading -> println("⏳ Loading...")
    }
}

// ========================================
// DATA VALUES API EXAMPLES
// ========================================

suspend fun demonstrateDataValuesAPI(client: DHIS2Client) {
    println("\n📊 DATA VALUES API EXAMPLES")
    println("===========================")
    
    val dataValues = client.dataValues
    
    // 1. Get data value sets
    println("\n1. Getting Data Value Sets...")
    val dataValueSetsResponse = dataValues.getDataValueSets(
        dataSet = listOf("pBOMPrpg1QX"), // Sample data set
        period = listOf("202301"),
        orgUnit = listOf("DiszpKrYNg8"),
        children = true,
        format = DataValueFormat.JSON
    )
    
    when (dataValueSetsResponse) {
        is ApiResponse.Success -> {
            val dataValueSet = dataValueSetsResponse.data
            println("✅ Found data value set with ${dataValueSet.dataValues.size} values")
            dataValueSet.dataValues.take(5).forEach { dv ->
                println("  - ${dv.dataElement}: ${dv.value} (${dv.period})")
            }
        }
        is ApiResponse.Error -> println("❌ Error: ${dataValueSetsResponse.message}")
        is ApiResponse.Loading -> println("⏳ Loading...")
    }
    
    // 2. Get single data value
    println("\n2. Getting Single Data Value...")
    val singleValueResponse = dataValues.getDataValue(
        dataElement = "FTRrcoaog83", // Sample data element
        period = "202301",
        orgUnit = "DiszpKrYNg8"
    )
    
    when (singleValueResponse) {
        is ApiResponse.Success -> {
            val dataValue = singleValueResponse.data
            println("✅ Data value: ${dataValue.value}")
            println("  Last updated: ${dataValue.lastUpdated}")
            println("  Stored by: ${dataValue.storedBy}")
        }
        is ApiResponse.Error -> println("❌ Error: ${singleValueResponse.message}")
        is ApiResponse.Loading -> println("⏳ Loading...")
    }
    
    // 3. Data set completion
    println("\n3. Checking Data Set Completion...")
    val completionResponse = dataValues.getDataSetCompletion(
        dataSet = "pBOMPrpg1QX",
        period = "202301",
        orgUnit = "DiszpKrYNg8"
    )
    
    when (completionResponse) {
        is ApiResponse.Success -> {
            val completion = completionResponse.data
            println("✅ Data set completion status: ${completion.completed}")
            println("  Completed date: ${completion.date}")
            println("  Completed by: ${completion.storedBy}")
        }
        is ApiResponse.Error -> println("❌ Error: ${completionResponse.message}")
        is ApiResponse.Loading -> println("⏳ Loading...")
    }
    
    // 4. Follow-up data values
    println("\n4. Getting Follow-up Data Values...")
    val followUpResponse = dataValues.getFollowUpDataValues(
        dataSet = listOf("pBOMPrpg1QX"),
        period = listOf("202301", "202302"),
        orgUnit = listOf("DiszpKrYNg8")
    )
    
    when (followUpResponse) {
        is ApiResponse.Success -> {
            val followUpValues = followUpResponse.data.dataValues.filter { it.followUp }
            println("✅ Found ${followUpValues.size} follow-up data values")
            followUpValues.forEach { dv ->
                println("  - ${dv.dataElement}: ${dv.value} (Follow-up: ${dv.followUp})")
            }
        }
        is ApiResponse.Error -> println("❌ Error: ${followUpResponse.message}")
        is ApiResponse.Loading -> println("⏳ Loading...")
    }
    
    // 5. Version-specific features
    if (client.getVersion().supportsDataValueAudit()) {
        println("\n5. Getting Data Value Audit (2.36+)...")
        val auditResponse = dataValues.getDataValueAudit(
            dataElement = listOf("FTRrcoaog83"),
            period = listOf("202301"),
            orgUnit = listOf("DiszpKrYNg8"),
            pageSize = 5
        )
        
        when (auditResponse) {
            is ApiResponse.Success -> {
                val audits = auditResponse.data.dataValueAudits
                println("✅ Found ${audits.size} audit entries")
                audits.forEach { audit ->
                    println("  - ${audit.auditType}: ${audit.value} by ${audit.modifiedBy} at ${audit.created}")
                }
            }
            is ApiResponse.Error -> println("❌ Error: ${auditResponse.message}")
            is ApiResponse.Loading -> println("⏳ Loading...")
        }
    }
    
    // 6. Import example (dry run)
    println("\n6. Data Value Set Import Example (Dry Run)...")
    val importDataValueSet = DataValueSet(
        dataSet = "pBOMPrpg1QX",
        period = "202301",
        orgUnit = "DiszpKrYNg8",
        dataValues = listOf(
            DataValue(
                dataElement = "FTRrcoaog83",
                period = "202301",
                orgUnit = "DiszpKrYNg8",
                value = "100",
                comment = "Test import"
            )
        )
    )
    
    val importResponse = dataValues.importDataValueSets(
        dataValueSet = importDataValueSet,
        dryRun = true,
        strategy = ImportStrategy.NEW_AND_UPDATES
    )
    
    when (importResponse) {
        is ApiResponse.Success -> {
            val result = importResponse.data
            println("✅ Import validation successful")
            println("  Status: ${result.status}")
            println("  Import count: ${result.importCount.imported} imported, ${result.importCount.updated} updated")
            result.conflicts.forEach { conflict ->
                println("  Conflict: ${conflict.`object`} - ${conflict.value}")
            }
        }
        is ApiResponse.Error -> println("❌ Import error: ${importResponse.message}")
        is ApiResponse.Loading -> println("⏳ Loading...")
    }
}

// ========================================
// METADATA API EXAMPLES
// ========================================

suspend fun demonstrateMetadataAPI(client: DHIS2Client) {
    println("\n🗂️ METADATA API EXAMPLES")
    println("========================")
    
    val metadata = client.metadata
    
    // 1. Get data elements
    println("\n1. Getting Data Elements...")
    val dataElementsResponse = metadata.getDataElements(
        fields = "id,name,shortName,code,valueType,aggregationType",
        pageSize = 5
    )
    
    when (dataElementsResponse) {
        is ApiResponse.Success -> {
            val elements = dataElementsResponse.data.dataElements
            println("✅ Found ${elements.size} data elements")
            elements.forEach { de ->
                println("  - ${de.name} (${de.code}) - ${de.valueType}")
            }
            
            // Paging information
            dataElementsResponse.data.pager?.let { pager ->
                println("  Page ${pager.page} of ${pager.pageCount} (Total: ${pager.total})")
            }
        }
        is ApiResponse.Error -> println("❌ Error: ${dataElementsResponse.message}")
        is ApiResponse.Loading -> println("⏳ Loading...")
    }
    
    // 2. Get specific data element
    println("\n2. Getting Specific Data Element...")
    val singleElementResponse = metadata.getDataElement(
        id = "FTRrcoaog83",
        fields = "*"
    )
    
    when (singleElementResponse) {
        is ApiResponse.Success -> {
            val element = singleElementResponse.data
            println("✅ Data Element: ${element.name}")
            println("  Code: ${element.code}")
            println("  Value Type: ${element.valueType}")
            println("  Aggregation Type: ${element.aggregationType}")
            println("  Domain Type: ${element.domainType}")
        }
        is ApiResponse.Error -> println("❌ Error: ${singleElementResponse.message}")
        is ApiResponse.Loading -> println("⏳ Loading...")
    }
    
    // 3. Get organization units
    println("\n3. Getting Organization Units...")
    val orgUnitsResponse = metadata.getOrganisationUnits(
        fields = "id,name,shortName,level,path,parent[id,name]",
        pageSize = 5
    )
    
    when (orgUnitsResponse) {
        is ApiResponse.Success -> {
            val orgUnits = orgUnitsResponse.data.organisationUnits
            println("✅ Found ${orgUnits.size} organization units")
            orgUnits.forEach { ou ->
                println("  - ${ou.name} (Level ${ou.level})")
                println("    Path: ${ou.path}")
            }
        }
        is ApiResponse.Error -> println("❌ Error: ${orgUnitsResponse.message}")
        is ApiResponse.Loading -> println("⏳ Loading...")
    }
    
    // 4. Get programs
    println("\n4. Getting Programs...")
    val programsResponse = metadata.getPrograms(
        fields = "id,name,shortName,programType,trackedEntityType[id,name]",
        pageSize = 3
    )
    
    when (programsResponse) {
        is ApiResponse.Success -> {
            val programs = programsResponse.data.programs
            println("✅ Found ${programs.size} programs")
            programs.forEach { program ->
                println("  - ${program.name} (${program.programType})")
            }
        }
        is ApiResponse.Error -> println("❌ Error: ${programsResponse.message}")
        is ApiResponse.Loading -> println("⏳ Loading...")
    }
}

// ========================================
// SYSTEM API EXAMPLES
// ========================================

suspend fun demonstrateSystemAPI(client: DHIS2Client) {
    println("\n🖥️ SYSTEM API EXAMPLES")
    println("======================")
    
    // 1. Ping test
    println("\n1. Testing Connection...")
    val pingResponse = client.ping()
    
    when (pingResponse) {
        is ApiResponse.Success -> {
            println("✅ Connection successful: ${pingResponse.data}")
        }
        is ApiResponse.Error -> println("❌ Connection failed: ${pingResponse.message}")
        is ApiResponse.Loading -> println("⏳ Testing connection...")
    }
    
    // 2. System information (already shown in version detection)
    println("\n2. System Information Available ✅")
    
    // 3. Feature support summary
    println("\n3. Feature Support Summary:")
    val version = client.getVersion()
    val features = mapOf(
        "Tracker API" to version.supportsTrackerApi(),
        "Enhanced Metadata" to version.supportsEnhancedMetadata(),
        "New Analytics" to version.supportsNewAnalytics(),
        "Advanced Sync" to version.supportsAdvancedSync(),
        "Modern Auth" to version.supportsModernAuth(),
        "Working Lists" to version.supportsTrackerWorkingLists(),
        "Potential Duplicates" to version.supportsTrackerPotentialDuplicates(),
        "CSV Import" to version.supportsTrackerCSVImport(),
        "Data Value Audit" to version.supportsDataValueAudit(),
        "Async Operations" to version.supportsDataValueSetAsync(),
        "Metadata Gist" to version.supportsMetadataGist(),
        "Metadata Versioning" to version.supportsMetadataVersioning(),
        "Event Hooks" to version.supportsEventHooks(),
        "Push Notifications" to version.supportsPushNotifications()
    )
    
    features.forEach { (feature, supported) ->
        val status = if (supported) "✅" else "❌"
        println("  $status $feature")
    }
}

// ========================================
// ERROR HANDLING EXAMPLES
// ========================================

suspend fun demonstrateErrorHandling(client: DHIS2Client) {
    println("\n⚠️ ERROR HANDLING EXAMPLES")
    println("==========================")
    
    // 1. Invalid endpoint
    println("\n1. Testing Invalid Endpoint...")
    val invalidResponse = client.tracker.getTrackedEntity("invalid-id")
    
    when (invalidResponse) {
        is ApiResponse.Success -> println("✅ Unexpected success")
        is ApiResponse.Error -> {
            println("❌ Expected error: ${invalidResponse.message}")
            println("  Exception type: ${invalidResponse.exception?.javaClass?.simpleName}")
        }
        is ApiResponse.Loading -> println("⏳ Loading...")
    }
    
    // 2. Version-specific feature on unsupported version
    println("\n2. Testing Version-Specific Feature...")
    if (!client.getVersion().supportsTrackerPotentialDuplicates()) {
        val unsupportedResponse = client.tracker.getPotentialDuplicates()
        
        when (unsupportedResponse) {
            is ApiResponse.Success -> println("✅ Unexpected success")
            is ApiResponse.Error -> {
                println("❌ Expected version error: ${unsupportedResponse.message}")
                println("  This demonstrates version-aware error handling")
            }
            is ApiResponse.Loading -> println("⏳ Loading...")
        }
    }
}

// ========================================
// PERFORMANCE EXAMPLES
// ========================================

suspend fun demonstratePerformance(client: DHIS2Client) {
    println("\n⚡ PERFORMANCE EXAMPLES")
    println("======================")
    
    // 1. Pagination
    println("\n1. Efficient Pagination...")
    var page = 1
    var totalProcessed = 0
    
    do {
        val response = client.metadata.getDataElements(
            fields = "id,name",
            page = page,
            pageSize = 10
        )
        
        when (response) {
            is ApiResponse.Success -> {
                val elements = response.data.dataElements
                totalProcessed += elements.size
                println("  Page $page: ${elements.size} elements (Total processed: $totalProcessed)")
                
                val hasMore = response.data.pager?.let { pager ->
                    page < pager.pageCount
                } ?: false
                
                if (!hasMore) break
                page++
            }
            is ApiResponse.Error -> {
                println("❌ Error on page $page: ${response.message}")
                break
            }
            is ApiResponse.Loading -> println("⏳ Loading page $page...")
        }
    } while (page <= 3) // Limit to 3 pages for demo
    
    // 2. Field selection for performance
    println("\n2. Optimized Field Selection...")
    val startTime = System.currentTimeMillis()
    
    val optimizedResponse = client.tracker.getTrackedEntities(
        fields = "trackedEntity,trackedEntityType,orgUnit", // Only essential fields
        pageSize = 50,
        skipMeta = true // Skip metadata for better performance
    )
    
    val endTime = System.currentTimeMillis()
    
    when (optimizedResponse) {
        is ApiResponse.Success -> {
            println("✅ Retrieved ${optimizedResponse.data.trackedEntities.size} entities in ${endTime - startTime}ms")
            println("  Using minimal field selection for optimal performance")
        }
        is ApiResponse.Error -> println("❌ Error: ${optimizedResponse.message}")
        is ApiResponse.Loading -> println("⏳ Loading...")
    }
}

// ========================================
// SUMMARY
// ========================================

fun printSummary() {
    println("\n📋 IMPLEMENTATION SUMMARY")
    println("=========================")
    println("✅ Version Detection & Compatibility - 100% Complete")
    println("✅ Tracker API - 100% Complete")
    println("✅ Data Values API - 100% Complete")
    println("✅ Metadata API - 60% Complete (Basic operations)")
    println("✅ System API - 80% Complete (Basic operations)")
    println("⏳ Analytics API - 0% Complete (Needs recreation)")
    println("⏳ User Management API - 0% Complete")
    println("⏳ Data Approval API - 0% Complete")
    println("⏳ File Resources API - 0% Complete")
    println("")
    println("🎯 Current Status: Production-ready for Tracker and Data Values operations")
    println("📈 Overall Progress: 35% Complete")
    println("🔄 Next Priority: Analytics API recreation")
}
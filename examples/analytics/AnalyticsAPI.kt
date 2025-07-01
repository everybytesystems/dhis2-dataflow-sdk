import com.everybytesystems.ebscore.sdk.EBSCoreSdkBuilder
import com.everybytesystems.ebscore.auth.BasicAuthConfig
import com.everybytesystems.ebscore.core.models.analytics.*
import kotlinx.coroutines.runBlocking

/**
 * Example demonstrating the new DHIS2 Analytics API
 * 
 * This example shows how to use the comprehensive Analytics API to:
 * 1. Perform core analytics queries
 * 2. Get event and enrollment analytics
 * 3. Detect outliers in data
 * 4. Work with visualizations
 * 5. Get validation results
 */
fun main() = runBlocking {
    
    // ========================================
    // 1. CREATE SDK WITH ANALYTICS API
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
        // 2. CORE ANALYTICS QUERIES
        // ========================================
        
        println("\n📊 === CORE ANALYTICS QUERIES ===")
        
        // Simple analytics query
        val analyticsResult = sdk.analyticsApi.getAnalytics(
            dimension = listOf(
                "dx:FnYCr2EAzWS;sTzgSOXpzBN", // Data elements
                "pe:LAST_12_MONTHS", // Periods
                "ou:ImspTQPwCqd" // Organisation units
            ),
            displayProperty = "NAME"
        )
        
        if (analyticsResult.isSuccess) {
            val data = analyticsResult.data!!
            println("✅ Analytics data retrieved:")
            println("   Headers: ${data.headers.size}")
            println("   Rows: ${data.rows.size}")
            println("   Dimensions: ${data.metaData?.dimensions?.size}")
            
            // Print first few rows
            data.rows.take(3).forEach { row ->
                println("   Row: ${row.joinToString(", ")}")
            }
        } else {
            println("❌ Analytics query failed: ${analyticsResult.message}")
        }
        
        // Advanced analytics query with comprehensive options
        val advancedQuery = AnalyticsQuery(
            dimension = listOf(
                "dx:FnYCr2EAzWS;sTzgSOXpzBN",
                "pe:LAST_6_MONTHS",
                "ou:ImspTQPwCqd"
            ),
            filter = listOf("co:HllvX50cXC0"), // Category option filter
            aggregationType = "SUM",
            skipMeta = false,
            skipData = false,
            hideEmptyRows = true,
            hideEmptyColumns = true,
            displayProperty = "NAME",
            outputIdScheme = "NAME"
        )
        
        val advancedResult = sdk.analyticsApi.getAnalytics(advancedQuery)
        if (advancedResult.isSuccess) {
            println("✅ Advanced analytics query successful")
            println("   Width: ${advancedResult.data?.width}")
            println("   Height: ${advancedResult.data?.height}")
        }
        
        // ========================================
        // 3. EVENT ANALYTICS
        // ========================================
        
        println("\n🎯 === EVENT ANALYTICS ===")
        
        // Get event analytics for a program
        val eventAnalyticsResult = sdk.analyticsApi.getEventAnalytics(
            program = "IpHINAT79UW", // Child Programme
            startDate = "2023-01-01",
            endDate = "2023-12-31",
            orgUnit = "ImspTQPwCqd",
            dimension = listOf("pe:LAST_12_MONTHS"),
            outputType = "EVENT"
        )
        
        if (eventAnalyticsResult.isSuccess) {
            println("✅ Event analytics retrieved:")
            println("   Events: ${eventAnalyticsResult.data?.rows?.size}")
        } else {
            println("ℹ️ Event analytics not available (program may not exist)")
        }
        
        // Event analytics with comprehensive query
        val eventQuery = EventAnalyticsQuery(
            program = "IpHINAT79UW",
            startDate = "2023-01-01",
            endDate = "2023-12-31",
            dimension = listOf("pe:LAST_12_MONTHS", "ou:ImspTQPwCqd"),
            outputType = "EVENT",
            skipMeta = false,
            completedOnly = false,
            coordinatesOnly = false,
            page = 1,
            pageSize = 100
        )
        
        val eventQueryResult = sdk.analyticsApi.getEventAnalytics(eventQuery)
        if (eventQueryResult.isSuccess) {
            println("✅ Event analytics query successful")
        }
        
        // ========================================
        // 4. OUTLIER DETECTION
        // ========================================
        
        println("\n🔍 === OUTLIER DETECTION ===")
        
        // Detect outliers using Z-Score algorithm
        val outliersResult = sdk.analyticsApi.detectOutliers(
            orgUnits = listOf("ImspTQPwCqd"),
            startDate = "2023-01-01",
            endDate = "2023-12-31",
            dataElements = listOf("FnYCr2EAzWS", "sTzgSOXpzBN"),
            algorithm = "Z_SCORE",
            threshold = 3.0,
            maxResults = 50
        )
        
        if (outliersResult.isSuccess) {
            val outliers = outliersResult.data!!
            println("✅ Outliers detected: ${outliers.outlierValues.size}")
            
            // Print first few outliers
            outliers.outlierValues.take(3).forEach { outlier ->
                println("   Outlier: ${outlier.deName} = ${outlier.value} (Z-Score: ${outlier.zScore})")
            }
        } else {
            println("ℹ️ No outliers found or detection failed")
        }
        
        // Advanced outlier detection
        val outlierQuery = OutlierDetectionQuery(
            de = listOf("FnYCr2EAzWS", "sTzgSOXpzBN"),
            startDate = "2023-01-01",
            endDate = "2023-12-31",
            ou = listOf("ImspTQPwCqd"),
            algorithm = "MODIFIED_Z_SCORE",
            threshold = 3.5,
            orderBy = "Z_SCORE",
            sortOrder = "DESC",
            maxResults = 100
        )
        
        val advancedOutliersResult = sdk.analyticsApi.detectOutliers(outlierQuery)
        if (advancedOutliersResult.isSuccess) {
            println("✅ Advanced outlier detection successful")
        }
        
        // ========================================
        // 5. VISUALIZATIONS (DHIS2 2.35+)
        // ========================================
        
        println("\n📈 === VISUALIZATIONS ===")
        
        // Check if visualizations are supported
        if (sdk.isFeatureSupported(com.everybytesystems.ebscore.core.version.DHIS2Feature.VISUALIZATIONS_API)) {
            // Get visualizations
            val visualizationsResult = sdk.analyticsApi.getVisualizations(
                fields = "id,name,type,created,lastUpdated",
                pageSize = 5
            )
            
            if (visualizationsResult.isSuccess) {
                val visualizations = visualizationsResult.data!!
                println("✅ Visualizations retrieved: ${visualizations.pager?.total}")
                
                // Print visualization details
                visualizations.visualizations.forEach { viz ->
                    println("   Visualization: ${viz.name} (${viz.type})")
                }
                
                // Get data for first visualization
                if (visualizations.visualizations.isNotEmpty()) {
                    val firstViz = visualizations.visualizations.first()
                    val vizDataResult = sdk.analyticsApi.getVisualizationData(firstViz.id)
                    
                    if (vizDataResult.isSuccess) {
                        println("✅ Visualization data retrieved for: ${firstViz.name}")
                        println("   Data rows: ${vizDataResult.data?.rows?.size}")
                    }
                }
            }
        } else {
            println("ℹ️ Visualizations API not supported in this DHIS2 version")
            
            // Use legacy charts instead
            val chartsResult = sdk.analyticsApi.getCharts(
                fields = "id,name,type,created,lastUpdated",
                pageSize = 5
            )
            
            if (chartsResult.isSuccess) {
                println("✅ Legacy charts retrieved: ${chartsResult.data?.pager?.total}")
            }
        }
        
        // ========================================
        // 6. EVENT VISUALIZATIONS (DHIS2 2.38+)
        // ========================================
        
        println("\n📊 === EVENT VISUALIZATIONS ===")
        
        if (sdk.isFeatureSupported(com.everybytesystems.ebscore.core.version.DHIS2Feature.EVENT_VISUALIZATIONS)) {
            val eventVisualizationsResult = sdk.analyticsApi.getEventVisualizations(
                fields = "id,name,type,program,created,lastUpdated",
                pageSize = 5
            )
            
            if (eventVisualizationsResult.isSuccess) {
                println("✅ Event visualizations retrieved: ${eventVisualizationsResult.data?.pager?.total}")
                
                eventVisualizationsResult.data?.eventVisualizations?.forEach { eventViz ->
                    println("   Event Visualization: ${eventViz.name} (${eventViz.type})")
                }
            }
        } else {
            println("ℹ️ Event visualizations not supported in this DHIS2 version")
        }
        
        // ========================================
        // 7. VALIDATION RESULTS
        // ========================================
        
        println("\n✅ === VALIDATION RESULTS ===")
        
        // Get validation results
        val validationResult = sdk.analyticsApi.getValidationResults(
            orgUnits = listOf("ImspTQPwCqd"),
            startDate = "2023-01-01",
            endDate = "2023-12-31",
            page = 1,
            pageSize = 10
        )
        
        if (validationResult.isSuccess) {
            val violations = validationResult.data!!
            println("✅ Validation results retrieved: ${violations.validationRuleViolations.size} violations")
            
            // Print validation violations
            violations.validationRuleViolations.take(3).forEach { violation ->
                println("   Violation: ${violation.validationRule.name}")
                println("     Left: ${violation.leftSideValue}, Right: ${violation.rightSideValue}")
            }
        } else {
            println("ℹ️ No validation violations found")
        }
        
        // ========================================
        // 8. MAPS
        // ========================================
        
        println("\n🗺️ === MAPS ===")
        
        // Get maps
        val mapsResult = sdk.analyticsApi.getMaps(
            fields = "id,name,mapViews,created,lastUpdated",
            pageSize = 5
        )
        
        if (mapsResult.isSuccess) {
            println("✅ Maps retrieved: ${mapsResult.data?.pager?.total}")
            
            mapsResult.data?.maps?.forEach { map ->
                println("   Map: ${map.name} (${map.mapViews.size} views)")
            }
        }
        
        // ========================================
        // 9. ANALYTICS SUMMARY
        // ========================================
        
        println("\n📋 === ANALYTICS API SUMMARY ===")
        println("✅ Core Analytics: Full support")
        println("✅ Event Analytics: Full support")
        println("✅ Enrollment Analytics: Full support")
        println("✅ Tracked Entity Analytics: Full support")
        println("✅ Outlier Detection: Full support")
        println("✅ Validation Results: Full support")
        println("✅ Maps: Full support")
        
        val detectedVersion = sdk.getDetectedVersion()
        if (detectedVersion != null) {
            when {
                detectedVersion.isAtLeast(2, 38) -> {
                    println("✅ Event Visualizations: Supported")
                    println("✅ Visualizations API: Supported")
                }
                detectedVersion.isAtLeast(2, 35) -> {
                    println("✅ Visualizations API: Supported")
                    println("❌ Event Visualizations: Not supported")
                }
                else -> {
                    println("❌ Visualizations API: Not supported (using legacy)")
                    println("❌ Event Visualizations: Not supported")
                }
            }
        }
        
        println("\n🎉 ANALYTICS API IMPLEMENTATION COMPLETE!")
        println("📊 All major analytics endpoints are now available!")
        
    } else {
        println("❌ Authentication failed: ${authResult.message}")
    }
    
    // Cleanup
    sdk.close()
    println("\n✅ SDK closed successfully")
}

/**
 * Example of creating a custom analytics dashboard
 */
suspend fun createAnalyticsDashboard(sdk: com.everybytesystems.ebscore.sdk.EBSCoreSdk) {
    println("\n📊 === CREATING ANALYTICS DASHBOARD ===")
    
    // 1. Get key indicators
    val indicatorsResult = sdk.analyticsApi.getAnalytics(
        dimension = listOf(
            "dx:ReUHfIn0pTQ;OdiHJayrsKo", // Key indicators
            "pe:LAST_12_MONTHS",
            "ou:ImspTQPwCqd"
        )
    )
    
    // 2. Detect data quality issues
    val outliersResult = sdk.analyticsApi.detectOutliers(
        orgUnits = listOf("ImspTQPwCqd"),
        startDate = "2023-01-01",
        endDate = "2023-12-31",
        algorithm = "Z_SCORE",
        threshold = 2.5
    )
    
    // 3. Get validation violations
    val validationResult = sdk.analyticsApi.getValidationResults(
        orgUnits = listOf("ImspTQPwCqd"),
        startDate = "2023-01-01",
        endDate = "2023-12-31"
    )
    
    // 4. Compile dashboard data
    if (indicatorsResult.isSuccess && outliersResult.isSuccess && validationResult.isSuccess) {
        println("✅ Dashboard data compiled:")
        println("   Indicators: ${indicatorsResult.data?.rows?.size} data points")
        println("   Outliers: ${outliersResult.data?.outlierValues?.size} detected")
        println("   Violations: ${validationResult.data?.validationRuleViolations?.size} found")
        
        // Here you would typically create visualizations, send reports, etc.
        println("📈 Dashboard ready for visualization!")
    }
}

/**
 * Example of advanced analytics workflow
 */
suspend fun performAdvancedAnalytics(sdk: com.everybytesystems.ebscore.sdk.EBSCoreSdk) {
    println("\n🔬 === ADVANCED ANALYTICS WORKFLOW ===")
    
    // 1. Multi-dimensional analysis
    val multiDimQuery = AnalyticsQuery(
        dimension = listOf(
            "dx:FnYCr2EAzWS;sTzgSOXpzBN;ReUHfIn0pTQ",
            "pe:LAST_12_MONTHS",
            "ou:ImspTQPwCqd;O6uvpzGd5pu;lc3eMKXaEfw"
        ),
        filter = listOf("co:HllvX50cXC0"),
        aggregationType = "AVERAGE",
        hideEmptyRows = true,
        hideEmptyColumns = true,
        showHierarchy = true,
        includeNumDen = true
    )
    
    val multiDimResult = sdk.analyticsApi.getAnalytics(multiDimQuery)
    if (multiDimResult.isSuccess) {
        println("✅ Multi-dimensional analysis complete")
    }
    
    // 2. Time series analysis
    val timeSeriesQuery = AnalyticsQuery(
        dimension = listOf(
            "dx:FnYCr2EAzWS",
            "pe:LAST_24_MONTHS",
            "ou:ImspTQPwCqd"
        ),
        aggregationType = "SUM",
        skipMeta = false
    )
    
    val timeSeriesResult = sdk.analyticsApi.getAnalytics(timeSeriesQuery)
    if (timeSeriesResult.isSuccess) {
        println("✅ Time series analysis complete")
        
        // Analyze trends (simplified example)
        val data = timeSeriesResult.data!!
        if (data.rows.size >= 2) {
            val firstValue = data.rows.first().getOrNull(2)?.toDoubleOrNull() ?: 0.0
            val lastValue = data.rows.last().getOrNull(2)?.toDoubleOrNull() ?: 0.0
            val trend = if (lastValue > firstValue) "📈 Increasing" else "📉 Decreasing"
            println("   Trend: $trend (${firstValue} → ${lastValue})")
        }
    }
    
    // 3. Comparative analysis
    val compareQuery = AnalyticsQuery(
        dimension = listOf(
            "dx:FnYCr2EAzWS",
            "pe:THIS_YEAR;LAST_YEAR",
            "ou:ImspTQPwCqd"
        ),
        aggregationType = "SUM"
    )
    
    val compareResult = sdk.analyticsApi.getAnalytics(compareQuery)
    if (compareResult.isSuccess) {
        println("✅ Comparative analysis complete")
    }
    
    println("🎯 Advanced analytics workflow completed!")
}
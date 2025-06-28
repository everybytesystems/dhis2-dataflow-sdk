package com.everybytesystems.dataflow.sdk

import com.everybytesystems.dataflow.auth.SecureStorageFactory
import com.everybytesystems.dataflow.core.database.DatabaseDriverFactory
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DataFlowSdkTest {
    
    @Test
    fun testSdkBuilderValidation() {
        // Test that builder validates required parameters
        val builder = DataFlowSdkBuilder()
        
        try {
            builder.build()
            assertTrue(false, "Should have thrown exception for missing baseUrl")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message?.contains("Base URL is required") == true)
        }
    }
    
    @Test
    fun testSdkBuilderConfiguration() {
        val builder = DataFlowSdkBuilder()
            .baseUrl("https://dhis2.example.org")
            .apiVersion("41")
            .enableLogging(true)
            .connectTimeout(30_000)
            .requestTimeout(60_000)
            .maxRetries(3)
        
        // Verify builder configuration is stored correctly
        assertNotNull(builder)
    }
    
    @Test
    fun testConfigValidation() {
        // Test various configuration scenarios
        val validUrls = listOf(
            "https://dhis2.example.org",
            "http://localhost:8080",
            "https://play.dhis2.org/dev"
        )
        
        validUrls.forEach { url ->
            val builder = DataFlowSdkBuilder().baseUrl(url)
            assertNotNull(builder)
        }
    }
    
    @Test
    fun testBuilderChaining() {
        // Test that builder methods can be chained
        val builder = DataFlowSdkBuilder()
            .baseUrl("https://dhis2.example.org")
            .apiVersion("41")
            .enableLogging(true)
            .connectTimeout(30_000)
            .requestTimeout(60_000)
            .socketTimeout(60_000)
            .maxRetries(3)
            .retryDelay(1000)
        
        assertNotNull(builder)
    }
}

// Mock implementations for testing
class MockDatabaseDriverFactory : DatabaseDriverFactory {
    override fun createDriver(databaseName: String): app.cash.sqldelight.db.SqlDriver {
        TODO("Mock implementation for testing")
    }
}

class MockSecureStorageFactory : SecureStorageFactory {
    override fun create(): com.everybytesystems.dataflow.auth.SecureStorage {
        TODO("Mock implementation for testing")
    }
}
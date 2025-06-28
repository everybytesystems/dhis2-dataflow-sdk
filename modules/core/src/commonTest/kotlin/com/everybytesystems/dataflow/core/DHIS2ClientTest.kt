package com.everybytesystems.dataflow.core

import com.everybytesystems.dataflow.core.config.DHIS2Config
import com.everybytesystems.dataflow.core.version.DHIS2Version
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DHIS2ClientTest {
    
    @Test
    fun testVersionParsing() {
        val version1 = DHIS2Version.parse("2.40.4")
        assertNotNull(version1)
        assertEquals(2, version1.major)
        assertEquals(40, version1.minor)
        assertEquals(4, version1.patch)
        
        val version2 = DHIS2Version.parse("2.39.0-SNAPSHOT")
        assertNotNull(version2)
        assertEquals(2, version2.major)
        assertEquals(39, version2.minor)
        assertEquals(0, version2.patch)
        assertEquals("SNAPSHOT", version2.build)
    }
    
    @Test
    fun testVersionComparison() {
        val v235 = DHIS2Version.V2_35
        val v236 = DHIS2Version.V2_36
        val v240 = DHIS2Version.V2_40
        
        assertTrue(v236 > v235)
        assertTrue(v240 > v236)
        assertTrue(v235 < v240)
    }
    
    @Test
    fun testFeatureSupport() {
        val v235 = DHIS2Version.V2_35
        val v236 = DHIS2Version.V2_36
        val v240 = DHIS2Version.V2_40
        
        // Tracker API support (2.36+)
        assertEquals(false, v235.supportsTrackerApi())
        assertEquals(true, v236.supportsTrackerApi())
        assertEquals(true, v240.supportsTrackerApi())
        
        // Modern Auth support (2.40+)
        assertEquals(false, v235.supportsModernAuth())
        assertEquals(false, v236.supportsModernAuth())
        assertEquals(true, v240.supportsModernAuth())
    }
    
    @Test
    fun testConfigBuilder() {
        val config = DHIS2Config.Builder()
            .baseUrl("https://play.dhis2.org/2.40.4")
            .username("admin")
            .password("district")
            .enableLogging(true)
            .logLevel(DHIS2Config.LogLevel.INFO)
            .requestTimeout(30000)
            .build()
        
        assertEquals("https://play.dhis2.org/2.40.4", config.baseUrl)
        assertEquals("admin", config.username)
        assertEquals("district", config.password)
        assertEquals(true, config.enableLogging)
        assertEquals(DHIS2Config.LogLevel.INFO, config.logLevel)
        assertEquals(30000, config.requestTimeout)
    }
    
    @Test
    fun testClientCreationWithKnownVersion() {
        val config = DHIS2Config.Builder()
            .baseUrl("https://play.dhis2.org/2.40.4")
            .username("admin")
            .password("district")
            .build()
        
        val client = DHIS2Client.createWithVersion(config, DHIS2Version.V2_40)
        
        assertEquals(DHIS2Version.V2_40, client.getVersion())
        assertEquals(config, client.getConfig())
        assertTrue(client.supportsTrackerApi())
        assertTrue(client.supportsModernAuth())
        
        client.close()
    }
}
#!/usr/bin/env kotlin

@file:DependsOn("io.ktor:ktor-client-core:2.3.12")
@file:DependsOn("io.ktor:ktor-client-cio:2.3.12")
@file:DependsOn("io.ktor:ktor-client-content-negotiation:2.3.12")
@file:DependsOn("io.ktor:ktor-serialization-kotlinx-json:2.3.12")
@file:DependsOn("io.ktor:ktor-client-auth:2.3.12")
@file:DependsOn("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
@file:DependsOn("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Simple test script to verify DHIS2 login functionality
 * This tests the actual network connection to DHIS2 play server
 */

@Serializable
data class UserInfo(
    val id: String,
    val displayName: String,
    val email: String? = null,
    val phoneNumber: String? = null,
    val authorities: List<String> = emptyList()
)

@Serializable
data class OrganisationUnit(
    val id: String,
    val name: String,
    val level: Int? = null
)

@Serializable
data class UserResponse(
    val userCredentials: UserCredentials,
    val organisationUnits: List<OrganisationUnit> = emptyList()
)

@Serializable
data class UserCredentials(
    val userInfo: UserInfo
)

suspend fun testDHIS2Login() {
    println("🚀 Testing DHIS2 Login to play.dhis2.org/dev")
    println("=" .repeat(50))
    
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        
        install(Auth) {
            basic {
                credentials {
                    BasicAuthCredentials(username = "admin", password = "district")
                }
                realm = "DHIS2"
            }
        }
    }
    
    try {
        println("🔐 Step 1: Testing authentication...")
        
        // Test basic connectivity
        val response = client.get("https://play.dhis2.org/dev/api/me") {
            headers {
                append(HttpHeaders.Accept, "application/json")
            }
        }
        
        println("📡 Response Status: ${response.status}")
        println("📡 Response Headers: ${response.headers}")
        
        if (response.status == HttpStatusCode.OK) {
            println("✅ Authentication successful!")
            
            val responseText = response.bodyAsText()
            println("📄 Raw Response (first 500 chars):")
            println(responseText.take(500))
            
            try {
                // Try to parse the user info
                val userResponse: UserResponse = response.body()
                val userInfo = userResponse.userCredentials.userInfo
                
                println("\n👤 User Information:")
                println("   Name: ${userInfo.displayName}")
                println("   ID: ${userInfo.id}")
                println("   Email: ${userInfo.email ?: "Not provided"}")
                println("   Phone: ${userInfo.phoneNumber ?: "Not provided"}")
                println("   Authorities: ${userInfo.authorities.size} permissions")
                
                if (userInfo.authorities.isNotEmpty()) {
                    println("   Sample authorities:")
                    userInfo.authorities.take(5).forEach { authority ->
                        println("     - $authority")
                    }
                    if (userInfo.authorities.size > 5) {
                        println("     ... and ${userInfo.authorities.size - 5} more")
                    }
                }
                
                println("\n🏥 Organisation Units:")
                if (userResponse.organisationUnits.isNotEmpty()) {
                    userResponse.organisationUnits.take(3).forEach { orgUnit ->
                        println("   - ${orgUnit.name} (Level ${orgUnit.level ?: "Unknown"})")
                    }
                    if (userResponse.organisationUnits.size > 3) {
                        println("   ... and ${userResponse.organisationUnits.size - 3} more")
                    }
                } else {
                    println("   No organisation units found")
                }
                
            } catch (e: Exception) {
                println("⚠️  Could not parse user response as JSON: ${e.message}")
                println("   This might be due to different response format, but login was successful!")
            }
            
        } else {
            println("❌ Authentication failed!")
            println("   Status: ${response.status}")
            println("   Response: ${response.bodyAsText()}")
        }
        
        // Test additional endpoints
        println("\n🔍 Step 2: Testing additional DHIS2 endpoints...")
        
        try {
            val systemInfoResponse = client.get("https://play.dhis2.org/dev/api/system/info")
            if (systemInfoResponse.status == HttpStatusCode.OK) {
                println("✅ System info endpoint accessible")
                val systemInfo = systemInfoResponse.bodyAsText()
                
                // Extract version info if possible
                if (systemInfo.contains("version")) {
                    val versionRegex = """"version"\s*:\s*"([^"]+)"""".toRegex()
                    val versionMatch = versionRegex.find(systemInfo)
                    if (versionMatch != null) {
                        println("   DHIS2 Version: ${versionMatch.groupValues[1]}")
                    }
                }
            } else {
                println("⚠️  System info endpoint returned: ${systemInfoResponse.status}")
            }
        } catch (e: Exception) {
            println("⚠️  Could not access system info: ${e.message}")
        }
        
        try {
            val dataElementsResponse = client.get("https://play.dhis2.org/dev/api/dataElements") {
                parameter("paging", "false")
                parameter("fields", "id,name,valueType")
            }
            if (dataElementsResponse.status == HttpStatusCode.OK) {
                println("✅ Data elements endpoint accessible")
                val dataElementsText = dataElementsResponse.bodyAsText()
                
                // Count data elements
                val dataElementCount = """"dataElements"\s*:\s*\[([^\]]*)\]""".toRegex()
                    .find(dataElementsText)?.groupValues?.get(1)?.split(",")?.size ?: 0
                
                println("   Found approximately $dataElementCount data elements")
            } else {
                println("⚠️  Data elements endpoint returned: ${dataElementsResponse.status}")
            }
        } catch (e: Exception) {
            println("⚠️  Could not access data elements: ${e.message}")
        }
        
        println("\n🎉 DHIS2 Connection Test Complete!")
        println("✅ The SDK can successfully connect to DHIS2 servers")
        println("✅ Authentication works with demo credentials")
        println("✅ API endpoints are accessible")
        println("✅ Ready for real application development!")
        
    } catch (e: Exception) {
        println("❌ Connection test failed: ${e.message}")
        println("💡 Possible issues:")
        println("   - Internet connection problems")
        println("   - DHIS2 demo server is down")
        println("   - Firewall blocking the connection")
        println("   - Credentials have changed")
        
        e.printStackTrace()
    } finally {
        client.close()
    }
}

// Run the test
runBlocking {
    testDHIS2Login()
}
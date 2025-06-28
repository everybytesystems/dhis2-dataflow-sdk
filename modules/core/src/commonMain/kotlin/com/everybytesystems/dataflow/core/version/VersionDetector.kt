package com.everybytesystems.dataflow.core.version

import com.everybytesystems.dataflow.core.config.DHIS2Config
import com.everybytesystems.dataflow.core.network.ApiResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Detects DHIS2 version from system info endpoint
 */
class VersionDetector(private val httpClient: HttpClient) {
    
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
    
    suspend fun detectVersion(config: DHIS2Config): ApiResponse<DHIS2Version> {
        return try {
            val response = httpClient.get {
                url("${config.baseUrl}/api/system/info")
                headers {
                    append(HttpHeaders.Accept, "application/json")
                    append(HttpHeaders.UserAgent, "DHIS2-DataFlow-SDK/1.0")
                }
                
                // Add authentication if available
                config.username?.let { username ->
                    config.password?.let { password ->
                        basicAuth(username, password)
                    }
                }
                
                config.bearerToken?.let { token ->
                    bearerAuth(token)
                }
                
                config.apiKey?.let { apiKey ->
                    headers {
                        append("X-API-Key", apiKey)
                    }
                }
            }
            
            when (response.status) {
                HttpStatusCode.OK -> {
                    val systemInfo = response.body<SystemInfo>()
                    val version = DHIS2Version.parse(systemInfo.version)
                    
                    if (version != null) {
                        ApiResponse.Success(version)
                    } else {
                        ApiResponse.Error(RuntimeException("Unable to parse version: ${systemInfo.version}"))
                    }
                }
                HttpStatusCode.Unauthorized -> {
                    ApiResponse.Error(RuntimeException("Authentication failed. Please check your credentials."))
                }
                HttpStatusCode.Forbidden -> {
                    ApiResponse.Error(RuntimeException("Access forbidden. Please check your permissions."))
                }
                HttpStatusCode.NotFound -> {
                    ApiResponse.Error(RuntimeException("DHIS2 instance not found. Please check the base URL."))
                }
                else -> {
                    ApiResponse.Error(RuntimeException("Failed to detect version. HTTP ${response.status.value}: ${response.status.description}"))
                }
            }
        } catch (e: Exception) {
            ApiResponse.Error(e, "Network error during version detection: ${e.message}")
        }
    }
    
    @Serializable
    private data class SystemInfo(
        val version: String,
        val revision: String? = null,
        val buildTime: String? = null,
        val serverDate: String? = null,
        val instanceBaseUrl: String? = null
    )
}
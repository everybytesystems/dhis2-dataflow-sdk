package com.everybytesystems.dataflow.core.api.base

import com.everybytesystems.dataflow.core.config.DHIS2Config
import com.everybytesystems.dataflow.core.network.ApiResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

/**
 * Base API class with common HTTP operations
 */
abstract class BaseApi(
    protected val httpClient: HttpClient,
    protected val config: DHIS2Config
) {
    
    protected val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = false
    }
    
    protected suspend inline fun <reified T> get(
        endpoint: String,
        params: Map<String, String> = emptyMap()
    ): ApiResponse<T> {
        return executeRequest {
            httpClient.get {
                url("${config.baseUrl}/api/$endpoint")
                params.forEach { (key, value) ->
                    parameter(key, value)
                }
            }
        }
    }
    
    protected suspend inline fun <reified T> post(
        endpoint: String,
        body: Any? = null,
        params: Map<String, String> = emptyMap()
    ): ApiResponse<T> {
        return executeRequest {
            httpClient.post {
                url("${config.baseUrl}/api/$endpoint")
                params.forEach { (key, value) ->
                    parameter(key, value)
                }
                body?.let {
                    contentType(ContentType.Application.Json)
                    setBody(it)
                }
            }
        }
    }
    
    protected suspend inline fun <reified T> put(
        endpoint: String,
        body: Any? = null,
        params: Map<String, String> = emptyMap()
    ): ApiResponse<T> {
        return executeRequest {
            httpClient.put {
                url("${config.baseUrl}/api/$endpoint")
                params.forEach { (key, value) ->
                    parameter(key, value)
                }
                body?.let {
                    contentType(ContentType.Application.Json)
                    setBody(it)
                }
            }
        }
    }
    
    protected suspend inline fun <reified T> delete(
        endpoint: String,
        params: Map<String, String> = emptyMap()
    ): ApiResponse<T> {
        return executeRequest {
            httpClient.delete {
                url("${config.baseUrl}/api/$endpoint")
                params.forEach { (key, value) ->
                    parameter(key, value)
                }
            }
        }
    }
    
    protected suspend inline fun <reified T, reified R> deleteWithBody(
        endpoint: String,
        body: R,
        params: Map<String, String> = emptyMap()
    ): ApiResponse<T> {
        return executeRequest {
            httpClient.delete {
                url("${config.baseUrl}/api/$endpoint")
                contentType(ContentType.Application.Json)
                setBody(body)
                params.forEach { (key, value) ->
                    parameter(key, value)
                }
            }
        }
    }
    
    protected suspend inline fun <reified T> executeRequest(
        crossinline request: suspend () -> io.ktor.client.statement.HttpResponse
    ): ApiResponse<T> {
        return try {
            val response = request()
            
            when (response.status) {
                HttpStatusCode.OK, HttpStatusCode.Created -> {
                    val body = response.body<T>()
                    ApiResponse.Success(body)
                }
                HttpStatusCode.NoContent -> {
                    @Suppress("UNCHECKED_CAST")
                    ApiResponse.Success(Unit as T)
                }
                HttpStatusCode.Unauthorized -> {
                    ApiResponse.Error(RuntimeException("Authentication failed"))
                }
                HttpStatusCode.Forbidden -> {
                    ApiResponse.Error(RuntimeException("Access forbidden"))
                }
                HttpStatusCode.NotFound -> {
                    ApiResponse.Error(RuntimeException("Resource not found"))
                }
                HttpStatusCode.BadRequest -> {
                    val errorBody = try {
                        response.body<String>()
                    } catch (e: Exception) {
                        "Bad request"
                    }
                    ApiResponse.Error(RuntimeException("Bad request: $errorBody"))
                }
                else -> {
                    ApiResponse.Error(RuntimeException("HTTP ${response.status.value}: ${response.status.description}"))
                }
            }
        } catch (e: Exception) {
            ApiResponse.Error(e, "Network error: ${e.message}")
        }
    }
}

// Platform-specific base64 encoding
expect fun BaseApi.encodeBase64(input: String): String
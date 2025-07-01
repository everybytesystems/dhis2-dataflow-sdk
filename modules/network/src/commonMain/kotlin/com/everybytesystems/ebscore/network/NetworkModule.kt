package com.everybytesystems.ebscore.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * EBSCore Network Module
 * Provides HTTP client configuration and network utilities
 */

class NetworkConfig(
    val baseUrl: String,
    val timeout: Long = 30_000L,
    val enableLogging: Boolean = true,
    val retryAttempts: Int = 3
)

class EBSCoreHttpClient(private val config: NetworkConfig) {
    
    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = false
            })
        }
        
        install(HttpTimeout) {
            requestTimeoutMillis = config.timeout
            connectTimeoutMillis = config.timeout
            socketTimeoutMillis = config.timeout
        }
        
        if (config.enableLogging) {
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.INFO
            }
        }
        
        install(HttpRequestRetry) {
            retryOnServerErrors(maxRetries = config.retryAttempts)
            exponentialDelay()
        }
        
        defaultRequest {
            url(config.baseUrl)
        }
    }
    
    fun configureAuth(token: String) {
        client.config {
            install(Auth) {
                bearer {
                    loadTokens {
                        BearerTokens(token, token)
                    }
                }
            }
        }
    }
    
    fun configureBasicAuth(username: String, password: String) {
        client.config {
            install(Auth) {
                basic {
                    credentials {
                        BasicAuthCredentials(username, password)
                    }
                }
            }
        }
    }
}

// Network Response Wrapper
sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val exception: Throwable) : NetworkResult<Nothing>()
    data class Loading(val message: String = "Loading...") : NetworkResult<Nothing>()
}

// Extension functions for common network operations
suspend inline fun <reified T> HttpClient.safeGet(
    urlString: String,
    crossinline onError: (Throwable) -> Unit = {}
): NetworkResult<T> {
    return try {
        val response = get(urlString)
        NetworkResult.Success(response.body<T>())
    } catch (e: Exception) {
        onError(e)
        NetworkResult.Error(e)
    }
}

suspend inline fun <reified T, reified R> HttpClient.safePost(
    urlString: String,
    body: T,
    crossinline onError: (Throwable) -> Unit = {}
): NetworkResult<R> {
    return try {
        val response = post(urlString) {
            setBody(body)
        }
        NetworkResult.Success(response.body<R>())
    } catch (e: Exception) {
        onError(e)
        NetworkResult.Error(e)
    }
}
package com.everybytesystems.ebscore.core.api

import com.everybytesystems.ebscore.core.auth.AuthProvider
import com.everybytesystems.ebscore.core.cache.CacheStrategy
import com.everybytesystems.ebscore.core.config.EBSCoreConfig
import com.everybytesystems.ebscore.core.error.EBSCoreException
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

/**
 * Main EBSCore API client providing HTTP operations and configuration
 */
class EBSCoreClient(
    private val config: EBSCoreConfig,
    private val authProvider: AuthProvider? = null,
    private val cacheStrategy: CacheStrategy? = null
) {
    
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = false
            })
        }
        
        install(Logging) {
            logger = Logger.DEFAULT
            level = if (config.enableLogging) LogLevel.INFO else LogLevel.NONE
        }
        
        install(HttpTimeout) {
            requestTimeoutMillis = config.timeoutMillis
            connectTimeoutMillis = config.connectTimeoutMillis
        }
        
        authProvider?.let { provider ->
            install(Auth) {
                bearer {
                    loadTokens {
                        BearerTokens(
                            accessToken = provider.getAccessToken(),
                            refreshToken = provider.getRefreshToken()
                        )
                    }
                    refreshTokens {
                        val newTokens = provider.refreshTokens()
                        BearerTokens(
                            accessToken = newTokens.accessToken,
                            refreshToken = newTokens.refreshToken
                        )
                    }
                }
            }
        }
        
        defaultRequest {
            url(config.baseUrl)
            header("User-Agent", config.userAgent)
            contentType(ContentType.Application.Json)
        }
    }
    
    /**
     * Perform GET request
     */
    suspend inline fun <reified T> get(
        path: String,
        parameters: Map<String, String> = emptyMap()
    ): ApiResult<T> {
        return try {
            val response = httpClient.get(path) {
                parameters.forEach { (key, value) ->
                    parameter(key, value)
                }
            }
            
            if (response.status.isSuccess()) {
                val data = response.body<T>()
                ApiResult.Success(data)
            } else {
                ApiResult.Error(
                    EBSCoreException.HttpException(
                        response.status.value,
                        response.status.description
                    )
                )
            }
        } catch (e: Exception) {
            ApiResult.Error(EBSCoreException.NetworkException(e.message ?: "Network error"))
        }
    }
    
    /**
     * Perform POST request
     */
    suspend inline fun <reified T, reified R> post(
        path: String,
        body: T
    ): ApiResult<R> {
        return try {
            val response = httpClient.post(path) {
                setBody(body)
            }
            
            if (response.status.isSuccess()) {
                val data = response.body<R>()
                ApiResult.Success(data)
            } else {
                ApiResult.Error(
                    EBSCoreException.HttpException(
                        response.status.value,
                        response.status.description
                    )
                )
            }
        } catch (e: Exception) {
            ApiResult.Error(EBSCoreException.NetworkException(e.message ?: "Network error"))
        }
    }
    
    /**
     * Perform PUT request
     */
    suspend inline fun <reified T, reified R> put(
        path: String,
        body: T
    ): ApiResult<R> {
        return try {
            val response = httpClient.put(path) {
                setBody(body)
            }
            
            if (response.status.isSuccess()) {
                val data = response.body<R>()
                ApiResult.Success(data)
            } else {
                ApiResult.Error(
                    EBSCoreException.HttpException(
                        response.status.value,
                        response.status.description
                    )
                )
            }
        } catch (e: Exception) {
            ApiResult.Error(EBSCoreException.NetworkException(e.message ?: "Network error"))
        }
    }
    
    /**
     * Perform DELETE request
     */
    suspend fun delete(path: String): ApiResult<Unit> {
        return try {
            val response = httpClient.delete(path)
            
            if (response.status.isSuccess()) {
                ApiResult.Success(Unit)
            } else {
                ApiResult.Error(
                    EBSCoreException.HttpException(
                        response.status.value,
                        response.status.description
                    )
                )
            }
        } catch (e: Exception) {
            ApiResult.Error(EBSCoreException.NetworkException(e.message ?: "Network error"))
        }
    }
    
    /**
     * Stream data with Flow
     */
    fun <T> stream(request: suspend () -> ApiResult<T>): Flow<ApiResult<T>> = flow {
        emit(request())
    }
    
    /**
     * Close the client and release resources
     */
    fun close() {
        httpClient.close()
    }
}

/**
 * API result wrapper
 */
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val exception: EBSCoreException) : ApiResult<Nothing>()
    data object Loading : ApiResult<Nothing>()
    
    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    val isLoading: Boolean get() = this is Loading
    
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }
    
    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Error -> throw exception
        is Loading -> throw IllegalStateException("Result is still loading")
    }
}
package com.everybytesystems.ebscore.core.error

import kotlinx.serialization.Serializable

/**
 * Base exception class for all EBSCore SDK errors
 */
sealed class EBSCoreException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {
    
    /**
     * Network-related exceptions
     */
    class NetworkException(
        message: String,
        cause: Throwable? = null
    ) : EBSCoreException("Network error: $message", cause)
    
    /**
     * HTTP-related exceptions
     */
    class HttpException(
        val statusCode: Int,
        val statusMessage: String,
        cause: Throwable? = null
    ) : EBSCoreException("HTTP $statusCode: $statusMessage", cause) {
        
        val isClientError: Boolean get() = statusCode in 400..499
        val isServerError: Boolean get() = statusCode in 500..599
        val isUnauthorized: Boolean get() = statusCode == 401
        val isForbidden: Boolean get() = statusCode == 403
        val isNotFound: Boolean get() = statusCode == 404
        val isConflict: Boolean get() = statusCode == 409
        val isTooManyRequests: Boolean get() = statusCode == 429
    }
    
    /**
     * Authentication-related exceptions
     */
    sealed class AuthException(
        message: String,
        cause: Throwable? = null
    ) : EBSCoreException("Authentication error: $message", cause) {
        
        class InvalidCredentials(
            message: String = "Invalid username or password"
        ) : AuthException(message)
        
        class TokenExpired(
            message: String = "Access token has expired"
        ) : AuthException(message)
        
        class TokenInvalid(
            message: String = "Access token is invalid"
        ) : AuthException(message)
        
        class RefreshFailed(
            message: String = "Failed to refresh access token"
        ) : AuthException(message)
        
        class MissingCredentials(
            message: String = "No credentials provided"
        ) : AuthException(message)
    }
    
    /**
     * Configuration-related exceptions
     */
    sealed class ConfigException(
        message: String,
        cause: Throwable? = null
    ) : EBSCoreException("Configuration error: $message", cause) {
        
        class InvalidUrl(
            url: String
        ) : ConfigException("Invalid URL: $url")
        
        class MissingConfiguration(
            key: String
        ) : ConfigException("Missing required configuration: $key")
        
        class InvalidConfiguration(
            key: String,
            value: String
        ) : ConfigException("Invalid configuration value for $key: $value")
    }
    
    /**
     * Data-related exceptions
     */
    sealed class DataException(
        message: String,
        cause: Throwable? = null
    ) : EBSCoreException("Data error: $message", cause) {
        
        class ValidationFailed(
            val errors: List<ValidationError>
        ) : DataException("Validation failed: ${errors.joinToString(", ") { it.message }}")
        
        class SerializationFailed(
            message: String,
            cause: Throwable? = null
        ) : DataException("Serialization failed: $message", cause)
        
        class DeserializationFailed(
            message: String,
            cause: Throwable? = null
        ) : DataException("Deserialization failed: $message", cause)
        
        class MappingFailed(
            from: String,
            to: String,
            cause: Throwable? = null
        ) : DataException("Failed to map from $from to $to", cause)
    }
    
    /**
     * Sync-related exceptions
     */
    sealed class SyncException(
        message: String,
        cause: Throwable? = null
    ) : EBSCoreException("Sync error: $message", cause) {
        
        class ConflictResolutionFailed(
            message: String
        ) : SyncException("Conflict resolution failed: $message")
        
        class SyncJobFailed(
            jobId: String,
            message: String
        ) : SyncException("Sync job $jobId failed: $message")
        
        class OfflineDataCorrupted(
            message: String
        ) : SyncException("Offline data corrupted: $message")
    }
    
    /**
     * Integration-specific exceptions
     */
    sealed class IntegrationException(
        val system: String,
        message: String,
        cause: Throwable? = null
    ) : EBSCoreException("$system integration error: $message", cause) {
        
        class DHIS2Exception(
            message: String,
            cause: Throwable? = null
        ) : IntegrationException("DHIS2", message, cause)
        
        class FHIRException(
            message: String,
            cause: Throwable? = null
        ) : IntegrationException("FHIR", message, cause)
        
        class OpenMRSException(
            message: String,
            cause: Throwable? = null
        ) : IntegrationException("OpenMRS", message, cause)
    }
    
    /**
     * Cache-related exceptions
     */
    sealed class CacheException(
        message: String,
        cause: Throwable? = null
    ) : EBSCoreException("Cache error: $message", cause) {
        
        class CacheWriteFailed(
            key: String,
            cause: Throwable? = null
        ) : CacheException("Failed to write cache entry: $key", cause)
        
        class CacheReadFailed(
            key: String,
            cause: Throwable? = null
        ) : CacheException("Failed to read cache entry: $key", cause)
        
        class CacheCorrupted(
            message: String
        ) : CacheException("Cache corrupted: $message")
    }
    
    /**
     * Unknown or unexpected exceptions
     */
    class UnknownException(
        message: String,
        cause: Throwable? = null
    ) : EBSCoreException("Unknown error: $message", cause)
}

/**
 * Validation error details
 */
@Serializable
data class ValidationError(
    val field: String,
    val message: String,
    val code: String? = null,
    val value: String? = null
)

/**
 * Error recovery strategy
 */
sealed class ErrorRecoveryStrategy {
    object Retry : ErrorRecoveryStrategy()
    object Ignore : ErrorRecoveryStrategy()
    object Fail : ErrorRecoveryStrategy()
    data class Custom(val action: suspend () -> Unit) : ErrorRecoveryStrategy()
}

/**
 * Error handler interface
 */
interface ErrorHandler {
    suspend fun handle(exception: EBSCoreException): ErrorRecoveryStrategy
}

/**
 * Default error handler implementation
 */
class DefaultErrorHandler : ErrorHandler {
    override suspend fun handle(exception: EBSCoreException): ErrorRecoveryStrategy {
        return when (exception) {
            is EBSCoreException.NetworkException -> ErrorRecoveryStrategy.Retry
            is EBSCoreException.HttpException -> when {
                exception.isTooManyRequests -> ErrorRecoveryStrategy.Retry
                exception.isServerError -> ErrorRecoveryStrategy.Retry
                exception.isClientError -> ErrorRecoveryStrategy.Fail
                else -> ErrorRecoveryStrategy.Fail
            }
            is EBSCoreException.AuthException.TokenExpired -> ErrorRecoveryStrategy.Retry
            is EBSCoreException.CacheException -> ErrorRecoveryStrategy.Ignore
            else -> ErrorRecoveryStrategy.Fail
        }
    }
}

/**
 * Extension functions for exception handling
 */
fun Throwable.toEBSCoreException(): EBSCoreException {
    return when (this) {
        is EBSCoreException -> this
        else -> EBSCoreException.UnknownException(
            message ?: "Unknown error occurred",
            this
        )
    }
}

/**
 * Result wrapper with error handling
 */
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Failure(val exception: EBSCoreException) : Result<Nothing>()
    
    val isSuccess: Boolean get() = this is Success
    val isFailure: Boolean get() = this is Failure
    
    fun getOrNull(): T? = when (this) {
        is Success -> data
        is Failure -> null
    }
    
    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Failure -> throw exception
    }
    
    fun getOrElse(defaultValue: T): T = when (this) {
        is Success -> data
        is Failure -> defaultValue
    }
    
    inline fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> try {
            Success(transform(data))
        } catch (e: Exception) {
            Failure(e.toEBSCoreException())
        }
        is Failure -> this
    }
    
    inline fun <R> flatMap(transform: (T) -> Result<R>): Result<R> = when (this) {
        is Success -> try {
            transform(data)
        } catch (e: Exception) {
            Failure(e.toEBSCoreException())
        }
        is Failure -> this
    }
    
    inline fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) action(data)
        return this
    }
    
    inline fun onFailure(action: (EBSCoreException) -> Unit): Result<T> {
        if (this is Failure) action(exception)
        return this
    }
}

/**
 * Helper function to create successful result
 */
fun <T> Result.Companion.success(data: T): Result<T> = Result.Success(data)

/**
 * Helper function to create failure result
 */
fun <T> Result.Companion.failure(exception: EBSCoreException): Result<T> = Result.Failure(exception)

/**
 * Helper function to wrap exceptions
 */
inline fun <T> runCatching(block: () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (e: Exception) {
        Result.failure(e.toEBSCoreException())
    }
}
package com.everybytesystems.ebscore.core.network

import kotlinx.serialization.Serializable

/**
 * Sealed class representing API response states
 */
sealed class ApiResponse<out T> {
    data class Success<T>(val data: T) : ApiResponse<T>()
    data class Error(val exception: Throwable, val message: String? = null) : ApiResponse<Nothing>()
    data object Loading : ApiResponse<Nothing>()
    
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
        is Loading -> throw IllegalStateException("Response is still loading")
    }
    
    inline fun <R> map(transform: (T) -> R): ApiResponse<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
        is Loading -> this
    }
    
    inline fun onSuccess(action: (T) -> Unit): ApiResponse<T> {
        if (this is Success) action(data)
        return this
    }
    
    inline fun onError(action: (Throwable, String?) -> Unit): ApiResponse<T> {
        if (this is Error) action(exception, message)
        return this
    }
    
    companion object {
        fun <T> success(data: T): ApiResponse<T> = Success(data)
        fun error(exception: Throwable, message: String? = null): ApiResponse<Nothing> = Error(exception, message)
        fun loading(): ApiResponse<Nothing> = Loading
    }
}

/**
 * Standard DHIS2 API response wrapper
 */
@Serializable
data class DHIS2Response<T>(
    val httpStatus: String? = null,
    val httpStatusCode: Int? = null,
    val status: String? = null,
    val message: String? = null,
    val response: T? = null
)

/**
 * DHIS2 API error response
 */
@Serializable
data class DHIS2ErrorResponse(
    val httpStatus: String? = null,
    val httpStatusCode: Int? = null,
    val status: String? = null,
    val message: String? = null,
    val devMessage: String? = null,
    val moreInfo: String? = null,
    val errorCode: String? = null
)

/**
 * Paging information for paginated responses
 */
@Serializable
data class Pager(
    val page: Int = 1,
    val pageCount: Int = 1,
    val pageSize: Int = 50,
    val total: Int = 0,
    val nextPage: String? = null,
    val prevPage: String? = null
)

/**
 * Paginated response wrapper
 */
@Serializable
data class PaginatedResponse<T>(
    val pager: Pager? = null,
    val data: List<T> = emptyList()
)
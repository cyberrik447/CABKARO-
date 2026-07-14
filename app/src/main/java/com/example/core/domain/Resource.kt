package com.example.core.domain

/**
 * A generic class that holds a value with its loading status.
 * Used for wrapping remote or local database transactions cleanly.
 */
sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val exception: Throwable, val message: String? = exception.localizedMessage) : Resource<Nothing>()
    object Loading : Resource<Nothing>()
}

/**
 * Helper to convert standard results into the Resource class.
 */
inline fun <T> runCatchingResource(block: () -> T): Resource<T> {
    return try {
        Resource.Success(block())
    } catch (e: Throwable) {
        Resource.Error(e)
    }
}

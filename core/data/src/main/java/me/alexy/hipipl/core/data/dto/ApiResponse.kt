package me.alexy.hipipl.core.data.dto

import kotlinx.serialization.Serializable
import me.alexy.hipipl.core.domain.DataError
import me.alexy.hipipl.core.domain.Result

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: ApiError? = null
)

@Serializable
data class ApiError(
    val message: String? = null,
    val code: String? = null
)

/**
 * Unwraps the API v1 envelope response into a Result type.
 * Maps success + data to Result.Success, otherwise to Result.Error.
 */
fun <T> ApiResponse<T>.unwrap(): Result<T, DataError.Network> {
    return if (success && data != null) {
        Result.Success(data)
    } else {
        // Map error codes/messages to appropriate DataError types
        val dataError = when (error?.code) {
            "UNAUTHORIZED", "401" -> DataError.Network.UNAUTHORIZED
            "FORBIDDEN", "403" -> DataError.Network.FORBIDDEN
            "NOT_FOUND", "404" -> DataError.Network.NOT_FOUND
            "BAD_REQUEST", "400" -> DataError.Network.BAD_REQUEST
            "TIMEOUT", "408" -> DataError.Network.REQUEST_TIMEOUT
            "CONFLICT", "409" -> DataError.Network.CONFLICT
            "PAYLOAD_TOO_LARGE", "413" -> DataError.Network.PAYLOAD_TOO_LARGE
            "TOO_MANY_REQUESTS", "429" -> DataError.Network.TOO_MANY_REQUESTS
            "SERVER_ERROR", "500" -> DataError.Network.SERVER_ERROR
            "NO_INTERNET" -> DataError.Network.NO_INTERNET
            "SERIALIZATION" -> DataError.Network.SERIALIZATION
            else -> DataError.Network.UNKNOWN
        }
        Result.Error(dataError)
    }
}

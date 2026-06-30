package me.alexy.hipipl.core.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import me.alexy.hipipl.core.data.dto.ApiResponse
import me.alexy.hipipl.core.data.dto.unwrap
import me.alexy.hipipl.core.domain.DataError
import me.alexy.hipipl.core.domain.Result
import java.net.UnknownHostException

suspend inline fun <reified Response : Any> HttpClient.get(
    route: String,
    queryParameters: Map<String, Any?> = mapOf()
): Result<Response, DataError.Network> {
    return safeCall {
        get(route) {
            queryParameters.forEach { (key, value) ->
                parameter(key, value)
            }
        }
    }
}

suspend inline fun <reified Request, reified Response : Any> HttpClient.post(
    route: String,
    body: Request
): Result<Response, DataError.Network> {
    return safeCall {
        post(route) {
            setBody(body)
        }
    }
}

suspend inline fun <reified Response : Any> HttpClient.delete(
    route: String,
    queryParameters: Map<String, Any?> = mapOf()
): Result<Response, DataError.Network> {
    return safeCall {
        delete(route) {
            queryParameters.forEach { (key, value) ->
                parameter(key, value)
            }
        }
    }
}

suspend inline fun <reified T> safeCall(
    execute: () -> HttpResponse
): Result<T, DataError.Network> {
    val response = try {
        execute()
    } catch (e: UnknownHostException) {
        e.printStackTrace()
        return Result.Error(DataError.Network.NO_INTERNET)
    } catch (e: SerializationException) {
        e.printStackTrace()
        return Result.Error(DataError.Network.SERIALIZATION)
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        e.printStackTrace()
        return Result.Error(DataError.Network.UNKNOWN)
    }

    return responseToResult(response)
}

suspend inline fun <reified T> responseToResult(
    response: HttpResponse
): Result<T, DataError.Network> {
    return when (response.status.value) {
        in 200..299 -> {
            try {
                Result.Success(response.body<T>())
            } catch (e: SerializationException) {
                e.printStackTrace()
                Result.Error(DataError.Network.SERIALIZATION)
            }
        }
        400 -> Result.Error(DataError.Network.BAD_REQUEST)
        401 -> Result.Error(DataError.Network.UNAUTHORIZED)
        403 -> Result.Error(DataError.Network.FORBIDDEN)
        404 -> Result.Error(DataError.Network.NOT_FOUND)
        408 -> Result.Error(DataError.Network.REQUEST_TIMEOUT)
        409 -> Result.Error(DataError.Network.CONFLICT)
        413 -> Result.Error(DataError.Network.PAYLOAD_TOO_LARGE)
        429 -> Result.Error(DataError.Network.TOO_MANY_REQUESTS)
        in 500..599 -> Result.Error(DataError.Network.SERVER_ERROR)
        else -> Result.Error(DataError.Network.UNKNOWN)
    }
}

/**
 * Helper for API v1 endpoints that return { success, data, error } envelope.
 * Automatically unwraps the ApiResponse.
 */
suspend inline fun <reified Response : Any> HttpClient.getV1(
    route: String,
    queryParameters: Map<String, Any?> = mapOf()
): Result<Response, DataError.Network> {
    val envelopeResult = get<ApiResponse<Response>>(route, queryParameters)
    return when (envelopeResult) {
        is Result.Success -> envelopeResult.data.unwrap()
        is Result.Error -> envelopeResult
    }
}

/**
 * Helper for API v1 POST endpoints that return { success, data, error } envelope.
 */
suspend inline fun <reified Request, reified Response : Any> HttpClient.postV1(
    route: String,
    body: Request
): Result<Response, DataError.Network> {
    val envelopeResult = post<Request, ApiResponse<Response>>(route, body)
    return when (envelopeResult) {
        is Result.Success -> envelopeResult.data.unwrap()
        is Result.Error -> envelopeResult
    }
}

/**
 * Helper for API v1 DELETE endpoints that return { success, data, error } envelope.
 */
suspend inline fun <reified Response : Any> HttpClient.deleteV1(
    route: String,
    queryParameters: Map<String, Any?> = mapOf()
): Result<Response, DataError.Network> {
    val envelopeResult = delete<ApiResponse<Response>>(route, queryParameters)
    return when (envelopeResult) {
        is Result.Success -> envelopeResult.data.unwrap()
        is Result.Error -> envelopeResult
    }
}


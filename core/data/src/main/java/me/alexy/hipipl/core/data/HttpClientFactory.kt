package me.alexy.hipipl.core.data

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import me.alexy.hipipl.core.data.dto.RefreshJwtRequestDto
import me.alexy.hipipl.core.data.dto.RefreshJwtResponseDto
import me.alexy.hipipl.core.data.mapper.toRefreshResult
import me.alexy.hipipl.core.domain.DataError
import me.alexy.hipipl.core.domain.RefreshResult
import me.alexy.hipipl.core.domain.Result

object HttpClientFactory {
    fun create(
        engine: HttpClientEngine,
        sessionManager: SessionManager,
        deviceIdManager: DeviceIdManager,
    ): HttpClient {
        return HttpClient(engine) {
            install(ContentNegotiation) {
                // Register JSON for any content type (PHP backend may not send application/json)
                json(
                    Json {
                        ignoreUnknownKeys = true
                        coerceInputValues = true
                        prettyPrint = true
                        isLenient = true
                    },
                    contentType = ContentType.Any
                )
            }

            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        println("Ktor: $message")
                    }
                }
                level = if (BuildConfig.DEBUG) LogLevel.BODY else LogLevel.NONE
            }

            install(Auth) {
                bearer {
                    sendWithoutRequest { true }

                    loadTokens { sessionManager.tokens() }

                    refreshTokens {
                        val userId = sessionManager.currentUserId() ?: return@refreshTokens null
                        val refreshToken = oldTokens?.refreshToken ?: return@refreshTokens null
                        val deviceId = deviceIdManager.getOrCreate()

                        if (BuildConfig.DEBUG) Log.d(TAG, "refreshTokens: requesting new JWT for userId=$userId")

                        // `client` here is a plain clone without this Auth plugin attached
                        // (Ktor's own mechanism to avoid the refresh call recursively triggering
                        // another refresh), safe to use directly.
                        val result = client.post<RefreshJwtRequestDto, RefreshJwtResponseDto>(
                            route = "api/v1/auth/get-jwt",
                            body = RefreshJwtRequestDto(
                                refreshToken = refreshToken,
                                deviceId = deviceId,
                                user = userId,
                            )
                        )
                        val refreshResult: Result<RefreshResult, DataError.Network> = when (result) {
                            is Result.Success -> result.data.toRefreshResult()
                            is Result.Error -> result
                        }
                        when (refreshResult) {
                            is Result.Success -> {
                                if (BuildConfig.DEBUG) Log.d(TAG, "refreshTokens: succeeded, expiresIn=${refreshResult.data.expiresIn}")
                                sessionManager.updateJwt(refreshResult.data.jwtToken)
                                BearerTokens(accessToken = refreshResult.data.jwtToken, refreshToken = refreshToken)
                            }
                            is Result.Error -> {
                                // A dead refresh token (not just a transient failure) - clear the
                                // session directly instead of returning null and letting Ktor
                                // retry indefinitely against a token that will never work again.
                                if (refreshResult.error == DataError.Network.UNAUTHORIZED) {
                                    if (BuildConfig.DEBUG) Log.w(TAG, "refreshTokens: refresh token invalid, clearing session")
                                    sessionManager.clear()
                                } else if (BuildConfig.DEBUG) {
                                    Log.e(TAG, "refreshTokens: failed with ${refreshResult.error}")
                                }
                                null
                            }
                        }
                    }
                }
            }

            defaultRequest {
                url(BuildConfig.BASE_URL)
            }
        }
    }

    private const val TAG = "HttpClientFactory"
}

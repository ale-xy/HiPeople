package me.alexy.hipipl.core.data

import android.util.Log
import io.ktor.client.HttpClient
import me.alexy.hipipl.core.data.dto.LoginVkRequestDto
import me.alexy.hipipl.core.data.dto.LoginVkResponseDto
import me.alexy.hipipl.core.data.dto.LogoutRequestDto
import me.alexy.hipipl.core.data.dto.LogoutResponseDto
import me.alexy.hipipl.core.data.dto.RefreshJwtRequestDto
import me.alexy.hipipl.core.data.dto.RefreshJwtResponseDto
import me.alexy.hipipl.core.data.mapper.toLoginResult
import me.alexy.hipipl.core.data.mapper.toRefreshResult
import me.alexy.hipipl.core.domain.AuthRemoteDataSource
import me.alexy.hipipl.core.domain.DataError
import me.alexy.hipipl.core.domain.EmptyResult
import me.alexy.hipipl.core.domain.LoginResult
import me.alexy.hipipl.core.domain.RefreshResult
import me.alexy.hipipl.core.domain.Result
import me.alexy.hipipl.core.domain.asEmptyResult

class KtorAuthDataSource(
    private val httpClient: HttpClient
) : AuthRemoteDataSource {

    override suspend fun loginVk(
        code: String,
        redirect: String,
        deviceId: String,
        vkDeviceId: String?,
        codeVerifier: String,
    ): Result<LoginResult, DataError.Network> {
        logDebug(
            "loginVk request: redirect=$redirect deviceId=${deviceId.truncated()} " +
                "vkDeviceId=${vkDeviceId?.truncated()} code=${code.truncated()}"
        )

        // Bespoke envelope (auth/user at the top level) - doesn't fit postV1's ApiResponse<T>,
        // so this uses the raw `post` and maps success/error itself. See LoginVkResponseDto.
        val result = httpClient.post<LoginVkRequestDto, LoginVkResponseDto>(
            route = "api/v1/login_vk",
            body = LoginVkRequestDto(
                code = code,
                redirect = redirect,
                deviceId = deviceId,
                vkDeviceId = vkDeviceId,
                codeVerifier = codeVerifier,
            )
        )
        return when (result) {
            is Result.Success -> result.data.toLoginResult().also {
                when (it) {
                    is Result.Success -> logDebug("loginVk response: success, userId=${it.data.userId}")
                    is Result.Error -> logError("loginVk response: success=false, mapped error=${it.error}")
                }
            }

            is Result.Error -> result.also { logError("loginVk request failed: ${it.error}") }
        }
    }

    override suspend fun refreshJwt(
        userId: Int,
        refreshToken: String,
        deviceId: String,
    ): Result<RefreshResult, DataError.Network> {
        logDebug("refreshJwt request: userId=$userId deviceId=${deviceId.truncated()} refreshToken=${refreshToken.truncated()}")

        val result = httpClient.post<RefreshJwtRequestDto, RefreshJwtResponseDto>(
            route = "api/v1/auth/get-jwt",
            body = RefreshJwtRequestDto(refreshToken = refreshToken, deviceId = deviceId, user = userId)
        )
        return when (result) {
            is Result.Success -> result.data.toRefreshResult().also {
                when (it) {
                    is Result.Success -> logDebug("refreshJwt response: success, expiresIn=${it.data.expiresIn}")
                    is Result.Error -> logError("refreshJwt response: mapped error=${it.error}")
                }
            }

            is Result.Error -> result.also { logError("refreshJwt request failed: ${it.error}") }
        }
    }

    override suspend fun logout(refreshToken: String): EmptyResult<DataError.Network> {
        logDebug("logout request: refreshToken=${refreshToken.truncated()}")

        return httpClient.post<LogoutRequestDto, LogoutResponseDto>(
            route = "api/v1/auth/logout",
            body = LogoutRequestDto(refreshToken = refreshToken)
        ).asEmptyResult().also {
            when (it) {
                is Result.Success -> logDebug("logout response: success")
                is Result.Error -> logError("logout response: ${it.error}")
            }
        }
    }

    private fun String.truncated(): String = take(TRUNCATE_LENGTH) + if (length > TRUNCATE_LENGTH) "..." else ""

    private fun logDebug(message: String) {
        if (BuildConfig.DEBUG) Log.d(TAG, message)
    }

    private fun logError(message: String) {
        if (BuildConfig.DEBUG) Log.e(TAG, message)
    }

    private companion object {
        const val TAG = "KtorAuthDataSource"
        const val TRUNCATE_LENGTH = 12
    }
}

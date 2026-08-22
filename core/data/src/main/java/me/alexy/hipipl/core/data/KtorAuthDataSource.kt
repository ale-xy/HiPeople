package me.alexy.hipipl.core.data

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import me.alexy.hipipl.core.data.dto.CheckAuthCodeRequestDto
import me.alexy.hipipl.core.data.dto.CheckAuthCodeResponseDto
import me.alexy.hipipl.core.data.dto.GetAuthCodeRequestDto
import me.alexy.hipipl.core.data.dto.GetAuthCodeResponseDto
import me.alexy.hipipl.core.data.dto.LoginVkRequestDto
import me.alexy.hipipl.core.data.dto.LoginVkResponseDto
import me.alexy.hipipl.core.data.dto.LogoutRequestDto
import me.alexy.hipipl.core.data.dto.LogoutResponseDto
import me.alexy.hipipl.core.data.dto.RefreshJwtRequestDto
import me.alexy.hipipl.core.data.dto.RefreshJwtResponseDto
import me.alexy.hipipl.core.data.mapper.toAuthCodeInfo
import me.alexy.hipipl.core.data.mapper.toCheckCodeResult
import me.alexy.hipipl.core.data.mapper.toLoginResult
import me.alexy.hipipl.core.data.mapper.toRefreshResult
import me.alexy.hipipl.core.domain.AuthCodeInfo
import me.alexy.hipipl.core.domain.AuthRemoteDataSource
import me.alexy.hipipl.core.domain.CheckCodeResult
import me.alexy.hipipl.core.domain.DataError
import me.alexy.hipipl.core.domain.EmptyResult
import me.alexy.hipipl.core.domain.LoginResult
import me.alexy.hipipl.core.domain.RefreshResult
import me.alexy.hipipl.core.domain.Result
import me.alexy.hipipl.core.domain.asEmptyResult
import java.net.UnknownHostException

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

    override suspend fun getAuthCode(
        deviceId: String,
        lang: String,
        ip: String?,
    ): Result<AuthCodeInfo, DataError.Network> {
        logDebug("getAuthCode request: deviceId=${deviceId.truncated()} lang=$lang")

        return when (
            val result = httpClient.postV1<GetAuthCodeRequestDto, GetAuthCodeResponseDto>(
                route = "api/v1/auth/get-code",
                body = GetAuthCodeRequestDto(deviceId = deviceId, lang = lang, ip = ip),
            )
        ) {
            is Result.Success -> result.data.toAuthCodeInfo().also {
                when (it) {
                    is Result.Success -> logDebug("getAuthCode response: success, code=${it.data.code}")
                    is Result.Error -> logError("getAuthCode response: mapped error=${it.error}")
                }
            }

            is Result.Error -> result.also { logError("getAuthCode request failed: ${it.error}") }
        }
    }

    override suspend fun checkAuthCode(code: String): Result<CheckCodeResult, DataError.Network> {
        logDebug("checkAuthCode request: code=${code.truncated()}")

        // Bespoke, status-code-driven call (same reasoning as loginVk's bespoke DTO): 200/202/404
        // all carry a meaningful body here ("completed"/"pending"/"expired"), whereas the generic
        // post()/postV1() helpers collapse a 404 straight to DataError.Network.NOT_FOUND before
        // ever parsing the response - see CheckAuthCodeResponseDto's kdoc.
        val response = try {
            httpClient.request("api/v1/auth/check-code") {
                method = HttpMethod.Post
                contentType(ContentType.Application.Json)
                setBody(CheckAuthCodeRequestDto(code = code))
            }
        } catch (e: UnknownHostException) {
            logError("checkAuthCode request failed: no internet")
            return Result.Error(DataError.Network.NO_INTERNET)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            logError("checkAuthCode request failed: $e")
            return Result.Error(DataError.Network.UNKNOWN)
        }

        return when (response.status.value) {
            200, 202, 404 -> try {
                response.body<CheckAuthCodeResponseDto>().toCheckCodeResult().also {
                    when (it) {
                        is Result.Success -> logDebug("checkAuthCode response: status=${it.data}")
                        is Result.Error -> logError("checkAuthCode response: mapped error=${it.error}")
                    }
                }
            } catch (e: SerializationException) {
                logError("checkAuthCode response: serialization error")
                Result.Error(DataError.Network.SERIALIZATION)
            }

            400 -> Result.Error(DataError.Network.BAD_REQUEST)
            in 500..599 -> Result.Error(DataError.Network.SERVER_ERROR)
            else -> Result.Error(DataError.Network.UNKNOWN)
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

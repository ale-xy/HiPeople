package me.alexy.hipipl.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginVkRequestDto(
    val code: String,
    val redirect: String,
    @SerialName("device_id") val deviceId: String,
    @SerialName("vk_device_id") val vkDeviceId: String? = null,
    @SerialName("code_verifier") val codeVerifier: String,
)

/**
 * Bespoke envelope: `auth`/`user` sit at the top level (not under `data`), and `error` can be
 * present alongside a successful `auth`/`user` (code 1 = incomplete profile / missing consent) as
 * well as on its own for real failures (HTTP-level 400/401 failures never reach this DTO - those
 * are handled by [me.alexy.hipipl.core.data.responseToResult] before a body is parsed here).
 * Doesn't fit [ApiResponse]/[ApiError], so it gets its own DTO - see `POST_auth_login_vk.md`.
 */
@Serializable
data class LoginVkResponseDto(
    val success: Boolean = false,
    val auth: LoginVkAuthDto? = null,
    val user: AccountDto? = null,
    val error: LoginVkErrorDto? = null,
)

@Serializable
data class LoginVkAuthDto(
    @SerialName("user_id") val userId: Int? = null,
    @SerialName("jwt_token") val jwtToken: String? = null,
    @SerialName("refresh_token") val refreshToken: String? = null,
    @SerialName("expires_in") val expiresIn: Int? = null,
    @SerialName("device_id") val deviceId: String? = null,
)

@Serializable
data class LoginVkErrorDto(
    val code: Int? = null,
    val message: String? = null,
    val fields: List<String>? = null,
)

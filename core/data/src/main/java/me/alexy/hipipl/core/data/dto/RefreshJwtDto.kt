package me.alexy.hipipl.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RefreshJwtRequestDto(
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("device_id") val deviceId: String,
    val user: Int,
)

/**
 * Bespoke envelope: on success `data` follows the usual v1 shape, but an invalid refresh token
 * is signaled by a sibling `refresh: {valid: false}` block (HTTP 200, `success: false`, no
 * `error`) rather than the standard `error` object - doesn't fit [ApiResponse]/[ApiError], and
 * that distinction matters here (invalid refresh token -> log out; anything else -> just failed).
 * See `POST_auth_get_jwt.md`.
 */
@Serializable
data class RefreshJwtResponseDto(
    val success: Boolean = false,
    val data: RefreshJwtDataDto? = null,
    val refresh: RefreshTokenValidityDto? = null,
)

@Serializable
data class RefreshJwtDataDto(
    @SerialName("jwt_token") val jwtToken: String? = null,
    @SerialName("expires_in") val expiresIn: Int? = null,
)

@Serializable
data class RefreshTokenValidityDto(
    val valid: Boolean? = null,
)

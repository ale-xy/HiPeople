package me.alexy.hipipl.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetAuthCodeRequestDto(
    @SerialName("device_id") val deviceId: String,
    val lang: String? = null,
    val ip: String? = null,
)

/** Fits the standard `{success, data, error}` envelope - `data` is this DTO, unwrapped by `postV1`. */
@Serializable
data class GetAuthCodeResponseDto(
    val code: String? = null,
    @SerialName("device_id") val deviceId: String? = null,
    @SerialName("expires_at") val expiresAt: String? = null,
    val lang: String? = null,
    @SerialName("social_urls") val socialUrls: SocialUrlsDto? = null,
    val reused: Boolean? = null,
    @SerialName("auth_restricted") val authRestricted: String? = null,
)

@Serializable
data class SocialUrlsDto(
    val vk: SocialUrlDto? = null,
    val tg: SocialUrlDto? = null,
    val wa: SocialUrlDto? = null,
    val fb: SocialUrlDto? = null,
)

@Serializable
data class SocialUrlDto(
    val url: String? = null,
    val instruction: String? = null,
)

@Serializable
data class CheckAuthCodeRequestDto(
    val code: String,
)

/**
 * Bespoke envelope, same reasoning as [LoginVkResponseDto]: `status` drives which of
 * `auth`/`user`/`error` (completed) vs `new_code`/`deep_link`/`social_urls` (expired) is
 * populated, and the HTTP status code (200/202/404) carries meaning `postV1`'s generic
 * {success,data} unwrap would discard - see `POST_auth_check_code.md`.
 */
@Serializable
data class CheckAuthCodeResponseDto(
    val success: Boolean = false,
    val status: String? = null,
    val code: String? = null,
    @SerialName("expires_at") val expiresAt: String? = null,
    @SerialName("old_code") val oldCode: String? = null,
    @SerialName("new_code") val newCode: String? = null,
    @SerialName("deep_link") val deepLink: String? = null,
    @SerialName("social_urls") val socialUrls: SocialUrlsDto? = null,
    val auth: LoginVkAuthDto? = null,
    val user: AccountDto? = null,
    val error: LoginVkErrorDto? = null,
    val message: String? = null,
)

package me.alexy.hipipl.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The `user` block inline in `POST /login_vk`'s response - the logged-in user's own profile.
 * Deliberately separate from [HostUserDto]: its `host` is a full listing (`hostId`, `text`,
 * `dist`, ...) but the login response's embedded `host` is just `{city, actual}` - reusing
 * [HostUserDto] would mean fabricating fields that don't exist here.
 */
@Serializable
data class AccountDto(
    val id: Int? = null,
    val name: String? = null,
    val sex: String? = null,
    val age: Int? = null,
    val about: String? = null,
    @SerialName("total_reviews") val totalReviews: Int? = null,
    val rating: Float? = null,
    val photos: List<PhotoDto>? = null,
    val donate: Int? = null,
    @SerialName("user_langs") val userLangs: List<UserLangDto>? = null,
)

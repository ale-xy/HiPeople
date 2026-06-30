package me.alexy.hipipl.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HostUserDto(
    val id: Int? = null,  // Top-level id is the user ID in v1
    val name: String? = null,
    val sex: String? = null,  // "m", "f" string in v1
    val age: Int? = null,
    val about: String? = null,  // v1 uses "about" not "descript"
    @SerialName("total_reviews") val totalReviews: Int? = null,
    val contacts: ContactsDto? = ContactsDto(),
    val rating: Float? = null,  // v1 uses "rating" not "average_rating"
    val photos: List<PhotoDto>? = null,
    val donate: Int? = null,
    @SerialName("user_langs") val userLangs: List<UserLangDto>? = null,
    @SerialName("last_activity") val lastActivity: String? = null,  // "today", "recently", "long ago"
    @SerialName("user_vibes") val userVibes: List<UserVibeDto>? = null,
    val host: HostDto? = null,
)

@Serializable
data class UserLangDto(
    val lvl: Int? = null,
    val code: String? = null,  // v1 doesn't send code, make it nullable
    val name: String? = null,
)

@Serializable
data class UserVibeDto(
    val label: String? = null,
    val code: String? = null,
)

@Serializable
data class PhotoDto(
    val url: String? = null,
    val id: Int? = null
)

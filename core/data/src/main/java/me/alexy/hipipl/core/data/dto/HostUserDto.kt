package me.alexy.hipipl.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HostUserDto(
    val id: Int? = null,
    @SerialName("user_id") val userId: Int? = null,  // present in /ad response alongside id
    val name: String? = null,
    val sex: String? = null,  // "m", "f" string in v1
    val age: Int? = null,
    val about: String? = null,  // v1 uses "about" not "descript"
    @SerialName("total_reviews") val totalReviews: Int? = null,
    val contacts: ContactsDto? = ContactsDto(),
    val rating: String? = null,  // v1 returns rating as string (e.g., "9.8")
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
    val code: String? = null,
    val name: String? = null,
)

@Serializable
data class UserVibeDto(
    val code: String? = null,
    val group: String? = null,  // "social_energy", "alcohol", etc.
)

@Serializable
data class PhotoDto(
    val url: String? = null,
    val id: Int? = null,
    val type: String? = null  // "user" or "review"
)

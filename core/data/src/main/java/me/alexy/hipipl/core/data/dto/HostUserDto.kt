package me.alexy.hipipl.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HostUserDto(
    @SerialName("id_user") val userId: Int? = null,
    @SerialName("id") val hostId: Int? = null,
    val name: String? = null,
    val sex: Int? = null,
    val age: Int? = null,
    val descript: String? = null,
    @SerialName("total_reviews") val totalReviews: Int? = null,
    val contacts: ContactsDto? = ContactsDto(),
    @SerialName("average_rating") val averageRating: Float? = null,
    val photos: List<PhotoDto?>? = null,
    val donate: Int? = null,
    @SerialName("user_langs") val userLangs: List<UserLangDto>? = null,
    val host: HostDto? = HostDto()
)

@Serializable
data class UserLangDto(
    val lvl: Int,
    val code: String,
    val name: String,
)

typealias PhotoDto = String

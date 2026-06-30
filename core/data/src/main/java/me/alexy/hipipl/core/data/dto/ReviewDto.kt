package me.alexy.hipipl.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
data class UserReviewsResponseDto(
    @SerialName("total_received") val totalReceived: Int? = null,
    val reviews: JsonElement = JsonObject(emptyMap()),  // Can be {} or []
)

@Serializable
data class ReviewThreadDto(
    val received: List<ReviewDto> = emptyList(),
    val response: List<ReviewDto> = emptyList(),
    val mutual: Boolean = false,
)

@Serializable
data class ReviewDto(
    val id: Int? = null,
    val date: String? = null,
    val text: String? = null,
    val photo: String? = null,
    val type: String? = null,  // "cs_host_pos", "cs_surf_neg", "cs_friend_pos", "cs_other"
    val name: String? = null,  // v1 uses "name" for author name
    val mutual: Boolean? = null,  // v1 uses Boolean not Int
)

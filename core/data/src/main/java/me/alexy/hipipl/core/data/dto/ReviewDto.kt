package me.alexy.hipipl.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
data class UserReviewsResponseDto(
    @SerialName("total_received") val totalReceived: Int? = null,
    @SerialName("has_more") val hasMore: Boolean? = null,  // Non-mutual threads only; mutual ones are always returned in full
    @SerialName("next_offset") val nextOffset: Int? = null,
    val reviews: JsonElement = JsonObject(emptyMap()),  // Can be {} or []
)

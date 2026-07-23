package me.alexy.hipipl.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SupportRequestDto(
    val type: String,
    val message: String,
    @SerialName("user_id") val userId: Int? = null,
    @SerialName("additional_data") val additionalData: String? = null,
)

@Serializable
data class SupportResponseDto(
    val success: Boolean? = null,
    val result: String? = null,
)

package me.alexy.hipipl.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class FavoriteRequestDto(
    val content: Int,
    val type: String,
)

@Serializable
data class FavoriteResponseDto(
    val success: Boolean? = null,
    val message: String? = null,
)

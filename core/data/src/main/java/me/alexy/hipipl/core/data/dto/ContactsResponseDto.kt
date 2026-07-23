package me.alexy.hipipl.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * GET /users/{id}/contacts has a bespoke envelope: `data` is a list of single-key
 * contact maps on success, `error` carries a numeric `code` (not the string codes
 * used by [ApiResponse]'s [ApiError]), and code 2 attaches a sibling `review` block.
 * Doesn't fit the generic [ApiResponse]/[ApiError] shape, so it gets its own DTO.
 */
@Serializable
data class ContactsResponseDto(
    val success: Boolean = false,
    val data: List<Map<String, String>> = emptyList(),
    val error: ContactsErrorDto? = null,
    val review: PendingReviewDto? = null,
)

@Serializable
data class ContactsErrorDto(
    val code: Int? = null,
    val message: String? = null,
)

@Serializable
data class PendingReviewDto(
    @SerialName("who_id") val whoId: Int? = null,
    @SerialName("who_name") val whoName: String? = null,
    val text: String? = null,
    val date: String? = null,
    val type: String? = null,
    val photo: String? = null,
)

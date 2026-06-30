package me.alexy.hipipl.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HostsPageDto(
    val hosts: List<HostListItemDto> = emptyList(),
    @SerialName("total_found") val totalFound: Int = 0,
    @SerialName("has_more") val hasMore: Boolean = false,
    @SerialName("search_cache_id") val searchCacheId: String? = null,
)

@Serializable
data class HostListItemDto(
    val id: Int? = null,  // User/host ID
    val name: String? = null,
    val age: Int? = null,
    val sex: String? = null,  // "m", "f", "ppl"
    val gender: String? = null,  // "m", "f", "ppl"
    @SerialName("total_reviews") val totalReviews: Int? = null,
    val rating: String? = null,  // Can be number or string
    val separate: String? = null,  // "y", "n"
    val kids: String? = null,  // "y", "n"
    val pets: String? = null,  // "y", "n"
    val distance: Float? = null,  // Distance in km (decimal)
    val direction: String? = null,
    val city: String? = null,
    val photos: List<PhotoDto>? = null,
)

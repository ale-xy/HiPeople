package me.alexy.hipipl.core.data.dto

import kotlinx.serialization.Serializable

/**
 * Response of GET /api/v1/get_search_find_multi. `type` is only present on the
 * success variants ("user"/"hosts"/"geo"); the "not found" variant instead has
 * `error`/`can_add`/`type_cont`/`contact` with no `type` at all.
 */
@Serializable
data class ContactSearchResponseDto(
    val type: String? = null,
    val id: Int? = null,
    val name: String? = null,
    val age: Int? = null,
    val photos: List<PhotoDto>? = null,
)

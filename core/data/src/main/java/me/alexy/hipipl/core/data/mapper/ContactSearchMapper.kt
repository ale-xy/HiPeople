package me.alexy.hipipl.core.data.mapper

import me.alexy.hipipl.core.data.dto.ContactSearchResponseDto
import me.alexy.hipipl.core.domain.ContactSearchResult

fun ContactSearchResponseDto.toContactSearchResult(): ContactSearchResult {
    val idValue = id
    val nameValue = name
    if (type == "user" && idValue != null && nameValue != null) {
        return ContactSearchResult.UserFound(
            id = idValue,
            name = nameValue,
            age = age ?: 0,
            photos = photos?.mapNotNull { it.toPhoto() } ?: emptyList(),
        )
    }
    return ContactSearchResult.NotFoundOrOther
}

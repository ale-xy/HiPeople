package me.alexy.hipipl.feature.hostme.ui

import me.alexy.hipipl.core.domain.Location

fun Location.toLocationUi(): LocationUi {
    val displayName = if (name.isNotBlank()) {
        "$name ($nameEn)"
    } else {
        nameEn
    }

    val regionLine = listOf(regionName, countryNameEn)
        .filter { it.isNotBlank() }
        .joinToString(", ")

    return LocationUi(
        id = id,
        displayName = displayName,
        regionLine = regionLine
    )
}

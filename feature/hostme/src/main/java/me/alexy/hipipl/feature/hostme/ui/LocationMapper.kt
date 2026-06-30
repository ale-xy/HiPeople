package me.alexy.hipipl.feature.hostme.ui

import me.alexy.hipipl.core.domain.Location

fun Location.toLocationUi(): LocationUi {
    return LocationUi(
        id = id,
        displayName = name,
        regionLine = subtitle,
        type = type
    )
}

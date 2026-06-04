package me.alexy.hipipl.feature.hostme.ui

import me.alexy.hipipl.core.domain.HostUser

fun HostUser.toHostListItemUi(): HostListItemUi {
    val ageText = if (age > 0) "$age лет" else ""
    val titleLine = "$name $averageRating* ($totalReviews)" +
            if (ageText.isNotBlank()) ", $ageText" else ""

    val distanceText = if (host.dist > 0) {
        "(${host.dist} км" +
                if (host.direction.isNotBlank()) {
                    " ${host.direction}"
                } else {
                    ""
                } + ")"
    } else {
        ""
    }

    val locationLine = listOf(host.city, distanceText)
        .filter { it.isNotBlank() }
        .joinToString(" ")

    val description = host.text.replace("\n\n", "\n")

    return HostListItemUi(
        hostId = host.hostId,
        userId = userId,
        photoUrl = photos.firstOrNull()?.url,
        titleLine = titleLine,
        locationLine = locationLine,
        description = description
    )
}

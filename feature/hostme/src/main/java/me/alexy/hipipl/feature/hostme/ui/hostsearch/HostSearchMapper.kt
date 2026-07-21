package me.alexy.hipipl.feature.hostme.ui.hostsearch

import me.alexy.hipipl.core.designsystem.GenderAccent
import me.alexy.hipipl.core.designsystem.components.TriState
import me.alexy.hipipl.core.domain.ContactSearchResult
import me.alexy.hipipl.core.domain.Gender
import me.alexy.hipipl.core.domain.HostSearchFilters
import me.alexy.hipipl.core.domain.HostUser
import me.alexy.hipipl.core.domain.Location
import me.alexy.hipipl.core.domain.TriStateFilter
import me.alexy.hipipl.feature.hostme.ui.usersearch.UserCardUi

fun Location.toLocationUi(): LocationUi {
    return LocationUi(
        id = id,
        displayName = name,
        regionLine = subtitle,
        type = type,
    )
}

fun HostUser.toHostCardUi(): HostCardUi {
    return HostCardUi(
        hostId = host.hostId,
        userId = userId,
        photoUrl = photos.firstOrNull()?.url,
        name = name,
        ageText = if (age > 0) " ($age)" else "",
        referenceCount = totalReviews,
        referenceScoreText = if (totalReviews > 0 && averageRating > 0f) {
            "%.1f".format(averageRating)
        } else {
            null
        },
        separateRoom = host.separateRoom,
        kidsAllowed = host.allowKids,
        petsAtHome = host.petsAtHome,
        hasDistance = host.dist > 0f,
        distanceValueText = "%.1f".format(host.dist),
        directionDegrees = host.direction.toCompassDegrees() ?: 0f,
        city = host.city,
        genderAccent = host.gender.toGenderAccent(),
    )
}

fun ContactSearchResult.UserFound.toUserCardUi(): UserCardUi {
    return UserCardUi(
        userId = id,
        photoUrl = photos.firstOrNull()?.url,
        name = name,
        ageText = if (age > 0) " ($age)" else "",
    )
}

fun Gender.toGenderAccent(): GenderAccent = when (this) {
    Gender.MALE -> GenderAccent.MALE
    Gender.FEMALE -> GenderAccent.FEMALE
    Gender.PEOPLE -> GenderAccent.SEVERAL
    Gender.UNKNOWN -> GenderAccent.UNSPECIFIED
}

private fun String.toCompassDegrees(): Float? = when (uppercase()) {
    "N" -> 0f
    "NE" -> 45f
    "E" -> 90f
    "SE" -> 135f
    "S" -> 180f
    "SW" -> 225f
    "W" -> 270f
    "NW" -> 315f
    else -> null
}

fun HostSearchFiltersUi.toDomain(): HostSearchFilters = HostSearchFilters(
    separateRoom = separateRoom.toDomain(),
    kidsAllowed = kidsAllowed.toDomain(),
    petsAtHome = petsAtHome.toDomain(),
)

private fun TriState.toDomain(): TriStateFilter = when (this) {
    TriState.YES -> TriStateFilter.YES
    TriState.NO -> TriStateFilter.NO
    TriState.UNSPECIFIED -> TriStateFilter.UNSPECIFIED
}

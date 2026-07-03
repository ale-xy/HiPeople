package me.alexy.hipipl.core.data.mapper

import me.alexy.hipipl.core.data.dto.HostMapMarkerDto
import me.alexy.hipipl.core.domain.HostMapMarker

fun HostMapMarkerDto.toHostMapMarker(): HostMapMarker? {
    val idValue = id ?: return null
    val latValue = lat ?: return null
    val lonValue = lon ?: return null
    val nameValue = name ?: return null

    return HostMapMarker(
        id = idValue,
        lat = latValue,
        lon = lonValue,
        gender = gender.toGenderFromString(),
        name = nameValue,
        separateRoom = separate == "y",
        kidsAllowed = kids == "y",
        petsAtHome = pets == "y",
        accurate = accurate ?: false,
    )
}

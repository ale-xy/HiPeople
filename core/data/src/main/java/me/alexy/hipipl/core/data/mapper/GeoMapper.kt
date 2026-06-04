package me.alexy.hipipl.core.data.mapper

import me.alexy.hipipl.core.data.dto.GeoNameDto
import me.alexy.hipipl.core.domain.Location

fun GeoNameDto.toLocation(): Location? {
    return Location(
        id = id ?: return null,
        name = name ?: return null,
        nameEn = nameEn.orEmpty(),
        regionId = region ?: 0,
        regionName = regionName.orEmpty(),
        countryId = country ?: 0,
        countryName = countryName.orEmpty(),
        countryNameEn = countryNameEn.orEmpty(),
    )
}

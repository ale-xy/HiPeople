package me.alexy.hipipl.core.data.mapper

import me.alexy.hipipl.core.data.dto.GeoNameDto
import me.alexy.hipipl.core.domain.Location

fun GeoNameDto.toLocation(): Location? {
    val type = type ?: return null
    val title = title ?: return null
    
    // Select ID based on type (matches web logic at search_hosts.html:1661-1666)
    val id = when (type) {
        "city" -> cityId
        "country" -> countryId
        "region" -> regionId
        else -> null
    } ?: return null
    
    return Location(
        id = id,
        type = type,
        name = title,
        subtitle = subtitle.orEmpty(),
        cityId = cityId,
        countryId = countryId,
        regionId = regionId,
    )
}

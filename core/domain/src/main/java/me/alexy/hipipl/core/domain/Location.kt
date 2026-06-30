package me.alexy.hipipl.core.domain

data class Location(
    val id: Int,  // Type-aware id: cityId, countryId, or regionId based on type
    val type: String,  // "city", "country", or "region"
    val name: String,  // Display name (title from v1)
    val subtitle: String,  // Context line (country name from v1)
    val cityId: Int?,
    val countryId: Int?,
    val regionId: Int?,
)

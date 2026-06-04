package me.alexy.hipipl.core.domain

data class Location(
    val id: Int,
    val name: String,
    val nameEn: String,
    val regionId: Int,
    val regionName: String,
    val countryId: Int,
    val countryName: String,
    val countryNameEn: String,
)

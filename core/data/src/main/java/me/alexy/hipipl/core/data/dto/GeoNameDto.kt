package me.alexy.hipipl.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeoNameDto(
    val id: Int? = null,
    @SerialName("or_name") val name: String? = null,
    @SerialName("en_name") val nameEn: String? = null,
    val search: String? = null,
    val type: String? = null,
    val lat: Float? = null,
    val lon: Float? = null,
    val radius: Int? = null,
    val region: Int? = null,
    @SerialName("name_region") val regionName: String? = null,
    val country: Int? = null,
    @SerialName("or_name_country") val countryName: String? = null,
    @SerialName("en_name_country") val countryNameEn: String? = null,
)

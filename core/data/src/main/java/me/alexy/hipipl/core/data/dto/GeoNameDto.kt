package me.alexy.hipipl.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeoNameDto(
    val type: String? = null,  // "city", "country", or "region"
    val title: String? = null,  // Display name from v1 API
    val subtitle: String? = null,  // Country context (e.g., "France")
    val lat: Float? = null,
    val lon: Float? = null,
    @SerialName("city_id") val cityId: Int? = null,
    @SerialName("country_id") val countryId: Int? = null,
    @SerialName("region_id") val regionId: Int? = null,
)

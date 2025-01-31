package com.hippl.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class NetworkGeoName(
    val id: Int,
    @SerialName("or_name") val name: String? = null,
    @SerialName("en_name") val nameEn: String? = null,
    val search: String? = null,
    val type: String,
    val lat: Int,
    val lon: Int,
    val radius: Int,
    val region: Int? = null,
    @SerialName("name_region") val regionName: String? = null,
    val country: Int,
    @SerialName("or_name_country") val countryName: String? = null,
    @SerialName("en_name_country") val countryNameEn: String? = null,
)
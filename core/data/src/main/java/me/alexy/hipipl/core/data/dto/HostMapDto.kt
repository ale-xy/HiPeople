package me.alexy.hipipl.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HostMapPageDto(
    @SerialName("total_found") val totalFound: Int = 0,
    val hosts: List<HostMapMarkerDto> = emptyList(),
)

@Serializable
data class HostMapMarkerDto(
    val id: Int? = null,
    val lat: Double? = null,
    val lon: Double? = null,
    val gender: String? = null,  // "m", "f", "ppl"
    val name: String? = null,
    val separate: String? = null,  // "y", "n", "0"
    val kids: String? = null,  // "y", "n", "0"
    val pets: String? = null,  // "y", "n", "0"
    val accurate: Boolean? = null,
)

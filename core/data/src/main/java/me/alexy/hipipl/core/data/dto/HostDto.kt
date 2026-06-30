package me.alexy.hipipl.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HostDto(
    val id: Int? = null,  // Host listing ID
    val text: String? = null,
    val accurate: Boolean? = null,  // v1 uses Boolean not Int
    val city: String? = null,
    val lat: Float? = null,
    val lon: Float? = null,
    val gender: String? = null,  // "m", "f", "ppl" string in v1
    @SerialName("separate_room") val separateRoom: String? = null,  // "y", "n"
    @SerialName("kids_allowed") val kidsAllowed: String? = null,  // "y", "n"
    @SerialName("pets_present") val petsPresent: String? = null,  // "y", "n"
    @SerialName("is_actual") val isActual: Boolean? = null,
    // Fields only in list view, optional in details
    val degree: String? = null,
    val dist: Int? = null,
    val date: String? = null,
)

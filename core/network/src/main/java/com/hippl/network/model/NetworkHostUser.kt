package com.hippl.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class NetworkHostUser(
    @SerialName("id_user") val userId: Int? = null,
    @SerialName("id") val hostId: Int? = null,
    val name: String? = null,
    val sex: Int? = null,
    val age: Int? = null,
    val descript: String? = null,
    @SerialName("total_reviews") val totalReviews: Int? = null,
    val contacts: NetworkContacts? = NetworkContacts(),
    @SerialName("average_rating") val averageRating: Float? = null,
    val photos: List<String?>? = null,
    val donate: Int? = null,
    @SerialName("user_langs") val userLangs: List<NetworkUserLang>? = null,
    val host: NetworkHost? = NetworkHost()
)

@Serializable
data class NetworkUserLang(
    val lvl: Int,
    val code: String,
    val name: String,
)
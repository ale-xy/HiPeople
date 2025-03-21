package com.hippl.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkMutualReview(
    val rec: NetworkReview? = null,
    val ans: NetworkReview? = null,
)

@Serializable
data class NetworkReview(
    val id: Int,
    @SerialName("id_author") val authorId: Int,
    @SerialName("id_receiver") val receiverId: Int,
    val date: String,
    val text: String,
    val photo: String? = null,
    val type: String,
    val mutal: Int? = null,
    @SerialName("n_author") val authorName: String,
)

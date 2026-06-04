package me.alexy.hipipl.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MutualReviewDto(
    val rec: ReviewDto? = null,
    val ans: ReviewDto? = null,
)

@Serializable
data class ReviewDto(
    val id: Int? = null,
    @SerialName("id_author") val authorId: Int? = null,
    @SerialName("id_receiver") val receiverId: Int? = null,
    val date: String? = null,
    val text: String? = null,
    val photo: String? = null,
    val type: String? = null,
    val mutal: Int? = null,
    @SerialName("n_author") val authorName: String? = null,
)

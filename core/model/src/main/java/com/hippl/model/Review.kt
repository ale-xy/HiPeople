package com.hippl.model

import java.time.LocalDateTime

data class Review(
    val id: Int,
    val receiverId: Int,
    val authorId: Int,
    val authorName: String,
    val date: LocalDateTime,
    val text: String,
    val photo: String? = null,
    val type: ReviewType,
    val mutal: Int,
)

data class MutualReview(
    val review: Review?,
    val response: Review?
)

enum class ReviewType {
    GUEST,
    HOST,
    UNKNOWN
}
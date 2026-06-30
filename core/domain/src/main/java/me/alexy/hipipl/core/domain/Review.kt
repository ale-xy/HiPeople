package me.alexy.hipipl.core.domain

import java.time.LocalDateTime

data class Review(
    val id: Int,
    val authorName: String,
    val date: LocalDateTime,
    val text: String,
    val photo: String? = null,
    val type: ReviewType,
    val isMutual: Boolean,
)

data class ReviewThread(
    val received: List<Review>,
    val response: List<Review>,
    val isMutual: Boolean,
)

data class UserReviews(
    val totalReceived: Int,
    val threads: List<ReviewThread>,  // Flattened from the map for UI consumption
)

enum class ReviewType {
    HOST_POSITIVE,
    HOST_NEGATIVE,
    SURF_POSITIVE,
    SURF_NEGATIVE,
    FRIEND_POSITIVE,
    FRIEND_NEGATIVE,
    OTHER,
    UNKNOWN
}

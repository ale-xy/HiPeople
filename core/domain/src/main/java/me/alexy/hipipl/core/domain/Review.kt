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
    val authorId: Int? = null,  // Parsed from the "u{id}" grouping key; used to de-dupe across pages
)

data class UserReviews(
    val totalReceived: Int,
    val threads: List<ReviewThread>,  // Flattened from the map for UI consumption
    val hasMore: Boolean = false,  // Non-mutual threads are paginated server-side; mutual ones always come back in full
    val nextOffset: Int? = null,
)

// v1 review types (host, guest, companion, driver, passenger, friend, buddy, other) -
// see GET_users_ID_reviews.md. Not the legacy Couchsurfing-style "cs_host_pos" values.
enum class ReviewType {
    HOST,
    GUEST,
    COMPANION,
    DRIVER,
    PASSENGER,
    FRIEND,
    BUDDY,
    OTHER,
    UNKNOWN
}

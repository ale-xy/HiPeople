package me.alexy.hipipl.core.data.mapper

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.contentOrNull
import me.alexy.hipipl.core.data.dto.ReviewDto
import me.alexy.hipipl.core.data.dto.ReviewThreadDto
import me.alexy.hipipl.core.data.dto.UserReviewsResponseDto
import me.alexy.hipipl.core.domain.Review
import me.alexy.hipipl.core.domain.ReviewThread
import me.alexy.hipipl.core.domain.ReviewType
import me.alexy.hipipl.core.domain.UserReviews

fun UserReviewsResponseDto.toUserReviews(): UserReviews {
    val threads = mutableListOf<ReviewThread>()
    
    // Handle case where reviews is an empty array [] or an object with review threads
    if (reviews is JsonObject) {

        // Iterate through each user's review thread (keyed by "u{userId}")
        for ((_, threadElement) in reviews) {
            try {
                val threadObj = threadElement.jsonObject
                
                // Parse received reviews
                val receivedList = threadObj["received"]?.jsonArray?.mapNotNull { reviewElement ->
                    parseReviewFromJson(reviewElement.jsonObject)
                } ?: emptyList()
                
                // Parse response reviews
                val responseList = threadObj["response"]?.jsonArray?.mapNotNull { reviewElement ->
                    parseReviewFromJson(reviewElement.jsonObject)
                } ?: emptyList()
                
                // Parse mutual flag
                val isMutual = threadObj["mutual"]?.jsonPrimitive?.boolean ?: false
                
                threads.add(ReviewThread(receivedList, responseList, isMutual))
            } catch (e: Exception) {
                // Skip malformed review threads
                continue
            }
        }
    }
    
    return UserReviews(
        totalReceived = totalReceived ?: 0,
        threads = threads
    )
}

private fun parseReviewFromJson(reviewObj: JsonObject): Review? {
    return try {
        // id and name are optional in v1 API - use defaults if missing
        val id = reviewObj["id"]?.jsonPrimitive?.contentOrNull?.toIntOrNull() ?: 0
        val name = reviewObj["name"]?.jsonPrimitive?.contentOrNull ?: "Unknown"
        val date = reviewObj["date"]?.jsonPrimitive?.contentOrNull
        val text = reviewObj["text"]?.jsonPrimitive?.contentOrNull ?: return null // text is required
        val photo = reviewObj["photo"]?.jsonPrimitive?.contentOrNull
        val type = reviewObj["type"]?.jsonPrimitive?.contentOrNull
        val mutual = reviewObj["mutual"]?.jsonPrimitive?.contentOrNull?.toBooleanStrictOrNull() ?: false
        
        Review(
            id = id,
            authorName = name,
            date = date.parseApiDateTime(),
            text = text,
            photo = if (photo.isNullOrBlank()) null else photo,
            type = type.toReviewType(),
            isMutual = mutual
        )
    } catch (e: Exception) {
        null
    }
}

fun ReviewThreadDto.toReviewThread(): ReviewThread {
    return ReviewThread(
        received = received.mapNotNull { it.toReview() },
        response = response.mapNotNull { it.toReview() },
        isMutual = mutual
    )
}

fun ReviewDto.toReview(): Review? {
    val reviewId = id ?: return null
    val authorName = name ?: return null
    
    return Review(
        id = reviewId,
        authorName = authorName,
        date = date.parseApiDateTime(),
        text = text.orEmpty(),
        photo = if (photo.isNullOrBlank()) null else photo,
        type = type.toReviewType(),
        isMutual = mutual ?: false
    )
}

// Parse v1 review types: "cs_host_pos", "cs_surf_neg", "cs_friend_pos", "cs_other"
private fun String?.toReviewType(): ReviewType {
    if (this == null) return ReviewType.UNKNOWN
    
    return when {
        this.startsWith("cs_host_pos") -> ReviewType.HOST_POSITIVE
        this.startsWith("cs_host_neg") -> ReviewType.HOST_NEGATIVE
        this.startsWith("cs_surf_pos") -> ReviewType.SURF_POSITIVE
        this.startsWith("cs_surf_neg") -> ReviewType.SURF_NEGATIVE
        this.startsWith("cs_friend_pos") -> ReviewType.FRIEND_POSITIVE
        this.startsWith("cs_friend_neg") -> ReviewType.FRIEND_NEGATIVE
        this.startsWith("cs_other") -> ReviewType.OTHER
        else -> ReviewType.UNKNOWN
    }
}

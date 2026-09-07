package me.alexy.hipipl.core.data.mapper

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
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
        for ((key, threadElement) in reviews) {
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

                val authorId = key.removePrefix("u").toIntOrNull()
                threads.add(ReviewThread(receivedList, responseList, isMutual, authorId))
            } catch (e: Exception) {
                // Skip malformed review threads
                continue
            }
        }
    }

    return UserReviews(
        totalReceived = totalReceived ?: 0,
        threads = threads,
        hasMore = hasMore ?: false,
        nextOffset = nextOffset,
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

// Parse v1 review types: "host", "guest", "companion", "driver", "passenger", "friend", "buddy", "other"
private fun String?.toReviewType(): ReviewType {
    return when (this) {
        "host" -> ReviewType.HOST
        "guest" -> ReviewType.GUEST
        "companion" -> ReviewType.COMPANION
        "driver" -> ReviewType.DRIVER
        "passenger" -> ReviewType.PASSENGER
        "friend" -> ReviewType.FRIEND
        "buddy" -> ReviewType.BUDDY
        "other" -> ReviewType.OTHER
        else -> ReviewType.UNKNOWN
    }
}

package me.alexy.hipipl.core.data.mapper

import me.alexy.hipipl.core.data.dto.MutualReviewDto
import me.alexy.hipipl.core.data.dto.ReviewDto
import me.alexy.hipipl.core.domain.MutualReview
import me.alexy.hipipl.core.domain.Review
import me.alexy.hipipl.core.domain.ReviewType

fun MutualReviewDto.toMutualReview(): MutualReview {
    return MutualReview(
        review = rec?.toReview(),
        response = ans?.toReview()
    )
}

fun ReviewDto.toReview(): Review {
    return Review(
        id = id ?: 0,
        receiverId = receiverId ?: 0,
        authorId = authorId ?: 0,
        authorName = authorName.orEmpty(),
        date = date.parseApiDateTime(),
        text = text.orEmpty(),
        photo = if (photo.isNullOrBlank()) null else photo,
        type = type.toReviewType(),
        mutal = mutal ?: 0
    )
}

private fun String?.toReviewType(): ReviewType {
    return when (this?.uppercase()) {
        "GUEST" -> ReviewType.GUEST
        "HOST" -> ReviewType.HOST
        else -> ReviewType.UNKNOWN
    }
}

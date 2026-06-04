package me.alexy.hipipl.feature.hostme.ui

import me.alexy.hipipl.core.domain.ContactType
import me.alexy.hipipl.core.domain.HostUser
import me.alexy.hipipl.core.domain.MutualReview
import me.alexy.hipipl.core.domain.Review
import me.alexy.hipipl.core.domain.ReviewType
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

fun HostUser.toHostDetailsUi(): HostDetailsUi {
    val ageText = if (age > 0) " ($age лет)" else ""
    val nameWithAge = "$name$ageText"
    
    val ratingText = "$averageRating* ($totalReviews)"
    
    val languagesText = userLanguages.joinToString(", ") { 
        "${it.langName}: ${it.level}" 
    }
    
    val contactsMap = contacts.mapKeys { (type, _) ->
        when (type) {
            ContactType.VK -> "VK"
            ContactType.TELEGRAM -> "Telegram"
            ContactType.FACEBOOK -> "Facebook"
            ContactType.PHONE -> "Phone"
            ContactType.OTHER -> "Other"
        }
    }

    return HostDetailsUi(
        userId = userId,
        name = name,
        photos = photos.map { it.url },
        languagesText = languagesText,
        cityText = host.city,
        nameWithAge = nameWithAge,
        ratingText = ratingText,
        donateAmount = donate,
        showDonation = donate > 0,
        description = description,
        hostText = host.text,
        contacts = contactsMap,
        hasContacts = contactsMap.isNotEmpty()
    )
}

fun MutualReview.toMutualReviewUi(): MutualReviewUi {
    return MutualReviewUi(
        review = review?.toReviewUi(null),
        response = response?.toReviewUi(review?.authorName)
    )
}

private fun Review.toReviewUi(receiverName: String?): ReviewUi {
    return ReviewUi(
        id = id,
        authorName = authorName,
        formattedDate = date.format(dateFormatter),
        text = text,
        photoUrl = if (photo.isNullOrBlank()) null else photo,
        isGuest = type == ReviewType.GUEST,
        receiverName = receiverName
    )
}

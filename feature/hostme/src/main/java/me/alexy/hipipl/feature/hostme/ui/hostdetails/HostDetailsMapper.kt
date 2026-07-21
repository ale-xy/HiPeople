package me.alexy.hipipl.feature.hostme.ui.hostdetails

import me.alexy.hipipl.core.domain.ContactType
import me.alexy.hipipl.core.domain.HostUser
import me.alexy.hipipl.core.domain.ReviewThread
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

fun ReviewThread.toReviewThreadUi(): MutualReviewUi {
    // Map thread to the existing MutualReviewUi structure
    // Take first received and first response for backward compatibility
    val firstReceived = received.firstOrNull()
    val firstResponse = response.firstOrNull()
    
    return MutualReviewUi(
        review = firstReceived?.toReviewUi(null),
        response = firstResponse?.toReviewUi(firstReceived?.authorName)
    )
}

private fun Review.toReviewUi(receiverName: String?): ReviewUi {
    return ReviewUi(
        id = id,
        authorName = authorName,
        formattedDate = date.format(dateFormatter),
        text = text,
        photoUrl = if (photo.isNullOrBlank()) null else photo,
        isGuest = type in listOf(ReviewType.SURF_POSITIVE, ReviewType.SURF_NEGATIVE),
        receiverName = receiverName
    )
}

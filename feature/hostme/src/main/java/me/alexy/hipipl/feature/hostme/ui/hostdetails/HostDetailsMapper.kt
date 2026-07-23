package me.alexy.hipipl.feature.hostme.ui.hostdetails

import me.alexy.hipipl.core.domain.ActivityStatus
import me.alexy.hipipl.core.domain.HostUser
import me.alexy.hipipl.core.domain.Review
import me.alexy.hipipl.core.domain.ReviewThread
import me.alexy.hipipl.core.domain.ReviewType
import me.alexy.hipipl.core.domain.UserLanguage
import me.alexy.hipipl.core.presentation.UiText
import me.alexy.hipipl.feature.hostitem.R
import me.alexy.hipipl.feature.hostme.ui.hostsearch.toGenderAccent
import kotlin.math.roundToInt
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

fun HostUser.toHostDetailsUi(): HostDetailsUi {
    val ageText = if (age > 0) " ($age лет)" else ""

    val languages = userLanguages.map { it.toLanguageUi() }

    val hostingParams = listOf(
        HostingParamUi(type = HostingParamType.SEPARATE_ROOM, isOk = host.separateRoom),
        HostingParamUi(type = HostingParamType.KIDS, isOk = host.allowKids),
        HostingParamUi(type = HostingParamType.PETS, isOk = host.petsAtHome),
    )

    return HostDetailsUi(
        userId = userId,
        name = name,
        ageText = ageText,
        genderAccent = gender.toGenderAccent(),
        photos = photos.map { it.url },
        totalReviews = totalReviews,
        ratingValueText = if (averageRating > 0f) averageRating.toString() else null,
        cityText = host.city,
        languages = languages,
        hostingParams = hostingParams,
        description = description,
        hostText = host.text,
        donateAmount = donate,
        showDonation = donate > 0,
        contacts = contacts,
        hasContacts = contacts.isNotEmpty(),
        hostListingId = host.hostId,
        lastActivityText = lastActivity?.toActivityStatusText(),
        vibeLabels = vibes.map { it.label },
    )
}

private fun ActivityStatus.toActivityStatusText(): UiText = when (this) {
    ActivityStatus.TODAY -> UiText.StringResource(R.string.activity_status_today)
    ActivityStatus.RECENTLY -> UiText.StringResource(R.string.activity_status_recently)
    ActivityStatus.LONG_AGO -> UiText.StringResource(R.string.activity_status_long_ago)
}

private fun UserLanguage.toLanguageUi(): LanguageUi {
    // Levels are reported on a 0..6 scale; the mockup shows up to 3 filled bars.
    val filledBars = (level / 6f * 3).roundToInt().coerceIn(1, 3)
    return LanguageUi(name = langName, filledBars = filledBars)
}

fun ReviewThread.toReviewGroupUi(hostName: String): ReviewGroupUi {
    return ReviewGroupUi(
        received = received.map { it.toReviewUi(receiverName = null) },
        responses = response.map { reply ->
            // API responses never carry a "name" field, so the reply's author is always the host being reviewed.
            reply.toReviewUi(receiverName = received.firstOrNull()?.authorName).copy(authorName = hostName)
        },
        isMutual = isMutual,
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

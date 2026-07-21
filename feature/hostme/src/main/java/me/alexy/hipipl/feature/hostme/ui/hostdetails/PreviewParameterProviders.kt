package me.alexy.hipipl.feature.hostme.ui.hostdetails

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import me.alexy.hipipl.core.presentation.UiText

private val sampleHostDetails = HostDetailsUi(
    userId = 1,
    name = "Anna",
    photos = emptyList(),
    languagesText = "English: B2, Russian: Native",
    cityText = "Moscow",
    nameWithAge = "Anna (28 лет)",
    ratingText = "4.5* (10)",
    donateAmount = 3,
    showDonation = true,
    description = "Friendly host, always happy to show guests around the city.",
    hostText = "I have a spare room and love meeting travelers.",
    contacts = mapOf("Telegram" to "annahost", "Phone" to "+79001234567"),
    hasContacts = true,
)

private val sampleReviews = listOf(
    MutualReviewUi(
        review = ReviewUi(
            id = 1,
            authorName = "Max",
            formattedDate = "12.05.2025",
            text = "Great host, very welcoming!",
            photoUrl = null,
            isGuest = true,
            receiverName = null,
        ),
        response = ReviewUi(
            id = 2,
            authorName = "Anna",
            formattedDate = "13.05.2025",
            text = "Thanks for staying, Max!",
            photoUrl = null,
            isGuest = false,
            receiverName = "Max",
        ),
    ),
)

class HostDetailsScreenPreviewParameterProvider : PreviewParameterProvider<HostDetailsState> {
    override val values = sequenceOf(
        HostDetailsState(isLoadingHost = true),
        HostDetailsState(isLoadingHost = false, hostError = UiText.DynamicString("Failed to load host")),
        HostDetailsState(
            isLoadingHost = false,
            host = sampleHostDetails,
            isLoadingReviews = false,
            reviews = sampleReviews,
        ),
    )
}

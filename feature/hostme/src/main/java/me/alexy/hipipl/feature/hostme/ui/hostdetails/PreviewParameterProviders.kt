package me.alexy.hipipl.feature.hostme.ui.hostdetails

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import me.alexy.hipipl.core.designsystem.GenderAccent
import me.alexy.hipipl.core.domain.ContactType
import me.alexy.hipipl.core.presentation.UiText

private val sampleHostDetails = HostDetailsUi(
    userId = 1,
    name = "Anna",
    ageText = " (28 лет)",
    genderAccent = GenderAccent.FEMALE,
    photos = emptyList(),
    totalReviews = 10,
    ratingValueText = "4.5",
    cityText = "Moscow",
    languages = listOf(
        LanguageUi(name = "Russian", filledBars = 3),
        LanguageUi(name = "English", filledBars = 2),
    ),
    hostingParams = listOf(
        HostingParamUi(type = HostingParamType.SEPARATE_ROOM, isOk = true),
        HostingParamUi(type = HostingParamType.KIDS, isOk = false),
        HostingParamUi(type = HostingParamType.PETS, isOk = true),
    ),
    description = "Friendly host, always happy to show guests around the city.",
    hostText = "I have a spare room and love meeting travelers.",
    donateAmount = 3,
    showDonation = true,
    contacts = mapOf(ContactType.TELEGRAM to "annahost", ContactType.PHONE to "+79001234567"),
    hasContacts = true,
)

private val sampleReviewGroups = listOf(
    ReviewGroupUi(
        received = listOf(
            ReviewUi(
                id = 1,
                authorName = "Max",
                formattedDate = "12.05.2025",
                text = "Great host, very welcoming!",
                photoUrl = null,
                isGuest = true,
                receiverName = null,
            )
        ),
        responses = listOf(
            ReviewUi(
                id = 2,
                authorName = "Anna",
                formattedDate = "13.05.2025",
                text = "Thanks for staying, Max!",
                photoUrl = null,
                isGuest = false,
                receiverName = "Max",
            )
        ),
        isMutual = true,
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
            reviewGroups = sampleReviewGroups,
        ),
    )
}

package me.alexy.hipipl.feature.hostme.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import me.alexy.hipipl.core.designsystem.GenderAccent
import me.alexy.hipipl.core.designsystem.components.TriState
import me.alexy.hipipl.core.presentation.UiText
import me.alexy.hipipl.feature.hostme.ui.HostCardUi
import me.alexy.hipipl.feature.hostme.ui.HostDetailsUi
import me.alexy.hipipl.feature.hostme.ui.HostSearchFiltersUi
import me.alexy.hipipl.feature.hostme.ui.HostsSearchState
import me.alexy.hipipl.feature.hostme.ui.HostDetailsState
import me.alexy.hipipl.feature.hostme.ui.LocationUi
import me.alexy.hipipl.feature.hostme.ui.MutualReviewUi
import me.alexy.hipipl.feature.hostme.ui.ReviewUi
import me.alexy.hipipl.feature.hostme.ui.SearchTab
import me.alexy.hipipl.feature.hostme.ui.UserCardUi
import me.alexy.hipipl.feature.hostme.ui.UserSearchState

private val sampleHosts = listOf(
    HostCardUi(
        hostId = 1,
        userId = 1,
        photoUrl = null,
        name = "Anna",
        ageText = " (28 лет)",
        referenceCount = 57,
        referenceScoreText = "9.4",
        separateRoom = true,
        kidsAllowed = true,
        petsAtHome = false,
        hasDistance = true,
        distanceValueText = "3.2",
        directionDegrees = 45f,
        city = "Moscow",
        genderAccent = GenderAccent.FEMALE,
    ),
    HostCardUi(
        hostId = 2,
        userId = 2,
        photoUrl = null,
        name = "Ivan Petrov",
        ageText = " (36 лет)",
        referenceCount = 12,
        referenceScoreText = null,
        separateRoom = false,
        kidsAllowed = false,
        petsAtHome = true,
        hasDistance = false,
        distanceValueText = "",
        directionDegrees = 0f,
        city = "Vidnoye",
        genderAccent = GenderAccent.MALE,
    ),
)

private val sampleLocationSuggestions = listOf(
    LocationUi(id = 1, displayName = "Moscow", regionLine = "Russia", type = "city"),
    LocationUi(id = 2, displayName = "Vidnoye", regionLine = "Russia, Moscow Oblast", type = "city"),
)

class HostsTabContentPreviewParameterProvider : PreviewParameterProvider<HostsSearchState> {
    override val values = sequenceOf(
        HostsSearchState(),
        HostsSearchState(locationQuery = "Moscow", selectedLocationId = 1, isLoadingHosts = true, totalFound = 0),
        HostsSearchState(
            locationQuery = "Moscow",
            selectedLocationId = 1,
            hostsError = UiText.DynamicString("Something went wrong"),
            totalFound = 0,
        ),
        HostsSearchState(locationQuery = "Moscow", selectedLocationId = 1, hostResults = emptyList(), totalFound = 0),
        HostsSearchState(
            locationQuery = "Moscow",
            selectedLocationId = 1,
            hostResults = sampleHosts,
            totalFound = sampleHosts.size,
        ),
        HostsSearchState(
            locationQuery = "Moscow",
            selectedLocationId = 1,
            hostResults = sampleHosts,
            totalFound = sampleHosts.size,
            filters = HostSearchFiltersUi(petsAtHome = TriState.YES),
        ),
    )
}

class LocationSearchOverlayPreviewParameterProvider : PreviewParameterProvider<HostsSearchState> {
    override val values = sequenceOf(
        HostsSearchState(locationQuery = "", isSearchSheetOpen = true),
        HostsSearchState(locationQuery = "Mos", isLoadingSuggestions = true, isSearchSheetOpen = true),
        HostsSearchState(
            locationQuery = "Mos",
            locationSuggestions = sampleLocationSuggestions,
            isSearchSheetOpen = true,
        ),
    )
}

class UsersTabContentPreviewParameterProvider : PreviewParameterProvider<UserSearchState> {
    override val values = sequenceOf(
        UserSearchState(userQuery = ""),
        UserSearchState(userQuery = "https://vk.com/id1", isSearchingUser = true),
        UserSearchState(
            userQuery = "https://vk.com/id1",
            userResult = UserCardUi(userId = 1, photoUrl = null, name = "Anna", ageText = " (28 лет)"),
        ),
        UserSearchState(
            userQuery = "https://vk.com/id1",
            userSearchError = UiText.DynamicString("User not found"),
        ),
    )
}

data class HostSearchScreenPreviewState(
    val selectedTab: SearchTab,
    val hostsState: HostsSearchState,
    val userState: UserSearchState,
)

class HostSearchScreenPreviewParameterProvider : PreviewParameterProvider<HostSearchScreenPreviewState> {
    override val values = sequenceOf(
        HostSearchScreenPreviewState(
            selectedTab = SearchTab.HOSTS,
            hostsState = HostsSearchState(locationQuery = "Moscow", hostResults = sampleHosts, totalFound = sampleHosts.size),
            userState = UserSearchState(),
        ),
        HostSearchScreenPreviewState(
            selectedTab = SearchTab.USERS,
            hostsState = HostsSearchState(),
            userState = UserSearchState(userQuery = "https://vk.com/id1"),
        ),
    )
}

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

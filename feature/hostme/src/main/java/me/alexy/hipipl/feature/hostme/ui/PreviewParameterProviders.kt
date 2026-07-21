package me.alexy.hipipl.feature.hostme.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import me.alexy.hipipl.core.designsystem.GenderAccent
import me.alexy.hipipl.feature.hostme.ui.hostsearch.HostCardUi
import me.alexy.hipipl.feature.hostme.ui.hostsearch.HostsSearchState
import me.alexy.hipipl.feature.hostme.ui.usersearch.UserSearchState

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

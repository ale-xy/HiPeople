package me.alexy.hipipl.feature.hostme.ui.hostsearch

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import me.alexy.hipipl.core.designsystem.GenderAccent
import me.alexy.hipipl.core.designsystem.components.TriState
import me.alexy.hipipl.core.presentation.UiText

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

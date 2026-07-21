package me.alexy.hipipl.feature.hostme.ui.hostsearch

import me.alexy.hipipl.core.designsystem.GenderAccent
import me.alexy.hipipl.core.designsystem.components.TriState
import me.alexy.hipipl.core.presentation.UiText

data class LocationUi(
    val id: Int,
    val displayName: String,
    val regionLine: String,
    val type: String
)

data class HostSearchFiltersUi(
    val separateRoom: TriState = TriState.UNSPECIFIED,
    val kidsAllowed: TriState = TriState.UNSPECIFIED,
    val petsAtHome: TriState = TriState.UNSPECIFIED,
) {
    val hasActiveFilters: Boolean
        get() = separateRoom != TriState.UNSPECIFIED ||
            kidsAllowed != TriState.UNSPECIFIED ||
            petsAtHome != TriState.UNSPECIFIED
}

data class HostCardUi(
    val hostId: Int,
    val userId: Int,
    val photoUrl: String?,
    val name: String,
    val ageText: String,
    val referenceCount: Int,
    val referenceScoreText: String?,
    val separateRoom: Boolean,
    val kidsAllowed: Boolean,
    val petsAtHome: Boolean,
    val hasDistance: Boolean,
    val distanceValueText: String,
    val directionDegrees: Float,
    val city: String,
    val genderAccent: GenderAccent,
)

data class HostsSearchState(
    val locationQuery: String = "",
    val selectedLocationId: Int? = null,
    val selectedLocationType: String = "city",
    val locationSuggestions: List<LocationUi> = emptyList(),
    val isSearchSheetOpen: Boolean = false,
    val isLoadingSuggestions: Boolean = false,

    val isFilterSheetOpen: Boolean = false,
    val filters: HostSearchFiltersUi = HostSearchFiltersUi(),

    val isLoadingHosts: Boolean = false,
    val hostResults: List<HostCardUi> = emptyList(),
    val totalFound: Int = 0,
    val hostsError: UiText? = null,
)

sealed interface HostsSearchAction {
    data object OnOpenSearchSheet : HostsSearchAction
    data object OnDismissSearchSheet : HostsSearchAction
    data class OnLocationQueryChange(val text: String) : HostsSearchAction
    data class OnLocationSuggestionClick(val location: LocationUi) : HostsSearchAction
    data object OnFilterIconClick : HostsSearchAction
    data object OnDismissFilterSheet : HostsSearchAction
    data class OnFilterChange(val filters: HostSearchFiltersUi) : HostsSearchAction
    data object OnApplyFilters : HostsSearchAction
    data class OnHostClick(val hostId: Int, val userId: Int) : HostsSearchAction
    data object OnHostsOnMapClick : HostsSearchAction
}

sealed interface HostsSearchEvent {
    data class NavigateToHostDetails(val hostId: Int, val userId: Int) : HostsSearchEvent
    data object NavigateToHostsMap : HostsSearchEvent
}

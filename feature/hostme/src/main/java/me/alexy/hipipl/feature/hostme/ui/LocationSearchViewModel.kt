package me.alexy.hipipl.feature.hostme.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.alexy.hipipl.core.domain.GeoRemoteDataSource
import me.alexy.hipipl.core.domain.onFailure
import me.alexy.hipipl.core.domain.onSuccess
import me.alexy.hipipl.core.presentation.UiText
import me.alexy.hipipl.core.presentation.toUiText

data class LocationSearchState(
    val searchText: String = "",
    val isSearchExpanded: Boolean = false,
    val isLoading: Boolean = false,
    val locations: List<LocationUi> = emptyList(),
    val error: UiText? = null
)

sealed interface LocationSearchAction {
    data class OnSearchTextChange(val text: String) : LocationSearchAction
    data object OnSearch : LocationSearchAction
    data object OnClearSearch : LocationSearchAction
    data class OnLocationClick(val locationId: Int, val locationName: String) : LocationSearchAction
}

sealed interface LocationSearchEvent {
    data class NavigateToHostList(val locationId: Int, val locationName: String) : LocationSearchEvent
}

data class LocationUi(
    val id: Int,
    val displayName: String,
    val regionLine: String
)

class LocationSearchViewModel(
    private val geoDataSource: GeoRemoteDataSource,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(
        LocationSearchState(
            searchText = savedStateHandle["searchText"] ?: ""
        )
    )
    val state: StateFlow<LocationSearchState> = _state

    private val _events = Channel<LocationSearchEvent>()
    val events: Flow<LocationSearchEvent> = _events.receiveAsFlow()

    fun onAction(action: LocationSearchAction) {
        when (action) {
            is LocationSearchAction.OnSearchTextChange -> {
                savedStateHandle["searchText"] = action.text
                _state.update {
                    it.copy(
                        searchText = action.text,
                        isSearchExpanded = false
                    )
                }
            }

            is LocationSearchAction.OnSearch -> {
                searchLocations()
            }

            is LocationSearchAction.OnClearSearch -> {
                savedStateHandle["searchText"] = ""
                _state.update {
                    it.copy(
                        searchText = "",
                        isSearchExpanded = false,
                        locations = emptyList(),
                        error = null
                    )
                }
            }

            is LocationSearchAction.OnLocationClick -> {
                viewModelScope.launch {
                    _events.send(
                        LocationSearchEvent.NavigateToHostList(
                            locationId = action.locationId,
                            locationName = action.locationName
                        )
                    )
                }
            }
        }
    }

    private fun searchLocations() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, isSearchExpanded = false, error = null) }

            geoDataSource.getLocationsByName(_state.value.searchText.trim())
                .onSuccess { locations ->
                    _state.update {
                        it.copy(
                            locations = locations.map { loc -> loc.toLocationUi() },
                            isLoading = false,
                            isSearchExpanded = true
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            locations = emptyList(),
                            isLoading = false,
                            isSearchExpanded = false,
                            error = error.toUiText()
                        )
                    }
                }
        }
    }
}

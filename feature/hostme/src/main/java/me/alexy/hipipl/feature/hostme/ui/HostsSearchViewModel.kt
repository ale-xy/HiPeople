package me.alexy.hipipl.feature.hostme.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.alexy.hipipl.core.domain.GeoRemoteDataSource
import me.alexy.hipipl.core.domain.HostRemoteDataSource
import me.alexy.hipipl.core.domain.onFailure
import me.alexy.hipipl.core.domain.onSuccess
import me.alexy.hipipl.core.presentation.toUiText

class HostsSearchViewModel(
    private val geoDataSource: GeoRemoteDataSource,
    private val hostDataSource: HostRemoteDataSource,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _state = MutableStateFlow(
        HostsSearchState(locationQuery = savedStateHandle["locationQuery"] ?: "")
    )
    val state: StateFlow<HostsSearchState> = _state

    private val _events = Channel<HostsSearchEvent>()
    val events: Flow<HostsSearchEvent> = _events.receiveAsFlow()

    private var suggestionsJob: Job? = null

    fun onAction(action: HostsSearchAction) {
        when (action) {
            HostsSearchAction.OnOpenSearchSheet -> {
                _state.update { it.copy(isSearchSheetOpen = true) }
            }

            HostsSearchAction.OnDismissSearchSheet -> {
                suggestionsJob?.cancel()
                _state.update {
                    it.copy(
                        isSearchSheetOpen = false,
                        locationSuggestions = emptyList(),
                        isLoadingSuggestions = false,
                    )
                }
            }

            is HostsSearchAction.OnLocationQueryChange -> {
                savedStateHandle["locationQuery"] = action.text
                _state.update { it.copy(locationQuery = action.text) }
                searchLocationSuggestions(action.text)
            }

            is HostsSearchAction.OnLocationSuggestionClick -> {
                suggestionsJob?.cancel()
                savedStateHandle["locationQuery"] = action.location.displayName
                _state.update {
                    it.copy(
                        locationQuery = action.location.displayName,
                        selectedLocationId = action.location.id,
                        selectedLocationType = action.location.type,
                        locationSuggestions = emptyList(),
                        isSearchSheetOpen = false,
                    )
                }
                loadHosts()
            }

            HostsSearchAction.OnFilterIconClick -> {
                _state.update { it.copy(isFilterSheetOpen = true) }
            }

            HostsSearchAction.OnDismissFilterSheet -> {
                _state.update { it.copy(isFilterSheetOpen = false) }
            }

            is HostsSearchAction.OnFilterChange -> {
                _state.update { it.copy(filters = action.filters) }
            }

            HostsSearchAction.OnApplyFilters -> {
                _state.update { it.copy(isFilterSheetOpen = false) }
                loadHosts()
            }

            is HostsSearchAction.OnHostClick -> {
                viewModelScope.launch {
                    _events.send(HostsSearchEvent.NavigateToHostDetails(action.hostId, action.userId))
                }
            }

            HostsSearchAction.OnHostsOnMapClick -> {
                viewModelScope.launch {
                    _events.send(HostsSearchEvent.NavigateToHostsMap)
                }
            }
        }
    }

    private fun searchLocationSuggestions(query: String) {
        suggestionsJob?.cancel()
        val trimmed = query.trim()
        if (trimmed.length < 2) {
            _state.update {
                it.copy(locationSuggestions = emptyList(), isLoadingSuggestions = false)
            }
            return
        }
        suggestionsJob = viewModelScope.launch {
            delay(300)
            _state.update { it.copy(isLoadingSuggestions = true) }

            geoDataSource.getLocationsByName(trimmed)
                .onSuccess { locations ->
                    val locationsUi = locations.map { it.toLocationUi() }
                    _state.update {
                        it.copy(
                            locationSuggestions = locationsUi,
                            isLoadingSuggestions = false,
                        )
                    }
                }
                .onFailure {
                    _state.update {
                        it.copy(locationSuggestions = emptyList(), isLoadingSuggestions = false)
                    }
                }
        }
    }

    private fun loadHosts() {
        val locationId = _state.value.selectedLocationId ?: return
        val locationType = _state.value.selectedLocationType

        viewModelScope.launch {
            _state.update { it.copy(isLoadingHosts = true, hostsError = null) }

            hostDataSource.searchHosts(
                locationId = locationId,
                locationType = locationType,
                userId = null, // TODO: Phase 1 - real auth
                filters = _state.value.filters.toDomain(),
            )
                .onSuccess { result ->
                    _state.update {
                        it.copy(
                            hostResults = result.hosts.map { host -> host.toHostCardUi() },
                            totalFound = result.totalFound,
                            isLoadingHosts = false,
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            hostResults = emptyList(),
                            totalFound = 0,
                            isLoadingHosts = false,
                            hostsError = error.toUiText(),
                        )
                    }
                }
        }
    }
}

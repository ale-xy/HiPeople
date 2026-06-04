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
import me.alexy.hipipl.core.domain.HostRemoteDataSource
import me.alexy.hipipl.core.domain.onFailure
import me.alexy.hipipl.core.domain.onSuccess
import me.alexy.hipipl.core.presentation.UiText
import me.alexy.hipipl.core.presentation.toUiText
import me.alexy.hipipl.feature.hostme.HostListByLocationRoute

data class HostListByLocationState(
    val isLoading: Boolean = false,
    val locationName: String = "",
    val hosts: List<HostListItemUi> = emptyList(),
    val error: UiText? = null
)

sealed interface HostListByLocationAction {
    data class OnHostClick(val hostId: Int, val userId: Int) : HostListByLocationAction
}

sealed interface HostListByLocationEvent {
    data class NavigateToHostDetails(val hostId: Int, val userId: Int) : HostListByLocationEvent
}

data class HostListItemUi(
    val hostId: Int,
    val userId: Int,
    val photoUrl: String?,
    val titleLine: String,
    val locationLine: String,
    val description: String
)

class HostListByLocationViewModel(
    private val hostDataSource: HostRemoteDataSource,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val args = HostListByLocationRoute.from(savedStateHandle)

    private val _state = MutableStateFlow(
        HostListByLocationState(
            locationName = args.locationName
        )
    )
    val state: StateFlow<HostListByLocationState> = _state

    private val _events = Channel<HostListByLocationEvent>()
    val events: Flow<HostListByLocationEvent> = _events.receiveAsFlow()

    init {
        loadHosts()
    }

    fun onAction(action: HostListByLocationAction) {
        when (action) {
            is HostListByLocationAction.OnHostClick -> {
                viewModelScope.launch {
                    _events.send(
                        HostListByLocationEvent.NavigateToHostDetails(
                            hostId = action.hostId,
                            userId = action.userId
                        )
                    )
                }
            }
        }
    }

    private fun loadHosts() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            hostDataSource.getHostsForLocation(
                locationId = args.locationId,
                userId = 1, // TODO: real auth
                token = "12345" // TODO: real auth
            )
                .onSuccess { hosts ->
                    _state.update {
                        it.copy(
                            hosts = hosts.map { host -> host.toHostListItemUi() },
                            isLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            hosts = emptyList(),
                            isLoading = false,
                            error = error.toUiText()
                        )
                    }
                }
        }
    }
}

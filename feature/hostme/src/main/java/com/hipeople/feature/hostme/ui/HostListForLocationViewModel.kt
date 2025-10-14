package com.hipeople.feature.hostme.ui

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hipeople.feature.hostme.HostListByLocation
import com.hipeople.model.HostUser
import com.hipeople.repository.HostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HostListForLocationViewModel @Inject constructor(
    private val repository: HostRepository,
    savedStateHandle: SavedStateHandle,
): ViewModel() {
    private val _uiState = MutableStateFlow<HostListUiState>(HostListUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val args = HostListByLocation.from(savedStateHandle)

    val locationName = args.locationName

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = HostListUiState.Loading
            try {
                val list = repository.getHostForLocation(args.locationId)
                _uiState.value = HostListUiState.Success(list)
            } catch (e: Throwable) {
                Log.e("HostListForLocation", "getHostForLocation failed", e)
                _uiState.value = HostListUiState.Error(e.message.orEmpty())
            }
        }
    }

}

sealed interface HostListUiState {
    data object Loading: HostListUiState
    data class Error(val error: String): HostListUiState
    data class Success(val list: List<HostUser>): HostListUiState
}
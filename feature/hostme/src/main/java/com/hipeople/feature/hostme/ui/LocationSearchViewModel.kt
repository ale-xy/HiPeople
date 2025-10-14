package com.hipeople.feature.hostme.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hipeople.model.Location
import com.hipeople.repository.GeoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LocationSearchViewModel @Inject constructor(
    private val repository: GeoRepository
) : ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _searchExpanded = MutableStateFlow(false)
    val searchExpanded = _searchExpanded.asStateFlow()

    private val _locationList = MutableStateFlow<List<Location>>(listOf())
    val locationList = _locationList.asStateFlow()

    fun onSearchTextChange(text: String) {
        _searchText.value = text
        _searchExpanded.value = false
    }

    fun onSearch() {
        viewModelScope.launch {
            _searchExpanded.value = false

            withContext(Dispatchers.IO) {
                try {
                    _locationList.value = repository.getLocationsByName(_searchText.value.trim())
                } catch (e: Throwable) {
                    Log.e("LocationSearchViewModel", "Location search failed", e)
                    _locationList.value = listOf()
                }
            }

            _searchExpanded.value = true
        }
    }
}

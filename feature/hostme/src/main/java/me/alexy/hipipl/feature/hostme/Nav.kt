package me.alexy.hipipl.feature.hostme

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
object LocationSearch

@Serializable
data class HostListByLocation(val locationId: Int, val locationName: String) {
    companion object {
        fun from(savedStateHandle: SavedStateHandle) =
            savedStateHandle.toRoute<HostListByLocation>()
    }
}

@Serializable
data class HostDetails(val hostId: Int) {
    companion object {
        fun from(savedStateHandle: SavedStateHandle) =
            savedStateHandle.toRoute<HostDetails>()
    }
}
package me.alexy.hipipl.feature.hostme

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
object LocationSearchRoute

@Serializable
data class HostListByLocationRoute(
    val locationId: Int,
    val locationName: String,
    val locationType: String
) {
    companion object {
        fun from(savedStateHandle: SavedStateHandle) =
            savedStateHandle.toRoute<HostListByLocationRoute>()
    }
}

@Serializable
data class HostDetailsRoute(val hostId: Int, val userId: Int) {
    companion object {
        fun from(savedStateHandle: SavedStateHandle) =
            savedStateHandle.toRoute<HostDetailsRoute>()
    }
}

package com.hippl.network

import com.hippl.network.model.NetworkGeoName
import com.hippl.network.model.NetworkHostUser

interface NetworkDataSource {
    suspend fun getGeoName(name: String): List<NetworkGeoName>

    suspend fun getHostsForLocation(locationId: Int): List<NetworkHostUser>
}
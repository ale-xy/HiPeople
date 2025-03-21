package com.hippl.network

import com.hippl.network.model.NetworkGeoName
import com.hippl.network.model.NetworkHostUser
import com.hippl.network.model.NetworkMutualReview

interface NetworkDataSource {
    suspend fun getGeoName(name: String): List<NetworkGeoName>

    suspend fun getHostsForLocation(locationId: Int): List<NetworkHostUser>

    suspend fun getHost(hostId: Int): NetworkHostUser

    suspend fun getReviews(hostId: Int): List<NetworkMutualReview>
}
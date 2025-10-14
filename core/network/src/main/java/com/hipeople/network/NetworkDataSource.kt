package com.hipeople.network

import com.hipeople.network.model.NetworkGeoName
import com.hipeople.network.model.NetworkHostUser
import com.hipeople.network.model.NetworkMutualReview

interface NetworkDataSource {
    suspend fun getGeoName(name: String): List<NetworkGeoName>

    suspend fun getHostsForLocation(locationId: Int): List<NetworkHostUser>

    suspend fun getHost(hostId: Int): NetworkHostUser

    suspend fun getReviews(hostId: Int): List<NetworkMutualReview>
}
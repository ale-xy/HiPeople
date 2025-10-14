package com.hipeople.repository

import com.hipeople.model.HostUser
import com.hipeople.model.MutualReview

interface HostRepository {
    suspend fun getHostForLocation(locationId: Int): List<HostUser>
    suspend fun getHost(hostId: Int): HostUser
    suspend fun getReviews(hostId: Int): List<MutualReview>
}

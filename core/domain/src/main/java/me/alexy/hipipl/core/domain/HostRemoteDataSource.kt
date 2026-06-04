package me.alexy.hipipl.core.domain

interface HostRemoteDataSource {
    suspend fun getHostsForLocation(
        locationId: Int,
        userId: Int,
        token: String
    ): Result<List<HostUser>, DataError.Network>

    suspend fun getHost(
        hostId: Int,
        userId: Int,
        token: String
    ): Result<HostUser, DataError.Network>

    suspend fun getReviews(
        hostId: Int,
        userId: Int,
        token: String
    ): Result<List<MutualReview>, DataError.Network>
}

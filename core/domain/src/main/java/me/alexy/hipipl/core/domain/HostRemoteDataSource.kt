package me.alexy.hipipl.core.domain

interface HostRemoteDataSource {
    suspend fun getHostsForLocation(
        locationId: Int,
        locationType: String,
        userId: Int?,
    ): Result<List<HostUser>, DataError.Network>

    suspend fun getHost(
        hostId: Int,
        userId: Int?,
    ): Result<HostUser, DataError.Network>

    suspend fun getReviews(
        userId: Int,
        viewerId: Int?,
    ): Result<UserReviews, DataError.Network>
}

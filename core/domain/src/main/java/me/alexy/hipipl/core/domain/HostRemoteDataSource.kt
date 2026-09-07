package me.alexy.hipipl.core.domain

interface HostRemoteDataSource {
    suspend fun searchHosts(
        locationId: Int,
        locationType: String,
        userId: Int?,
        filters: HostSearchFilters = HostSearchFilters(),
        offset: Int = 0,
        limit: Int = 20,
    ): Result<HostSearchResult, DataError.Network>

    suspend fun getHost(
        hostId: Int,
        userId: Int?,
    ): Result<HostUser, DataError.Network>

    suspend fun getReviews(
        userId: Int,
        viewerId: Int?,
        offset: Int = 0,
        limit: Int = 20,
    ): Result<UserReviews, DataError.Network>
}

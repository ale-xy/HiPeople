package me.alexy.hipipl.core.data

import io.ktor.client.HttpClient
import me.alexy.hipipl.core.data.dto.HostUserDto
import me.alexy.hipipl.core.data.dto.HostsPageDto
import me.alexy.hipipl.core.data.dto.UserReviewsResponseDto
import me.alexy.hipipl.core.data.mapper.toHostSearchResult
import me.alexy.hipipl.core.data.mapper.toHostUser
import me.alexy.hipipl.core.data.mapper.toQueryParams
import me.alexy.hipipl.core.data.mapper.toUserReviews
import me.alexy.hipipl.core.domain.DataError
import me.alexy.hipipl.core.domain.HostRemoteDataSource
import me.alexy.hipipl.core.domain.HostSearchFilters
import me.alexy.hipipl.core.domain.HostSearchResult
import me.alexy.hipipl.core.domain.HostUser
import me.alexy.hipipl.core.domain.UserReviews
import me.alexy.hipipl.core.domain.Result
import me.alexy.hipipl.core.domain.map

class KtorHostDataSource(
    private val httpClient: HttpClient
) : HostRemoteDataSource {

    // TODO: Replace with real auth from Phase 1 of IMPLEMENTATION_PLAN.md
    private companion object {
        const val TEMP_USER_ID = 1
    }

    override suspend fun searchHosts(
        locationId: Int,
        locationType: String,
        userId: Int?,
        filters: HostSearchFilters,
        offset: Int,
        limit: Int,
    ): Result<HostSearchResult, DataError.Network> {
        // Build query params based on location type (city_id, country_id, or region_id)
        val locationParam = when (locationType) {
            "city" -> "city_id"
            "country" -> "country_id"
            "region" -> "region_id"
            else -> "city_id"
        }

        val queryParams = mutableMapOf<String, Any>(
            locationParam to locationId,
            "offset" to offset,
            "limit" to limit,
        )
        queryParams.putAll(filters.toQueryParams())

        // Add optional user parameter (no token in v1)
        userId?.let { queryParams["user"] = it }

        return httpClient.getV1<HostsPageDto>(
            route = "api/v1/hosts",
            queryParameters = queryParams
        ).map { page ->
            page.toHostSearchResult()
        }
    }

    override suspend fun getHost(
        hostId: Int,
        userId: Int?,
    ): Result<HostUser, DataError.Network> {
        val queryParams = mutableMapOf<String, Any>()
        
        // Add optional user parameter (no token in v1)
        userId?.let { queryParams["user"] = it }
        
        val result = httpClient.getV1<HostUserDto>(
            route = "api/v1/hosts/$hostId",
            queryParameters = queryParams
        )
        return when (result) {
            is Result.Success -> result.data.toHostUser()
                ?.let { Result.Success(it) }
                ?: Result.Error(DataError.Network.SERIALIZATION)

            is Result.Error -> result
        }
    }

    override suspend fun getReviews(
        userId: Int,
        viewerId: Int?,
    ): Result<UserReviews, DataError.Network> {
        val queryParams = mutableMapOf<String, Any>()
        
        // Add optional viewer_id parameter (no token in v1)
        viewerId?.let { queryParams["viewer_id"] = it }
        
        return httpClient.getV1<UserReviewsResponseDto>(
            route = "api/v1/users/$userId/reviews",
            queryParameters = queryParams
        ).map { response ->
            response.toUserReviews()
        }
    }
}

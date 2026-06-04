package me.alexy.hipipl.core.data

import io.ktor.client.HttpClient
import me.alexy.hipipl.core.data.dto.HostUserDto
import me.alexy.hipipl.core.data.dto.MutualReviewDto
import me.alexy.hipipl.core.data.mapper.toHostUser
import me.alexy.hipipl.core.data.mapper.toMutualReview
import me.alexy.hipipl.core.domain.DataError
import me.alexy.hipipl.core.domain.HostRemoteDataSource
import me.alexy.hipipl.core.domain.HostUser
import me.alexy.hipipl.core.domain.MutualReview
import me.alexy.hipipl.core.domain.Result
import me.alexy.hipipl.core.domain.map

class KtorHostDataSource(
    private val httpClient: HttpClient
) : HostRemoteDataSource {

    // TODO: Replace with real auth from Phase 0/1 of IMPLEMENTATION_PLAN.md
    private companion object {
        const val TEMP_USER_ID = 1
        const val TEMP_TOKEN = "12345"
    }

    override suspend fun getHostsForLocation(
        locationId: Int,
        userId: Int,
        token: String
    ): Result<List<HostUser>, DataError.Network> {
        return httpClient.get<List<HostUserDto>>(
            route = "api.php",
            queryParameters = mapOf(
                "method" to "get_hosts_city",
                "id" to locationId,
                "user" to TEMP_USER_ID,
                "token" to TEMP_TOKEN
            )
        ).map { dtos ->
            dtos.mapNotNull { it.toHostUser() }
        }
    }

    override suspend fun getHost(
        hostId: Int,
        userId: Int,
        token: String
    ): Result<HostUser, DataError.Network> {
        val result = httpClient.get<HostUserDto>(
            route = "api.php",
            queryParameters = mapOf(
                "method" to "get_host",
                "host" to hostId,
                "user" to TEMP_USER_ID,
                "token" to TEMP_TOKEN
            )
        )
        return when (result) {
            is Result.Success -> result.data.toHostUser()
                ?.let { Result.Success(it) }
                ?: Result.Error(DataError.Network.SERIALIZATION)

            is Result.Error -> result
        }
    }

    override suspend fun getReviews(
        hostId: Int,
        userId: Int,
        token: String
    ): Result<List<MutualReview>, DataError.Network> {
        return httpClient.get<List<MutualReviewDto>>(
            route = "api.php",
            queryParameters = mapOf(
                "method" to "get_rev",
                "id" to hostId,
                "user" to TEMP_USER_ID,
                "token" to TEMP_TOKEN
            )
        ).map { dtos ->
            dtos.map { it.toMutualReview() }
        }
    }
}

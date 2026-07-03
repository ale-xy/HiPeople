package me.alexy.hipipl.core.data

import io.ktor.client.HttpClient
import me.alexy.hipipl.core.data.dto.HostMapPageDto
import me.alexy.hipipl.core.data.mapper.toHostMapMarker
import me.alexy.hipipl.core.data.mapper.toQueryParams
import me.alexy.hipipl.core.domain.DataError
import me.alexy.hipipl.core.domain.HostMapMarker
import me.alexy.hipipl.core.domain.HostMapRemoteDataSource
import me.alexy.hipipl.core.domain.HostSearchFilters
import me.alexy.hipipl.core.domain.Result
import me.alexy.hipipl.core.domain.map

class KtorHostMapDataSource(
    private val httpClient: HttpClient
) : HostMapRemoteDataSource {

    override suspend fun getHostsInBounds(
        latMin: Double,
        lonMin: Double,
        latMax: Double,
        lonMax: Double,
        zoom: Int?,
        userId: Int?,
        filters: HostSearchFilters,
        limit: Int,
    ): Result<List<HostMapMarker>, DataError.Network> {
        val queryParams = mutableMapOf<String, Any>(
            "lat_min" to latMin,
            "lon_min" to lonMin,
            "lat_max" to latMax,
            "lon_max" to lonMax,
            "limit" to limit,
        )
        zoom?.let { queryParams["zoom"] = it }
        userId?.let { queryParams["user"] = it }
        queryParams.putAll(filters.toQueryParams())

        return httpClient.getV1<HostMapPageDto>(
            route = "api/v1/hosts/map",
            queryParameters = queryParams
        ).map { page ->
            page.hosts.mapNotNull { it.toHostMapMarker() }
        }
    }
}

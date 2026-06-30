package me.alexy.hipipl.core.data

import io.ktor.client.HttpClient
import me.alexy.hipipl.core.data.dto.GeoNameDto
import me.alexy.hipipl.core.data.mapper.toLocation
import me.alexy.hipipl.core.domain.DataError
import me.alexy.hipipl.core.domain.GeoRemoteDataSource
import me.alexy.hipipl.core.domain.Location
import me.alexy.hipipl.core.domain.Result
import me.alexy.hipipl.core.domain.map

class KtorGeoDataSource(
    private val httpClient: HttpClient
) : GeoRemoteDataSource {

    override suspend fun getLocationsByName(name: String): Result<List<Location>, DataError.Network> {
        return httpClient.getV1<List<GeoNameDto>>(
            route = "api/v1/geo/search",
            queryParameters = mapOf(
                "query" to name
            )
        ).map { dtos ->
            dtos.mapNotNull { it.toLocation() }
        }
    }
}

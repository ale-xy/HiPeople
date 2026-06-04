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
        return httpClient.get<List<GeoNameDto>>(
            route = "api.php",
            queryParameters = mapOf(
                "method" to "get_geo_name",
                "name" to name
            )
        ).map { dtos ->
            dtos.mapNotNull { it.toLocation() }
        }
    }
}

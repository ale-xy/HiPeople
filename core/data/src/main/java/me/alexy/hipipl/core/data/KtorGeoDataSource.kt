package me.alexy.hipipl.core.data

import io.ktor.client.HttpClient
import me.alexy.hipipl.core.data.dto.GeoSearchResponseDto
import me.alexy.hipipl.core.data.mapper.toLocation
import me.alexy.hipipl.core.domain.DataError
import me.alexy.hipipl.core.domain.GeoRemoteDataSource
import me.alexy.hipipl.core.domain.Location
import me.alexy.hipipl.core.domain.Result
import me.alexy.hipipl.core.domain.map

class KtorGeoDataSource(
    private val httpClient: HttpClient
) : GeoRemoteDataSource {

    // GET /geo/search is deprecated/legacy; geo-name search now lives behind the
    // universal get_search_find_multi endpoint's "type": "geo" branch.
    override suspend fun getLocationsByName(name: String): Result<List<Location>, DataError.Network> {
        return httpClient.getV1<GeoSearchResponseDto>(
            route = "api/v1/get_search_find_multi",
            queryParameters = mapOf(
                "query" to name
            )
        ).map { response ->
            response.results.orEmpty().mapNotNull { it.toLocation() }
        }
    }
}

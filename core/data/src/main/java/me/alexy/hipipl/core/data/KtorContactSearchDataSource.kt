package me.alexy.hipipl.core.data

import io.ktor.client.HttpClient
import me.alexy.hipipl.core.data.dto.ContactSearchResponseDto
import me.alexy.hipipl.core.data.mapper.toContactSearchResult
import me.alexy.hipipl.core.domain.ContactSearchRemoteDataSource
import me.alexy.hipipl.core.domain.ContactSearchResult
import me.alexy.hipipl.core.domain.DataError
import me.alexy.hipipl.core.domain.Result
import me.alexy.hipipl.core.domain.map

class KtorContactSearchDataSource(
    private val httpClient: HttpClient
) : ContactSearchRemoteDataSource {

    override suspend fun findByQuery(
        query: String,
        userId: Int?,
    ): Result<ContactSearchResult, DataError.Network> {
        val queryParams = mutableMapOf<String, Any>("query" to query)
        userId?.let { queryParams["user"] = it }

        return httpClient.getV1<ContactSearchResponseDto>(
            route = "api/v1/get_search_find_multi",
            queryParameters = queryParams
        ).map { it.toContactSearchResult() }
    }
}

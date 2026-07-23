package me.alexy.hipipl.core.data

import io.ktor.client.HttpClient
import me.alexy.hipipl.core.data.dto.FavoriteRequestDto
import me.alexy.hipipl.core.data.dto.FavoriteResponseDto
import me.alexy.hipipl.core.domain.DataError
import me.alexy.hipipl.core.domain.EmptyResult
import me.alexy.hipipl.core.domain.FavoritesRemoteDataSource
import me.alexy.hipipl.core.domain.asEmptyResult

class KtorFavoritesDataSource(
    private val httpClient: HttpClient
) : FavoritesRemoteDataSource {

    override suspend fun addFavorite(contentId: Int, type: String): EmptyResult<DataError.Network> {
        return httpClient.postV1<FavoriteRequestDto, FavoriteResponseDto>(
            route = "api/v1/favorites",
            body = FavoriteRequestDto(content = contentId, type = type)
        ).asEmptyResult()
    }

    override suspend fun removeFavorite(contentId: Int, type: String): EmptyResult<DataError.Network> {
        return httpClient.deleteV1<FavoriteResponseDto>(
            route = "api/v1/favorites/$contentId",
            queryParameters = mapOf("type" to type)
        ).asEmptyResult()
    }
}

package me.alexy.hipipl.core.domain

interface FavoritesRemoteDataSource {
    suspend fun addFavorite(contentId: Int, type: String): EmptyResult<DataError.Network>
    suspend fun removeFavorite(contentId: Int, type: String): EmptyResult<DataError.Network>
}

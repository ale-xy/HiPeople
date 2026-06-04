package me.alexy.hipipl.core.domain

interface GeoRemoteDataSource {
    suspend fun getLocationsByName(name: String): Result<List<Location>, DataError.Network>
}

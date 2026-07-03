package me.alexy.hipipl.core.domain

interface HostMapRemoteDataSource {
    suspend fun getHostsInBounds(
        latMin: Double,
        lonMin: Double,
        latMax: Double,
        lonMax: Double,
        zoom: Int? = null,
        userId: Int? = null,
        filters: HostSearchFilters = HostSearchFilters(),
        limit: Int = 500,
    ): Result<List<HostMapMarker>, DataError.Network>
}

data class HostMapMarker(
    val id: Int,
    val lat: Double,
    val lon: Double,
    val gender: Gender,
    val name: String,
    val separateRoom: Boolean,
    val kidsAllowed: Boolean,
    val petsAtHome: Boolean,
    val accurate: Boolean,
)

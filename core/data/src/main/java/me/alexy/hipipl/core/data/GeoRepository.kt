package me.alexy.hipipl.core.data

import com.hippl.model.Location
import com.hippl.network.NetworkDataSource
import com.hippl.network.model.NetworkGeoName
import javax.inject.Inject

interface GeoRepository {
    suspend fun getLocationsByName(name: String): List<Location>
}

class DefaultGeoRepository @Inject constructor(
    private val dataSource: NetworkDataSource
) : GeoRepository {
    override suspend fun getLocationsByName(name: String): List<Location> =
        dataSource.getGeoName(name).map { it.toLocation() }
}

private fun NetworkGeoName.toLocation() = Location(
    id = id,
    name = name.orEmpty(),
    nameEn = nameEn.orEmpty(),
    regionId = region ?: 0,
    regionName = regionName.orEmpty(),
    countryId = country,
    countryName = countryName.orEmpty(),
    countryNameEn = countryNameEn.orEmpty(),
)
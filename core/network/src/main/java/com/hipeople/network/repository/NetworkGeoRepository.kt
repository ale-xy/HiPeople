package com.hipeople.network.repository

import com.hipeople.model.Location
import com.hipeople.network.NetworkDataSource
import com.hipeople.network.model.NetworkGeoName
import com.hipeople.repository.GeoRepository
import javax.inject.Inject


class NetworkGeoRepository @Inject constructor(
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
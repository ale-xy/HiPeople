package com.hipeople.repository

import com.hipeople.model.Location


interface GeoRepository {
    suspend fun getLocationsByName(name: String): List<Location>
}

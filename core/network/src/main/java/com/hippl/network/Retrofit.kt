package com.hippl.network

import com.hippl.network.model.NetworkGeoName
import com.hippl.network.model.NetworkHostUser
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

private interface RetrofitNetworkApi {
    @GET(value = "couchbot.php?method=get_geo_name")
    suspend fun getGeoName(@Query("name") name: String): List<NetworkGeoName>

    @GET(value = "couchbot.php?method=get_hosts_city")
    suspend fun getHostsForLocation(
        @Query("id") locationId: Int,
        @Query("user") userId: Int,
        @Query("token") token: String,
    ) : List<NetworkHostUser>
}

private const val BACKEND_URL = "https://hipipl.com/"

@Singleton
internal class RetrofitDataSource @Inject constructor(
    networkJson: Json,
    okhttpCallFactory:Call.Factory,
) : NetworkDataSource {
    private val networkApi = Retrofit.Builder()
        .baseUrl(BACKEND_URL)
        // We use callFactory lambda here with dagger.Lazy<Call.Factory>
        // to prevent initializing OkHttp on the main thread.
        .callFactory { okhttpCallFactory.newCall(it) }
        .addConverterFactory(
//            networkJson.asConverterFactory("application/json".toMediaType()),
            networkJson.asConverterFactory("text/html".toMediaType()),
        )
        .build()
        .create(RetrofitNetworkApi::class.java)

    override suspend fun getGeoName(name: String): List<NetworkGeoName> =
        networkApi.getGeoName(name)

    override suspend fun getHostsForLocation(locationId: Int): List<NetworkHostUser> =
        //todo replace parameters
        networkApi.getHostsForLocation(locationId, 1, "12345")
}
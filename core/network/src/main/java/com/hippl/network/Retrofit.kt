package com.hippl.network

import com.hippl.network.model.NetworkGeoName
import com.hippl.network.model.NetworkHostUser
import com.hippl.network.model.NetworkMutualReview
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
    @GET(value = "$BACKEND_BASE?method=get_geo_name")
    suspend fun getGeoName(@Query("name") name: String): List<NetworkGeoName>

    @GET(value = "$BACKEND_BASE?method=get_hosts_city")
    suspend fun getHostsForLocation(
        @Query("id") locationId: Int,
        @Query("user") userId: Int,
        @Query("token") token: String,
    ): List<NetworkHostUser>

    @GET(value = "$BACKEND_BASE?method=get_host")
    suspend fun getHost(
        @Query("host") hostId: Int,
        @Query("user") userId: Int,
        @Query("token") token: String,
    ): NetworkHostUser


    @GET(value = "$BACKEND_BASE?method=get_rev")
    suspend fun getReviews(
        @Query("id") hostId: Int,
        @Query("user") userId: Int,
        @Query("token") token: String,
    ):  List<NetworkMutualReview>
}

private const val BACKEND_URL = "https://hipipl.com/"
//private const val BACKEND_URL = "http://10.0.2.2:3001/"

//private const val BACKEND_BASE = "couchbot.php"
private const val BACKEND_BASE = "api.php"

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
            networkJson.asConverterFactory("application/json".toMediaType()),
        )
        .build()
        .create(RetrofitNetworkApi::class.java)

    override suspend fun getGeoName(name: String): List<NetworkGeoName> =
        networkApi.getGeoName(name)

    override suspend fun getHostsForLocation(locationId: Int): List<NetworkHostUser> =
        //todo replace parameters
        networkApi.getHostsForLocation(locationId, 1, "12345")

    override suspend fun getHost(hostId: Int): NetworkHostUser =
        //todo replace parameters
        networkApi.getHost(hostId, 1, "12345")

    override suspend fun getReviews(hostId: Int): List<NetworkMutualReview> =
        networkApi.getReviews(hostId, 1, "12345")



}
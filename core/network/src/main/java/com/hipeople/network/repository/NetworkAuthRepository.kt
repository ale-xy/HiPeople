package com.hipeople.network.repository

import com.hipeople.network.NetworkDataSource
import com.hipeople.repository.AuthRepository
import javax.inject.Inject


class NetworkAuthRepository @Inject constructor(
    val dataSource: NetworkDataSource
) : AuthRepository {
    override suspend fun authVk() {
        TODO("Not yet implemented")
    }

    override suspend fun authTg() {
        TODO("Not yet implemented")
    }

    override suspend fun authFb() {
        TODO("Not yet implemented")
    }

    override suspend fun refreshJwt() {
        TODO("Not yet implemented")
    }

}


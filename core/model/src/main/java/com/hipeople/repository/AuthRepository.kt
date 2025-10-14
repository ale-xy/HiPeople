package com.hipeople.repository

interface AuthRepository {
    suspend fun authVk()
    suspend fun authTg()
    suspend fun authFb()
    suspend fun refreshJwt()
}


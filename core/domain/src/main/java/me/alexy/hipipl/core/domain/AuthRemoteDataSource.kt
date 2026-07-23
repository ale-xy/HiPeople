package me.alexy.hipipl.core.domain

interface AuthRemoteDataSource {
    suspend fun loginVk(
        code: String,
        redirect: String,
        deviceId: String,
        vkDeviceId: String?,
        codeVerifier: String,
    ): Result<LoginResult, DataError.Network>

    suspend fun refreshJwt(
        userId: Int,
        refreshToken: String,
        deviceId: String,
    ): Result<RefreshResult, DataError.Network>

    suspend fun logout(refreshToken: String): EmptyResult<DataError.Network>
}

data class LoginResult(
    val userId: Int,
    val jwtToken: String,
    val refreshToken: String,
    val expiresIn: Int,
    val deviceId: String,
    val account: Account?,
)

data class RefreshResult(
    val jwtToken: String,
    val expiresIn: Int,
)

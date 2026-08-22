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

    /** `POST /auth/get-code` - a short code + per-network bot/deep-link URLs (see `SocialLoginOption`). */
    suspend fun getAuthCode(
        deviceId: String,
        lang: String,
        ip: String? = null,
    ): Result<AuthCodeInfo, DataError.Network>

    /** `POST /auth/check-code` - polled every ~5s while waiting for a bot-auth code to be confirmed. */
    suspend fun checkAuthCode(code: String): Result<CheckCodeResult, DataError.Network>
}

data class LoginResult(
    val userId: Int,
    val jwtToken: String,
    val refreshToken: String,
    val expiresIn: Int,
    val deviceId: String,
    val account: Account?,
    val profileIncomplete: Boolean,
)

data class RefreshResult(
    val jwtToken: String,
    val expiresIn: Int,
)

enum class AuthProvider { VK, TELEGRAM, WHATSAPP, FACEBOOK }

data class SocialLoginOption(
    val url: String,
    val instruction: String,
)

data class AuthCodeInfo(
    val code: String,
    val expiresAt: String,
    val socialLogins: Map<AuthProvider, SocialLoginOption>,
    val restrictedToVkOnly: Boolean,
)

sealed interface CheckCodeResult {
    data object Pending : CheckCodeResult
    data class Completed(val loginResult: LoginResult) : CheckCodeResult

    /** [socialLogins] is the server's refreshed full set of links for the new code, when sent. */
    data class Expired(val newCode: String, val socialLogins: Map<AuthProvider, SocialLoginOption>?) : CheckCodeResult
}

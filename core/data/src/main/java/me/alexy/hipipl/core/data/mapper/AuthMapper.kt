package me.alexy.hipipl.core.data.mapper

import me.alexy.hipipl.core.data.dto.AccountDto
import me.alexy.hipipl.core.data.dto.LoginVkResponseDto
import me.alexy.hipipl.core.data.dto.RefreshJwtResponseDto
import me.alexy.hipipl.core.domain.Account
import me.alexy.hipipl.core.domain.DataError
import me.alexy.hipipl.core.domain.LoginResult
import me.alexy.hipipl.core.domain.RefreshResult
import me.alexy.hipipl.core.domain.Result
import me.alexy.hipipl.core.domain.UserLanguage

// error.code == 1 (incomplete profile / missing consent) is intentionally not treated as a
// failure here - MVP logs the user in regardless; a "complete your profile" nudge needs Phase 2's
// profile-edit screen to send the user to.
fun LoginVkResponseDto.toLoginResult(): Result<LoginResult, DataError.Network> {
    if (!success) {
        return Result.Error(
            when (error?.code) {
                14 -> DataError.Network.UNAUTHORIZED // stale VK code, must re-authenticate
                else -> DataError.Network.UNKNOWN
            }
        )
    }
    val userId = auth?.userId ?: return Result.Error(DataError.Network.SERIALIZATION)
    val jwtToken = auth.jwtToken ?: return Result.Error(DataError.Network.SERIALIZATION)
    val refreshToken = auth.refreshToken ?: return Result.Error(DataError.Network.SERIALIZATION)
    val deviceId = auth.deviceId ?: return Result.Error(DataError.Network.SERIALIZATION)

    return Result.Success(
        LoginResult(
            userId = userId,
            jwtToken = jwtToken,
            refreshToken = refreshToken,
            expiresIn = auth.expiresIn ?: 0,
            deviceId = deviceId,
            account = user?.toAccount(userId),
        )
    )
}

fun AccountDto.toAccount(userId: Int): Account {
    return Account(
        userId = userId,
        name = name.orEmpty(),
        gender = sex.toGenderFromString(),
        age = age ?: 0,
        description = about.orEmpty(),
        totalReviews = totalReviews ?: 0,
        averageRating = rating ?: 0.0f,
        photos = photos?.mapNotNull { it.toPhoto() } ?: emptyList(),
        donate = donate ?: 0,
        userLanguages = userLangs?.mapNotNull { lang ->
            val langName = lang.name ?: return@mapNotNull null
            val langLevel = lang.lvl ?: return@mapNotNull null
            UserLanguage(
                langCode = lang.code.orEmpty(),
                langName = langName,
                level = langLevel,
            )
        } ?: emptyList(),
    )
}

// `refresh.valid == false` means the refresh token itself is dead - map it to UNAUTHORIZED so
// callers (the Ktor Auth plugin's refreshTokens hook) can tell "log the user out" apart from a
// transient failure, instead of both collapsing into the same generic error.
fun RefreshJwtResponseDto.toRefreshResult(): Result<RefreshResult, DataError.Network> {
    if (refresh?.valid == false) {
        return Result.Error(DataError.Network.UNAUTHORIZED)
    }
    if (!success) {
        return Result.Error(DataError.Network.UNKNOWN)
    }
    val jwtToken = data?.jwtToken ?: return Result.Error(DataError.Network.SERIALIZATION)
    return Result.Success(RefreshResult(jwtToken = jwtToken, expiresIn = data.expiresIn ?: 0))
}

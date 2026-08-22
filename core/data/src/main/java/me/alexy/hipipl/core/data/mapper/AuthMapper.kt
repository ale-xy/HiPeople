package me.alexy.hipipl.core.data.mapper

import me.alexy.hipipl.core.data.dto.AccountDto
import me.alexy.hipipl.core.data.dto.CheckAuthCodeResponseDto
import me.alexy.hipipl.core.data.dto.GetAuthCodeResponseDto
import me.alexy.hipipl.core.data.dto.LoginVkAuthDto
import me.alexy.hipipl.core.data.dto.LoginVkErrorDto
import me.alexy.hipipl.core.data.dto.LoginVkResponseDto
import me.alexy.hipipl.core.data.dto.RefreshJwtResponseDto
import me.alexy.hipipl.core.data.dto.SocialUrlDto
import me.alexy.hipipl.core.data.dto.SocialUrlsDto
import me.alexy.hipipl.core.domain.Account
import me.alexy.hipipl.core.domain.AuthCodeInfo
import me.alexy.hipipl.core.domain.AuthProvider
import me.alexy.hipipl.core.domain.CheckCodeResult
import me.alexy.hipipl.core.domain.DataError
import me.alexy.hipipl.core.domain.LoginResult
import me.alexy.hipipl.core.domain.RefreshResult
import me.alexy.hipipl.core.domain.Result
import me.alexy.hipipl.core.domain.SocialLoginOption
import me.alexy.hipipl.core.domain.UserLanguage

// error.code == 1 (incomplete profile / missing consent) is surfaced as `profileIncomplete`
// rather than a failure - MVP still logs the user in, the presentation layer decides whether to
// nudge them towards a "complete your profile" step.
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
            profileIncomplete = error?.code == 1,
        )
    )
}

/** Builds the login result from the same `auth`/`user`/`error` shape `login_vk` uses (see `POST_auth_check_code.md`). */
private fun toLoginResultFrom(
    auth: LoginVkAuthDto?,
    user: AccountDto?,
    error: LoginVkErrorDto?,
): Result<LoginResult, DataError.Network> {
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
            profileIncomplete = error?.code == 1,
        )
    )
}

fun SocialUrlsDto.toSocialLogins(): Map<AuthProvider, SocialLoginOption> {
    return buildMap {
        vk?.toSocialLoginOption()?.let { put(AuthProvider.VK, it) }
        tg?.toSocialLoginOption()?.let { put(AuthProvider.TELEGRAM, it) }
        wa?.toSocialLoginOption()?.let { put(AuthProvider.WHATSAPP, it) }
        fb?.toSocialLoginOption()?.let { put(AuthProvider.FACEBOOK, it) }
    }
}

private fun SocialUrlDto.toSocialLoginOption(): SocialLoginOption? {
    val safeUrl = url ?: return null
    return SocialLoginOption(url = safeUrl, instruction = instruction.orEmpty())
}

fun GetAuthCodeResponseDto.toAuthCodeInfo(): Result<AuthCodeInfo, DataError.Network> {
    val safeCode = code ?: return Result.Error(DataError.Network.SERIALIZATION)
    return Result.Success(
        AuthCodeInfo(
            code = safeCode,
            expiresAt = expiresAt.orEmpty(),
            socialLogins = socialUrls?.toSocialLogins() ?: emptyMap(),
            restrictedToVkOnly = authRestricted == "ru_vk_only",
        )
    )
}

fun CheckAuthCodeResponseDto.toCheckCodeResult(): Result<CheckCodeResult, DataError.Network> {
    return when (status) {
        "pending" -> Result.Success(CheckCodeResult.Pending)
        "completed" -> when (val result = toLoginResultFrom(auth, user, error)) {
            is Result.Success -> Result.Success(CheckCodeResult.Completed(result.data))
            is Result.Error -> result
        }
        "expired" -> {
            val safeNewCode = newCode ?: return Result.Error(DataError.Network.SERIALIZATION)
            Result.Success(CheckCodeResult.Expired(newCode = safeNewCode, socialLogins = socialUrls?.toSocialLogins()))
        }
        else -> Result.Error(DataError.Network.UNKNOWN)
    }
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

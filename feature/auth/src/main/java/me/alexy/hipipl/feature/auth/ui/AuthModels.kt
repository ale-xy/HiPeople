package me.alexy.hipipl.feature.auth.ui

import me.alexy.hipipl.core.domain.AuthProvider
import me.alexy.hipipl.core.domain.SocialLoginOption
import me.alexy.hipipl.core.presentation.UiText

data class AuthState(
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val authCode: String? = null,
    val socialLogins: Map<AuthProvider, SocialLoginOption> = emptyMap(),
    val pollingProvider: AuthProvider? = null,
    val fbSheetVisible: Boolean = false,
    val profileIncompleteVisible: Boolean = false,
)

sealed interface AuthAction {
    data class OnVkAuthCode(val code: String, val deviceId: String) : AuthAction
    data class OnVkAuthFail(val message: String) : AuthAction
    data class OnProviderClick(val provider: AuthProvider) : AuthAction
    data object OnFbSendConfirmed : AuthAction
    data object OnDismissFbSheet : AuthAction
    data object OnFillProfileNow : AuthAction
    data object OnSkipProfile : AuthAction
}

sealed interface AuthEvent {
    data object NavigateToApp : AuthEvent
    data class ShowError(val message: UiText) : AuthEvent
    data class OpenUrl(val url: String) : AuthEvent
    data class CopyToClipboard(val text: String) : AuthEvent
}

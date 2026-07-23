package me.alexy.hipipl.feature.auth.ui

import me.alexy.hipipl.core.presentation.UiText

data class AuthState(
    val isLoading: Boolean = false,
    val error: UiText? = null,
)

sealed interface AuthAction {
    data class OnVkAuthCode(val code: String, val deviceId: String) : AuthAction
    data class OnVkAuthFail(val message: String) : AuthAction
}

sealed interface AuthEvent {
    data object NavigateToApp : AuthEvent
    data class ShowError(val message: UiText) : AuthEvent
}

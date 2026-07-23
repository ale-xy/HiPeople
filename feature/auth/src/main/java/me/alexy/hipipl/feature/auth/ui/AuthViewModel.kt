package me.alexy.hipipl.feature.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.alexy.hipipl.core.data.DeviceIdManager
import me.alexy.hipipl.core.data.SessionManager
import me.alexy.hipipl.core.domain.AuthRemoteDataSource
import me.alexy.hipipl.core.domain.onFailure
import me.alexy.hipipl.core.domain.onSuccess
import me.alexy.hipipl.core.presentation.UiText
import me.alexy.hipipl.core.presentation.toUiText
import me.alexy.hipipl.feature.auth.BuildConfig
import me.alexy.hipipl.feature.auth.PkceGenerator

class AuthViewModel(
    private val authDataSource: AuthRemoteDataSource,
    private val sessionManager: SessionManager,
    private val deviceIdManager: DeviceIdManager,
) : ViewModel() {

    // Generated once per screen instance and kept only in memory - the matching challenge is
    // handed to VK ID's OneTap so it skips its own code<->token exchange (see PkceGenerator).
    private val codeVerifier = PkceGenerator.generateVerifier()
    val codeChallenge: String = PkceGenerator.deriveChallenge(codeVerifier)
    val redirectUri: String = BuildConfig.VKID_REDIRECT_URI

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state

    private val _events = Channel<AuthEvent>()
    val events: Flow<AuthEvent> = _events.receiveAsFlow()

    fun onAction(action: AuthAction) {
        when (action) {
            is AuthAction.OnVkAuthCode -> onVkAuthCode(action.code, action.deviceId)
            is AuthAction.OnVkAuthFail -> {
                _state.update { it.copy(isLoading = false) }
                sendEvent(AuthEvent.ShowError(UiText.DynamicString(action.message)))
            }
        }
    }

    private fun onVkAuthCode(code: String, vkDeviceId: String) {
        if (_state.value.isLoading) return
        _state.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val deviceId = deviceIdManager.getOrCreate()
            authDataSource.loginVk(
                code = code,
                redirect = redirectUri,
                deviceId = deviceId,
                vkDeviceId = vkDeviceId,
                codeVerifier = codeVerifier,
            )
                .onSuccess { result ->
                    sessionManager.save(
                        userId = result.userId,
                        jwtToken = result.jwtToken,
                        refreshToken = result.refreshToken,
                    )
                    _state.update { it.copy(isLoading = false) }
                    sendEvent(AuthEvent.NavigateToApp)
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.toUiText()) }
                    sendEvent(AuthEvent.ShowError(error.toUiText()))
                }
        }
    }

    private fun sendEvent(event: AuthEvent) {
        viewModelScope.launch { _events.send(event) }
    }
}

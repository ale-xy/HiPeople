package me.alexy.hipipl.feature.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import me.alexy.hipipl.core.data.DeviceIdManager
import me.alexy.hipipl.core.data.SessionManager
import me.alexy.hipipl.core.domain.AuthProvider
import me.alexy.hipipl.core.domain.AuthRemoteDataSource
import me.alexy.hipipl.core.domain.CheckCodeResult
import me.alexy.hipipl.core.domain.LoginResult
import me.alexy.hipipl.core.domain.Result
import me.alexy.hipipl.core.domain.onFailure
import me.alexy.hipipl.core.domain.onSuccess
import me.alexy.hipipl.core.presentation.UiText
import me.alexy.hipipl.core.presentation.toUiText
import me.alexy.hipipl.feature.auth.BuildConfig
import me.alexy.hipipl.feature.auth.PkceGenerator
import me.alexy.hipipl.feature.auth.R
import java.util.Locale
import kotlin.time.Duration.Companion.minutes

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

    private var pollingJob: Job? = null

    init {
        loadAuthCode()
    }

    fun onAction(action: AuthAction) {
        when (action) {
            is AuthAction.OnVkAuthCode -> onVkAuthCode(action.code, action.deviceId)
            is AuthAction.OnVkAuthFail -> {
                _state.update { it.copy(isLoading = false) }
                sendEvent(AuthEvent.ShowError(UiText.DynamicString(action.message)))
            }

            is AuthAction.OnProviderClick -> onProviderClick(action.provider)
            AuthAction.OnFbSendConfirmed -> onFbSendConfirmed()
            AuthAction.OnDismissFbSheet -> _state.update { it.copy(fbSheetVisible = false) }
            AuthAction.OnFillProfileNow -> onProfileModalDismissed()
            AuthAction.OnSkipProfile -> onProfileModalDismissed()
        }
    }

    private fun loadAuthCode() {
        viewModelScope.launch {
            val deviceId = deviceIdManager.getOrCreate()
            authDataSource.getAuthCode(deviceId = deviceId, lang = Locale.getDefault().language)
                .onSuccess { info ->
                    _state.update { it.copy(authCode = info.code, socialLogins = info.socialLogins) }
                }
                .onFailure { error ->
                    _state.update { it.copy(error = error.toUiText()) }
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
                    _state.update { it.copy(isLoading = false) }
                    onLoginResult(result)
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.toUiText()) }
                    sendEvent(AuthEvent.ShowError(error.toUiText()))
                }
        }
    }

    private fun onProviderClick(provider: AuthProvider) {
        if (_state.value.pollingProvider == provider) {
            cancelPolling()
            return
        }
        if (provider == AuthProvider.FACEBOOK) {
            _state.update { it.copy(fbSheetVisible = true) }
            return
        }
        val url = _state.value.socialLogins[provider]?.url ?: return
        sendEvent(AuthEvent.OpenUrl(url))
        startPolling(provider)
    }

    private fun onFbSendConfirmed() {
        val code = _state.value.authCode
        val url = _state.value.socialLogins[AuthProvider.FACEBOOK]?.url
        _state.update { it.copy(fbSheetVisible = false) }
        if (code != null) sendEvent(AuthEvent.CopyToClipboard(code))
        if (url != null) sendEvent(AuthEvent.OpenUrl(url))
        startPolling(AuthProvider.FACEBOOK)
    }

    private fun cancelPolling() {
        if (_state.value.pollingProvider == null) return
        pollingJob?.cancel()
        pollingJob = null
        _state.update { it.copy(pollingProvider = null) }
    }

    private fun startPolling(provider: AuthProvider) {
        pollingJob?.cancel()
        _state.update { it.copy(pollingProvider = provider) }

        pollingJob = viewModelScope.launch {
            var finished = false

            val completedInTime = withTimeoutOrNull(POLL_TIMEOUT) {
                while (!finished) {
                    delay(POLL_INTERVAL_MS)
                    val code = _state.value.authCode ?: return@withTimeoutOrNull

                    when (val result = authDataSource.checkAuthCode(code)) {
                        is Result.Success -> when (val checkResult = result.data) {
                            CheckCodeResult.Pending -> Unit
                            is CheckCodeResult.Completed -> {
                                finished = true
                                _state.update { it.copy(pollingProvider = null) }
                                onLoginResult(checkResult.loginResult)
                            }

                            is CheckCodeResult.Expired -> {
                                _state.update { state ->
                                    state.copy(
                                        authCode = checkResult.newCode,
                                        socialLogins = checkResult.socialLogins ?: state.socialLogins,
                                    )
                                }
                            }
                        }

                        is Result.Error -> {
                            finished = true
                            _state.update { it.copy(pollingProvider = null, error = result.error.toUiText()) }
                            sendEvent(AuthEvent.ShowError(result.error.toUiText()))
                        }
                    }
                }
            }

            if (completedInTime == null && !finished) {
                _state.update { it.copy(pollingProvider = null) }
                sendEvent(AuthEvent.ShowError(UiText.StringResource(R.string.auth_timeout)))
            }
        }
    }

    private suspend fun onLoginResult(loginResult: LoginResult) {
        sessionManager.save(
            userId = loginResult.userId,
            jwtToken = loginResult.jwtToken,
            refreshToken = loginResult.refreshToken,
        )
        if (loginResult.profileIncomplete) {
            _state.update { it.copy(profileIncompleteVisible = true) }
        } else {
            sendEvent(AuthEvent.NavigateToApp)
        }
    }

    // "Fill in now" should send the user to the registration/profile-edit screen, but that
    // screen doesn't exist yet (Phase 2 - see plans/IMPLEMENTATION_PLAN.md) - both buttons
    // proceed into the app for now.
    private fun onProfileModalDismissed() {
        _state.update { it.copy(profileIncompleteVisible = false) }
        sendEvent(AuthEvent.NavigateToApp)
    }

    private fun sendEvent(event: AuthEvent) {
        viewModelScope.launch { _events.send(event) }
    }

    override fun onCleared() {
        pollingJob?.cancel()
        _state.update { it.copy(isLoading = false, pollingProvider = null) }
    }

    private companion object {
        const val POLL_INTERVAL_MS = 5_000L
        val POLL_TIMEOUT = 15.minutes
    }
}

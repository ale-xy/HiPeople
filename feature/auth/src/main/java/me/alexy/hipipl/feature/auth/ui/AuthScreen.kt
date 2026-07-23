package me.alexy.hipipl.feature.auth.ui

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vk.id.auth.VKIDAuthUiParams
import com.vk.id.onetap.compose.onetap.OneTap
import kotlinx.coroutines.launch
import me.alexy.hipipl.core.presentation.ObserveAsEvents
import me.alexy.hipipl.feature.auth.BuildConfig
import me.alexy.hipipl.core.presentation.asString
import org.koin.androidx.compose.koinViewModel

@Composable
fun AuthScreen(
    onNavigateToApp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            AuthEvent.NavigateToApp -> onNavigateToApp()
            is AuthEvent.ShowError -> {
                coroutineScope.launch { snackbarHostState.showSnackbar(event.message.asString(context)) }
            }
        }
    }

    AuthScreen(
        state = state,
        pkceCodeChallenge = viewModel.codeChallenge,
        onAction = viewModel::onAction,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    )
}

@Composable
private fun AuthScreen(
    state: AuthState,
    pkceCodeChallenge: String,
    onAction: (AuthAction) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Per the auth screen doc: the loading state must not take over the whole screen or
            // hide the button - the user needs to be able to retry immediately if a given social
            // provider is acting up, so OneTap always stays visible; a small indicator is shown
            // alongside it instead of replacing it.
            OneTap(
                signInAnotherAccountButtonEnabled = true,
                authParams = VKIDAuthUiParams {
                    scopes = setOf("vkid.personal_info", "email", "phone")
                    codeChallenge = pkceCodeChallenge
                },
                onAuth = { _, _ ->
                    // Won't fire: passing `codeChallenge` above makes the SDK skip its own
                    // exchange and call onAuthCode instead. See PkceGenerator's kdoc.
                    if (BuildConfig.DEBUG) Log.w(TAG, "onAuth fired unexpectedly - codeChallenge wasn't honored by the SDK")
                },
                onAuthCode = { data, isCompletion ->
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "onAuthCode: isCompletion=$isCompletion deviceId=${data.deviceId} code=${data.code.take(12)}...")
                    }
                    onAction(AuthAction.OnVkAuthCode(code = data.code, deviceId = data.deviceId))
                },
                onFail = { oAuth, fail ->
                    if (BuildConfig.DEBUG) Log.e(TAG, "onFail: oAuth=$oAuth reason=${fail.description}")
                    onAction(AuthAction.OnVkAuthFail(fail.description))
                },
            )

            if (state.isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator(modifier = Modifier.height(24.dp))
            }

            state.error?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = it.asString(), color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

private const val TAG = "AuthScreen"

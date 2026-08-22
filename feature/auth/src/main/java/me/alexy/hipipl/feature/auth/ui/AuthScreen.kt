package me.alexy.hipipl.feature.auth.ui

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vk.id.auth.VKIDAuthUiParams
import com.vk.id.onetap.compose.onetap.OneTap
import kotlinx.coroutines.launch
import me.alexy.hipipl.core.designsystem.AppColors
import me.alexy.hipipl.core.designsystem.HiPeopleTheme
import me.alexy.hipipl.core.domain.AuthProvider
import me.alexy.hipipl.core.domain.SocialLoginOption
import me.alexy.hipipl.core.presentation.ObserveAsEvents
import me.alexy.hipipl.core.presentation.asString
import me.alexy.hipipl.feature.auth.BuildConfig
import me.alexy.hipipl.feature.auth.R
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
    val clipboardManager = LocalClipboardManager.current

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            AuthEvent.NavigateToApp -> onNavigateToApp()
            is AuthEvent.ShowError -> {
                coroutineScope.launch { snackbarHostState.showSnackbar(event.message.asString(context)) }
            }

            is AuthEvent.OpenUrl -> {
                context.startActivity(Intent(Intent.ACTION_VIEW, event.url.toUri()))
            }

            is AuthEvent.CopyToClipboard -> {
                clipboardManager.setText(AnnotatedString(event.text))
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

@OptIn(ExperimentalMaterial3Api::class)
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
            Wordmark()

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                shape = RoundedCornerShape(24.dp),
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = stringResource(R.string.auth_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    AgreementText()
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.auth_code_send_instruction),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        VkProviderRow(
                            pkceCodeChallenge = pkceCodeChallenge,
                            isLoading = state.isLoading,
                            instruction = state.socialLogins[AuthProvider.VK]?.instruction.orEmpty(),
                            onAction = onAction,
                        )

                        state.socialLogins[AuthProvider.TELEGRAM]?.let { option ->
                            ProviderRow(
                                provider = AuthProvider.TELEGRAM,
                                option = option,
                                brandColor = AppColors.TelegramBrand,
                                logoRes = R.drawable.ic_logo_telegram,
                                name = stringResource(R.string.auth_provider_telegram),
                                isPolling = state.pollingProvider == AuthProvider.TELEGRAM,
                                onAction = onAction,
                            )
                        }

                        state.socialLogins[AuthProvider.WHATSAPP]?.let { option ->
                            ProviderRow(
                                provider = AuthProvider.WHATSAPP,
                                option = option,
                                brandColor = AppColors.WhatsAppBrand,
                                logoRes = R.drawable.ic_logo_whatsapp,
                                name = stringResource(R.string.auth_provider_whatsapp),
                                isPolling = state.pollingProvider == AuthProvider.WHATSAPP,
                                onAction = onAction,
                            )
                        }

                        state.socialLogins[AuthProvider.FACEBOOK]?.let { option ->
                            ProviderRow(
                                provider = AuthProvider.FACEBOOK,
                                option = option,
                                brandColor = AppColors.FacebookBrand,
                                logoRes = R.drawable.ic_logo_facebook,
                                name = stringResource(R.string.auth_provider_facebook),
                                isPolling = state.pollingProvider == AuthProvider.FACEBOOK,
                                onAction = onAction,
                            )
                        }
                    }
                }
            }

            state.error?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = it.asString(), color = MaterialTheme.colorScheme.error)
            }
        }

        if (state.fbSheetVisible) {
            FacebookCodeSheet(
                code = state.authCode,
                onSendConfirmed = { onAction(AuthAction.OnFbSendConfirmed) },
                onDismiss = { onAction(AuthAction.OnDismissFbSheet) },
            )
        }

        if (state.profileIncompleteVisible) {
            ProfileIncompleteDialog(
                onFillNow = { onAction(AuthAction.OnFillProfileNow) },
                onSkip = { onAction(AuthAction.OnSkipProfile) },
            )
        }
    }
}

@Composable
private fun Wordmark(modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = buildAnnotatedString {
            withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurface)) { append("Hi") }
            withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) { append("People") }
        },
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
    )
}

@Composable
private fun AgreementText(modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current
    val link = stringResource(R.string.auth_link_data_use)

    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
                append(stringResource(R.string.auth_agree_data_use))
                append(" ")
            }
            withStyle(
                SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.SemiBold,
                )
            ) {
                append(link)
            }
        },
        style = MaterialTheme.typography.bodyMedium,
        modifier = modifier.clickable { uriHandler.openUri(link) },
    )
}

@Composable
private fun VkProviderRow(
    pkceCodeChallenge: String,
    isLoading: Boolean,
    instruction: String,
    onAction: (AuthAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isPreview = LocalInspectionMode.current
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isPreview) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .background(Color(0xFF0077FF), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = "VK ID", color = Color.White, fontWeight = FontWeight.Bold)
                }
            } else OneTap(
                modifier = Modifier.weight(1f),
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

            if (isLoading) {
                Spacer(modifier = Modifier.width(12.dp))
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            }
        }

        if (instruction.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = instruction,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ProviderRow(
    provider: AuthProvider,
    option: SocialLoginOption,
    brandColor: Color,
    logoRes: Int,
    name: String,
    isPolling: Boolean,
    onAction: (AuthAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(color = brandColor, shape = RoundedCornerShape(16.dp))
                .clickable { onAction(AuthAction.OnProviderClick(provider)) }
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(color = Color.White.copy(alpha = 0.2f), shape = CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                if (isPolling) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Icon(
                        painter = painterResource(logoRes),
                        contentDescription = name,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = stringResource(R.string.auth_login_via, name),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        if (option.instruction.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = option.instruction,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FacebookCodeSheet(
    code: String?,
    onSendConfirmed: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(),
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(color = AppColors.FacebookBrand, shape = CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_logo_facebook),
                        contentDescription = stringResource(R.string.auth_provider_facebook),
                        tint = Color.White,
                        modifier = Modifier.size(18.dp),
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.auth_provider_facebook),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
            ) {
                val digits = (code.orEmpty()).padEnd(6).take(6)
                digits.forEach { digit ->
                    Box(
                        modifier = Modifier
                            .size(width = 38.dp, height = 46.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(10.dp),
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = if (digit.isWhitespace()) "" else digit.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.auth_code_copied),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(color = AppColors.FacebookBrand, shape = RoundedCornerShape(16.dp))
                    .clickable(onClick = onSendConfirmed),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.auth_login_via, stringResource(R.string.auth_provider_facebook)),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ProfileIncompleteDialog(
    onFillNow: () -> Unit,
    onSkip: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onSkip,
        title = null,
        text = {
            Text(
                text = stringResource(R.string.auth_text_skip),
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        confirmButton = {
            TextButton(onClick = onFillNow) {
                Text(stringResource(R.string.auth_button_now))
            }
        },
        dismissButton = {
            TextButton(onClick = onSkip) {
                Text(stringResource(R.string.auth_button_skip))
            }
        },
    )
}

@Preview
@Composable
private fun AuthScreenPreview() {
    HiPeopleTheme {
        AuthScreen(
            state = AuthState(
                socialLogins = mapOf(
                    AuthProvider.VK to SocialLoginOption(
                        url = "",
                        instruction = "Войдите через VK ID",
                    ),
                    AuthProvider.TELEGRAM to SocialLoginOption(
                        url = "https://t.me/HiPeopleHiBot?start=code_HNU4P8",
                        instruction = "Нажмите на кнопку, затем Start в чате бота",
                    ),
                    AuthProvider.WHATSAPP to SocialLoginOption(
                        url = "https://wa.me/79053605551?text=HNU4P8",
                        instruction = "Нажмите на кнопку и отправьте сообщение",
                    ),
                    AuthProvider.FACEBOOK to SocialLoginOption(
                        url = "https://www.messenger.com/t/583585024829135?ref=code_HNU4P8",
                        instruction = "Откроется Facebook Messenger с кодом",
                    ),
                ),
                authCode = "HNU4P8",
            ),
            pkceCodeChallenge = "",
            onAction = {},
            snackbarHostState = remember { SnackbarHostState() },
        )
    }
}

private const val TAG = "AuthScreen"

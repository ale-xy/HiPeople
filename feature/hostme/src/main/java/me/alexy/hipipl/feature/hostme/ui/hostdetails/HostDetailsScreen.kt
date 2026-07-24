package me.alexy.hipipl.feature.hostme.ui.hostdetails

import android.content.ClipData
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import me.alexy.hipipl.core.designsystem.AppColors
import me.alexy.hipipl.core.designsystem.HiPeopleTheme
import me.alexy.hipipl.core.presentation.ObserveAsEvents
import me.alexy.hipipl.core.presentation.asString
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

@Composable
fun HostDetailsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAuth: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HostDetailsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val clipboard = LocalClipboard.current
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var customTabsSession by remember { mutableStateOf<CustomTabsSession?>(null) }

    // Partial Custom Tabs only render as a bottom sheet when launched through a real
    // CustomTabsSession bound to the browser's CustomTabsService — a plain CustomTabsIntent
    // with no session just opens the browser's regular full-screen tab.
    DisposableEffect(context) {
        val connection = object : CustomTabsServiceConnection() {
            override fun onCustomTabsServiceConnected(name: ComponentName, client: CustomTabsClient) {
                client.warmup(0L)
                customTabsSession = client.newSession(null)
            }

            override fun onServiceDisconnected(name: ComponentName) {
                customTabsSession = null
            }
        }
        val browserPackage = CustomTabsClient.getPackageName(context, null)
        val bound = browserPackage != null &&
            CustomTabsClient.bindCustomTabsService(context, browserPackage, connection)

        onDispose {
            customTabsSession = null
            if (bound) context.unbindService(connection)
        }
    }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is HostDetailsEvent.ShowSnackbar -> {
                coroutineScope.launch { snackbarHostState.showSnackbar(event.message.asString(context)) }
            }
            is HostDetailsEvent.CopyToClipboard -> {
                coroutineScope.launch {
                    clipboard.setClipEntry(ClipEntry(ClipData.newPlainText(event.text, event.text)))
                }
            }
            is HostDetailsEvent.OpenUrl -> {
                openInPartialTab(context = context, url = event.url, session = customTabsSession)
            }
            is HostDetailsEvent.OpenMap -> {
                openMapIntent(context = context, query = event.query, preferGoogleMaps = event.preferGoogleMaps)
            }
            HostDetailsEvent.NavigateToAuth -> onNavigateToAuth()
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.inverseSurface,
                    contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                )
            }
        },
    ) { padding ->
        HostDetailsScreen(
            state = state,
            onAction = viewModel::onAction,
            onNavigateBack = onNavigateBack,
            modifier = Modifier.padding(padding),
        )
    }
}

@Composable
fun HostDetailsScreen(
    state: HostDetailsState,
    onAction: (HostDetailsAction) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        when {
            state.isLoadingHost -> {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
            state.hostError != null -> {
                Text(state.hostError.asString(), modifier = Modifier.align(Alignment.Center))
            }
            state.host != null -> {
                HostDetailsContent(state = state, host = state.host, onAction = onAction, onNavigateBack = onNavigateBack)
            }
        }

        if (state.isPhotoViewerOpen && state.host != null) {
            PhotoViewerOverlay(
                photos = state.host.photos,
                startIndex = state.photoViewerStartIndex,
                onClose = { onAction(HostDetailsAction.ClosePhotoViewer) },
            )
        }
    }

    if (state.reportSheet.isOpen) {
        ReportBottomSheet(
            text = state.reportSheet.text,
            error = state.reportSheet.error,
            isSending = state.reportSheet.isSending,
            onTextChange = { onAction(HostDetailsAction.OnReportTextChange(it)) },
            onDismiss = { onAction(HostDetailsAction.DismissReport) },
            onSend = { onAction(HostDetailsAction.SendReport) },
        )
    }
}

// Partial Custom Tabs: launches the URL as a bottom sheet over the app instead of a full-screen
// browser. Falls back to a normal full-screen tab if the resolved browser doesn't support it.
private const val PARTIAL_TAB_HEIGHT_DP = 640

private fun openInPartialTab(context: Context, url: String, session: CustomTabsSession?) {
    val heightPx = (PARTIAL_TAB_HEIGHT_DP * context.resources.displayMetrics.density).roundToInt()
    CustomTabsIntent.Builder(session)
        .setInitialActivityHeightPx(heightPx, CustomTabsIntent.ACTIVITY_HEIGHT_ADJUSTABLE)
        .setToolbarCornerRadiusDp(16)
        .build()
        .launchUrl(context, url.toUri())
}

private fun openMapIntent(context: Context, query: String, preferGoogleMaps: Boolean) {
    val uri = "geo:0,0?q=${Uri.encode(query)}".toUri()
    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        if (preferGoogleMaps) setPackage("com.google.android.apps.maps")
    }
    val resolvedIntent = if (intent.resolveActivity(context.packageManager) != null) {
        intent
    } else {
        Intent(Intent.ACTION_VIEW, uri)
    }
    context.startActivity(resolvedIntent)
}

@Preview
@Composable
private fun HostDetailsScreenPreview(
    @PreviewParameter(HostDetailsScreenPreviewParameterProvider::class) state: HostDetailsState,
) {
    HiPeopleTheme {
        HostDetailsScreen(state = state, onAction = {}, onNavigateBack = {})
    }
}

@Composable
private fun HostDetailsContent(
    state: HostDetailsState,
    host: HostDetailsUi,
    onAction: (HostDetailsAction) -> Unit,
    onNavigateBack: () -> Unit,
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            PhotoHero(
                photos = host.photos,
                hostName = host.name,
                isFavorited = state.isFavorited,
                onBackClick = onNavigateBack,
                onToggleFavorite = { onAction(HostDetailsAction.ToggleFavorite) },
                onCopyLink = { onAction(HostDetailsAction.CopyProfileLink) },
                onOpenViewer = { index -> onAction(HostDetailsAction.OpenPhotoViewer(index)) },
            )
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                HostHeaderSection(
                    name = host.name,
                    ageText = host.ageText,
                    genderAccent = host.genderAccent,
                    totalReviews = host.totalReviews,
                    ratingValueText = host.ratingValueText,
                    cityText = host.cityText,
                    onOpenMap = { preferGoogleMaps -> onAction(HostDetailsAction.OpenCityMap(preferGoogleMaps)) },
                    lastActivityText = host.lastActivityText,
                    lastActivityStatus = host.lastActivityStatus,
                )

                HorizontalDivider(color = AppColors.Divider)

                LanguagesSection(languages = host.languages)

                VibePillsSection(vibes = host.vibes)

                HostingGuestsCard(
                    hostText = host.hostText,
                    hostingParams = host.hostingParams,
                    onTranslate = { onAction(HostDetailsAction.Translate(host.hostText)) },
                )

                AboutCard(
                    description = host.description,
                    onTranslate = { onAction(HostDetailsAction.Translate(host.description)) },
                )

                ContactsCard(
                    contactsState = state.contactsState,
                    messageDraft = state.messageDraft,
                    isSendingMessage = state.isSendingMessage,
                    canSendMessage = state.canSendMessage,
                    onRevealContacts = { onAction(HostDetailsAction.RevealContacts) },
                    onMessageDraftChange = { onAction(HostDetailsAction.OnMessageDraftChange(it)) },
                    onSendMessage = { onAction(HostDetailsAction.SendMessage) },
                )

                ActionRow(
                    isFavorited = state.isFavorited,
                    onToggleFavorite = { onAction(HostDetailsAction.ToggleFavorite) },
                    onReport = { onAction(HostDetailsAction.OpenReport) },
                )

                ReviewsSection(
                    totalReviews = host.totalReviews,
                    reviewGroups = state.reviewGroups,
                    visibleGroupCount = state.visibleReviewGroupCount,
                    expandedGroups = state.expandedReviewGroups,
                    isLoading = state.isLoadingReviews,
                    errorText = state.reviewsError?.asString(),
                    onToggleGroupExpanded = { onAction(HostDetailsAction.ToggleReviewGroupExpanded(it)) },
                    onShowMoreReviews = { onAction(HostDetailsAction.ShowMoreReviews) },
                    onAddReview = { onAction(HostDetailsAction.AddReview) },
                )
            }
        }
    }
}

package me.alexy.hipipl.feature.hostme.ui.hostdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.alexy.hipipl.core.data.SessionManager
import me.alexy.hipipl.core.domain.FavoritesRemoteDataSource
import me.alexy.hipipl.core.domain.HostContacts
import me.alexy.hipipl.core.domain.HostRemoteDataSource
import me.alexy.hipipl.core.domain.MessagingRemoteDataSource
import me.alexy.hipipl.core.domain.SupportRemoteDataSource
import me.alexy.hipipl.core.domain.onFailure
import me.alexy.hipipl.core.domain.onSuccess
import me.alexy.hipipl.core.presentation.UiText
import me.alexy.hipipl.core.presentation.toUiText
import me.alexy.hipipl.feature.hostitem.R
import me.alexy.hipipl.feature.hostme.HostDetailsRoute
import java.net.URLEncoder
import java.util.Locale

class HostDetailsViewModel(
    private val hostDataSource: HostRemoteDataSource,
    private val favoritesDataSource: FavoritesRemoteDataSource,
    private val supportDataSource: SupportRemoteDataSource,
    private val messagingDataSource: MessagingRemoteDataSource,
    private val sessionManager: SessionManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val args = HostDetailsRoute.from(savedStateHandle)

    private val _state = MutableStateFlow(HostDetailsState())
    val state: StateFlow<HostDetailsState> = _state

    private val _events = Channel<HostDetailsEvent>()
    val events: Flow<HostDetailsEvent> = _events.receiveAsFlow()

    init {
        loadHost()
    }

    fun onAction(action: HostDetailsAction) {
        when (action) {
            HostDetailsAction.ToggleFavorite -> toggleFavorite()
            HostDetailsAction.RevealContacts -> revealContacts()
            is HostDetailsAction.OnMessageDraftChange -> {
                _state.update { it.copy(messageDraft = action.text) }
            }
            HostDetailsAction.SendMessage -> sendMessage()
            HostDetailsAction.OpenReport -> {
                _state.update { it.copy(reportSheet = it.reportSheet.copy(isOpen = true)) }
            }
            HostDetailsAction.DismissReport -> {
                _state.update { it.copy(reportSheet = ReportSheetState()) }
            }
            is HostDetailsAction.OnReportTextChange -> {
                _state.update { it.copy(reportSheet = it.reportSheet.copy(text = action.text, error = null)) }
            }
            HostDetailsAction.SendReport -> sendReport()
            is HostDetailsAction.OpenPhotoViewer -> {
                _state.update { it.copy(isPhotoViewerOpen = true, photoViewerStartIndex = action.index) }
            }
            HostDetailsAction.ClosePhotoViewer -> {
                _state.update { it.copy(isPhotoViewerOpen = false) }
            }
            is HostDetailsAction.ToggleReviewGroupExpanded -> {
                _state.update { current ->
                    current.copy(
                        reviewGroups = current.reviewGroups.mapIndexed { index, group ->
                            if (index == action.index) group.copy(isExpanded = !group.isExpanded) else group
                        }
                    )
                }
            }
            HostDetailsAction.LoadMoreReviews -> loadMoreReviews()
            HostDetailsAction.CopyProfileLink -> copyProfileLink()
            HostDetailsAction.AddReview -> {
                sendEvent(HostDetailsEvent.ShowSnackbar(UiText.StringResource(R.string.review_form_coming_soon)))
            }
            is HostDetailsAction.Translate -> {
                sendEvent(HostDetailsEvent.OpenUrl(action.text.toGoogleTranslateUrl()))
            }
            is HostDetailsAction.OpenCityMap -> {
                val cityText = _state.value.host?.cityText ?: return
                sendEvent(HostDetailsEvent.OpenMap(query = cityText, preferGoogleMaps = action.preferGoogleMaps))
            }
        }
    }

    private fun toggleFavorite() {
        val host = _state.value.host ?: return
        if (_state.value.isTogglingFavorite) return

        viewModelScope.launch {
            if (sessionManager.currentUserId() == null) {
                sendEvent(HostDetailsEvent.NavigateToAuth)
                return@launch
            }

            val wasFavorited = _state.value.isFavorited
            val nowFavorited = !wasFavorited
            _state.update { it.copy(isFavorited = nowFavorited, isTogglingFavorite = true) }

            val result = if (nowFavorited) {
                favoritesDataSource.addFavorite(contentId = host.hostListingId, type = FAVORITE_TYPE_HOST)
            } else {
                favoritesDataSource.removeFavorite(contentId = host.hostListingId, type = FAVORITE_TYPE_HOST)
            }
            result
                .onSuccess {
                    sendEvent(
                        HostDetailsEvent.ShowSnackbar(
                            UiText.StringResource(
                                if (nowFavorited) R.string.added_to_favorites else R.string.removed_from_favorites
                            )
                        )
                    )
                }
                .onFailure { error ->
                    _state.update { it.copy(isFavorited = wasFavorited) }
                    sendEvent(HostDetailsEvent.ShowSnackbar(error.toUiText()))
                }
            _state.update { it.copy(isTogglingFavorite = false) }
        }
    }

    private fun revealContacts() {
        val host = _state.value.host ?: return
        if (_state.value.contactsState is ContactsUiState.Loading) return

        viewModelScope.launch {
            val userId = sessionManager.currentUserId()
            if (userId == null) {
                sendEvent(HostDetailsEvent.NavigateToAuth)
                return@launch
            }

            _state.update { it.copy(contactsState = ContactsUiState.Loading) }
            messagingDataSource.getContacts(listingId = host.hostListingId)
                .onSuccess { contacts ->
                    _state.update { it.copy(contactsState = contacts.toContactsUiState()) }
                }
                .onFailure { error ->
                    _state.update { it.copy(contactsState = ContactsUiState.Blocked(error.toUiText())) }
                }
        }
    }

    private fun HostContacts.toContactsUiState(): ContactsUiState = when (this) {
        is HostContacts.Available -> ContactsUiState.Revealed(
            chips = contacts.flatMap { (type, values) -> values.map { ContactChipUi(type, it) } }
        )
        HostContacts.ProfileIncomplete -> ContactsUiState.Blocked(UiText.StringResource(R.string.contacts_profile_incomplete))
        HostContacts.PrivacyHidden -> ContactsUiState.Blocked(UiText.StringResource(R.string.contacts_privacy_hidden))
        is HostContacts.UnansweredReview -> ContactsUiState.Blocked(UiText.StringResource(R.string.contacts_unanswered_review))
    }

    private fun sendMessage() {
        val host = _state.value.host ?: return
        val draft = _state.value.messageDraft.trim()
        if (draft.isEmpty() || _state.value.isSendingMessage || !_state.value.canSendMessage) return

        viewModelScope.launch {
            if (sessionManager.currentUserId() == null) {
                sendEvent(HostDetailsEvent.NavigateToAuth)
                return@launch
            }

            _state.update { it.copy(isSendingMessage = true) }
            messagingDataSource.sendMessage(
                targetUserId = host.userId,
                listingId = host.hostListingId,
                text = draft,
            )
                .onSuccess { result ->
                    _state.update {
                        it.copy(
                            messageDraft = "",
                            isSendingMessage = false,
                            canSendMessage = result.canSend,
                        )
                    }
                    sendEvent(HostDetailsEvent.ShowSnackbar(UiText.StringResource(R.string.message_sent)))
                }
                .onFailure { error ->
                    _state.update { it.copy(isSendingMessage = false) }
                    sendEvent(HostDetailsEvent.ShowSnackbar(error.toUiText()))
                }
        }
    }

    private fun sendReport() {
        val host = _state.value.host
        val text = _state.value.reportSheet.text.trim()
        if (text.isEmpty()) {
            _state.update {
                it.copy(reportSheet = it.reportSheet.copy(error = UiText.StringResource(R.string.complaint_text_required)))
            }
            return
        }
        _state.update { it.copy(reportSheet = it.reportSheet.copy(isSending = true, error = null)) }

        viewModelScope.launch {
            val userId = sessionManager.currentUserId()
            supportDataSource.submitComplaint(
                message = text,
                userId = userId,
                additionalData = buildMap {
                    put("screen", "host_details")
                    if (host != null) put("host_user_id", host.userId.toString())
                    if (userId != null) put("viewer_user_id", userId.toString())
                },
            )
                .onSuccess {
                    _state.update { it.copy(reportSheet = ReportSheetState()) }
                    sendEvent(HostDetailsEvent.ShowSnackbar(UiText.StringResource(R.string.complaint_sent_success)))
                }
                .onFailure { error ->
                    _state.update { it.copy(reportSheet = it.reportSheet.copy(isSending = false, error = error.toUiText())) }
                }
        }
    }

    private fun copyProfileLink() {
        val userId = _state.value.host?.userId ?: return
        sendEvent(HostDetailsEvent.CopyToClipboard("$PROFILE_LINK_BASE_URL$userId"))
        sendEvent(HostDetailsEvent.ShowSnackbar(UiText.StringResource(R.string.copied)))
    }

    private fun sendEvent(event: HostDetailsEvent) {
        viewModelScope.launch { _events.send(event) }
    }

    private fun String.toGoogleTranslateUrl(): String {
        val targetLang = Locale.getDefault().language
        val encodedText = URLEncoder.encode(this, "UTF-8")
        return "https://translate.google.com/?sl=auto&tl=$targetLang&text=$encodedText"
    }

    private fun loadHost() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingHost = true, hostError = null) }

            hostDataSource.getHost(
                hostId = args.hostId,
                userId = sessionManager.currentUserId(),
            )
                .onSuccess { host ->
                    _state.update {
                        it.copy(
                            host = host.toHostDetailsUi(),
                            isLoadingHost = false
                        )
                    }
                    // Chain reviews loading after host is loaded, using the real user ID
                    loadReviews(userId = host.userId)
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            host = null,
                            isLoadingHost = false,
                            hostError = error.toUiText()
                        )
                    }
                }
        }
    }

    private fun loadReviews(userId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingReviews = true, reviewsError = null) }

            hostDataSource.getReviews(
                userId = userId,
                viewerId = sessionManager.currentUserId(),
            )
                .onSuccess { userReviews ->
                    val hostName = _state.value.host?.name.orEmpty()
                    _state.update {
                        it.copy(
                            reviewGroups = userReviews.threads.map { thread -> thread.toReviewGroupUi(hostName) },
                            isLoadingReviews = false,
                            reviewsHasMore = userReviews.hasMore,
                            reviewsNextOffset = userReviews.nextOffset,
                            seenReviewAuthorIds = userReviews.threads.mapNotNull { it.authorId }.toSet(),
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            reviewGroups = emptyList(),
                            isLoadingReviews = false,
                            reviewsError = error.toUiText()
                        )
                    }
                }
        }
    }

    private fun loadMoreReviews() {
        val state = _state.value
        val host = state.host ?: return
        val offset = state.reviewsNextOffset ?: return
        if (!state.reviewsHasMore || state.isLoadingMoreReviews) return

        viewModelScope.launch {
            _state.update { it.copy(isLoadingMoreReviews = true) }

            hostDataSource.getReviews(
                userId = host.userId,
                viewerId = sessionManager.currentUserId(),
                offset = offset,
            )
                .onSuccess { userReviews ->
                    _state.update { current ->
                        // Mutual threads are returned in full on every page, so drop authors we've already shown.
                        val newThreads = userReviews.threads.filter { it.authorId !in current.seenReviewAuthorIds }
                        current.copy(
                            reviewGroups = current.reviewGroups + newThreads.map { it.toReviewGroupUi(host.name) },
                            seenReviewAuthorIds = current.seenReviewAuthorIds + newThreads.mapNotNull { it.authorId },
                            reviewsHasMore = userReviews.hasMore,
                            reviewsNextOffset = userReviews.nextOffset,
                            isLoadingMoreReviews = false,
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoadingMoreReviews = false) }
                    sendEvent(HostDetailsEvent.ShowSnackbar(error.toUiText()))
                }
        }
    }

    private companion object {
        const val PROFILE_LINK_BASE_URL = "https://hipipl.com/host/"
        const val FAVORITE_TYPE_HOST = "host"
    }
}

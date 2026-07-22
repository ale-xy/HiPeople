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
import me.alexy.hipipl.core.domain.HostRemoteDataSource
import me.alexy.hipipl.core.domain.onFailure
import me.alexy.hipipl.core.domain.onSuccess
import me.alexy.hipipl.core.presentation.UiText
import me.alexy.hipipl.core.presentation.toUiText
import me.alexy.hipipl.feature.hostitem.R
import me.alexy.hipipl.feature.hostme.HostDetailsRoute

class HostDetailsViewModel(
    private val hostDataSource: HostRemoteDataSource,
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
            HostDetailsAction.RevealContacts -> _state.update { it.copy(isContactsRevealed = true) }
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
                _state.update {
                    val expanded = it.expandedReviewGroups
                    val updated = if (action.index in expanded) expanded - action.index else expanded + action.index
                    it.copy(expandedReviewGroups = updated)
                }
            }
            HostDetailsAction.ShowMoreReviews -> {
                _state.update {
                    it.copy(visibleReviewGroupCount = (it.visibleReviewGroupCount + 2).coerceAtMost(it.reviewGroups.size))
                }
            }
            HostDetailsAction.CopyProfileLink -> copyProfileLink()
        }
    }

    private fun toggleFavorite() {
        val nowFavorited = !_state.value.isFavorited
        _state.update { it.copy(isFavorited = nowFavorited) }
        sendEvent(
            HostDetailsEvent.ShowSnackbar(
                UiText.StringResource(
                    if (nowFavorited) R.string.added_to_favorites else R.string.removed_from_favorites
                )
            )
        )
    }

    private fun sendMessage() {
        val draft = _state.value.messageDraft.trim()
        if (draft.isEmpty()) return
        _state.update { it.copy(messageDraft = "") }
        sendEvent(HostDetailsEvent.ShowSnackbar(UiText.StringResource(R.string.message_sent)))
    }

    private fun sendReport() {
        val text = _state.value.reportSheet.text.trim()
        if (text.isEmpty()) {
            _state.update {
                it.copy(reportSheet = it.reportSheet.copy(error = UiText.StringResource(R.string.complaint_text_required)))
            }
            return
        }
        _state.update { it.copy(reportSheet = ReportSheetState()) }
        sendEvent(HostDetailsEvent.ShowSnackbar(UiText.StringResource(R.string.complaint_sent_success)))
    }

    private fun copyProfileLink() {
        val userId = _state.value.host?.userId ?: return
        sendEvent(HostDetailsEvent.CopyToClipboard("$PROFILE_LINK_BASE_URL$userId"))
        sendEvent(HostDetailsEvent.ShowSnackbar(UiText.StringResource(R.string.copied)))
    }

    private fun sendEvent(event: HostDetailsEvent) {
        viewModelScope.launch { _events.send(event) }
    }

    private fun loadHost() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingHost = true, hostError = null) }

            hostDataSource.getHost(
                hostId = args.hostId,
                userId = null, // TODO: Phase 1 - real auth
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
                viewerId = null, // TODO: Phase 1 - real auth
            )
                .onSuccess { userReviews ->
                    val hostName = _state.value.host?.name.orEmpty()
                    _state.update {
                        it.copy(
                            reviewGroups = userReviews.threads.map { thread -> thread.toReviewGroupUi(hostName) },
                            isLoadingReviews = false
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

    private companion object {
        const val PROFILE_LINK_BASE_URL = "https://hipipl.com/host/"
    }
}

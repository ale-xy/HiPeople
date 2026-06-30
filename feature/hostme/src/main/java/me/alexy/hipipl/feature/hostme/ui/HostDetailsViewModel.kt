package me.alexy.hipipl.feature.hostme.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.alexy.hipipl.core.domain.HostRemoteDataSource
import me.alexy.hipipl.core.domain.onFailure
import me.alexy.hipipl.core.domain.onSuccess
import me.alexy.hipipl.core.presentation.UiText
import me.alexy.hipipl.core.presentation.toUiText
import me.alexy.hipipl.feature.hostme.HostDetailsRoute

data class HostDetailsState(
    val isLoadingHost: Boolean = true,
    val host: HostDetailsUi? = null,
    val hostError: UiText? = null,
    val isLoadingReviews: Boolean = true,
    val reviews: List<MutualReviewUi> = emptyList(),
    val reviewsError: UiText? = null
)

sealed interface HostDetailsAction {
    // No actions yet - read-only screen
}

data class HostDetailsUi(
    val userId: Int,
    val name: String,
    val photos: List<String>,
    val languagesText: String,
    val cityText: String,
    val nameWithAge: String,  // "Alex (30 лет)" or just "Alex"
    val ratingText: String,  // "4.5* (10)"
    val donateAmount: Int,
    val showDonation: Boolean,
    val description: String,
    val hostText: String,
    val contacts: Map<String, String>,  // Already string keys instead of ContactType
    val hasContacts: Boolean
)

data class MutualReviewUi(
    val review: ReviewUi?,
    val response: ReviewUi?
)

data class ReviewUi(
    val id: Int,
    val authorName: String,
    val formattedDate: String,  // "dd.MM.yyyy"
    val text: String,
    val photoUrl: String?,
    val isGuest: Boolean,
    val receiverName: String?  // For responses: "to: {name}"
)

class HostDetailsViewModel(
    private val hostDataSource: HostRemoteDataSource,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val args = HostDetailsRoute.from(savedStateHandle)

    private val _state = MutableStateFlow(HostDetailsState())
    val state: StateFlow<HostDetailsState> = _state

    init {
        loadHost()
    }

    fun onAction(action: HostDetailsAction) {
        // No actions yet
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
                    _state.update {
                        it.copy(
                            reviews = userReviews.threads.map { thread -> thread.toReviewThreadUi() },
                            isLoadingReviews = false
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            reviews = emptyList(),
                            isLoadingReviews = false,
                            reviewsError = error.toUiText()
                        )
                    }
                }
        }
    }
}

package me.alexy.hipipl.feature.hostme.ui

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hippl.model.HostUser
import com.hippl.model.MutualReview
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import me.alexy.hipipl.core.data.HostRepository
import me.alexy.hipipl.feature.hostme.HostDetails
import javax.inject.Inject

@HiltViewModel
class HostDetailsViewModel @Inject constructor(
    private val repository: HostRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _hostState = MutableStateFlow<HostState>(HostState.Loading)

    private val _reviewsState = MutableStateFlow<ReviewsState>(ReviewsState.Loading)

    private val _uiState = MutableStateFlow<HostDetailsUiState>(HostDetailsUiState.Loading)
    val uiState: StateFlow<HostDetailsUiState> = _uiState.asStateFlow()

    private val args = HostDetails.from(savedStateHandle)

    init {
        loadHostDetails(args.hostId)
        loadReviews(args.userId)
        combineUiState()
    }

    private fun loadHostDetails(hostId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _hostState.value = HostState.Loading
            try {
                val hostDetails = repository.getHost(hostId)
                _hostState.value = HostState.Success(hostDetails)
            } catch (e: Throwable) {
                Log.e("HostDetailsViewModel", e.message, e)
                _hostState.value = HostState.Error(e.message.orEmpty())
            }
        }
    }

    private fun loadReviews(hostId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _reviewsState.value = ReviewsState.Loading
            try {
                val reviews = repository.getReviews(hostId)
                _reviewsState.value = ReviewsState.Success(reviews)
            } catch (e: Throwable) {
                Log.e("HostDetailsViewModel", e.message, e)
                _reviewsState.value = ReviewsState.Error(e.message.orEmpty())
            }
        }
    }

    private fun combineUiState() {
        viewModelScope.launch {
            combine(_hostState, _reviewsState) { hostState, reviewsState ->
                when {
                    // Both are loading
                    hostState is HostState.Loading ->
                        HostDetailsUiState.Loading

                    // Host details are loaded, but reviews are still loading
                    hostState is HostState.Success && reviewsState is ReviewsState.Loading ->
                        HostDetailsUiState.HostLoadSuccess(hostState.hostDetails, null)

                    // Host details are loaded, and reviews are loaded
                    hostState is HostState.Success && reviewsState is ReviewsState.Success -> {
                        val filteredReviews = filterReviews(reviewsState.reviews)
                        HostDetailsUiState.Success(hostState.hostDetails, filteredReviews)
                    }

                    // Host details are loaded, but reviews failed
                    hostState is HostState.Success && reviewsState is ReviewsState.Error ->
                        HostDetailsUiState.HostLoadSuccess(hostState.hostDetails, reviewsState.error)

                    // Host details failed, but reviews are loaded
                    hostState is HostState.Error && reviewsState is ReviewsState.Success ->
                        HostDetailsUiState.Error(hostState.error)

                    // Both failed
                    hostState is HostState.Error && reviewsState is ReviewsState.Error ->
                        HostDetailsUiState.Error("Host details: ${hostState.error}, Reviews: ${reviewsState.error}")

                    // Unknown state
                    else -> HostDetailsUiState.Error("Unknown state")
                }
            }.collect { _uiState.value = it }
        }
    }

    private fun filterReviews(reviews: List<MutualReview>): List<MutualReview> {
        // Add your filtering logic here
        return reviews // Return filtered reviews
    }
}

sealed interface HostDetailsUiState {
    data object Loading : HostDetailsUiState
    data class HostLoadSuccess(val hostDetails: HostUser, val reviewsError: String?) : HostDetailsUiState
    data class Success(val hostDetails: HostUser, val reviews: List<MutualReview>) : HostDetailsUiState
    data class Error(val error: String) : HostDetailsUiState
}

sealed interface HostState {
    data object Loading : HostState
    data class Success(val hostDetails: HostUser) : HostState
    data class Error(val error: String) : HostState
}

sealed interface ReviewsState {
    data object Loading : ReviewsState
    data class Success(val reviews: List<MutualReview>) : ReviewsState
    data class Error(val error: String) : ReviewsState
}

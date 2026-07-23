package me.alexy.hipipl.feature.hostme.ui.hostdetails

import me.alexy.hipipl.core.designsystem.GenderAccent
import me.alexy.hipipl.core.domain.ContactType
import me.alexy.hipipl.core.presentation.UiText

data class HostDetailsState(
    val isLoadingHost: Boolean = true,
    val host: HostDetailsUi? = null,
    val hostError: UiText? = null,
    val isLoadingReviews: Boolean = true,
    val reviewGroups: List<ReviewGroupUi> = emptyList(),
    val reviewsError: UiText? = null,
    val isFavorited: Boolean = false,
    val isTogglingFavorite: Boolean = false,
    val contactsState: ContactsUiState = ContactsUiState.Idle,
    val messageDraft: String = "",
    val isSendingMessage: Boolean = false,
    val canSendMessage: Boolean = true,
    val reportSheet: ReportSheetState = ReportSheetState(),
    val photoViewerStartIndex: Int = 0,
    val isPhotoViewerOpen: Boolean = false,
    val expandedReviewGroups: Set<Int> = emptySet(),
    val visibleReviewGroupCount: Int = 2,
)

sealed interface ContactsUiState {
    data object Idle : ContactsUiState
    data object Loading : ContactsUiState
    data class Revealed(val chips: List<ContactChipUi>) : ContactsUiState
    data class Blocked(val message: UiText) : ContactsUiState
}

data class ContactChipUi(val type: ContactType, val value: String)

data class ReportSheetState(
    val isOpen: Boolean = false,
    val text: String = "",
    val isSending: Boolean = false,
    val error: UiText? = null,
)

sealed interface HostDetailsAction {
    data object ToggleFavorite : HostDetailsAction
    data object RevealContacts : HostDetailsAction
    data class OnMessageDraftChange(val text: String) : HostDetailsAction
    data object SendMessage : HostDetailsAction
    data object OpenReport : HostDetailsAction
    data object DismissReport : HostDetailsAction
    data class OnReportTextChange(val text: String) : HostDetailsAction
    data object SendReport : HostDetailsAction
    data class OpenPhotoViewer(val index: Int) : HostDetailsAction
    data object ClosePhotoViewer : HostDetailsAction
    data class ToggleReviewGroupExpanded(val index: Int) : HostDetailsAction
    data object ShowMoreReviews : HostDetailsAction
    data object CopyProfileLink : HostDetailsAction
}

sealed interface HostDetailsEvent {
    data class ShowSnackbar(val message: UiText) : HostDetailsEvent
    data class CopyToClipboard(val text: String) : HostDetailsEvent
}

data class HostDetailsUi(
    val userId: Int,
    val name: String,
    val ageText: String, // " (28 лет)" or "" — feeds NameWithAgeText
    val genderAccent: GenderAccent,
    val photos: List<String>,
    val totalReviews: Int,
    val ratingValueText: String?, // e.g. "4.5" for the ReferencesText score, null if no rating yet
    val cityText: String,
    val languages: List<LanguageUi>,
    val hostingParams: List<HostingParamUi>,
    val description: String,
    val hostText: String,
    val donateAmount: Int,
    val showDonation: Boolean,
    val contacts: Map<ContactType, String>,
    val hasContacts: Boolean,
    val hostListingId: Int,
    val lastActivityText: UiText?,
    val vibeLabels: List<String>,
)

data class LanguageUi(
    val name: String,
    val filledBars: Int, // 1..3
)

enum class HostingParamType { SEPARATE_ROOM, KIDS, PETS }

data class HostingParamUi(
    val type: HostingParamType,
    val isOk: Boolean,
)

data class ReviewGroupUi(
    val received: List<ReviewUi>,
    val responses: List<ReviewUi>,
    val isMutual: Boolean,
)

data class ReviewUi(
    val id: Int,
    val authorName: String,
    val formattedDate: String, // "dd.MM.yyyy"
    val text: String,
    val photoUrl: String?,
    val isGuest: Boolean,
    val receiverName: String?, // for responses: "to: {name}"
)

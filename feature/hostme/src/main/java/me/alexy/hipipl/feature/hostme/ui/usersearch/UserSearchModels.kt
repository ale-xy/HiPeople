package me.alexy.hipipl.feature.hostme.ui.usersearch

import me.alexy.hipipl.core.presentation.UiText

// find_multi's "user" response has no host listing id or reference count,
// so this card is display-only: no click target, no reference line.
data class UserCardUi(
    val userId: Int,
    val photoUrl: String?,
    val name: String,
    val ageText: String,
)

data class UserSearchState(
    val userQuery: String = "",
    val isSearchingUser: Boolean = false,
    val userResult: UserCardUi? = null,
    val userSearchError: UiText? = null,
)

sealed interface UserSearchAction {
    data class OnUserQueryChange(val text: String) : UserSearchAction
    data object OnSearchUserClick : UserSearchAction
    data class OnPasteFromClipboard(val text: String) : UserSearchAction
    data object OnUsersOnMapClick : UserSearchAction
}

sealed interface UserSearchEvent {
    data object NavigateToUsersMap : UserSearchEvent
}

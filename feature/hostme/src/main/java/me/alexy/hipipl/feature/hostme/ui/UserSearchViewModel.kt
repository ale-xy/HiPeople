package me.alexy.hipipl.feature.hostme.ui

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
import me.alexy.hipipl.core.domain.ContactSearchRemoteDataSource
import me.alexy.hipipl.core.domain.ContactSearchResult
import me.alexy.hipipl.core.domain.onFailure
import me.alexy.hipipl.core.domain.onSuccess
import me.alexy.hipipl.core.presentation.UiText
import me.alexy.hipipl.core.presentation.toUiText
import me.alexy.hipipl.feature.hostitem.R

class UserSearchViewModel(
    private val contactSearchDataSource: ContactSearchRemoteDataSource,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _state = MutableStateFlow(
        UserSearchState(userQuery = savedStateHandle["userQuery"] ?: "")
    )
    val state: StateFlow<UserSearchState> = _state

    private val _events = Channel<UserSearchEvent>()
    val events: Flow<UserSearchEvent> = _events.receiveAsFlow()

    fun onAction(action: UserSearchAction) {
        when (action) {
            is UserSearchAction.OnUserQueryChange -> {
                savedStateHandle["userQuery"] = action.text
                _state.update { it.copy(userQuery = action.text, userSearchError = null) }
            }

            is UserSearchAction.OnPasteFromClipboard -> {
                savedStateHandle["userQuery"] = action.text
                _state.update { it.copy(userQuery = action.text, userSearchError = null) }
            }

            UserSearchAction.OnSearchUserClick -> {
                searchUser()
            }

            UserSearchAction.OnUsersOnMapClick -> {
                viewModelScope.launch {
                    _events.send(UserSearchEvent.NavigateToUsersMap)
                }
            }
        }
    }

    private fun searchUser() {
        val query = _state.value.userQuery.trim()
        if (query.isEmpty()) return

        viewModelScope.launch {
            _state.update { it.copy(isSearchingUser = true, userSearchError = null, userResult = null) }

            contactSearchDataSource.findByQuery(query, userId = null)
                .onSuccess { result ->
                    when (result) {
                        is ContactSearchResult.UserFound -> {
                            _state.update {
                                it.copy(userResult = result.toUserCardUi(), isSearchingUser = false)
                            }
                        }

                        ContactSearchResult.NotFoundOrOther -> {
                            _state.update {
                                it.copy(
                                    userResult = null,
                                    isSearchingUser = false,
                                    userSearchError = UiText.StringResource(R.string.user_not_found),
                                )
                            }
                        }
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(userResult = null, isSearchingUser = false, userSearchError = error.toUiText())
                    }
                }
        }
    }
}

package me.alexy.hipipl.feature.hostme.ui.hostsearch

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import me.alexy.hipipl.core.designsystem.HiPeopleTheme
import me.alexy.hipipl.core.designsystem.components.SearchTextField
import me.alexy.hipipl.core.designsystem.components.SuggestionItemUi
import me.alexy.hipipl.core.designsystem.components.SuggestionsDropdown
import me.alexy.hipipl.feature.hostitem.R

/**
 * Full-screen search overlay using SearchTextField. Replaces LocationSearchBottomSheet
 * to avoid IME/anchor interaction issues inherent to ModalBottomSheet.
 *
 * Must be called unconditionally from the parent so AnimatedVisibility can play the exit
 * transition. Visibility is controlled internally via state.isSearchSheetOpen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LocationSearchOverlay(
    state: HostsSearchState,
    onAction: (HostsSearchAction) -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    BackHandler(enabled = state.isSearchSheetOpen) {
        onAction(HostsSearchAction.OnDismissSearchSheet)
    }

    AnimatedVisibility(
        visible = state.isSearchSheetOpen,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 4 }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 4 }),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFF1EB))
                .padding(16.dp)
        ) {
            SearchTextField(
                query = state.locationQuery,
                onQueryChange = { onAction(HostsSearchAction.OnLocationQueryChange(it)) },
                placeholder = stringResource(R.string.city_coordinates),
                leadingIcon = {
                    IconButton(onClick = { onAction(HostsSearchAction.OnDismissSearchSheet) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                },
                trailingIcon = if (state.locationQuery.isNotEmpty()) {
                    {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.cd_clear_search),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    null
                },
                onTrailingIconClick = { onAction(HostsSearchAction.OnLocationQueryChange("")) },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 8.dp)
            ) {
                item {
                    SuggestionsDropdown(
                        isLoading = state.isLoadingSuggestions,
                        loadingText = stringResource(R.string.searching_locations),
                        suggestions = state.locationSuggestions.map {
                            SuggestionItemUi(
                                id = it.id.toString(),
                                primaryText = it.displayName,
                                secondaryText = it.regionLine
                            )
                        },
                        onSuggestionClick = { suggestion ->
                            val location = state.locationSuggestions.first { it.id.toString() == suggestion.id }
                            onAction(HostsSearchAction.OnLocationSuggestionClick(location))
                        }
                    )
                }
            }
        }

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}

@Preview
@Composable
private fun LocationSearchOverlayPreview(
    @PreviewParameter(LocationSearchOverlayPreviewParameterProvider::class) state: HostsSearchState,
) {
    HiPeopleTheme {
        LocationSearchOverlay(state = state, onAction = {})
    }
}

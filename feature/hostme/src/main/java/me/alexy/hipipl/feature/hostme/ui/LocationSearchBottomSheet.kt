package me.alexy.hipipl.feature.hostme.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import me.alexy.hipipl.core.designsystem.AppColors
import me.alexy.hipipl.core.designsystem.HiPeopleTheme
import me.alexy.hipipl.core.designsystem.components.SearchTextField
import me.alexy.hipipl.core.designsystem.components.SuggestionItemUi
import me.alexy.hipipl.core.designsystem.components.SuggestionsDropdown
import me.alexy.hipipl.feature.hostitem.R
import me.alexy.hipipl.feature.hostme.ui.preview.LocationSearchBottomSheetPreviewParameterProvider

/**
 * Full-screen-ish sheet holding the editable location search field and its suggestions list,
 * replacing the old inline dropdown so the search UI never has to fight for space with (or get
 * covered by) the results list underneath.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LocationSearchBottomSheet(
    state: HostsSearchState,
    onAction: (HostsSearchAction) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val configuration = LocalConfiguration.current
    val imeBottomPadding = WindowInsets.ime.asPaddingValues().calculateBottomPadding()
    // Sized against the IME-aware available height so it never exceeds what's left above the
    // keyboard (which would pin the sheet to the top of the screen); animated so the resize as
    // the keyboard opens/closes is a smooth transition instead of an abrupt jump.
    val sheetHeight by animateDpAsState(
        targetValue = (configuration.screenHeightDp.dp - imeBottomPadding) * 0.8f,
        label = "LocationSearchSheetHeight"
    )

    ModalBottomSheet(
        onDismissRequest = { onAction(HostsSearchAction.OnDismissSearchSheet) },
        sheetState = sheetState,
        scrimColor = AppColors.Scrim.copy(alpha = 0.42f),
        contentWindowInsets = {
            WindowInsets.safeDrawing
                .only(WindowInsetsSides.Top)
                .union(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom))
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(sheetHeight)
                .padding(horizontal = 16.dp)
        ) {
            SearchTextField(
                query = state.locationQuery,
                onQueryChange = { onAction(HostsSearchAction.OnLocationQueryChange(it)) },
                placeholder = stringResource(R.string.city_coordinates),
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .focusRequester(focusRequester),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )

            LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
                item {
                    SuggestionsDropdown(
                        isLoading = state.isLoadingSuggestions,
                        loadingText = stringResource(R.string.searching_locations),
                        suggestions = state.locationSuggestions.map {
                            SuggestionItemUi(id = it.id.toString(), primaryText = it.displayName, secondaryText = it.regionLine)
                        },
                        onSuggestionClick = { suggestion ->
                            val location = state.locationSuggestions.first { it.id.toString() == suggestion.id }
                            onAction(HostsSearchAction.OnLocationSuggestionClick(location))
                        }
                    )
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Preview
@Composable
private fun LocationSearchBottomSheetPreview(
    @PreviewParameter(LocationSearchBottomSheetPreviewParameterProvider::class) state: HostsSearchState,
) {
    HiPeopleTheme {
        LocationSearchBottomSheet(state = state, onAction = {})
    }
}

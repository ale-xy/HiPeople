package me.alexy.hipipl.feature.hostme.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import me.alexy.hipipl.core.designsystem.AppColors
import me.alexy.hipipl.core.designsystem.HiPeopleTheme
import me.alexy.hipipl.core.designsystem.components.EmptyStateIllustration
import me.alexy.hipipl.core.designsystem.components.SearchTextField
import me.alexy.hipipl.core.presentation.asString
import me.alexy.hipipl.feature.hostitem.R
import me.alexy.hipipl.feature.hostme.ui.preview.HostsTabContentPreviewParameterProvider

@Composable
internal fun HostsTabContent(
    state: HostsSearchState,
    onAction: (HostsSearchAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                HostsMapCard(
                    onClick = { onAction(HostsSearchAction.OnHostsOnMapClick) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }

            item {
                SearchTextField(
                    query = state.locationQuery,
                    onQueryChange = {},
                    placeholder = stringResource(R.string.city_coordinates),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (state.filters.hasActiveFilters) {
                                            MaterialTheme.colorScheme.secondaryContainer
                                        } else {
                                            Color.Transparent
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = stringResource(R.string.cd_filter_button),
                                    tint = if (state.filters.hasActiveFilters) {
                                        MaterialTheme.colorScheme.onSecondaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }
                            if (state.filters.hasActiveFilters) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(9.dp)
                                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                                        .border(1.5.dp, Color.White, CircleShape)
                                )
                            }
                        }
                    },
                    onTrailingIconClick = { onAction(HostsSearchAction.OnFilterIconClick) },
                    readOnly = true,
                    onClick = { onAction(HostsSearchAction.OnOpenSearchSheet) }
                )
            }

            when {
                state.selectedLocationId == null -> {
                    item {
                        EmptyStateIllustration(
                            icon = Icons.Default.Home,
                            title = stringResource(R.string.search_hosts_initial_title),
                            subtitle = stringResource(R.string.search_hosts_initial_subtitle),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                state.isLoadingHosts -> {
                    item {
                        LinearProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }

                state.hostsError != null -> {
                    item {
                        Text(
                            text = state.hostsError.asString(),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.fillMaxWidth().padding(32.dp)
                        )
                    }
                }

                state.hostResults.isEmpty() -> {
                    item {
                        EmptyStateIllustration(
                            icon = Icons.Default.SearchOff,
                            title = stringResource(R.string.search_hosts_no_find),
                            subtitle = stringResource(R.string.search_hosts_empty_subtitle),
                            iconTint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.fillMaxWidth(),
                            action = {
                                OutlinedButton(
                                    onClick = { onAction(HostsSearchAction.OnFilterIconClick) }
                                ) {
                                    Text(stringResource(R.string.edit_filters))
                                }
                            }
                        )
                    }
                }

                else -> {
                    item {
                        Text(
                            text = stringResource(R.string.search_hosts_find, state.totalFound),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.Success,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                    items(items = state.hostResults, key = { it.hostId }) { host ->
                        HostResultCard(
                            host = host,
                            onAction = onAction,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }

        LocationSearchOverlay(state = state, onAction = onAction)
    }

    if (state.isFilterSheetOpen) {
        FilterBottomSheet(filters = state.filters, onAction = onAction)
    }
}

@Preview
@Composable
private fun HostsTabContentPreview(
    @PreviewParameter(HostsTabContentPreviewParameterProvider::class) state: HostsSearchState,
) {
    HiPeopleTheme {
        HostsTabContent(state = state, onAction = {})
    }
}

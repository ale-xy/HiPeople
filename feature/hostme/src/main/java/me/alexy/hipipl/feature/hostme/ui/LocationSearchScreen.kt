
package me.alexy.hipipl.feature.hostme.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.alexy.hipipl.core.presentation.ObserveAsEvents
import me.alexy.hipipl.core.presentation.asString
import me.alexy.hipipl.feature.hostitem.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun LocationSearchScreen(
    onNavigateToHostList: (Int, String) -> Unit,
    viewModel: LocationSearchViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is LocationSearchEvent.NavigateToHostList -> {
                onNavigateToHostList(event.locationId, event.locationName)
            }
        }
    }

    LocationSearchScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSearchScreen(
    state: LocationSearchState,
    onAction: (LocationSearchAction) -> Unit
) {
    Scaffold(
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineMedium,
                    text = stringResource(R.string.host_search)
                )

                val inputField = @Composable {
                    SearchBarDefaults.InputField(
                        query = state.searchText,
                        onQueryChange = { onAction(LocationSearchAction.OnSearchTextChange(it)) },
                        expanded = !state.isSearchExpanded,
                        onExpandedChange = { },
                        modifier = Modifier,
                        onSearch = { onAction(LocationSearchAction.OnSearch) },
                        placeholder = { Text(stringResource(R.string.enter_city)) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (state.searchText.isNotBlank()) {
                                IconButton(
                                    onClick = { onAction(LocationSearchAction.OnClearSearch) }
                                ) {
                                    Icon(Icons.Default.Clear, contentDescription = null)
                                }
                            }
                        },
                    )
                }

                DockedSearchBar(
                    modifier = Modifier.fillMaxWidth(),
                    inputField = inputField,
                    expanded = state.isSearchExpanded,
                    onExpandedChange = { },
                ) {
                    when {
                        state.isLoading -> {
                            Box(Modifier.fillMaxSize()) {
                                Text(
                                    modifier = Modifier.align(Alignment.Center),
                                    style = MaterialTheme.typography.bodyLarge,
                                    text = stringResource(R.string.loading)
                                )
                            }
                        }

                        state.error != null -> {
                            Box(Modifier.fillMaxSize()) {
                                Text(
                                    modifier = Modifier.align(Alignment.Center),
                                    style = MaterialTheme.typography.bodyLarge,
                                    text = state.error.asString()
                                )
                            }
                        }

                        state.locations.isNotEmpty() -> {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(32.dp),
                                contentPadding = PaddingValues(16.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(
                                    count = state.locations.size,
                                    key = { index -> state.locations[index].id },
                                    itemContent = { index ->
                                        LocationListItem(
                                            location = state.locations[index],
                                            onAction = onAction
                                        )
                                    }
                                )
                            }
                        }

                        else -> {
                            Box(Modifier.fillMaxSize()) {
                                Text(
                                    modifier = Modifier.align(Alignment.Center),
                                    style = MaterialTheme.typography.bodyLarge,
                                    text = stringResource(R.string.nothing_found)
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun LocationListItem(
    location: LocationUi,
    onAction: (LocationSearchAction) -> Unit
) {
    Column(
        modifier = Modifier.clickable {
            onAction(LocationSearchAction.OnLocationClick(location.id, location.displayName))
        }
    ) {
        Text(
            style = MaterialTheme.typography.bodyLarge,
            text = location.displayName
        )
        Text(
            style = MaterialTheme.typography.bodyMedium,
            text = location.regionLine
        )
    }
}

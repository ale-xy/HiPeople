
package me.alexy.hipipl.feature.hostme.ui

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hippl.model.Location
import me.alexy.hipipl.feature.hostitem.R

@Composable
fun LocationSearchScreen(
    modifier: Modifier,
    viewModel: LocationSearchViewModel = hiltViewModel()
) {
    val searchText by viewModel.searchText.collectAsState()
    val searchExpanded by viewModel.searchExpanded.collectAsState()
    val locationList by viewModel.locationList.collectAsState()

    LocationSearchScreen(modifier,
        searchText,
        searchExpanded,
        locationList,
        { viewModel.onSearchTextChange(it) },
        { viewModel.onSearch() },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSearchScreen(
    modifier: Modifier,
    searchText: String,
    searchExpanded: Boolean,
    locationList: List<Location>,
    onSearchTextChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        content = { padding ->

            Column(
                modifier = modifier
                    .padding(padding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                Text(
                    style = MaterialTheme.typography.headlineMedium,
                    text = stringResource(R.string.host_search)
                )

                val inputField =
                    @Composable {
                        SearchBarDefaults.InputField(
                            query = searchText,
                            onQueryChange = { onSearchTextChange(it) },
                            expanded = !searchExpanded,
                            onExpandedChange = { },
                            modifier = Modifier,
                            onSearch = { onSearch() },
                            placeholder = { Text(stringResource(R.string.enter_city)) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            trailingIcon = {
                                IconButton(
                                    onClick = { onSearchTextChange("") }
                                ) {
                                    if (searchText.isNotBlank()) {
                                        Icon(Icons.Default.Clear, contentDescription = null)
                                    }
                                }
                            },
                        )
                    }

                DockedSearchBar(
                    modifier = Modifier.padding(padding).fillMaxWidth(),
                    inputField = inputField,
                    expanded = searchExpanded,
                    onExpandedChange = { },
                ) {
                    if (locationList.isNotEmpty()) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(32.dp),
                            contentPadding = PaddingValues(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                count = locationList.size,
                                key = { index ->
                                    locationList[index].id
                                },
                                itemContent = { index ->
                                    LocationListItem(locationList[index])
                                }
                            )
                        }
                    } else {
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
    )
}

@Composable
fun LocationListItem(location: Location) {
    Column {
        with(location) {
            Text(
                style = MaterialTheme.typography.bodyLarge,
                text = if (name.isNotBlank()) "$name ($nameEn)" else nameEn
            )
            Text(
                style = MaterialTheme.typography.bodyMedium,
                text = listOf(regionName, countryNameEn)
                    .filter { it.isNotBlank() }
                    .joinToString(", ")
            )
        }
    }
}
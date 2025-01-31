package me.alexy.hipipl.feature.hostme.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hippl.model.Location

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSearchScreen(
    modifier: Modifier,
    viewModel: LocationSearchViewModel = hiltViewModel()
) {
    Scaffold(
        modifier = modifier,
        content = { padding ->
            val searchText by viewModel.searchText.collectAsState()
            val searchExpanded by viewModel.searchExpanded.collectAsState()
            val locationList by viewModel.locationList.collectAsState()

            val inputField =
                @Composable {
                    SearchBarDefaults.InputField(
                        query = searchText,
                        onQueryChange = { viewModel.onSearchTextChange(it) },
                        expanded = !searchExpanded,
                        onExpandedChange = { },
                        modifier = Modifier,
                        onSearch = { viewModel.onSearch() },
                        placeholder = { Text("Search...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = { Icon(Icons.Default.MoreVert, contentDescription = null) },
                    )
                }

            SearchBar(
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
                    Text("Ничего нет")
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
                listOf(name, regionName, countryName)
                    .filter { it.isNotEmpty() }
                    .joinToString(", ")
            )
            Text(
                listOf(nameEn, regionName, countryNameEn)
                    .filter { it.isNotEmpty() }
                    .joinToString(", ")
            )
        }
    }
}
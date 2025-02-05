package me.alexy.hipipl.feature.hostme.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hippl.model.HostUser
import me.alexy.hipipl.core.ui.LightGreen
import me.alexy.hipipl.feature.hostitem.R

@Composable
fun HostListByLocationScreen(
    modifier: Modifier = Modifier,
    viewModel: HostListForLocationViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()

    HostListByLocationScreen(
        modifier = modifier,
        locationName = viewModel.locationName,
        uiState = uiState.value
    )
}
@Composable
fun HostListByLocationScreen(
    locationName: String,
    modifier: Modifier = Modifier,
    uiState: HostListUiState,
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
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineMedium,
                    text = stringResource(R.string.host_list_in_city, locationName)
                )

                when(uiState) {
                    is HostListUiState.Loading -> {
                        Box(Modifier.fillMaxSize()) {
                            CircularProgressIndicator(Modifier.align(Alignment.Center))
                        }
                    }
                    is HostListUiState.Error -> {
                        Box(Modifier.fillMaxSize()) {
                            Text(uiState.error)
                        }
                    }
                    is HostListUiState.Success -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                count = uiState.list.size,
                                key = { index -> uiState.list[index].host.hostId },
                                itemContent = { index ->
                                    HostListItem(uiState.list[index])
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun HostListItem(hostUser: HostUser) {
    //todo image
    with(hostUser) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = LightGreen, shape = RoundedCornerShape(12.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            val ageText = if (age > 0) stringResource(R.string.age_format, age) else ""
            val nameText = "$name $averageRating* ($totalReviews)" +
                    if (ageText.isNotBlank()) ", $ageText" else ""

            Text(
                style = MaterialTheme.typography.titleMedium,
                text = nameText
            )

            val distanceText = if (host.dist > 0) {
                "(${stringResource(R.string.distance_km_format, host.dist)}" +
                        if (host.direction.isNotBlank()) {
                            " ${host.direction}"
                        } else {
                            ""
                        } + ")"
            } else {
                ""
            }

            Text(
                style = MaterialTheme.typography.bodyLarge,
                text = listOf(host.city, distanceText).joinToString(" ")
            )

            Text(
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                text = host.text.replace("\n\n", "\n")
            )
        }
    }
}
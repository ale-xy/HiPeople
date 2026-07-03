package me.alexy.hipipl.feature.hostme.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import me.alexy.hipipl.core.presentation.ObserveAsEvents
import me.alexy.hipipl.core.presentation.asString
import me.alexy.hipipl.core.designsystem.LightGreen
import me.alexy.hipipl.feature.hostitem.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun HostListByLocationScreen(
    onNavigateToHostDetails: (Int, Int) -> Unit,
    viewModel: HostListByLocationViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is HostListByLocationEvent.NavigateToHostDetails -> {
                onNavigateToHostDetails(event.hostId, event.userId)
            }
        }
    }

    HostListByLocationScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun HostListByLocationScreen(
    state: HostListByLocationState,
    onAction: (HostListByLocationAction) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium,
            text = stringResource(R.string.host_list_in_city, state.locationName)
        )

        when {
            state.isLoading -> {
                Box(Modifier.fillMaxSize()) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
            }

            state.error != null -> {
                Box(Modifier.fillMaxSize()) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = state.error.asString()
                    )
                }
            }

            state.hosts.isNotEmpty() -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        count = state.hosts.size,
                        key = { index -> state.hosts[index].hostId },
                        itemContent = { index ->
                            HostListItem(
                                host = state.hosts[index],
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
                        text = stringResource(R.string.nothing_found)
                    )
                }
            }
        }
    }
}

@Composable
fun HostListItem(
    host: HostListItemUi,
    onAction: (HostListByLocationAction) -> Unit
) {
    Row(
        modifier = Modifier
            .clickable {
                onAction(HostListByLocationAction.OnHostClick(host.hostId, host.userId))
            }
            .fillMaxWidth()
            .background(color = LightGreen, shape = RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val placeholder = painterResource(me.alexy.hipipl.core.designsystem.R.drawable.avatar)

        AsyncImage(
            modifier = Modifier.size(120.dp),
            model = host.photoUrl,
            alignment = Alignment.Center,
            contentScale = ContentScale.Crop,
            placeholder = placeholder,
            error = placeholder,
            fallback = placeholder,
            contentDescription = null
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                style = MaterialTheme.typography.titleMedium,
                text = host.titleLine
            )

            Text(
                style = MaterialTheme.typography.bodyLarge,
                text = host.locationLine
            )

            Text(
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                text = host.description
            )
        }
    }
}

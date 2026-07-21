package me.alexy.hipipl.feature.hostme.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import me.alexy.hipipl.core.designsystem.HiPeopleTheme
import me.alexy.hipipl.core.presentation.ObserveAsEvents
import me.alexy.hipipl.feature.hostitem.R
import me.alexy.hipipl.feature.hostme.ui.hostsearch.FilterBottomSheet
import me.alexy.hipipl.feature.hostme.ui.hostsearch.HostsSearchAction
import me.alexy.hipipl.feature.hostme.ui.hostsearch.HostsSearchEvent
import me.alexy.hipipl.feature.hostme.ui.hostsearch.HostsSearchState
import me.alexy.hipipl.feature.hostme.ui.hostsearch.HostsSearchViewModel
import me.alexy.hipipl.feature.hostme.ui.hostsearch.HostsTabContent
import me.alexy.hipipl.feature.hostme.ui.usersearch.UserSearchAction
import me.alexy.hipipl.feature.hostme.ui.usersearch.UserSearchEvent
import me.alexy.hipipl.feature.hostme.ui.usersearch.UserSearchState
import me.alexy.hipipl.feature.hostme.ui.usersearch.UserSearchViewModel
import me.alexy.hipipl.feature.hostme.ui.usersearch.UsersTabContent
import org.koin.androidx.compose.koinViewModel

enum class SearchTab { HOSTS, USERS }

@Composable
fun HostSearchScreen(
    onNavigateToHostDetails: (Int, Int) -> Unit,
    hostsSearchViewModel: HostsSearchViewModel = koinViewModel(),
    userSearchViewModel: UserSearchViewModel = koinViewModel(),
) {
    val hostsState by hostsSearchViewModel.state.collectAsStateWithLifecycle()
    val userState by userSearchViewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val mapComingSoonMessage = stringResource(R.string.hosts_on_map)
    var selectedTab by rememberSaveable { mutableStateOf(SearchTab.HOSTS) }

    ObserveAsEvents(hostsSearchViewModel.events) { event ->
        when (event) {
            is HostsSearchEvent.NavigateToHostDetails -> {
                onNavigateToHostDetails(event.hostId, event.userId)
            }

            HostsSearchEvent.NavigateToHostsMap -> {
                coroutineScope.launch { snackbarHostState.showSnackbar(mapComingSoonMessage) }
            }
        }
    }

    ObserveAsEvents(userSearchViewModel.events) { event ->
        when (event) {
            UserSearchEvent.NavigateToUsersMap -> {
                coroutineScope.launch { snackbarHostState.showSnackbar(mapComingSoonMessage) }
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.inverseSurface,
                    contentColor = MaterialTheme.colorScheme.inverseOnSurface
                )
            }
        }
    ) { padding ->
        HostSearchScreen(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            hostsState = hostsState,
            onHostsAction = hostsSearchViewModel::onAction,
            userState = userState,
            onUserAction = userSearchViewModel::onAction,
            modifier = Modifier.padding(padding)
        )
    }
}

@Composable
fun HostSearchScreen(
    selectedTab: SearchTab,
    onTabSelected: (SearchTab) -> Unit,
    hostsState: HostsSearchState,
    onHostsAction: (HostsSearchAction) -> Unit,
    userState: UserSearchState,
    onUserAction: (UserSearchAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.button__search),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        val tabTitles = listOf(stringResource(R.string.tab_hosts), stringResource(R.string.tab_users))
        PrimaryTabRow(
            selectedTabIndex = selectedTab.ordinal,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab.ordinal == index,
                    onClick = { onTabSelected(SearchTab.entries[index]) },
                    text = { Text(title, style = MaterialTheme.typography.labelLarge) },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        when (selectedTab) {
            SearchTab.HOSTS -> HostsTabContent(hostsState, onHostsAction, modifier = Modifier.weight(1f))
            SearchTab.USERS -> UsersTabContent(userState, onUserAction, modifier = Modifier.weight(1f))
        }
    }

    if (hostsState.isFilterSheetOpen) {
        FilterBottomSheet(hostsState.filters, onHostsAction)
    }
}

@Preview
@Composable
private fun HostSearchScreenPreview(
    @PreviewParameter(HostSearchScreenPreviewParameterProvider::class) previewState: HostSearchScreenPreviewState,
) {
    HiPeopleTheme {
        HostSearchScreen(
            selectedTab = previewState.selectedTab,
            onTabSelected = {},
            hostsState = previewState.hostsState,
            onHostsAction = {},
            userState = previewState.userState,
            onUserAction = {}
        )
    }
}

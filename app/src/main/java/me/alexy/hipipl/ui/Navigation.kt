
package me.alexy.hipipl.ui

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import me.alexy.hipipl.core.designsystem.components.BottomNavItemUi
import me.alexy.hipipl.core.designsystem.components.HiPeopleBottomNavigationBar
import me.alexy.hipipl.feature.auth.AuthRoute
import me.alexy.hipipl.feature.auth.ui.AuthScreen
import me.alexy.hipipl.feature.hostme.HostDetailsRoute
import me.alexy.hipipl.feature.hostme.HostSearchRoute
import me.alexy.hipipl.feature.hostme.ui.HostSearchScreen
import me.alexy.hipipl.feature.hostme.ui.hostdetails.HostDetailsScreen
import me.alexy.hipipl.feature.menu.AddMenuRoute
import me.alexy.hipipl.feature.menu.FavoritesRoute
import me.alexy.hipipl.feature.menu.HostListingFormRoute
import me.alexy.hipipl.feature.menu.LeaveReviewSearchRoute
import me.alexy.hipipl.feature.menu.MessagesRoute
import me.alexy.hipipl.feature.menu.ProfileMenuRoute
import me.alexy.hipipl.feature.menu.R
import me.alexy.hipipl.feature.menu.SettingsRoute
import me.alexy.hipipl.feature.menu.ui.AddMenuScreen
import me.alexy.hipipl.feature.menu.ui.FavoritesScreen
import me.alexy.hipipl.feature.menu.ui.HostListingFormScreen
import me.alexy.hipipl.feature.menu.ui.LeaveReviewSearchScreen
import me.alexy.hipipl.feature.menu.ui.MessagesScreen
import me.alexy.hipipl.feature.menu.ui.ProfileMenuScreen
import me.alexy.hipipl.feature.menu.ui.SettingsScreen

private data class BottomNavTab(
    val route: Any,
    val item: BottomNavItemUi,
)

// Per the auth screen doc ("Авторизация.md"): browsing is available unauthenticated - the auth
// screen only opens contextually, when the user hits a gated action (contact/message/favorite
// today; a nav-bar login button and "add listing" entry point will trigger it too once those
// screens exist), not as a blanket gate on app start.
@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    val bottomNavTabs = listOf(
        BottomNavTab(HostSearchRoute, BottomNavItemUi(Icons.Default.Search, stringResource(R.string.nav_search))),
        BottomNavTab(FavoritesRoute, BottomNavItemUi(Icons.Default.FavoriteBorder, stringResource(R.string.nav_favorites))),
        BottomNavTab(AddMenuRoute, BottomNavItemUi(Icons.Default.Add, stringResource(R.string.nav_add))),
        BottomNavTab(MessagesRoute, BottomNavItemUi(Icons.AutoMirrored.Filled.Message, stringResource(R.string.nav_messages))),
        BottomNavTab(ProfileMenuRoute, BottomNavItemUi(Icons.Default.Menu, stringResource(R.string.nav_menu))),
    )

    fun navigateToTab(route: Any) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val selectedTabIndex = bottomNavTabs.indexOfFirst { tab ->
        currentDestination?.hasRoute(tab.route::class) == true
    }

    Scaffold(
        bottomBar = {
            if (selectedTabIndex >= 0) {
                HiPeopleBottomNavigationBar(
                    items = bottomNavTabs.map { it.item },
                    selectedIndex = selectedTabIndex,
                    onItemSelected = { index -> navigateToTab(bottomNavTabs[index].route) },
                )
            }
        },
    ) { padding ->
        NavHost(
            modifier = Modifier
                .consumeWindowInsets(padding)
                .padding(padding),
            navController = navController,
            startDestination = HostSearchRoute,
        ) {
            composable<AuthRoute> {
                AuthScreen(
                    onNavigateToApp = {
                        // Return to whatever screen triggered the auth flow; only falls back to
                        // Host Search if Auth was somehow the sole back stack entry (e.g. reached via
                        // a future direct entry point rather than a contextual gate).
                        if (!navController.popBackStack()) {
                            navController.navigate(route = HostSearchRoute)
                        }
                    }
                )
            }
            composable<HostSearchRoute> {
                HostSearchScreen(
                    onNavigateToHostDetails = { hostId: Int, userId: Int ->
                        navController.navigate(route = HostDetailsRoute(hostId, userId))
                    }
                )
            }
            composable<HostDetailsRoute> {
                HostDetailsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToAuth = { navController.navigate(route = AuthRoute) },
                )
            }
            composable<FavoritesRoute> {
                FavoritesScreen()
            }
            composable<MessagesRoute> {
                MessagesScreen()
            }
            composable<ProfileMenuRoute> {
                ProfileMenuScreen(
                    onNavigateToSettings = { navController.navigate(SettingsRoute) },
                )
            }
            composable<SettingsRoute> {
                SettingsScreen(onNavigateBack = { navController.popBackStack() })
            }
            composable<AddMenuRoute> {
                AddMenuScreen(
                    onNavigateToHostListingForm = { navController.navigate(HostListingFormRoute) },
                    onNavigateToHostSearch = { navigateToTab(HostSearchRoute) },
                    onNavigateToLeaveReview = { navController.navigate(LeaveReviewSearchRoute) },
                )
            }
            composable<HostListingFormRoute> {
                HostListingFormScreen(onNavigateBack = { navController.popBackStack() })
            }
            composable<LeaveReviewSearchRoute> {
                LeaveReviewSearchScreen(onNavigateBack = { navController.popBackStack() })
            }
        }
    }
}

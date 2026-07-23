
package me.alexy.hipipl.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import me.alexy.hipipl.feature.auth.AuthRoute
import me.alexy.hipipl.feature.auth.ui.AuthScreen
import me.alexy.hipipl.feature.hostme.HostDetailsRoute
import me.alexy.hipipl.feature.hostme.HostSearchRoute
import me.alexy.hipipl.feature.hostme.ui.hostdetails.HostDetailsScreen
import me.alexy.hipipl.feature.hostme.ui.HostSearchScreen

// Per the auth screen doc ("Авторизация.md"): browsing is available unauthenticated - the auth
// screen only opens contextually, when the user hits a gated action (contact/message/favorite
// today; a nav-bar login button and "add listing" entry point will trigger it too once those
// screens exist), not as a blanket gate on app start.
@Composable
fun MainNavigation(modifier: Modifier) {
    val navController = rememberNavController()

    NavHost(
        modifier = modifier,
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
    }
}


package me.alexy.hipipl.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import me.alexy.hipipl.feature.hostme.HostDetailsRoute
import me.alexy.hipipl.feature.hostme.HostSearchRoute
import me.alexy.hipipl.feature.hostme.ui.hostdetails.HostDetailsScreen
import me.alexy.hipipl.feature.hostme.ui.HostSearchScreen

@Composable
fun MainNavigation(modifier: Modifier) {
    val navController = rememberNavController()

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = HostSearchRoute,
    ) {
        composable<HostSearchRoute> {
            HostSearchScreen(
                onNavigateToHostDetails = { hostId: Int, userId: Int ->
                    navController.navigate(route = HostDetailsRoute(hostId, userId))
                }
            )
        }
        composable<HostDetailsRoute> {
            HostDetailsScreen()
        }
    }
}

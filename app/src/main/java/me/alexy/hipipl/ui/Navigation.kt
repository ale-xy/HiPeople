
package me.alexy.hipipl.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import me.alexy.hipipl.feature.hostme.HostDetailsRoute
import me.alexy.hipipl.feature.hostme.HostListByLocationRoute
import me.alexy.hipipl.feature.hostme.LocationSearchRoute
import me.alexy.hipipl.feature.hostme.ui.HostDetailsScreen
import me.alexy.hipipl.feature.hostme.ui.HostListByLocationScreen
import me.alexy.hipipl.feature.hostme.ui.LocationSearchScreen

@Composable
fun MainNavigation(modifier: Modifier) {
    val navController = rememberNavController()

    NavHost(
        modifier = modifier.padding(16.dp),
        navController = navController,
        startDestination = LocationSearchRoute,
    ) {
        composable<LocationSearchRoute> {
            LocationSearchScreen(
                onNavigateToHostList = { id: Int, name: String ->
                    navController.navigate(route = HostListByLocationRoute(id, name))
                }
            )
        }
        composable<HostListByLocationRoute> {
            HostListByLocationScreen(
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

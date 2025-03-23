
package me.alexy.hipipl.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import me.alexy.hipipl.feature.hostme.HostDetails
import me.alexy.hipipl.feature.hostme.HostListByLocation
import me.alexy.hipipl.feature.hostme.LocationSearch
import me.alexy.hipipl.feature.hostme.ui.HostDetailsScreen
import me.alexy.hipipl.feature.hostme.ui.HostListByLocationScreen
import me.alexy.hipipl.feature.hostme.ui.LocationSearchScreen



@Composable
fun MainNavigation(modifier: Modifier) {
    val navController = rememberNavController()

    NavHost(
        modifier = modifier.padding(16.dp),
        navController = navController,
        startDestination = LocationSearch,
//        startDestination = HostDetails(1813),
    ) {
        composable<LocationSearch> {
            LocationSearchScreen {
                id, name -> navController.navigate(route = HostListByLocation(id, name))
            }
        }
        composable<HostListByLocation> {
            HostListByLocationScreen {
                id, userId -> navController.navigate(route = HostDetails(id, userId))
            }
        }
        composable<HostDetails> {
            HostDetailsScreen()
        }
    }
}

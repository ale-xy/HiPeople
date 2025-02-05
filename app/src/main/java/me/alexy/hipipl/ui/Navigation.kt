
package me.alexy.hipipl.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import me.alexy.hipipl.feature.hostme.HostListByLocation
import me.alexy.hipipl.feature.hostme.ui.HostListByLocationScreen
import me.alexy.hipipl.feature.hostme.ui.LocationSearchScreen

@Serializable
object LocationSearch


@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    NavHost(
        modifier = Modifier.padding(16.dp),
        navController = navController,
        startDestination = LocationSearch
    ) {
        composable<LocationSearch> {
            LocationSearchScreen() {
                id, name -> navController.navigate(route = HostListByLocation(id, name))
            }
        }
        composable<HostListByLocation> { backStackEntry ->
            HostListByLocationScreen()
        }
    }
}


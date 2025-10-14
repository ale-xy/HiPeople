
package com.hipeople.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hipeople.auth.Auth
import com.hipeople.auth.ui.AuthScreen
import com.hipeople.feature.hostme.HostDetails
import com.hipeople.feature.hostme.HostListByLocation
import com.hipeople.feature.hostme.LocationSearch
import com.hipeople.feature.hostme.ui.HostDetailsScreen
import com.hipeople.feature.hostme.ui.HostListByLocationScreen
import com.hipeople.feature.hostme.ui.LocationSearchScreen



@Composable
fun MainNavigation(modifier: Modifier) {
    val navController = rememberNavController()

    NavHost(
        modifier = modifier.padding(16.dp),
        navController = navController,
        startDestination = Auth,
        //startDestination = LocationSearch,
        //todo start destination
//        startDestination = HostDetails(1813),
    ) {
        composable<Auth> {
            AuthScreen(
                onAuthSuccess = { navController.navigate(LocationSearch) },
                onAuthFailure = { navController.navigate(LocationSearch) } //todo
            )
        }
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

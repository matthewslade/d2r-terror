package com.d2rterror.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.d2rterror.ui.screens.home.HomeScreen
import com.d2rterror.ui.screens.settings.SettingsScreen
import com.d2rterror.ui.screens.zones.ZoneSelectionScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object ZoneSelection : Screen("zone_selection")
    object Settings : Screen("settings")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToZoneSelection = {
                    navController.navigate(Screen.ZoneSelection.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(Screen.ZoneSelection.route) {
            ZoneSelectionScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

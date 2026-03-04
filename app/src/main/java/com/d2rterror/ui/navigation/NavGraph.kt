package com.d2rterror.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.d2rterror.ui.screens.home.HomeScreen
import com.d2rterror.ui.screens.settings.SettingsScreen
import com.d2rterror.ui.screens.zones.ZoneSelectionScreen

sealed class Screen(val route: String) {
    object CurrentZones : Screen("current_zones")
    object SelectedZones : Screen("selected_zones")
    object Settings : Screen("settings")
}

sealed class BottomNavItem(
    val screen: Screen,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object CurrentZones : BottomNavItem(
        screen = Screen.CurrentZones,
        title = "Current",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )

    object SelectedZones : BottomNavItem(
        screen = Screen.SelectedZones,
        title = "My Zones",
        selectedIcon = Icons.Filled.Checklist,
        unselectedIcon = Icons.Outlined.Checklist
    )

    object Settings : BottomNavItem(
        screen = Screen.Settings,
        title = "Settings",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )

    companion object {
        val items = listOf(CurrentZones, SelectedZones, Settings)
    }
}

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    bottomPadding: Dp = 0.dp,
    startDestination: String = Screen.CurrentZones.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.CurrentZones.route) {
            HomeScreen(bottomPadding = bottomPadding)
        }

        composable(Screen.SelectedZones.route) {
            ZoneSelectionScreen(bottomPadding = bottomPadding)
        }

        composable(Screen.Settings.route) {
            SettingsScreen(bottomPadding = bottomPadding)
        }
    }
}

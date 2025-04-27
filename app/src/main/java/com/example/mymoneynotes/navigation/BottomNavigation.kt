package com.example.mymoneynotes.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.List // Use specific icon
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector // Import ImageVector
import androidx.compose.ui.res.stringResource // Import stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.mymoneynotes.R // Import R class

// Bottom Navigation Item Data Class
sealed class BottomNavItem(
    val route: String,
    val titleResId: Int, // Use String Resource ID
    val icon: ImageVector // Use ImageVector
) {
    object Transactions : BottomNavItem(
        "transactions",
        R.string.bottom_nav_transactions, // Use string resource
        Icons.AutoMirrored.Filled.List // Use Material Icon
    )

    object Charts : BottomNavItem(
        "charts",
        R.string.bottom_nav_charts, // Use string resource
        Icons.Filled.Build // Use Material Icon
    )
}

@Composable
fun MainBottomNavigation(navController: NavController) {
    val items = listOf(
        BottomNavItem.Transactions,
        BottomNavItem.Charts
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            val title = stringResource(id = item.titleResId) // Resolve string resource
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon, // Use ImageVector
                        contentDescription = title // Use resolved title
                    )
                },
                label = { Text(title) } // Use resolved title
            )
        }
    }
}
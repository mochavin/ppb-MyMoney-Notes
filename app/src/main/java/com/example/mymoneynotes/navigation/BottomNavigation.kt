package com.example.mymoneynotes.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

// Bottom Navigation Item Data Class
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: Int
) {
    object Transactions : BottomNavItem(
        "transactions",
        "Transactions",
        android.R.drawable.ic_menu_agenda
    )

    object Charts : BottomNavItem(
        "charts",
        "Charts",
        android.R.drawable.ic_menu_report_image
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
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.title
                    )
                },
                label = { Text(item.title) }
            )
        }
    }
}
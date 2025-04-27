package com.example.mymoneynotes.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.mymoneynotes.ui.theme.screens.ChartScreen
import com.example.mymoneynotes.ui.theme.screens.TransactionScreen
import com.example.mymoneynotes.viewmodel.TransactionViewModels

fun NavGraphBuilder.mainGraph(navController: NavHostController, viewModel: TransactionViewModels) {
    composable(BottomNavItem.Transactions.route) {
        TransactionScreen(viewModel)
    }
    composable(BottomNavItem.Charts.route) {
        ChartScreen(viewModel)
    }
}
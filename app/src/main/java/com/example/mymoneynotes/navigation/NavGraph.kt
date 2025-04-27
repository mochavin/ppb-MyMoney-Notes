package com.example.mymoneynotes.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.mymoneynotes.ui.theme.screens.ChartScreen
import com.example.mymoneynotes.ui.theme.screens.TransactionScreen
import com.example.mymoneynotes.viewmodel.TransactionViewModel

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.mainGraph(navController: NavHostController, viewModel: TransactionViewModel) {
    composable(BottomNavItem.Transactions.route) {
        TransactionScreen(viewModel)
    }
    composable(BottomNavItem.Charts.route) {
        ChartScreen(viewModel)
    }
}
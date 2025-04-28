package com.example.mymoneynotes.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.mymoneynotes.data.Transaction
import com.example.mymoneynotes.ui.theme.screens.ChartScreen
import com.example.mymoneynotes.ui.theme.screens.IncomeChartScreen
import com.example.mymoneynotes.ui.theme.screens.TransactionScreen
import com.example.mymoneynotes.viewmodel.TransactionViewModel

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.mainGraph(
    navController: NavHostController,
    viewModel: TransactionViewModel,
    onEditRequest: (Transaction) -> Unit,
    onDeleteRequest: (Transaction) -> Unit
) {
    composable(BottomNavItem.Transactions.route) {
        TransactionScreen(
            viewModel = viewModel,
            onEditClick = onEditRequest,
            onDeleteClick = onDeleteRequest
        )
    }
    composable(BottomNavItem.ExpenseCharts.route) {
        ChartScreen(viewModel)
    }
    composable(BottomNavItem.IncomeCharts.route) {
        IncomeChartScreen(viewModel)
    }
}
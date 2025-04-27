package com.example.mymoneynotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.mymoneynotes.navigation.BottomNavItem
import com.example.mymoneynotes.navigation.MainBottomNavigation
import com.example.mymoneynotes.navigation.mainGraph
import com.example.mymoneynotes.ui.components.AddTransactionDialog
import com.example.mymoneynotes.ui.theme.MyMoneyNotesTheme
import com.example.mymoneynotes.viewmodel.TransactionViewModels

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyMoneyNotesTheme {
                val navController = rememberNavController()
                val viewModel: TransactionViewModels = viewModel()
                var showDialog by remember { mutableStateOf(false) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { MainBottomNavigation(navController) },
                    floatingActionButton = {
                        FloatingActionButton(onClick = { showDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Transaction"
                            )
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = BottomNavItem.Transactions.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        mainGraph(navController, viewModel)
                    }

                    if (showDialog) {
                        AddTransactionDialog(
                            viewModel = viewModel,
                            onDismiss = { showDialog = false }
                        )
                    }
                }
            }
        }
    }
}

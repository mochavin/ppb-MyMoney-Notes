package com.example.mymoneynotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels // Import for viewModels delegate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.* // Import SnackbarHostState if needed
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext // For getting context if needed
import androidx.compose.ui.res.stringResource
// import androidx.lifecycle.viewmodel.compose.viewModel // Remove if using Factory/Hilt
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.mymoneynotes.data.AppDatabase // Import Database
import com.example.mymoneynotes.data.TransactionRepository // Import Repository
import com.example.mymoneynotes.navigation.BottomNavItem
import com.example.mymoneynotes.navigation.MainBottomNavigation
import com.example.mymoneynotes.navigation.mainGraph
import com.example.mymoneynotes.ui.components.AddTransactionDialog
import com.example.mymoneynotes.ui.theme.MyMoneyNotesTheme
import com.example.mymoneynotes.viewmodel.TransactionViewModel // Import ViewModel
import com.example.mymoneynotes.viewmodel.TransactionViewModelFactory // Import Factory

// If using Hilt: @AndroidEntryPoint
// @AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // --- ViewModel Initialization (Choose ONE method) ---

    // Method 1: Manual Factory (if NOT using Hilt)
    private val database by lazy { AppDatabase.getDatabase(this) }
    private val repository by lazy { TransactionRepository(database.transactionDao()) }
    private val viewModel: TransactionViewModel by viewModels {
        TransactionViewModelFactory(repository)
    }

    // Method 2: Hilt (if using Hilt - Requires @AndroidEntryPoint on Activity)
    // private val viewModel: TransactionViewModel by viewModels()

    // ----------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyMoneyNotesTheme {
                val navController = rememberNavController()
                // Use the class-level viewModel initialized above
                // val viewModel: TransactionViewModel = viewModel() // Remove compose viewModel() call if using factory/hilt

                var showDialog by remember { mutableStateOf(false) }
                val snackbarHostState = remember { SnackbarHostState() } // Optional: For feedback

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { MainBottomNavigation(navController) },
                    floatingActionButton = {
                        FloatingActionButton(onClick = { showDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(R.string.add_transaction_fab) // Use string resource
                            )
                        }
                    },
                    snackbarHost = { SnackbarHost(snackbarHostState) } // Optional
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = BottomNavItem.Transactions.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // Pass the single ViewModel instance to the graph
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
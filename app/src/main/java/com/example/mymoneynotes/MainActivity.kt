package com.example.mymoneynotes

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.mymoneynotes.data.AppDatabase
import com.example.mymoneynotes.data.Transaction // Import Transaction
import com.example.mymoneynotes.data.TransactionRepository
import com.example.mymoneynotes.navigation.BottomNavItem
import com.example.mymoneynotes.navigation.MainBottomNavigation
import com.example.mymoneynotes.navigation.mainGraph
import com.example.mymoneynotes.ui.theme.components.AddTransactionDialog
import com.example.mymoneynotes.ui.theme.MyMoneyNotesTheme
import com.example.mymoneynotes.ui.theme.components.EditTransactionDialog
import com.example.mymoneynotes.viewmodel.TransactionViewModel
import com.example.mymoneynotes.viewmodel.TransactionViewModelFactory

class MainActivity : ComponentActivity() {

    private val database by lazy { AppDatabase.getDatabase(this) }
    private val repository by lazy { TransactionRepository(database.transactionDao()) }
    private val viewModel: TransactionViewModel by viewModels {
        TransactionViewModelFactory(repository)
    }

    // Opt-in required for ExperimentalMaterial3Api used in dialogs/scaffold
    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.O) // Needed for LocalDate usage passed around
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyMoneyNotesTheme {
                val navController = rememberNavController()
                val snackbarHostState = remember { SnackbarHostState() }

                // State for dialogs
                var showAddDialog by remember { mutableStateOf(false) }
                var showEditDialog by remember { mutableStateOf(false) } // <-- State for Edit Dialog
                var transactionToEdit by remember { mutableStateOf<Transaction?>(null) } // <-- State for Transaction being edited
                var showDeleteConfirmDialog by remember { mutableStateOf(false) } // <-- State for Delete Confirmation
                var transactionToDelete by remember { mutableStateOf<Transaction?>(null) } // <-- State for Transaction to delete

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { MainBottomNavigation(navController) },
                    floatingActionButton = {
                        FloatingActionButton(onClick = { showAddDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(R.string.add_transaction_fab)
                            )
                        }
                    },
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = BottomNavItem.Transactions.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        mainGraph(
                            navController = navController,
                            viewModel = viewModel,
                            // Provide lambdas to handle requests from TransactionScreen
                            onEditRequest = { transaction ->  // <-- Handle Edit Request
                                transactionToEdit = transaction
                                showEditDialog = true
                            },
                            onDeleteRequest = { transaction -> // <-- Handle Delete Request
                                transactionToDelete = transaction
                                showDeleteConfirmDialog = true
                            }
                        )
                    }

                    // --- Dialogs ---

                    // Add Transaction Dialog
                    if (showAddDialog) {
                        AddTransactionDialog(
                            viewModel = viewModel,
                            onDismiss = { showAddDialog = false }
                        )
                    }

                    // Edit Transaction Dialog
                    transactionToEdit?.let { currentTransactionToEdit -> // Ensure non-null
                        if (showEditDialog) {
                            EditTransactionDialog(
                                viewModel = viewModel,
                                initialTransaction = currentTransactionToEdit,
                                onDismiss = {
                                    showEditDialog = false
                                    transactionToEdit = null // Clear state on dismiss
                                }
                            )
                        }
                    }


                    // Delete Confirmation Dialog
                    transactionToDelete?.let { currentTransactionToDelete -> // Ensure non-null
                        if (showDeleteConfirmDialog) {
                            AlertDialog(
                                onDismissRequest = {
                                    showDeleteConfirmDialog = false
                                    transactionToDelete = null // Clear state on dismiss
                                },
                                title = { Text(stringResource(R.string.confirm_deletion_title)) }, // Add string res
                                text = { Text(stringResource(R.string.confirm_deletion_message)) }, // Add string res
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            viewModel.deleteTransaction(currentTransactionToDelete) // Call delete
                                            showDeleteConfirmDialog = false
                                            transactionToDelete = null
                                            // Optional: Show Snackbar confirmation
                                            // scope.launch { snackbarHostState.showSnackbar("Transaction deleted") }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error) // Red button
                                    ) {
                                        Text(stringResource(R.string.delete)) // Add string res
                                    }
                                },
                                dismissButton = {
                                    Button(onClick = {
                                        showDeleteConfirmDialog = false
                                        transactionToDelete = null
                                    }) {
                                        Text(stringResource(R.string.cancel))
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
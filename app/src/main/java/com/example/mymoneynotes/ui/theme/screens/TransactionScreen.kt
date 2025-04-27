package com.example.mymoneynotes.ui.theme.screens // Keep this package or move to ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize // Use fillMaxSize for Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue // Import getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle // Import lifecycle-aware collector
import com.example.mymoneynotes.R
import com.example.mymoneynotes.ui.theme.components.TransactionItem
import com.example.mymoneynotes.viewmodel.TransactionViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionScreen(viewModel: TransactionViewModel) {
    // Collect state using lifecycle-aware collector
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            stringResource(R.string.transaction_history),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Handle Empty State
        if (transactions.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(), // Fill remaining space
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.no_transactions_yet),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) { // Add spacing between items
                items(transactions, key = { it.id }) { transaction -> // Use ID as key for better performance
                    TransactionItem(transaction)
                }
            }
        }
    }
}

// TransactionItem Composable is now moved to ui/components/TransactionItem.kt
// @Composable
// fun TransactionItem(transaction: Transaction) { ... }
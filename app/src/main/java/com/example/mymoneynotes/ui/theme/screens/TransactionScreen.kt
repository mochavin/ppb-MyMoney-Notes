package com.example.mymoneynotes.ui.theme.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mymoneynotes.R
import com.example.mymoneynotes.data.Transaction // Import Transaction
import com.example.mymoneynotes.ui.theme.components.TransactionItem
import com.example.mymoneynotes.viewmodel.TransactionViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionScreen(
    viewModel: TransactionViewModel,
    onEditClick: (Transaction) -> Unit,
    onDeleteClick: (Transaction) -> Unit
) {
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) { // Use fillMaxSize
        Text(
            stringResource(R.string.transaction_history),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (transactions.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
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
            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(transactions, key = { it.id }) { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        // Pass the transaction object in the callbacks
                        onEditClick = { onEditClick(transaction) },    // <-- Call the passed lambda
                        onDeleteClick = { onDeleteClick(transaction) } // <-- Call the passed lambda
                    )
                }
            }
        }
    }
}
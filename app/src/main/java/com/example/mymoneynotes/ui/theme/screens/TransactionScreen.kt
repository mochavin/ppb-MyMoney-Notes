package com.example.mymoneynotes.ui.theme.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mymoneynotes.data.Transaction
import com.example.mymoneynotes.data.TransactionType
import com.example.mymoneynotes.viewmodel.TransactionViewModels

@Composable
fun TransactionScreen(viewModel: TransactionViewModels) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Transaction History", style = MaterialTheme.typography.headlineMedium)

        LazyColumn {
            items(viewModel.transactions) { transaction ->
                TransactionItem(transaction)
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(transaction.category.name)
                Text(transaction.date)
            }
            Text(
                text = "${if (transaction.type == TransactionType.INCOME) "+" else "-"} $${transaction.amount}",
                color = if (transaction.type == TransactionType.INCOME) Color.Green else Color.Red
            )
        }
    }
}

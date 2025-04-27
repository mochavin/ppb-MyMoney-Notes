package com.example.mymoneynotes.ui.theme.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mymoneynotes.data.Transaction
import com.example.mymoneynotes.data.TransactionType
import java.text.NumberFormat // For currency formatting
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale // For currency locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionItem(transaction: Transaction) {
    // Remember formatter for efficiency
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale.getDefault()) }
    val dateFormatter = remember { DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT) } // Short date format

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp), // Add horizontal padding
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // Add subtle elevation
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.category.name, // Consider stringResource if translated
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = dateFormatter.format(transaction.date), // Format LocalDate
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant // Subtler color
                )
            }
            Text(
                // Format amount based on type and locale
                text = "${if (transaction.type == TransactionType.INCOME) "+" else "-"} ${currencyFormatter.format(transaction.amount)}",
                style = MaterialTheme.typography.bodyLarge,
                color = if (transaction.type == TransactionType.INCOME) Color(0xFF18A558) else MaterialTheme.colorScheme.error // Use theme error color or custom green
            )
        }
    }
}
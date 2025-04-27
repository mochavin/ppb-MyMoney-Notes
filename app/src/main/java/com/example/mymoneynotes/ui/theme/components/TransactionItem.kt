package com.example.mymoneynotes.ui.theme.components // Ensure correct package

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons // Import Icons
import androidx.compose.material.icons.filled.Delete // Import Delete Icon
import androidx.compose.material.icons.filled.Edit // Import Edit Icon
import androidx.compose.material3.* // Import IconButton, Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.mymoneynotes.R
import com.example.mymoneynotes.data.Transaction
import com.example.mymoneynotes.data.TransactionType
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionItem(
    transaction: Transaction,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale.getDefault()) }
    val dateFormatter = remember { DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth() // Ensure row takes full width
                .padding(horizontal = 16.dp, vertical = 8.dp), // Adjust padding
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween // Push items apart
        ) {
            // Info Column (Category, Date, Amount)
            Column(modifier = Modifier.weight(1f, fill = false)) { // Don't let it expand infinitely
                Text(
                    text = transaction.category.name,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = dateFormatter.format(transaction.date),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${if (transaction.type == TransactionType.INCOME) "+" else "-"} ${currencyFormatter.format(transaction.amount)}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (transaction.type == TransactionType.INCOME) Color(0xFF18A558) else MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 4.dp) // Add some space
                )
            }

            // Action Buttons Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.edit_transaction) // Add string res
                    )
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete_transaction), // Add string res
                        tint = MaterialTheme.colorScheme.error // Make delete icon red
                    )
                }
            }
        }
    }
}
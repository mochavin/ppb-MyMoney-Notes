package com.example.mymoneynotes.ui.theme.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier // Make modifier parameter optional with default
) {
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale.getDefault()) }
    val dateFormatter = remember { DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT) }

    // Define consistent colors for transaction types
    val amountColor = remember(transaction.type) {
        when (transaction.type) {
            TransactionType.INCOME -> Color(0xFF18A558) // Green for income
            TransactionType.EXPENSE -> Color(0xFFE53935) // Red for expense
        }
    }

    val amountPrefix = if (transaction.type == TransactionType.INCOME) "+" else "-"
    val formattedAmount = "$amountPrefix ${currencyFormatter.format(transaction.amount)}"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 8.dp)
            .clip(RoundedCornerShape(12.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left side: Transaction info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                // Category with overflow handling
                Text(
                    text = transaction.category.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Date with subtle styling
                Text(
                    text = dateFormatter.format(transaction.date),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )

                // Amount with proper color coding
                Text(
                    text = formattedAmount,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = amountColor,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }

            // Right side: Action buttons with improved touch targets
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Edit button with subtle background
                FilledTonalIconButton(
                    onClick = onEditClick,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.edit_transaction),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                // Delete button with error styling
                FilledTonalIconButton(
                    onClick = onDeleteClick,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete_transaction),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
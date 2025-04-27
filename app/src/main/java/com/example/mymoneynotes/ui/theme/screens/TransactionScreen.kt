package com.example.mymoneynotes.ui.theme.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mymoneynotes.R
import com.example.mymoneynotes.data.Transaction
import com.example.mymoneynotes.ui.theme.components.TransactionItem
import com.example.mymoneynotes.viewmodel.TransactionViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionScreen(
    viewModel: TransactionViewModel,
    onEditClick: (Transaction) -> Unit,
    onDeleteClick: (Transaction) -> Unit
) {
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val today = remember { LocalDate.now() }

    // Calculate total amount
    val totalAmount = remember(transactions) {
        transactions.sumOf { it.amount }
    }

    // Group transactions by date and sort dates descending (newest first)
    val groupedTransactions = remember(transactions) {
        transactions
            .groupBy { it.date }
            .toSortedMap(compareByDescending { it }) // Sort dates newest first
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Screen Title with improved styling
        Text(
            stringResource(R.string.transaction_history),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
        )

        // Summary Card (only if transactions exist)
        if (transactions.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Total",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            "Rp. ${String.format("%.2f", totalAmount)}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Text(
                        "${transactions.size} transactions",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        }

        // LazyColumn for transactions list
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp), // Add padding at the bottom
            verticalArrangement = Arrangement.spacedBy(8.dp) // Increased spacing between items/headers
        ) {
            if (groupedTransactions.isEmpty()) {
                // Empty State - centered in the list area
                item {
                    Box(
                        modifier = Modifier
                            .fillParentMaxSize() // Fill the LazyColumn viewport
                            .padding(32.dp), // Add padding all around
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            // Circle background for icon
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp),
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                )
                            }

                            Spacer(Modifier.height(24.dp))

                            Text(
                                text = stringResource(R.string.no_transactions_yet),
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            Spacer(Modifier.height(8.dp))

                            Text(
                                text = "Add transactions to track your expenses",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                // Iterate through grouped transactions
                groupedTransactions.forEach { (date, dateTransactions) ->
                    // Sticky Date Header
                    stickyHeader {
                        DateHeader(date = date, today = today)
                    }
                    // Items for the current date
                    items(dateTransactions, key = { it.id }) { transaction ->
                        TransactionItem(
                            transaction = transaction,
                            onEditClick = { onEditClick(transaction) },
                            onDeleteClick = { onDeleteClick(transaction) },
                            modifier = Modifier.padding(horizontal = 16.dp)  // Increased padding
                        )
                    }
                }
            }
        }
    }
}

// Enhanced Date Header with relative date info
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateHeader(date: LocalDate, today: LocalDate) {
    val dateFormatter = remember { DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM) }

    // Calculate relative day description
    val relativeDay = when {
        date.isEqual(today) -> "Today"
        date.isEqual(today.minusDays(1)) -> "Yesterday"
        date.isEqual(today.plusDays(1)) -> "Tomorrow"
        ChronoUnit.DAYS.between(date, today) <= 7 && date.isBefore(today) ->
            "${ChronoUnit.DAYS.between(date, today)} days ago"
        ChronoUnit.DAYS.between(today, date) <= 7 && date.isAfter(today) ->
            "In ${ChronoUnit.DAYS.between(today, date)} days"
        else -> null
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        tonalElevation = 3.dp,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = dateFormatter.format(date),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Show relative day if available
            relativeDay?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
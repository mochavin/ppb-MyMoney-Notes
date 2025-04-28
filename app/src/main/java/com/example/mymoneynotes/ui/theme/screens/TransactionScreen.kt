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
import com.example.mymoneynotes.data.TransactionType // Import TransactionType
import com.example.mymoneynotes.ui.theme.components.TransactionItem
import com.example.mymoneynotes.viewmodel.TransactionViewModel
import java.text.NumberFormat // Import NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale // Import Locale

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionScreen(
    viewModel: TransactionViewModel,
    onEditClick: (Transaction) -> Unit,
    onDeleteClick: (Transaction) -> Unit
) {
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val today = remember { LocalDate.now() }

    // Calculate total income and expense
    val totalIncome = remember(transactions) {
        transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    }
    val totalExpense = remember(transactions) {
        transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    }
    val netTotal = totalIncome - totalExpense

    // Currency Formatter for Rupiah
    val currencyFormatter = remember {
        NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
            maximumFractionDigits = 0
            minimumFractionDigits = 0
        }
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
                    // Use primary container for positive net, error container for negative
                    containerColor = if (netTotal >= 0) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
                    else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Net Total Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            stringResource(R.string.net_total_summary), // "Net Total"
                            style = MaterialTheme.typography.titleMedium, // Make it stand out
                            fontWeight = FontWeight.Bold,
                            color = if (netTotal >= 0) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            currencyFormatter.format(netTotal), // Format as Rupiah
                            style = MaterialTheme.typography.titleLarge, // Larger font for net total
                            fontWeight = FontWeight.Bold,
                            color = if (netTotal >= 0) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onErrorContainer
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp)) // Add spacing

                    // Income/Expense Breakdown Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.Start) {
                            Text(
                                stringResource(R.string.total_income_summary), // "Income"
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                currencyFormatter.format(totalIncome), // Format as Rupiah
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary // Use primary color for income
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                stringResource(R.string.total_expense_summary), // "Expense"
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                currencyFormatter.format(totalExpense), // Format as Rupiah
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.error // Use error color for expense
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "${transactions.size} ${stringResource(R.string.transactions_count_summary)}", // "X transactions"
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.align(Alignment.End) // Align count to the end
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
                                text = stringResource(R.string.add_transactions_prompt), // Updated prompt
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
                            modifier = Modifier.padding(horizontal = 16.dp)  // Keep horizontal padding for items
                        )
                    }
                    // Add a small spacer after each group's items for better visual separation
                    item { Spacer(Modifier.height(8.dp)) }
                }
            }
        }
    }
}

// Enhanced Date Header with relative date info
@Composable
fun DateHeader(date: LocalDate, today: LocalDate) {
    val dateFormatter = remember { DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM) }

    // Calculate relative day description
    val relativeDay = when {
        date.isEqual(today) -> stringResource(R.string.date_header_today)
        date.isEqual(today.minusDays(1)) -> stringResource(R.string.date_header_yesterday)
        date.isEqual(today.plusDays(1)) -> stringResource(R.string.date_header_tomorrow)
        // Add more relative descriptions if needed (e.g., "X days ago")
        // ChronoUnit.DAYS.between(date, today) <= 7 && date.isBefore(today) ->
        //    stringResource(R.string.date_header_days_ago, ChronoUnit.DAYS.between(date, today))
        else -> null
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        // Use a slightly different background for headers to make them distinct
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        tonalElevation = 1.dp, // Reduced elevation
        // shadowElevation = 0.5.dp // Reduced shadow
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp), // Adjusted padding
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = dateFormatter.format(date),
                style = MaterialTheme.typography.titleSmall, // Slightly smaller title
                fontWeight = FontWeight.Medium, // Medium weight
                color = MaterialTheme.colorScheme.onSurfaceVariant // Use variant color
            )

            // Show relative day if available
            relativeDay?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelMedium, // Use label style
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary // Keep primary color for emphasis
                )
            }
        }
    }
}
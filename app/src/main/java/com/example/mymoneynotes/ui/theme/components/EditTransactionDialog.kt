package com.example.mymoneynotes.ui.theme.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.mymoneynotes.R
import com.example.mymoneynotes.data.Category
import com.example.mymoneynotes.data.Transaction
import com.example.mymoneynotes.data.TransactionType
import com.example.mymoneynotes.viewmodel.TransactionViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTransactionDialog(
    viewModel: TransactionViewModel,
    initialTransaction: Transaction, // The transaction to edit
    onDismiss: () -> Unit
) {
    // Initialize state FROM the initialTransaction
    var selectedType by remember { mutableStateOf(initialTransaction.type) }
    var selectedCategory by remember { mutableStateOf(initialTransaction.category) }
    var amount by remember { mutableStateOf(initialTransaction.amount.toString()) } // Amount needs conversion
    var selectedDate by remember { mutableStateOf(initialTransaction.date) }

    var showCategoryDropdown by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )
    val dateFormatter = remember { DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM) }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        selectedDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                    }
                    showDatePicker = false
                }) { Text(stringResource(R.string.ok)) } // OK instead of Save
            },
            dismissButton = {
                Button(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.edit_transaction)) }, // Update Title
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // --- Transaction Type --- (Same as Add)
                Text(stringResource(R.string.transaction_type), style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TransactionType.entries.forEach { type ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { selectedType = type }
                        ) {
                            RadioButton(
                                selected = selectedType == type,
                                onClick = { selectedType = type }
                            )
                            Text(text = type.name)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))

                // --- Category Selection --- (Same as Add)
                OutlinedTextField(
                    value = selectedCategory.name,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text(stringResource(R.string.select_category)) },
                    trailingIcon = {
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = stringResource(R.string.open_category_dropdown),
                            Modifier.clickable { showCategoryDropdown = true }
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                DropdownMenu(
                    expanded = showCategoryDropdown,
                    onDismissRequest = { showCategoryDropdown = false }
                ) {
                    Category.entries.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = {
                                selectedCategory = category
                                showCategoryDropdown = false
                            }
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))

                // --- Date Selection --- (Same as Add)
                OutlinedTextField(
                    value = dateFormatter.format(selectedDate),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.select_date)) },
                    trailingIcon = {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = stringResource(R.string.select_date),
                            Modifier.clickable { showDatePicker = true }
                        )
                    },
                    modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true }
                )
                Spacer(Modifier.height(8.dp))

                // --- Amount Input --- (Same as Add)
                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        amount = it
                        errorMessage = null
                    },
                    label = { Text(stringResource(R.string.amount)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    isError = errorMessage != null,
                    prefix = { Text("Rp. ") }
                )
                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amountValue = amount.toDoubleOrNull()
                    if (amountValue != null && amountValue > 0) {
                        // Create UPDATED transaction object, keeping the ORIGINAL ID
                        val updatedTransaction = initialTransaction.copy(
                            type = selectedType,
                            category = selectedCategory,
                            amount = amountValue,
                            date = selectedDate
                            // id = initialTransaction.id // copy() preserves the ID
                        )
                        viewModel.updateTransaction(updatedTransaction) // Call UPDATE
                        onDismiss()
                    } else {
                    }
                }
            ) {
                Text(stringResource(R.string.update)) // Change button text
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
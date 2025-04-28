// FILE: app/src/main/java/com/example/mymoneynotes/ui/theme/components/AddTransactionDialog.kt
package com.example.mymoneynotes.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.* // Import Material 3 DatePicker
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.mymoneynotes.R // Import R class
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
fun AddTransactionDialog(
    viewModel: TransactionViewModel,
    onDismiss: () -> Unit
) {
    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) } // Default to expense
    var selectedCategory by remember { mutableStateOf(Category.FOOD) }
    var amount by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) } // Use LocalDate state
    var showCategoryDropdown by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) } // State for date picker dialog
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )

    // Date Formatter
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
                }) { Text(stringResource(R.string.save)) }
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
        title = { Text(stringResource(R.string.add_new_transaction)) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // --- Transaction Type ---
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
                            Text(text = type.name) // Consider using stringResource if you translate enum names
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))

                // --- Category Selection ---
                OutlinedTextField( // Use OutlinedTextField for dropdown appearance
                    value = selectedCategory.name, // Display selected category
                    onValueChange = { }, // Value changes via dropdown
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
                    // .clickable { showCategoryDropdown = true } // Make whole field clickable too
                )

                DropdownMenu(
                    expanded = showCategoryDropdown,
                    onDismissRequest = { showCategoryDropdown = false }
                    // Consider using ExposedDropdownMenuBox for better integration with OutlinedTextField
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

                // --- Date Selection ---
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
                    modifier = Modifier.fillMaxWidth()
                        .clickable { showDatePicker = true }
                )
                Spacer(Modifier.height(8.dp))

                // --- Amount Input ---
                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        // Allow only digits and a single decimal point
                        val filtered = it.filter { char -> char.isDigit() || char == '.' }
                        // Ensure only one decimal point
                        if (filtered.count { it == '.' } <= 1) {
                            amount = filtered
                        }
                        errorMessage = null // Clear error on change
                    },
                    label = { Text(stringResource(R.string.amount)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    isError = errorMessage != null,
                    placeholder = { Text("0.00") } // Placeholder instead of prefix
                    // prefix = { Text("Rp. ") } // Basic currency prefix - REMOVED
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
                        viewModel.addTransaction(
                            Transaction( // ID is generated by Room (default 0 is fine)
                                type = selectedType,
                                category = selectedCategory,
                                amount = amountValue,
                                date = selectedDate // Save selected LocalDate
                            )
                        )
                        onDismiss()
                    } else {
                        // Show error if amount is invalid
                        errorMessage = "Please enter a valid positive amount."
                    }
                }
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
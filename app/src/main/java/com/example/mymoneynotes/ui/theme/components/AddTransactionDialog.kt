package com.example.mymoneynotes.ui.theme.components 

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
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
fun AddTransactionDialog(
    viewModel: TransactionViewModel,
    onDismiss: () -> Unit
) {
    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var availableCategories by remember { mutableStateOf(Category.getCategoriesForType(selectedType)) }
    var selectedCategory by remember { mutableStateOf(Category.getDefaultCategory(selectedType)) }
    var amount by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showCategoryDropdown by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    // Update available categories and reset selection when type changes
    LaunchedEffect(selectedType) {
        availableCategories = Category.getCategoriesForType(selectedType)
        // Ensure the current selection is valid, otherwise reset to default
        if (selectedCategory !in availableCategories) {
            selectedCategory = Category.getDefaultCategory(selectedType)
        }
        // OR always reset to default when type changes:
        // selectedCategory = Category.getDefaultCategory(selectedType)
    }

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
                }) { Text(stringResource(R.string.ok)) } // Use OK for DatePicker confirm
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
        shape = RoundedCornerShape(16.dp), // Rounded corners for the dialog
        title = {
            Text(
                stringResource(R.string.add_new_transaction),
                textAlign = TextAlign.Center, // Center title
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp) // Increased spacing
            ) {
                // --- Transaction Type Selection ---
                Text(stringResource(R.string.transaction_type), style = MaterialTheme.typography.labelLarge)
                // Using Filter Chips for a toggle-like feel
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TransactionType.entries.forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = { Text(type.name) }, // Consider using stringResource if needed
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp), // Slightly rounded chips
                            border = BorderStroke(
                                1.dp,
                                if (selectedType == type) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                            ),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                // --- Category Selection (ExposedDropdownMenuBox) ---
                ExposedDropdownMenuBox(
                    expanded = showCategoryDropdown,
                    onExpandedChange = { showCategoryDropdown = !showCategoryDropdown },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedCategory.name, // Display selected category name
                        onValueChange = {}, // Not directly changeable
                        readOnly = true,
                        label = { Text(stringResource(R.string.select_category)) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCategoryDropdown)
                        },
                        modifier = Modifier
                            .menuAnchor() // Important for ExposedDropdownMenuBox
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = showCategoryDropdown,
                        onDismissRequest = { showCategoryDropdown = false }
                    ) {
                        availableCategories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    selectedCategory = category
                                    showCategoryDropdown = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }

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
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true }
                )

                // --- Amount Input ---
                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        val filtered = it.filter { char -> char.isDigit() || char == '.' }
                        if (filtered.count { it == '.' } <= 1) {
                            amount = filtered
                        }
                        errorMessage = null // Clear error on change
                    },
                    label = { Text(stringResource(R.string.amount)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    isError = errorMessage != null,
                    placeholder = { Text("0.00") },
                    singleLine = true
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
                            Transaction( // ID is generated by Room
                                type = selectedType,
                                category = selectedCategory, // Use the selected category
                                amount = amountValue,
                                date = selectedDate
                            )
                        )
                        onDismiss()
                    } else {
                        errorMessage = context.getString(R.string.error_invalid_amount)
                    }
                },
                // Optional: Add icon to save button
                // icon = { Icon(Icons.Default.Save, contentDescription = null) }
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { // Use TextButton for less emphasis
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
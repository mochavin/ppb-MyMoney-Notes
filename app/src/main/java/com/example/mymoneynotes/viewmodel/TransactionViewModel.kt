package com.example.mymoneynotes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.mymoneynotes.data.Category
import com.example.mymoneynotes.data.Transaction
import com.example.mymoneynotes.data.TransactionRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TransactionViewModel(private val repository: TransactionRepository) : ViewModel() {

    // Timeout duration for StateFlow sharing
    private val StopTimeoutMillis: Long = 5000

    val transactions: StateFlow<List<Transaction>> = repository.allTransactions
        .stateIn(
            scope = viewModelScope,
            // Use Lazily: starts when first collector appears, never stops
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    val expenseSummary: StateFlow<Map<Category, Double>> = repository.expenseSummary
        .stateIn(
            scope = viewModelScope,
            // Use Lazily: starts when first collector appears, never stops
            started = SharingStarted.Lazily,
            initialValue = emptyMap()
        )

    // Add state flow for income summary
    val incomeSummary: StateFlow<Map<Category, Double>> = repository.incomeSummary
        .stateIn(
            scope = viewModelScope,
            // Use Lazily: starts when first collector appears, never stops
            started = SharingStarted.Lazily,
            initialValue = emptyMap()
        )

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.insert(transaction)
        }
    }

    // Add Update function
    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.update(transaction)
        }
    }

    // Add Delete function (by object)
    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.delete(transaction)
        }
    }
}

// --- ViewModel Factory (No changes needed here) ---
class TransactionViewModelFactory(private val repository: TransactionRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST") // Keep the suppression as the check is done manually
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T { // <-- Correct signature
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            return TransactionViewModel(repository) as T
        }
        // Provide a more informative error message
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
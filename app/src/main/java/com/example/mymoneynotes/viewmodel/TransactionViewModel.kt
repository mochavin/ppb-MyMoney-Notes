package com.example.mymoneynotes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mymoneynotes.data.Category
import com.example.mymoneynotes.data.Transaction
import com.example.mymoneynotes.data.TransactionRepository
import kotlinx.coroutines.flow.Flow // Keep Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map // Import map operator
import kotlinx.coroutines.flow.stateIn // Import stateIn
import kotlinx.coroutines.launch

// If using Hilt: @HiltViewModel and inject repository
// @HiltViewModel
// class TransactionViewModel @Inject constructor(
class TransactionViewModel( // Remove @Inject if not using Hilt
    private val repository: TransactionRepository
) : ViewModel() {

    // Use stateIn to convert Flow to StateFlow - caches the last value
    val transactions: StateFlow<List<Transaction>> = repository.allTransactions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L), // Keep active 5s after last subscriber
            initialValue = emptyList() // Initial value while loading
        )

    // StateFlow for chart data
    val expenseSummary: StateFlow<Map<Category, Double>> = repository.expenseSummary
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyMap()
        )

    // Function to add a transaction - runs in CoroutineScope
    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.insert(transaction)
        }
    }

    // Optional: Add delete function
    fun deleteTransaction(transactionId: Long) {
        viewModelScope.launch {
            repository.delete(transactionId)
        }
    }

    // getCategorySummary is now handled by repository.expenseSummary Flow
    // fun getCategorySummary(): Map<Category, Double> { ... } // Remove old method
}

// --- ViewModel Factory (Needed if NOT using Hilt) ---
// This factory allows you to pass the repository to the ViewModel constructor.
class TransactionViewModelFactory(private val repository: TransactionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
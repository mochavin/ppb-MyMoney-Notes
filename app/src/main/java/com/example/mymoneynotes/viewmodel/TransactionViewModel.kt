package com.example.mymoneynotes.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.mymoneynotes.data.Category
import com.example.mymoneynotes.data.Transaction

class TransactionViewModels : ViewModel() {
    private val _transactions = mutableStateListOf<Transaction>()
    val transactions: List<Transaction> get() = _transactions

    fun addTransaction(transaction: Transaction) {
        _transactions.add(transaction)
    }

    fun getCategorySummary(): Map<Category, Double> {
        return _transactions.groupBy { it.category }
            .mapValues { (_, transactions) ->
                transactions.sumOf { it.amount }
            }
    }
}

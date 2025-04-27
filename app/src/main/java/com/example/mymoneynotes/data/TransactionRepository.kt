package com.example.mymoneynotes.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionRepository(private val transactionDao: TransactionDao) {

    // Expose Flow for observing all transactions
    val allTransactions: Flow<List<Transaction>> = transactionDao.getAllTransactions()

    // Expose Flow for observing expense summary
    val expenseSummary: Flow<Map<Category, Double>> = transactionDao.getExpenseSummaryList() // <-- This returns Flow<List<CategoryExpenseSummary>>
        .map { summaryList -> // <-- Flow's map operator transforms the list
            // Use associate to convert the List<DTO> into a Map<Category, Double>
            summaryList.associate { summaryDto ->
                summaryDto.category to summaryDto.total
            }
        }

    // Suspend function for inserting (called from ViewModel)
    suspend fun insert(transaction: Transaction) {
        transactionDao.insertTransaction(transaction)
    }

    // Optional: Add other methods like delete, update if needed
    suspend fun delete(transactionId: Long) {
        transactionDao.deleteTransactionById(transactionId)
    }
}
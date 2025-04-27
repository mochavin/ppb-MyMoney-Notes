package com.example.mymoneynotes.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionRepository(private val transactionDao: TransactionDao) {

    val allTransactions: Flow<List<Transaction>> = transactionDao.getAllTransactions()

    val expenseSummary: Flow<Map<Category, Double>> = transactionDao.getExpenseSummaryList()
        .map { summaryList ->
            summaryList.associate { summaryDto ->
                summaryDto.category to summaryDto.total
            }
        }

    suspend fun insert(transaction: Transaction) {
        transactionDao.insertTransaction(transaction)
    }

    // Add Update method
    suspend fun update(transaction: Transaction) {
        transactionDao.updateTransaction(transaction)
    }

    // Add Delete method (using the object)
    suspend fun delete(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction)
    }
}
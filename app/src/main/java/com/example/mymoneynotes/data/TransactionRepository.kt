package com.example.mymoneynotes.data

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionRepository(private val transactionDao: TransactionDao) {

    val allTransactions: Flow<List<Transaction>> = transactionDao.getAllTransactions()

    val expenseSummary: Flow<Map<Category, Double>> = transactionDao.getExpenseSummaryList()
        .map { summaryList ->
            Log.d("Repository", "Expense Summary List from DAO: $summaryList") // <-- Log input list
            val resultMap = summaryList.associate { summaryDto ->
                summaryDto.category to summaryDto.total
            }
            Log.d("Repository", "Mapped Expense Summary: $resultMap") // <-- Log resulting map
            resultMap
        }

    // Add income summary flow
    val incomeSummary: Flow<Map<Category, Double>> = transactionDao.getIncomeSummaryList()
        .map { summaryList ->
            Log.d("Repository", "Income Summary List from DAO: $summaryList") // <-- Log input list
            val resultMap = summaryList.associate { summaryDto ->
                summaryDto.category to summaryDto.total
            }
            Log.d("Repository", "Mapped Income Summary: $resultMap") // <-- Log resulting map
            resultMap
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
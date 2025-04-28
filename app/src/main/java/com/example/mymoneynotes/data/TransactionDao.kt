package com.example.mymoneynotes.data

import androidx.room.* // Import necessary annotations
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    // Add Update method
    @Update
    suspend fun updateTransaction(transaction: Transaction)

    // Add Delete method (by object)
    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Long): Transaction?

    @Query("SELECT category, SUM(amount) as total FROM transactions WHERE type = 'EXPENSE' GROUP BY category")
    fun getExpenseSummaryList(): Flow<List<CategoryExpenseSummary>>

    // Add query for income summary
    @Query("SELECT category, SUM(amount) as total FROM transactions WHERE type = 'INCOME' GROUP BY category")
    fun getIncomeSummaryList(): Flow<List<CategoryExpenseSummary>>

    // Keep delete by ID if you might need it elsewhere, but @Delete is often simpler
    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: Long)

    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()
}
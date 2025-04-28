package com.example.mymoneynotes.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.time.LocalDate

enum class TransactionType { INCOME, EXPENSE }

enum class Category {
    // Expenses
    FOOD, TRANSPORT, SHOPPING, BILLS, ENTERTAINMENT, HEALTH, EDUCATION, GIFTS,
    // Income
    SALARY, BUSINESS, INVESTMENT, FREELANCE,
    // Common
    OTHER;

    companion object {
        val expenseCategories = listOf(
            FOOD, TRANSPORT, SHOPPING, BILLS, ENTERTAINMENT, HEALTH, EDUCATION, GIFTS, OTHER
        )
        val incomeCategories = listOf(
            SALARY, BUSINESS, INVESTMENT, FREELANCE, OTHER
        )

        fun getCategoriesForType(type: TransactionType): List<Category> {
            return when (type) {
                TransactionType.INCOME -> incomeCategories
                TransactionType.EXPENSE -> expenseCategories
            }
        }

        // Get default category for a type
        fun getDefaultCategory(type: TransactionType): Category {
            return when (type) {
                TransactionType.INCOME -> SALARY // Default income category
                TransactionType.EXPENSE -> FOOD   // Default expense category
            }
        }
    }
}

@Entity(tableName = "transactions")
@TypeConverters(Converters::class)
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0, // Keep default for autoGenerate = true ID
    val type: TransactionType,
    val category: Category,
    val amount: Double,
    val date: LocalDate
)
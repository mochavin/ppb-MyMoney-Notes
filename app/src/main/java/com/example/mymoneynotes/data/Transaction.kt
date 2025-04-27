package com.example.mymoneynotes.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.time.LocalDate

enum class TransactionType { INCOME, EXPENSE }

enum class Category {
    FOOD, TRANSPORT, SHOPPING, SALARY, BILLS, ENTERTAINMENT, OTHER
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

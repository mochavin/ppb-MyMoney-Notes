package com.example.mymoneynotes.data

import java.util.UUID

enum class TransactionType { INCOME, EXPENSE }

enum class Category {
    FOOD, TRANSPORT, SHOPPING, SALARY, BILLS, OTHER
}

data class Transaction(
    val id: Int = UUID.randomUUID().hashCode(),
    val type: TransactionType,
    val category: Category,
    val amount: Double,
    val date: String // Format: "YYYY-MM-DD"
)
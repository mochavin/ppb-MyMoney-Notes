package com.example.mymoneynotes.data

data class CategoryExpenseSummary(
    val category: Category, // Room will use the TypeConverter to populate this
    val total: Double
)
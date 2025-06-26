package com.example.budgetvibes.Data

data class Expense(
    val expenseId: Int = 0,
    val category: String,
    val notes: String,
    val date: String,
    val amount: Double,
    val receiptPhoto: ByteArray? = null
)

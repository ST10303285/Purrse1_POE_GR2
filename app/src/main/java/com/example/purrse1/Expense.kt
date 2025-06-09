package com.example.purrse1

data class Expense(
    val userId: String = "",
    val id: String = "",
    val amount: Double = 0.0,
    val category: String = "",
    val date: String = "",
    val description: String = "",
    var receiptUrl: String? = null,
    val isIncome: Boolean = false
)

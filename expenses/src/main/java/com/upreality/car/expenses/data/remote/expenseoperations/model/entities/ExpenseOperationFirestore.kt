package com.upreality.car.expenses.data.remote.expenseoperations.model.entities

data class ExpenseOperationFirestore(
    val id: String,
    val expenseId: String,
    val type: ExpenseOperationFirestoreType,
    val timestamp: Long
)
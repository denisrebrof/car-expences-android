package com.upreality.car.expenses.data.remote.expenseoperations.model.entities

data class ExpenseRemoteOperation(
    val id: String,
    val expenseId: String,
    val type: ExpenseRemoteOperationType,
    val timestamp: Long
)
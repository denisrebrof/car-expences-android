package com.upreality.car.expenses.data.sync.expenseoperations.model.entities

data class ExpenseOperationRemote(
    val id: String,
    val expenseId: String,
    val type: ExpenseOperationRemoteType,
    val timestamp: Long
)
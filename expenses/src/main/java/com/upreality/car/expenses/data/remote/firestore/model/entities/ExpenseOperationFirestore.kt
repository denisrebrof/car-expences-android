package com.upreality.car.expenses.data.remote.firestore.model.entities

data class ExpenseOperationFirestore(
    val id: String,
    val type: ExpenseOperationFirestoreType,
    val timestamp: Long
)
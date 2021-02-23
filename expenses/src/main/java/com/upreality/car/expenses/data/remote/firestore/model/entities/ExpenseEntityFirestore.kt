package com.upreality.car.expenses.data.remote.firestore.model.entities

data class ExpenseEntityFirestore(
    val id: String,
    val date: Long,
    val cost: Float,
    val type: Int,
    val detailsId: String
)
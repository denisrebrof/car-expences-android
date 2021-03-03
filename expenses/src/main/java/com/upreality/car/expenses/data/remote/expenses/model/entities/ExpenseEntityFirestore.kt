package com.upreality.car.expenses.data.remote.expenses.model.entities

data class ExpenseEntityFirestore(
    val id: String,
    val date: Long,
    val cost: Float,
    val type: Int,
    val detailsId: String
)
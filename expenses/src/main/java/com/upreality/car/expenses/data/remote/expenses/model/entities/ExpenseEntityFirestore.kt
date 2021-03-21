package com.upreality.car.expenses.data.remote.expenses.model.entities

data class ExpenseEntityFirestore(
    val id: String = String(),
    val date: Long = 0L,
    val cost: Float = 0f,
    val type: Int = 0,
    val detailsId: String = String()
)
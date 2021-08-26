package com.upreality.car.expenses.data.backend.model

data class ExpensesBackendRequest(
    val paged: Boolean,
    val cursor: Long = 0L,
    val pageSize: Long = 0L,
    val filterIds: List<Long> = listOf()
)
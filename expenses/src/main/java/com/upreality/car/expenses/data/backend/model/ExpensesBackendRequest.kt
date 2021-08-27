package com.upreality.car.expenses.data.backend.model

data class ExpensesBackendRequest(
    val paged: Boolean,
    val cursor: Long = 0L,
    val pageSize: Long = 0L,
    val id: Long? = null,
    val types: List<Long>? = null,
    val startTime: Long? = null,
    val endTime: Long? = null,
)
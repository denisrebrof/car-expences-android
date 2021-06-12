package com.upreality.car.expenses.domain.model

sealed class ExpenseFilter {
    object All : ExpenseFilter()
    object Fines : ExpenseFilter()
    object Maintenance : ExpenseFilter()
    object Fuel : ExpenseFilter()
    data class Paged(val cursor: Long, val pageSize: Int) : ExpenseFilter()
}
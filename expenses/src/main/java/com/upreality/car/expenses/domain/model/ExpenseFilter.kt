package com.upreality.car.expenses.domain.model

import java.util.*

sealed class ExpenseFilter {
    object All : ExpenseFilter()
    object Fines : ExpenseFilter()
    object Maintenance : ExpenseFilter()
    object Fuel : ExpenseFilter()
    data class Paged(val cursor: Long, val pageSize: Int) : ExpenseFilter()
    data class Id(val id: Long) : ExpenseFilter()
    data class DateRange(val from: Date, val to: Date) : ExpenseFilter()
}
package com.upreality.car.expenses.data.remote.firestore.model.filters

sealed class ExpenseOperationFilter {
    object All: ExpenseOperationFilter()
    data class Id(val id: String): ExpenseOperationFilter()
    data class FromTime(val time: Long): ExpenseOperationFilter()
}
package com.upreality.car.expenses.data.sync.expenseoperations.model.filters

sealed class ExpenseRemoteOperationFilter {
    object All: ExpenseRemoteOperationFilter()
    data class Id(val id: String): ExpenseRemoteOperationFilter()
    data class FromTime(val time: Long): ExpenseRemoteOperationFilter()
}
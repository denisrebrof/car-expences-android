package com.upreality.car.expenses.data.remote.expenses.model.filters

sealed class ExpenseDetailsRemoteFilter {
    object All : ExpenseDetailsRemoteFilter()
    data class Id(val id : String) : ExpenseDetailsRemoteFilter()
}

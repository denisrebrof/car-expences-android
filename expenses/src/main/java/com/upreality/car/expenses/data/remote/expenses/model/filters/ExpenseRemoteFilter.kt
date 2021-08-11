package com.upreality.car.expenses.data.remote.expenses.model.filters

sealed class ExpenseRemoteFilter {
    object All : ExpenseRemoteFilter()
    data class Id(val id: String) : ExpenseRemoteFilter()
}

package com.upreality.car.expenses.data.remote.expenses.model.filters

sealed class ExpenseRemoteDetailsFilter {
    object All : ExpenseRemoteDetailsFilter()
    data class Id(val id : String) : ExpenseRemoteDetailsFilter()
}

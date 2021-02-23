package com.upreality.car.expenses.data.remote.firestore.model.filters

sealed class ExpenseRemoteFilter {
    object All : ExpenseRemoteFilter()
    object Updated : ExpenseRemoteFilter()
    object Created : ExpenseRemoteFilter()
    data class Id(val id : String) : ExpenseRemoteFilter()
}

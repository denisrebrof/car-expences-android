package com.upreality.car.expenses.data.remote.firestore.model.filters

sealed class ExpenseRemoteFilter {
    object All : ExpenseRemoteFilter()
    data class Id(val id : String) : ExpenseRemoteFilter()
}

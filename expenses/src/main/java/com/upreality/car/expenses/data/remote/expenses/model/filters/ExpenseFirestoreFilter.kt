package com.upreality.car.expenses.data.remote.expenses.model.filters

sealed class ExpenseFirestoreFilter {
    object All : ExpenseFirestoreFilter()
    data class Id(val id : String) : ExpenseFirestoreFilter()
}

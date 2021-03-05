package com.upreality.car.expenses.data.remote.expenses.model.filters

sealed class ExpenseDetailsFirestoreFilter {
    object All : ExpenseDetailsFirestoreFilter()
    data class Id(val id : String) : ExpenseDetailsFirestoreFilter()
}

package com.upreality.car.expenses.data.sync.remote.expenses.model.filters

import com.upreality.car.expenses.data.shared.model.ExpenseType

sealed class ExpenseDetailsRemoteFilter {
    object All : ExpenseDetailsRemoteFilter()
    data class Id(val id: String, val type: ExpenseType) : ExpenseDetailsRemoteFilter()
}

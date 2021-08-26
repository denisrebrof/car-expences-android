package com.upreality.car.expenses.data.sync.remote.expensestate.model

sealed class ExpenseRemoteStateFilter {
    object All : ExpenseRemoteStateFilter()
    data class Id(val id: String) : ExpenseRemoteStateFilter()
    data class FromTime(val time: Long) : ExpenseRemoteStateFilter()
    data class FromTimePersisted(val time: Long) : ExpenseRemoteStateFilter()
    data class ByRemoteId(val remoteId: String) : ExpenseRemoteStateFilter()
}

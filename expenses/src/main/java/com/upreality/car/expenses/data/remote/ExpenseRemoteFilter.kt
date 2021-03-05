package com.upreality.car.expenses.data.remote

import com.upreality.car.expenses.data.remote.expenseoperations.model.entities.ExpenseOperationFirestoreType

sealed class ExpenseRemoteFilter {
    object All : ExpenseRemoteFilter()
    data class FromTime(
        val time: Long,
        val type: ExpenseOperationFirestoreType
    ) : ExpenseRemoteFilter()
}

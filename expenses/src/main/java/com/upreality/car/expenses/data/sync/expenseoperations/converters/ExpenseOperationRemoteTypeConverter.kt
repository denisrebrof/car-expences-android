package com.upreality.car.expenses.data.sync.expenseoperations.converters

import com.upreality.car.expenses.data.sync.expenseoperations.model.entities.ExpenseOperationRemoteType

class ExpenseOperationRemoteTypeConverter {
    fun toId(type: ExpenseOperationRemoteType) = when (type) {
        ExpenseOperationRemoteType.Created -> 0
        ExpenseOperationRemoteType.Updated -> 1
        ExpenseOperationRemoteType.Deleted -> 2
    }

    fun fromId(id: Int) = when (id) {
        0 -> ExpenseOperationRemoteType.Created
        2 -> ExpenseOperationRemoteType.Deleted
        else -> ExpenseOperationRemoteType.Updated
    }
}
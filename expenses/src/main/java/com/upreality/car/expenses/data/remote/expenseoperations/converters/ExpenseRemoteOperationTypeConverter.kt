package com.upreality.car.expenses.data.remote.expenseoperations.converters

import com.upreality.car.expenses.data.remote.expenseoperations.model.entities.ExpenseRemoteOperationType

class ExpenseRemoteOperationTypeConverter {
    fun toId(type: ExpenseRemoteOperationType) = when (type) {
        ExpenseRemoteOperationType.Created -> 0
        ExpenseRemoteOperationType.Updated -> 1
        ExpenseRemoteOperationType.Deleted -> 2
    }

    fun fromId(id: Int) = when (id) {
        0 -> ExpenseRemoteOperationType.Created
        2 -> ExpenseRemoteOperationType.Deleted
        else -> ExpenseRemoteOperationType.Updated
    }
}
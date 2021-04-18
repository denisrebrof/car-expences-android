package com.upreality.car.expenses.data.local.expensesinfo.model.entities

enum class ExpenseInfoSyncState(val id: Int) {
    Created(0),
    Sent(1),
    Updated(2),
    Deleted(3),
    PendingSync(4)
}
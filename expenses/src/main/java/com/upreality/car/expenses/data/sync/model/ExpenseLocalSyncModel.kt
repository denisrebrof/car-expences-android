package com.upreality.car.expenses.data.sync.model

import com.upreality.car.expenses.data.local.expensesinfo.model.entities.ExpenseInfoSyncState
import com.upreality.car.expenses.domain.model.expence.Expense

data class ExpenseLocalSyncModel(
    val expense: Expense,
    val state: ExpenseInfoSyncState,
    val remoteId: String = String()
)



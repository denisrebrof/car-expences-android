package com.upreality.car.expenses.data.sync.model

import com.upreality.car.expenses.data.local.expensesinfo.model.entities.ExpenseInfoSyncState
import com.upreality.car.expenses.domain.model.expence.Expense

sealed class ExpenseLocalSyncModel {
    data class Update(
        val expense: Expense,
        val state: ExpenseInfoSyncState
    ) : ExpenseLocalSyncModel()

    object Empty : ExpenseLocalSyncModel()
}



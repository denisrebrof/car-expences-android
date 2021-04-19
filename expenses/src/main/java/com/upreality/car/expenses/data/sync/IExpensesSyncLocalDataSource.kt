package com.upreality.car.expenses.data.sync

import com.upreality.car.expenses.domain.model.expence.Expense
import io.reactivex.Completable

interface IExpensesSyncLocalDataSource {
    fun createOrUpdate(expense: Expense, remoteId: String): Completable
    fun delete(remoteId: String): Completable
}
package com.upreality.car.expenses.data.sync

import com.upreality.car.expenses.data.sync.model.ExpenseLocalSyncModel
import com.upreality.car.expenses.domain.model.expence.Expense
import io.reactivex.Completable
import io.reactivex.Flowable

interface IExpensesSyncLocalDataSource {
    fun getUpdates(): Flowable<List<ExpenseLocalSyncModel>>
    fun update(expense: Expense): Completable
    fun delete(expense: Expense): Completable
    fun create(expense: Expense): Completable
}
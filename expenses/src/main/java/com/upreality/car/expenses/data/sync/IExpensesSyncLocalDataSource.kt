package com.upreality.car.expenses.data.sync

import com.upreality.car.expenses.data.sync.model.ExpenseLocalSyncModel
import com.upreality.car.expenses.data.sync.model.ExpensesLocalSyncFilter
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe

interface IExpensesSyncLocalDataSource {
    fun get(filter: ExpensesLocalSyncFilter): Flowable<List<ExpenseLocalSyncModel>>
    fun update(expense: ExpenseLocalSyncModel): Completable
    fun delete(expense: ExpenseLocalSyncModel): Completable
    fun create(expense: ExpenseLocalSyncModel): Maybe<Long>
}
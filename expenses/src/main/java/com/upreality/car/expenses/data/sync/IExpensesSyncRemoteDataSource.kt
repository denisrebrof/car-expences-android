package com.upreality.car.expenses.data.sync

import com.upreality.car.expenses.data.sync.model.ExpenseRemoteSyncModel
import com.upreality.car.expenses.data.sync.model.ExpensesRemoteSyncFilter
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe

interface IExpensesSyncRemoteDataSource {
    fun get(filter: ExpensesRemoteSyncFilter): Flowable<List<ExpenseRemoteSyncModel>>
    fun update(expense: ExpenseRemoteSyncModel): Maybe<Long>
    fun delete(expense: ExpenseRemoteSyncModel): Maybe<Long>
    fun create(expense: ExpenseRemoteSyncModel): Maybe<String>
}
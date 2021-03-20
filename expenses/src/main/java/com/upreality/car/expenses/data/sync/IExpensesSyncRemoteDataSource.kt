package com.upreality.car.expenses.data.sync

import com.upreality.car.expenses.data.remote.expenses.model.ExpenseRemote
import com.upreality.car.expenses.data.sync.model.ExpenseRemoteSyncFilter
import com.upreality.car.expenses.data.sync.model.ExpenseRemoteSyncModel
import io.reactivex.Flowable
import io.reactivex.Maybe

interface IExpensesSyncRemoteDataSource {
    fun get(filter: ExpenseRemoteSyncFilter): Flowable<List<ExpenseRemoteSyncModel>>
    fun update(expense: ExpenseRemote): Maybe<Long>
    fun delete(expense: ExpenseRemote): Maybe<Long>
    fun create(expense: ExpenseRemote): Maybe<String>
}
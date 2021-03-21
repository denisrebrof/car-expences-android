package com.upreality.car.expenses.data.sync

import com.upreality.car.expenses.data.remote.expenses.model.ExpenseRemote
import com.upreality.car.expenses.data.sync.model.ExpenseRemoteSyncOperationModel
import io.reactivex.Flowable
import io.reactivex.Maybe

interface IExpensesSyncRemoteDataSource {
    fun getModified(fromTime: Long): Flowable<List<ExpenseRemoteSyncOperationModel>>
    fun update(expense: ExpenseRemote): Maybe<Long>
    fun delete(expense: ExpenseRemote): Maybe<Long>
    fun create(expense: ExpenseRemote): Maybe<Long>
}
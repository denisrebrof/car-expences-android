package com.upreality.car.expenses.data.sync

import com.upreality.car.expenses.data.remote.expenses.model.ExpenseRemote
import com.upreality.car.expenses.data.sync.model.ExpenseSyncRemoteModel
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe

interface IExpensesSyncRemoteDataSource {
    fun getModified(fromTime: Long): Flowable<List<ExpenseSyncRemoteModel>>
    fun update(expense: ExpenseRemote, localId: Long): Completable
    fun delete(localId: Long): Completable
    fun create(remoteExpense: ExpenseRemote, localId: Long): Completable
}
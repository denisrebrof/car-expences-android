package com.upreality.car.expenses.data.sync

import com.upreality.car.expenses.data.sync.model.ExpenseSyncRemoteModel
import io.reactivex.Flowable

interface IExpensesSyncRemoteDataSource {
    fun getModified(fromTime: Long): Flowable<List<ExpenseSyncRemoteModel>>
}
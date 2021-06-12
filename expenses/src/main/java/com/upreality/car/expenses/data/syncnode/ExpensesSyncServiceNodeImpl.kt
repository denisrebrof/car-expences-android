package com.upreality.car.expenses.data.syncnode

import com.upreality.car.expenses.domain.IExpensesSyncService
import io.reactivex.Completable

class ExpensesSyncServiceNodeImpl : IExpensesSyncService{
    override fun createSyncLoop(): Completable {
        //TODO: implement sync logic here
        return Completable.complete()
    }
}
package com.upreality.car.expenses.data.sync.datasources

import com.upreality.car.expenses.data.remote.ExpensesRemoteDataSource
import com.upreality.car.expenses.data.remote.expenseoperations.dao.ExpenseOperationRemoteDAO
import com.upreality.car.expenses.data.remote.expenses.model.ExpenseRemote
import com.upreality.car.expenses.data.sync.IExpensesSyncRemoteDataSource
import com.upreality.car.expenses.data.sync.model.ExpenseRemoteSyncFilter
import com.upreality.car.expenses.data.sync.model.ExpenseRemoteSyncModel
import io.reactivex.Flowable
import io.reactivex.Maybe
import javax.inject.Inject

class ExpensesSyncRemoteDataSourceImpl @Inject constructor(
    private val remoteDataSource: ExpensesRemoteDataSource,
    private val operationsDAO: ExpenseOperationRemoteDAO,
): IExpensesSyncRemoteDataSource {

    override fun get(filter: ExpenseRemoteSyncFilter): Flowable<List<ExpenseRemoteSyncModel>> {
        TODO("Not yet implemented")
    }

    override fun update(expense: ExpenseRemote): Maybe<Long> {
        TODO("Not yet implemented")
    }

    override fun delete(expense: ExpenseRemote): Maybe<Long> {
        TODO("Not yet implemented")
    }

    override fun create(expense: ExpenseRemote): Maybe<Long> {
        TODO("Not yet implemented")
    }
}
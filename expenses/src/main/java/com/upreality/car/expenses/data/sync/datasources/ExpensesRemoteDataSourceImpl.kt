package com.upreality.car.expenses.data.sync.datasources

import com.upreality.car.expenses.data.remote.ExpensesRemoteDataSource
import com.upreality.car.expenses.data.remote.expenseoperations.dao.ExpenseOperationFirestoreDAO
import com.upreality.car.expenses.data.remote.expenses.model.filters.ExpenseRemoteFilter
import com.upreality.car.expenses.data.sync.IExpensesSyncRemoteDataSource
import com.upreality.car.expenses.data.sync.model.ExpenseRemoteSyncModel
import com.upreality.car.expenses.data.sync.model.ExpensesRemoteSyncFilter
import io.reactivex.Flowable
import io.reactivex.Maybe
import javax.inject.Inject

class ExpensesRemoteDataSourceImpl @Inject constructor(
    private val expensesRemoteDataSource: ExpensesRemoteDataSource,
    private val expenseOperationRemoteDataSource: ExpenseOperationFirestoreDAO
) : IExpensesSyncRemoteDataSource {

    override fun get(filter: ExpensesRemoteSyncFilter): Flowable<List<ExpenseRemoteSyncModel>> {
        return when(filter){
            is ExpensesRemoteSyncFilter.Id -> expensesRemoteDataSource.get(ExpenseRemoteFilter.Id(filter.id)).flatMapMaybe {

            }
        }
    }

    override fun update(expense: ExpenseRemoteSyncModel): Maybe<Long> {
        TODO("Not yet implemented")
    }

    override fun delete(expense: ExpenseRemoteSyncModel): Maybe<Long> {
        TODO("Not yet implemented")
    }

    override fun create(expense: ExpenseRemoteSyncModel): Maybe<String> {
        TODO("Not yet implemented")
    }


}
package com.upreality.car.expenses.data.sync.datasources

import com.upreality.car.expenses.data.local.expenses.ExpensesLocalDataSource
import com.upreality.car.expenses.data.local.expensesinfo.ExpensesInfoLocalDataSource
import com.upreality.car.expenses.data.sync.IExpensesSyncLocalDataSource
import com.upreality.car.expenses.data.sync.model.ExpenseLocalSyncModel
import com.upreality.car.expenses.data.sync.model.ExpensesLocalSyncFilter
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import javax.inject.Inject

class ExpensesSyncLocalDataSourceImpl @Inject constructor(
    private val expensesLocalDataSource: ExpensesLocalDataSource,
    private val expensesInfoLocalDataSource: ExpensesInfoLocalDataSource
): IExpensesSyncLocalDataSource {

    override fun get(filter: ExpensesLocalSyncFilter): Flowable<List<ExpenseLocalSyncModel>> {
        TODO("Not yet implemented")
    }

    override fun update(expense: ExpenseLocalSyncModel): Completable {
        TODO("Not yet implemented")
    }

    override fun delete(expense: ExpenseLocalSyncModel): Completable {
        TODO("Not yet implemented")
    }

    override fun create(expense: ExpenseLocalSyncModel): Maybe<Long> {
        TODO("Not yet implemented")
    }
}
package com.upreality.car.expenses.data.local.expensesinfo

import androidx.sqlite.db.SimpleSQLiteQuery
import com.upreality.car.expenses.data.local.expensesinfo.dao.ExpenseInfoDAO
import com.upreality.car.expenses.data.local.expensesinfo.model.entities.ExpenseInfo
import com.upreality.car.expenses.data.local.expensesinfo.model.queries.IExpenseInfoFilter
import com.upreality.car.expenses.data.sync.schedulers.ILocalInfoSchedulerProvider
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import javax.inject.Inject

class ExpensesInfoLocalDataSource @Inject constructor(
    private val expenseInfoDAO: ExpenseInfoDAO,
    private val infoSchedulerProvider: ILocalInfoSchedulerProvider
) {
    fun create(info: ExpenseInfo): Maybe<Long> {
        return expenseInfoDAO.insert(info).subscribeOn(infoSchedulerProvider.get())
    }

    fun get(filter: IExpenseInfoFilter): Flowable<List<ExpenseInfo>> {
        val query = SimpleSQLiteQuery(filter.getFilterExpression())
        return expenseInfoDAO.load(query).subscribeOn(infoSchedulerProvider.get())
    }

    fun update(info: ExpenseInfo): Completable {
        return expenseInfoDAO.update(info).subscribeOn(infoSchedulerProvider.get())
    }

    fun delete(info: ExpenseInfo): Completable {
        return expenseInfoDAO.update(info).subscribeOn(infoSchedulerProvider.get())
    }
}
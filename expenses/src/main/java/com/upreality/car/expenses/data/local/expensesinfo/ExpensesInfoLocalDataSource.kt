package com.upreality.car.expenses.data.local.expensesinfo

import android.util.Log
import androidx.sqlite.db.SimpleSQLiteQuery
import com.upreality.car.expenses.data.local.expensesinfo.dao.ExpenseInfoDAO
import com.upreality.car.expenses.data.local.expensesinfo.model.entities.ExpenseInfo
import com.upreality.car.expenses.data.local.expensesinfo.model.queries.IExpenseInfoFilter
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import javax.inject.Inject

class ExpensesInfoLocalDataSource @Inject constructor(
    private val expenseInfoDAO: ExpenseInfoDAO
) {
    fun create(info: ExpenseInfo): Maybe<Long> {
        return expenseInfoDAO.insert(info)
    }

    fun get(filter: IExpenseInfoFilter): Flowable<List<ExpenseInfo>> {
        val query = SimpleSQLiteQuery(filter.getFilterExpression())
        return expenseInfoDAO.load(query).doOnNext {
            Log.d("SYNC","Sync on ExpensesInfoLocalDataSource")
        }
    }

    fun update(info: ExpenseInfo): Completable {
        return expenseInfoDAO.update(info)
    }

    fun delete(info: ExpenseInfo): Completable {
        return expenseInfoDAO.update(info)
    }
}
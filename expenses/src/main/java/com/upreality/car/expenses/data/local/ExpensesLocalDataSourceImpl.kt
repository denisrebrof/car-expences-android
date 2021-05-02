package com.upreality.car.expenses.data.local

import com.upreality.car.common.data.database.IDatabaseFilter
import com.upreality.car.expenses.data.local.expenses.ExpensesLocalDAO
import com.upreality.car.expenses.data.local.expenses.converters.RoomExpenseConverter
import com.upreality.car.expenses.data.repository.IExpensesLocalDataSource
import com.upreality.car.expenses.domain.model.expence.Expense
import io.reactivex.Flowable
import javax.inject.Inject

class ExpensesLocalDataSourceImpl @Inject constructor(
    private val expensesLocalDAO: ExpensesLocalDAO
) : IExpensesLocalDataSource {

    override fun get(filter: IDatabaseFilter): Flowable<List<Expense>> {
        return expensesLocalDAO.get(filter).map {
            it.map(RoomExpenseConverter::toExpense)
        }
    }
}
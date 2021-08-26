package com.upreality.car.expenses.data.sync.room

import data.database.IDatabaseFilter
import com.upreality.car.expenses.data.sync.room.expenses.ExpensesLocalDAO
import com.upreality.car.expenses.data.sync.room.expenses.converters.RoomExpenseConverter
import com.upreality.car.expenses.data.sync.repository.IExpensesSyncLocalDataSource
import com.upreality.car.expenses.domain.model.expence.Expense
import io.reactivex.Flowable
import javax.inject.Inject

class ExpensesLocalDataSourceImpl @Inject constructor(
    private val expensesLocalDAO: ExpensesLocalDAO
) : IExpensesSyncLocalDataSource {

    override fun get(filter: IDatabaseFilter): Flowable<List<Expense>> {
        return expensesLocalDAO.get(filter).map {
            it.map(RoomExpenseConverter::toExpense)
        }
    }
}
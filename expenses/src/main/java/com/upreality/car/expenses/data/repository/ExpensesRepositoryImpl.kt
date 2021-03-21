package com.upreality.car.expenses.data.repository

import com.upreality.car.common.data.time.TimeDataSource
import com.upreality.car.expenses.data.local.expenses.ExpensesLocalDataSource
import com.upreality.car.expenses.data.local.expenses.converters.RoomExpenseConverter
import com.upreality.car.expenses.data.local.expenses.converters.RoomExpenseFilterConverter
import com.upreality.car.expenses.data.local.expensesinfo.ExpensesInfoLocalDataSource
import com.upreality.car.expenses.data.local.expensesinfo.model.entities.ExpenseInfo
import com.upreality.car.expenses.data.local.expensesinfo.model.entities.ExpenseInfoSyncState
import com.upreality.car.expenses.data.local.expensesinfo.model.queries.ExpenseInfoLocalIdFilter
import com.upreality.car.expenses.domain.IExpensesRepository
import com.upreality.car.expenses.domain.model.ExpenseFilter
import com.upreality.car.expenses.domain.model.expence.Expense
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import javax.inject.Inject

class ExpensesRepositoryImpl @Inject constructor(
    private val expensesLocalDataSource: IExpensesLocalDataSource
) : IExpensesRepository {

    companion object {
        const val NEW_INSTANCE_ID = 0L
    }

    override fun create(expense: Expense): Maybe<Long> {
        expense.id = NEW_INSTANCE_ID
        val localModel = RoomExpenseConverter.fromExpense(expense)
        return expensesLocalDataSource.create(localModel)
    }

    override fun get(filter: ExpenseFilter): Flowable<List<Expense>> {
        val localFilter = RoomExpenseFilterConverter.convert(filter)
        return expensesLocalDataSource.get(localFilter).map { localExpenses ->
            localExpenses.map(RoomExpenseConverter::toExpense)
        }
    }

    override fun update(expense: Expense): Completable {
        val localModel = RoomExpenseConverter.fromExpense(expense)
        return expensesLocalDataSource.update(localModel)
    }

    override fun delete(expense: Expense): Completable {
        val localModel = RoomExpenseConverter.fromExpense(expense)
        return expensesLocalDataSource.delete(localModel)
    }
}
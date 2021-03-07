package com.upreality.car.expenses.data.repository

import com.upreality.car.expenses.data.local.ExpensesLocalDataSource
import com.upreality.car.expenses.data.local.expenses.converters.ExpenseLocalConverter
import com.upreality.car.expenses.data.local.expenses.converters.ExpenseLocalFilterConverter
import com.upreality.car.expenses.domain.IExpensesRepository
import com.upreality.car.expenses.domain.model.ExpenseFilter
import com.upreality.car.expenses.domain.model.expence.Expense
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import javax.inject.Inject

class ExpensesRepositoryImpl @Inject constructor(
    private val expensesLocalDataSource: ExpensesLocalDataSource,
) : IExpensesRepository {

    companion object {
        const val NEW_INSTANCE_ID = 0L
    }

    override fun create(expense: Expense): Maybe<Long> {
        expense.id = NEW_INSTANCE_ID
        val localModel = ExpenseLocalConverter.fromExpense(expense)
        return expensesLocalDataSource.create(localModel)
    }

    override fun get(filter: ExpenseFilter): Flowable<List<Expense>> {
        val localFilter = ExpenseLocalFilterConverter.convert(filter)
        return expensesLocalDataSource.get(localFilter).map { list ->
            list.map(ExpenseLocalConverter::toExpense)
        }
    }

    override fun update(expense: Expense): Completable {
        val localModel = ExpenseLocalConverter.fromExpense(expense)
        return expensesLocalDataSource.update(localModel)
    }

    override fun delete(expense: Expense): Completable {
        val localModel = ExpenseLocalConverter.fromExpense(expense)
        return expensesLocalDataSource.delete(localModel)
    }
}
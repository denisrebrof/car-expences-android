package com.upreality.car.expenses.data

import com.upreality.car.expenses.data.local.expenses.ExpensesLocalDataSource
import com.upreality.car.expenses.domain.IExpensesRepository
import com.upreality.car.expenses.domain.model.ExpenseFilter
import com.upreality.car.expenses.domain.model.expence.Expense
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import javax.inject.Inject

class ExpensesRepositoryImpl @Inject constructor(
    private val expensesLocalDataSource: ExpensesLocalDataSource
) : IExpensesRepository {

    companion object {
        const val NEW_INSTANCE_ID = 0L
    }

    override fun create(expense: Expense): Maybe<Long> {
        expense.id = NEW_INSTANCE_ID
        return expensesLocalDataSource.create(expense)
    }

    override fun get(filter: ExpenseFilter): Flowable<List<Expense>> {
        return expensesLocalDataSource.get(filter)
    }

    override fun update(expense: Expense) : Completable {
        return expensesLocalDataSource.update(expense)
    }

    override fun delete(expense: Expense) : Completable  {
        return expensesLocalDataSource.delete(expense)
    }
}
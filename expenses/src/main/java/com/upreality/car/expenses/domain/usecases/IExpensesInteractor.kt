package com.upreality.car.expenses.domain.usecases

import com.upreality.car.expenses.domain.model.ExpenseFilter
import com.upreality.car.expenses.domain.model.expence.Expense
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe

interface IExpensesInteractor {
    fun createExpense(expense: Expense): Maybe<Long>
    fun getExpensesFlow(filter: ExpenseFilter): Flowable<List<Expense>>
    fun deleteExpense(expense: Expense): Completable
    fun updateExpense(expense: Expense): Completable
}
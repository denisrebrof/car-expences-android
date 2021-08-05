package com.upreality.car.expenses.domain.usecases

import com.upreality.car.expenses.domain.model.ExpenseFilter
import com.upreality.car.expenses.domain.model.expence.Expense
import domain.RequestPagingState
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe

interface IExpensesInteractor {
    fun createExpense(expense: Expense): Completable
    fun getExpensesFlow(filter: ExpenseFilter, pagingState: RequestPagingState = RequestPagingState.Undefined): Flowable<List<Expense>>
    fun getExpensesFlow(filters: List<ExpenseFilter>, pagingState: RequestPagingState = RequestPagingState.Undefined): Flowable<List<Expense>>
    fun deleteExpense(expense: Expense): Completable
    fun updateExpense(expense: Expense): Completable

    fun getExpenseMaybe(id: Long): Maybe<Expense>
    fun deleteExpense(id: Long): Completable
}
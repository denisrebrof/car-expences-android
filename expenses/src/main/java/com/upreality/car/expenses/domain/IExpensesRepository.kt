package com.upreality.car.expenses.domain

import com.upreality.car.expenses.domain.model.ExpenseFilter
import com.upreality.car.expenses.domain.model.expence.Expense
import domain.RequestPagingState
import io.reactivex.Completable
import io.reactivex.Flowable

interface IExpensesRepository {
    fun create(expense: Expense): Completable
    fun get(
        filters: List<ExpenseFilter>,
        pagingState: RequestPagingState = RequestPagingState.Undefined
    ): Flowable<List<Expense>>
    fun update(expense: Expense): Completable
    fun delete(expense: Expense): Completable
}
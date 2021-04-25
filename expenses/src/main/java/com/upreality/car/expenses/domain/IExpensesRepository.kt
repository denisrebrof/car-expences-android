package com.upreality.car.expenses.domain

import com.upreality.car.expenses.domain.model.ExpenseFilter
import com.upreality.car.expenses.domain.model.expence.Expense
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe

interface IExpensesRepository {
    fun create(expense: Expense): Completable
    fun get(filter: ExpenseFilter): Flowable<List<Expense>>
    fun update(expense: Expense): Completable
    fun delete(expense: Expense): Completable
}
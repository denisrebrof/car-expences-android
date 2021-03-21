package com.upreality.car.expenses.data.repository

import com.upreality.car.common.data.database.IDatabaseFilter
import com.upreality.car.expenses.data.local.expenses.model.ExpenseRoom
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe

interface IExpensesLocalDataSource {
    fun delete(expense: ExpenseRoom): Completable
    fun update(expense: ExpenseRoom): Completable
    fun get(filter: IDatabaseFilter): Flowable<List<ExpenseRoom>>
    fun create(expense: ExpenseRoom): Maybe<Long>
}
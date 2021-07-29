package com.upreality.car.expenses.data.repository

import com.upreality.car.expenses.domain.model.expence.Expense
import data.database.IDatabaseFilter
import io.reactivex.Flowable

interface IExpensesLocalDataSource {
    fun get(filter: IDatabaseFilter): Flowable<List<Expense>>
}
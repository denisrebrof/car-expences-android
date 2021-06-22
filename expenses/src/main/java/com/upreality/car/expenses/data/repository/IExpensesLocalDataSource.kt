package com.upreality.car.expenses.data.repository

import com.upreality.car.common.data.database.IDatabaseFilter
import com.upreality.car.expenses.domain.model.expence.Expense
import io.reactivex.Flowable

interface IExpensesLocalDataSource {
    fun get(filter: IDatabaseFilter): Flowable<List<Expense>>
}
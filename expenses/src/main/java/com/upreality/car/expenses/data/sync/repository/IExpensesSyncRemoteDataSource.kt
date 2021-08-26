package com.upreality.car.expenses.data.sync.repository

import com.upreality.car.expenses.domain.model.expence.Expense
import io.reactivex.Completable

interface IExpensesSyncRemoteDataSource {
    fun delete(expense: Expense): Completable
    fun update(expense: Expense): Completable
    fun create(expense: Expense): Completable
}
package com.upreality.car.expenses.data.remote

import com.upreality.car.expenses.data.remote.firestore.model.entities.ExpenseEntityFirestore
import com.upreality.car.expenses.domain.model.ExpenseFilter
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe

interface IExpensesRemoteDAO {
    fun delete(expense: ExpenseEntityFirestore): Completable
    fun update(expense: ExpenseEntityFirestore): Completable
    fun get(filter: ExpenseFilter): Flowable<List<ExpenseEntityFirestore>>
    fun create(expense: ExpenseEntityFirestore): Maybe<String>
}
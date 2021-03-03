package com.upreality.car.expenses.data.remote.expenses

import com.google.firebase.database.*
import com.upreality.car.expenses.data.remote.expenses.converters.RemoteExpenseEntityConverter
import com.upreality.car.expenses.data.remote.expenses.converters.RemoteExpenseEntityConverter.toExpenseDetails
import com.upreality.car.expenses.data.remote.expenses.dao.ExpenseDetailsFirestoreDAO
import com.upreality.car.expenses.data.remote.expenses.dao.ExpensesFirestoreDAO
import com.upreality.car.expenses.data.remote.expenses.model.ExpenseFirestore
import com.upreality.car.expenses.data.remote.expenses.model.entities.ExpenseDetailsFirestore
import com.upreality.car.expenses.data.remote.expenses.model.entities.ExpenseEntityFirestore
import com.upreality.car.expenses.data.remote.expenses.model.filters.ExpenseDetailsRemoteFilter
import com.upreality.car.expenses.data.remote.expenses.model.filters.ExpenseRemoteFilter
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import javax.inject.Inject

class ExpensesFirestoreDataSource @Inject constructor(
    private val expensesDAO: ExpensesFirestoreDAO,
    private val expenseDetailsDAO: ExpenseDetailsFirestoreDAO
) {

    fun delete(expense: ExpenseFirestore): Completable {
        return getRemoteInstance(expense.id).flatMapCompletable { remoteExpense ->
            val deleteExpense = expensesDAO.delete(remoteExpense)
            val details = toExpenseDetails(expense, remoteExpense.detailsId)
            val deleteDetails = expenseDetailsDAO.delete(details)
            deleteExpense.andThen(deleteDetails)
        }
    }

    fun update(expense: ExpenseFirestore): Completable {
        return getRemoteInstance(expense.id).flatMapCompletable { remoteExpense ->
            val updateExpense = expensesDAO.update(remoteExpense)
            val details = toExpenseDetails(expense, remoteExpense.detailsId)
            val updateDetails = expenseDetailsDAO.update(details)
            updateExpense.andThen(updateDetails)
        }
    }

    private fun getRemoteInstance(expenseId: String): Maybe<ExpenseEntityFirestore> {
        val selector = ExpenseRemoteFilter.Id(expenseId)
        return expensesDAO
            .get(selector)
            .firstElement()
            .map(List<ExpenseEntityFirestore>::first)
    }

    fun get(filter: ExpenseRemoteFilter): Flowable<List<ExpenseFirestore>> {
        return expensesDAO.get(filter).flatMapSingle(this::convertToFirestoreExpenses)
    }

    private fun convertToFirestoreExpenses(entities: List<ExpenseEntityFirestore>): Single<List<ExpenseFirestore>> {
        return Flowable.fromIterable(entities).flatMapMaybe { remoteEntity ->
            val detailsSelector = ExpenseDetailsRemoteFilter.Id(remoteEntity.detailsId)
            val detailsMaybe = expenseDetailsDAO
                .get(detailsSelector)
                .firstElement()
                .map(List<ExpenseDetailsFirestore>::firstOrNull)

            val expenseMaybe = detailsMaybe.map { remoteDetails ->
                RemoteExpenseEntityConverter.toExpense(remoteEntity, remoteDetails)
            }
            expenseMaybe
        }.toList()
    }

    fun create(expense: ExpenseFirestore): Maybe<String> {
        val details = toExpenseDetails(expense, String())
        val createDetailsMaybe = expenseDetailsDAO.create(details)
        return createDetailsMaybe.flatMap { detailsId ->
            val expenseEntity = RemoteExpenseEntityConverter.toExpenseEntity(expense, detailsId)
            expensesDAO.create(expenseEntity)
        }
    }
}
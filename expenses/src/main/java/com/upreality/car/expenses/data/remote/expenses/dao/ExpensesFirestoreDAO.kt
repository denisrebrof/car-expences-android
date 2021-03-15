package com.upreality.car.expenses.data.remote.expenses.dao

import com.google.firebase.database.*
import com.upreality.car.expenses.data.remote.expenses.converters.RemoteExpenseEntityConverter
import com.upreality.car.expenses.data.remote.expenses.converters.RemoteExpenseEntityConverter.toExpenseDetails
import com.upreality.car.expenses.data.remote.expenses.model.ExpenseRemote
import com.upreality.car.expenses.data.remote.expenses.model.entities.ExpenseDetailsFirestore
import com.upreality.car.expenses.data.remote.expenses.model.entities.ExpenseEntityFirestore
import com.upreality.car.expenses.data.remote.expenses.model.filters.ExpenseDetailsRemoteFilter
import com.upreality.car.expenses.data.remote.expenses.model.filters.ExpenseRemoteFilter
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import javax.inject.Inject

class ExpensesFirestoreDAO @Inject constructor(
    private val expenseEntityDAO: ExpenseEntityFirestoreDAO,
    private val expenseDetailsDAO: ExpenseDetailsFirestoreDAO
) {

    fun delete(expense: ExpenseRemote): Completable {
        return getRemoteInstance(expense.id).flatMapCompletable { remoteExpense ->
            val deleteExpense = expenseEntityDAO.delete(remoteExpense)
            val details = toExpenseDetails(expense, remoteExpense.detailsId)
            val deleteDetails = expenseDetailsDAO.delete(details)
            deleteExpense.andThen(deleteDetails)
        }
    }

    fun update(expense: ExpenseRemote): Completable {
        return getRemoteInstance(expense.id).flatMapCompletable { remoteExpense ->
            val updateExpense = expenseEntityDAO.update(remoteExpense)
            val details = toExpenseDetails(expense, remoteExpense.detailsId)
            val updateDetails = expenseDetailsDAO.update(details)
            updateExpense.andThen(updateDetails)
        }
    }

    private fun getRemoteInstance(expenseId: String): Maybe<ExpenseEntityFirestore> {
        val selector = ExpenseRemoteFilter.Id(expenseId)
        return expenseEntityDAO
            .get(selector)
            .firstElement()
            .map(List<ExpenseEntityFirestore>::first)
    }

    fun get(filter: ExpenseRemoteFilter): Flowable<List<ExpenseRemote>> {
        return expenseEntityDAO.get(filter).flatMapSingle(this::convertToFirestoreExpenses)
    }

    private fun convertToFirestoreExpenses(entities: List<ExpenseEntityFirestore>): Single<List<ExpenseRemote>> {
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

    fun create(expense: ExpenseRemote): Maybe<String> {
        val details = toExpenseDetails(expense, String())
        val createDetailsMaybe = expenseDetailsDAO.create(details)
        return createDetailsMaybe.flatMap { detailsId ->
            val expenseEntity = RemoteExpenseEntityConverter.toExpenseEntity(expense, detailsId)
            expenseEntityDAO.create(expenseEntity)
        }
    }
}
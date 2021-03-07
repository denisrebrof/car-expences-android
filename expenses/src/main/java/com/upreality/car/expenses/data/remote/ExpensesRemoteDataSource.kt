package com.upreality.car.expenses.data.remote

import com.google.firebase.database.*
import com.upreality.car.expenses.data.remote.expenses.converters.RemoteExpenseEntityConverter
import com.upreality.car.expenses.data.remote.expenses.converters.RemoteExpenseEntityConverter.toExpenseDetails
import com.upreality.car.expenses.data.remote.expenses.dao.ExpenseRemoteDetailsDAO
import com.upreality.car.expenses.data.remote.expenses.dao.ExpenseRemoteEntityDAO
import com.upreality.car.expenses.data.remote.expenses.model.ExpenseRemote
import com.upreality.car.expenses.data.remote.expenses.model.entities.ExpenseRemoteDetailsEntity
import com.upreality.car.expenses.data.remote.expenses.model.entities.ExpenseRemoteEntity
import com.upreality.car.expenses.data.remote.expenses.model.filters.ExpenseRemoteDetailsFilter
import com.upreality.car.expenses.data.remote.expenses.model.filters.ExpenseRemoteFilter
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import javax.inject.Inject

open class ExpensesRemoteDataSource @Inject constructor(
    private val expenseEntityDAO: ExpenseRemoteEntityDAO,
    private val expenseDetailsDAO: ExpenseRemoteDetailsDAO
) {

    open fun create(expense: ExpenseRemote): Maybe<String> {
        val details = toExpenseDetails(expense, String())
        val createDetailsMaybe = expenseDetailsDAO.create(details)
        return createDetailsMaybe.flatMap { detailsId ->
            val expenseEntity = RemoteExpenseEntityConverter.toExpenseEntity(expense, detailsId)
            expenseEntityDAO.create(expenseEntity)
        }
    }

    open fun get(filter: ExpenseRemoteFilter): Flowable<List<ExpenseRemote>> {
        return expenseEntityDAO.get(filter).flatMapSingle(this::convertToFirestoreExpenses)
    }

    open fun update(expense: ExpenseRemote): Completable {
        return getRemoteInstance(expense.id).flatMapCompletable { remoteExpense ->
            val updateExpense = expenseEntityDAO.update(remoteExpense)
            val details = toExpenseDetails(expense, remoteExpense.detailsId)
            val updateDetails = expenseDetailsDAO.update(details)
            updateExpense.andThen(updateDetails)
        }
    }

    open fun delete(expense: ExpenseRemote): Completable {
        return getRemoteInstance(expense.id).flatMapCompletable { remoteExpense ->
            val deleteExpense = expenseEntityDAO.delete(remoteExpense)
            val details = toExpenseDetails(expense, remoteExpense.detailsId)
            val deleteDetails = expenseDetailsDAO.delete(details)
            deleteExpense.andThen(deleteDetails)
        }
    }

    private fun getRemoteInstance(expenseId: String): Maybe<ExpenseRemoteEntity> {
        val selector = ExpenseRemoteFilter.Id(expenseId)
        return expenseEntityDAO
            .get(selector)
            .firstElement()
            .map(List<ExpenseRemoteEntity>::first)
    }

    private fun convertToFirestoreExpenses(entities: List<ExpenseRemoteEntity>): Single<List<ExpenseRemote>> {
        return Flowable.fromIterable(entities).flatMapMaybe { remoteEntity ->
            val detailsSelector = ExpenseRemoteDetailsFilter.Id(remoteEntity.detailsId)
            val detailsMaybe = expenseDetailsDAO
                .get(detailsSelector)
                .firstElement()
                .map(List<ExpenseRemoteDetailsEntity>::firstOrNull)

            val expenseMaybe = detailsMaybe.map { remoteDetails ->
                RemoteExpenseEntityConverter.toExpense(remoteEntity, remoteDetails)
            }
            expenseMaybe
        }.toList()
    }
}
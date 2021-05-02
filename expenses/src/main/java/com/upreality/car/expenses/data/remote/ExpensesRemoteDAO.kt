package com.upreality.car.expenses.data.remote

import android.util.Log
import com.upreality.car.expenses.data.remote.expenses.converters.RemoteExpenseEntityConverter
import com.upreality.car.expenses.data.remote.expenses.converters.RemoteExpenseTypeConverter
import com.upreality.car.expenses.data.remote.expenses.dao.ExpenseDetailsRemoteDAO
import com.upreality.car.expenses.data.remote.expenses.dao.ExpenseEntityRemoteDAO
import com.upreality.car.expenses.data.remote.expenses.model.ExpenseRemote
import com.upreality.car.expenses.data.remote.expenses.model.entities.ExpenseDetailsRemote
import com.upreality.car.expenses.data.remote.expenses.model.entities.ExpenseEntityRemote
import com.upreality.car.expenses.data.remote.expenses.model.filters.ExpenseDetailsRemoteFilter
import com.upreality.car.expenses.data.remote.expenses.model.filters.ExpenseRemoteFilter
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import javax.inject.Inject

class ExpensesRemoteDAO @Inject constructor(
    private val expenseEntityDAO: ExpenseEntityRemoteDAO,
    private val expenseDetailsDAO: ExpenseDetailsRemoteDAO
) {

    fun delete(expense: ExpenseRemote): Completable {
        return getRemoteInstance(expense.id).flatMapCompletable { remoteExpense ->
            val deletedExpenseEntity = RemoteExpenseEntityConverter
                .toExpenseEntity(expense, remoteExpense.detailsId)
            val deleteExpense = expenseEntityDAO.delete(deletedExpenseEntity)
            val details = RemoteExpenseEntityConverter
                .toExpenseDetails(expense, remoteExpense.detailsId)
            val deleteDetails = expenseDetailsDAO.delete(details)
            deleteExpense.andThen(deleteDetails)
        }
    }

    fun update(expense: ExpenseRemote): Completable {
        return getRemoteInstance(expense.id).flatMapCompletable { remoteExpense ->
            val updatedExpenseEntity = RemoteExpenseEntityConverter
                .toExpenseEntity(expense, remoteExpense.detailsId)
            val updateExpense = expenseEntityDAO.update(updatedExpenseEntity)
            val details = RemoteExpenseEntityConverter
                .toExpenseDetails(expense, remoteExpense.detailsId)
            val updateDetails = expenseDetailsDAO.update(details)
            updateExpense.andThen(updateDetails)
        }
    }

    private fun getRemoteInstance(expenseId: String): Maybe<ExpenseEntityRemote> {
        val selector = ExpenseRemoteFilter.Id(expenseId)
        return expenseEntityDAO
            .get(selector)
            .firstElement()
            .map(List<ExpenseEntityRemote>::first)
    }

    fun get(filter: ExpenseRemoteFilter): Flowable<List<ExpenseRemote>> {
        return expenseEntityDAO.get(filter).flatMapSingle(this::convertToRemoteExpenses)
    }

    private fun convertToRemoteExpenses(entities: List<ExpenseEntityRemote>): Single<List<ExpenseRemote>> {
        return Flowable.fromIterable(entities).flatMapMaybe { remoteEntity ->
            val type = RemoteExpenseTypeConverter.toExpenseType(remoteEntity.type)
            val detailsSelector = ExpenseDetailsRemoteFilter.Id(remoteEntity.detailsId, type)
            val detailsMaybe = expenseDetailsDAO
                .get(detailsSelector)
                .firstElement()
                .map(List<ExpenseDetailsRemote>::firstOrNull)

            val expenseMaybe = detailsMaybe.map { remoteDetails ->
                RemoteExpenseEntityConverter.toExpenseRemote(remoteEntity, remoteDetails)
            }
            expenseMaybe
        }.toList()
    }

    fun create(expense: ExpenseRemote): Maybe<String> {
        val details = RemoteExpenseEntityConverter.toExpenseDetails(expense, String())
        val createDetailsMaybe = expenseDetailsDAO.create(details)
        return createDetailsMaybe.flatMap { detailsId ->
            val expenseEntity = RemoteExpenseEntityConverter.toExpenseEntity(expense, detailsId)
            expenseEntityDAO.create(expenseEntity)
        }
    }
}
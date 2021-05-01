package com.upreality.car.expenses.data.repository

import android.util.Log
import com.upreality.car.expenses.data.local.expensesinfo.ExpensesInfoLocalDataSource
import com.upreality.car.expenses.data.local.expensesinfo.model.entities.ExpenseInfo
import com.upreality.car.expenses.data.local.expensesinfo.model.queries.ExpenseInfoLocalIdFilter
import com.upreality.car.expenses.data.remote.ExpensesRemoteDAO
import com.upreality.car.expenses.data.remote.expenses.converters.RemoteExpenseConverter
import com.upreality.car.expenses.data.remote.expenses.model.ExpenseRemote
import com.upreality.car.expenses.data.remote.expenses.model.filters.ExpenseRemoteFilter
import com.upreality.car.expenses.data.remote.expensestate.dao.ExpenseStateRemoteDAO
import com.upreality.car.expenses.data.remote.expensestate.model.ExpenseRemoteState
import com.upreality.car.expenses.data.remote.expensestate.model.ExpenseRemoteStateFilter
import com.upreality.car.expenses.domain.model.expence.Expense
import io.reactivex.Completable
import io.reactivex.Maybe
import javax.inject.Inject

class ExpensesRemoteDataSourceImpl @Inject constructor(
    private val remoteDAO: ExpensesRemoteDAO,
    private val statesDAO: ExpenseStateRemoteDAO,
    private val expensesInfoLocalDataSource: ExpensesInfoLocalDataSource
) : IExpensesRemoteDataSource {

    override fun delete(expense: Expense): Completable {
        return getLocalInfo(expense)
            .map(ExpenseInfo::remoteId)
            .flatMap(this::getExpense)
            .flatMapCompletable { expenseRemote ->
                val updatedState = updateState(expenseRemote, true)
                remoteDAO.delete(expenseRemote).andThen(updatedState)
            }
    }

    override fun update(expense: Expense): Completable {
        return getLocalInfo(expense)
            .map(ExpenseInfo::remoteId)
            .map { RemoteExpenseConverter.fromExpense(expense, it) }
            .flatMapCompletable { expenseRemote ->
                val updateStateOperation = updateState(expenseRemote, false)
                remoteDAO.update(expenseRemote).andThen(updateStateOperation)
            }
    }

    override fun create(expense: Expense): Completable {
        val remoteExpense = RemoteExpenseConverter.fromExpense(expense)
        return remoteDAO.create(remoteExpense).flatMapCompletable(this::createState)
    }

    private fun getExpense(remoteId: String): Maybe<ExpenseRemote> {
        val filter = ExpenseRemoteFilter.Id(remoteId)
        return remoteDAO
            .get(filter)
            .firstElement()
            .map(List<ExpenseRemote>::firstOrNull)
    }

    private fun updateState(expense: ExpenseRemote, deleted: Boolean): Completable {
        val filter = ExpenseRemoteStateFilter.ByRemoteId(expense.id)
        return statesDAO.get(filter)
            .firstElement()
            .map(List<ExpenseRemoteState>::firstOrNull)
            .map { it.copy(deleted = deleted, timestamp = null) }
            .flatMapCompletable(statesDAO::update)
    }

    private fun getLocalInfo(expense: Expense): Maybe<ExpenseInfo> {
        val infoFilter = ExpenseInfoLocalIdFilter(expense.id)
        return expensesInfoLocalDataSource
            .get(infoFilter)
            .firstElement()
            .map(List<ExpenseInfo>::firstOrNull)
    }

    private fun createState(remoteId: String): Completable {
        val state = ExpenseRemoteState(remoteId = remoteId)
        return statesDAO.create(state)
            .map(ExpenseRemoteStateFilter::Id)
            .flatMap {
                statesDAO
                    .get(it)
                    .firstElement()
                    .map(List<ExpenseRemoteState>::firstOrNull)
                    .doOnError {
                        Log.d("Gotcha","")
                    }.doOnSuccess {
                        Log.d("Gotcha 2","")
                    }
            }.ignoreElement()
    }
}
package com.upreality.car.expenses.data.remote

import com.upreality.car.common.data.time.TimeDataSource
import com.upreality.car.expenses.data.remote.expenseoperations.dao.ExpenseOperationFirestoreDAO
import com.upreality.car.expenses.data.remote.expenseoperations.model.entities.ExpenseOperationFirestore
import com.upreality.car.expenses.data.remote.expenseoperations.model.entities.ExpenseOperationFirestoreType
import com.upreality.car.expenses.data.remote.expenses.converters.RemoteExpenseEntityConverter
import com.upreality.car.expenses.data.remote.expenses.dao.ExpensesFirestoreDAO
import com.upreality.car.expenses.data.remote.expenses.model.ExpenseRemote
import com.upreality.car.expenses.data.remote.expenses.model.entities.ExpenseEntityFirestore
import com.upreality.car.expenses.data.remote.expenses.model.filters.ExpenseRemoteFilter
import com.upreality.car.expenses.data.shared.model.DateConverter
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import javax.inject.Inject

class ExpensesRemoteDataSource @Inject constructor(
    private val timeDataSource: TimeDataSource,
    private val expenseEntityDAO: ExpensesFirestoreDAO,
    private val expenseOperationFirestoreDAO: ExpenseOperationFirestoreDAO
) {
    fun delete(expense: ExpenseRemote): Completable {
        return timeDataSource.getTime().map(DateConverter::toTimestamp).flatMapCompletable { time ->
            val deleteOperation = ExpenseOperationFirestore(
                "",
                expense.id,
                ExpenseOperationFirestoreType.Deleted,
                time
                )
            val addDeleteOperation = expenseOperationFirestoreDAO.create(deleteOperation)
            expensesFirestoreDAO.delete(expense).andThen(addDeleteOperation)
        }
    }

    fun update(expense: ExpenseRemote): Completable {
        return getRemoteInstance(expense.id).flatMapCompletable { remoteExpense ->
            val updateExpense = expenseEntityDAO.update(remoteExpense)
            val details =
                RemoteExpenseEntityConverter.toExpenseDetails(expense, remoteExpense.detailsId)
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
}
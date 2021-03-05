package com.upreality.car.expenses.data.remote

import com.upreality.car.common.data.time.TimeDataSource
import com.upreality.car.expenses.data.remote.expenseoperations.dao.ExpenseOperationFirestoreDAO
import com.upreality.car.expenses.data.remote.expenseoperations.model.entities.ExpenseOperationFirestore
import com.upreality.car.expenses.data.remote.expenseoperations.model.entities.ExpenseOperationFirestoreType
import com.upreality.car.expenses.data.remote.expenseoperations.model.entities.ExpenseOperationFirestoreType.*
import com.upreality.car.expenses.data.remote.expenseoperations.model.filters.ExpenseOperationFilter
import com.upreality.car.expenses.data.remote.expenses.dao.ExpensesFirestoreDAO
import com.upreality.car.expenses.data.remote.expenses.model.ExpenseFirestore
import com.upreality.car.expenses.data.remote.expenses.model.filters.ExpenseFirestoreFilter
import com.upreality.car.expenses.data.shared.model.DateConverter
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import javax.inject.Inject

class ExpensesRemoteDataSource @Inject constructor(
    private val timeDataSource: TimeDataSource,
    private val expensesFirestoreDAO: ExpensesFirestoreDAO,
    private val expenseOperationFirestoreDAO: ExpenseOperationFirestoreDAO
) {

    fun create(expense: ExpenseFirestore): Maybe<String> {
        val createExpense = expensesFirestoreDAO.create(expense)
        return createExpense.flatMap { expenseId ->
            val tempMaybe = Maybe.just(expenseId)
            saveOperation(expenseId, Created).andThen(tempMaybe)
        }
    }

    fun get(filter: ExpenseRemoteFilter): Flowable<List<ExpenseFirestore>> {
        return when (filter) {
            is ExpenseRemoteFilter.All -> expensesFirestoreDAO.get(ExpenseFirestoreFilter.All)
            is ExpenseRemoteFilter.FromTime -> getOperationsList(filter.time, filter.type)
        }
    }

    private fun getOperationsList(
        fromTime: Long,
        type: ExpenseOperationFirestoreType
    ): Flowable<List<ExpenseOperationFirestore>> {
        return expenseOperationFirestoreDAO
            .get(ExpenseOperationFilter.FromTime(fromTime))
            .map { list -> list
                .filter { it.type == type }
                .sortedByDescending { it.timestamp }
                .distinctBy { it.expenseId }
                .reversed()
            }
    }

    fun update(expense: ExpenseFirestore): Completable {
        val saveUpdateOperation = saveOperation(expense.id, Updated)
        return expensesFirestoreDAO.update(expense).andThen(saveUpdateOperation)
    }

    fun delete(expense: ExpenseFirestore): Completable {
        val saveDeleteOperation = saveOperation(expense.id, Deleted)
        return expensesFirestoreDAO.delete(expense).andThen(saveDeleteOperation)
    }

    private fun saveOperation(
        targetExpenseId: String,
        type: ExpenseOperationFirestoreType
    ): Completable {
        val currentTimeMaybe = timeDataSource.getTime().map(DateConverter::toTimestamp)
        return currentTimeMaybe.flatMapCompletable { time ->
            val updateOperation = ExpenseOperationFirestore("", targetExpenseId, type, time)
            expenseOperationFirestoreDAO.create(updateOperation).ignoreElement()
        }
    }
}
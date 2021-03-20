package com.upreality.car.expenses.data.sync.datasources

import com.upreality.car.expenses.data.remote.ExpensesRemoteDataSource
import com.upreality.car.expenses.data.remote.expenseoperations.dao.ExpenseOperationRemoteDAO
import com.upreality.car.expenses.data.remote.expenseoperations.model.entities.ExpenseRemoteOperation
import com.upreality.car.expenses.data.remote.expenseoperations.model.entities.ExpenseRemoteOperationType
import com.upreality.car.expenses.data.remote.expenseoperations.model.filters.ExpenseRemoteOperationFilter
import com.upreality.car.expenses.data.remote.expenses.model.ExpenseRemote
import com.upreality.car.expenses.data.remote.expenses.model.filters.ExpenseRemoteFilter
import com.upreality.car.expenses.data.shared.model.DateConverter
import com.upreality.car.expenses.data.sync.IExpensesSyncRemoteDataSource
import com.upreality.car.expenses.data.sync.model.ExpenseRemoteSyncOperationModel
import io.reactivex.Flowable
import io.reactivex.Maybe
import java.util.*
import javax.inject.Inject

class ExpensesSyncRemoteDataSourceImpl @Inject constructor(
    private val remoteDataSource: ExpensesRemoteDataSource,
    private val operationsDAO: ExpenseOperationRemoteDAO,
) : IExpensesSyncRemoteDataSource {

    private val dateConverter = DateConverter()

    override fun getModified(fromTime: Long): Flowable<List<ExpenseRemoteSyncOperationModel>> {
        val filter = ExpenseRemoteOperationFilter.FromTime(fromTime)
        return operationsDAO.get(filter).flatMapMaybe { operations ->
            Flowable.fromIterable(operations).flatMapMaybe { operation ->
                val timestamp = dateConverter.toTimestamp(operation.timestamp!!)
                when (operation.type) {
                    ExpenseRemoteOperationType.Created -> getExpenseFromOperation(operation).map {
                        ExpenseRemoteSyncOperationModel.Create(it, timestamp)
                    }
                    ExpenseRemoteOperationType.Updated -> getExpenseFromOperation(operation).map {
                        ExpenseRemoteSyncOperationModel.Update(it, timestamp)
                    }
                    ExpenseRemoteOperationType.Deleted -> Maybe.just(
                        ExpenseRemoteSyncOperationModel.Delete(operation.expenseId, timestamp)
                    )
                }
            }.toList().toMaybe()
        }
    }

    private fun getExpenseFromOperation(operation: ExpenseRemoteOperation): Maybe<ExpenseRemote> {
        val filter = ExpenseRemoteFilter.Id(operation.expenseId)
        return remoteDataSource
            .get(filter)
            .firstElement()
            .map(List<ExpenseRemote>::firstOrNull)
    }

    override fun update(expense: ExpenseRemote): Maybe<Long> {
        val addOperation = createOperation(expense.id, ExpenseRemoteOperationType.Updated)
        return remoteDataSource.update(expense).andThen(addOperation)
    }

    private fun createOperation(remoteId: String, type: ExpenseRemoteOperationType): Maybe<Long> {
        val operation = ExpenseRemoteOperation(String(), remoteId, type)
        return operationsDAO.create(operation)
            .map(ExpenseRemoteOperationFilter::Id)
            .flatMap {
                operationsDAO
                    .get(it)
                    .firstElement()
                    .map(List<ExpenseRemoteOperation>::firstOrNull)
            }.map(ExpenseRemoteOperation::timestamp)
            .map(dateConverter::toTimestamp)
    }

    override fun delete(expense: ExpenseRemote): Maybe<Long> {
        val addOperation = createOperation(expense.id, ExpenseRemoteOperationType.Deleted)
        return remoteDataSource.delete(expense).andThen(addOperation)
    }

    override fun create(expense: ExpenseRemote): Maybe<Long> {
        return remoteDataSource.create(expense).flatMap { id ->
            createOperation(id, ExpenseRemoteOperationType.Created)
        }
    }
}
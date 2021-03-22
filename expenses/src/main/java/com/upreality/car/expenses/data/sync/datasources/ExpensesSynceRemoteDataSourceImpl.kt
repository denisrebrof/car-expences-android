package com.upreality.car.expenses.data.sync.datasources

import com.upreality.car.expenses.data.local.expensesinfo.ExpensesInfoLocalDataSource
import com.upreality.car.expenses.data.local.expensesinfo.model.entities.ExpenseInfo
import com.upreality.car.expenses.data.local.expensesinfo.model.entities.ExpenseInfoSyncState
import com.upreality.car.expenses.data.local.expensesinfo.model.queries.ExpenseInfoIdFilter
import com.upreality.car.expenses.data.local.expensesinfo.model.queries.ExpenseInfoLocalIdFilter
import com.upreality.car.expenses.data.remote.ExpensesRemoteDataSource
import com.upreality.car.expenses.data.remote.expenseoperations.dao.ExpenseOperationRemoteDAO
import com.upreality.car.expenses.data.remote.expenseoperations.model.entities.ExpenseRemoteOperation
import com.upreality.car.expenses.data.remote.expenseoperations.model.entities.ExpenseRemoteOperationType
import com.upreality.car.expenses.data.remote.expenseoperations.model.filters.ExpenseRemoteOperationFilter
import com.upreality.car.expenses.data.remote.expenses.converters.RemoteExpenseConverter
import com.upreality.car.expenses.data.remote.expenses.model.ExpenseRemote
import com.upreality.car.expenses.data.remote.expenses.model.filters.ExpenseRemoteFilter
import com.upreality.car.expenses.data.shared.model.DateConverter
import com.upreality.car.expenses.data.sync.IExpensesSyncRemoteDataSource
import com.upreality.car.expenses.data.sync.model.ExpenseRemoteSyncOperationModel
import com.upreality.car.expenses.domain.model.expence.Expense
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import java.util.*
import javax.inject.Inject

class ExpensesSyncRemoteDataSourceImpl @Inject constructor(
    private val remoteDataSource: ExpensesRemoteDataSource,
    private val operationsDAO: ExpenseOperationRemoteDAO,
    private val expensesInfoLocalDataSource: ExpensesInfoLocalDataSource
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

    override fun create(remoteExpense: ExpenseRemote, localId: Long): Maybe<Long> {
        return remoteDataSource.create(remoteExpense)
            .flatMap { id ->
                val operationTimestampMaybe =
                    createOperation(id, ExpenseRemoteOperationType.Created)
                setRemoteId(localId, id).andThen(operationTimestampMaybe)
            }
    }

    private fun setRemoteId(localId: Long, remoteId: String): Completable {
        val filter = ExpenseInfoLocalIdFilter(localId)
        return expensesInfoLocalDataSource.get(filter)
            .map(List<ExpenseInfo>::firstOrNull)
            .firstElement()
            .map { it.copy(remoteId = remoteId, state = ExpenseInfoSyncState.Persists) }
            .flatMapCompletable(expensesInfoLocalDataSource::update)
    }
}
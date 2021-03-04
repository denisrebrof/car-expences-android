package com.upreality.car.expenses.data

import com.upreality.car.common.data.time.TimeDataSource
import com.upreality.car.expenses.data.local.expenses.ExpensesLocalDataSource
import com.upreality.car.expenses.data.local.expenses.converters.RoomExpenseConverter
import com.upreality.car.expenses.data.local.expenses.model.ExpenseRoom
import com.upreality.car.expenses.data.local.expenses.model.filters.ExpenseIdFilter
import com.upreality.car.expenses.data.local.expensesinfo.ExpensesInfoLocalDataSource
import com.upreality.car.expenses.data.local.expensesinfo.model.entities.ExpenseInfo
import com.upreality.car.expenses.data.local.expensesinfo.model.entities.ExpenseInfoSyncState
import com.upreality.car.expenses.data.local.expensesinfo.model.queries.ExpenseInfoStateFilter
import com.upreality.car.expenses.data.remote.expenseoperations.dao.ExpenseOperationFirestoreDAO
import com.upreality.car.expenses.data.remote.expenseoperations.model.entities.ExpenseOperationFirestore
import com.upreality.car.expenses.data.remote.expenseoperations.model.entities.ExpenseOperationFirestoreType
import com.upreality.car.expenses.data.remote.expenses.dao.ExpensesFirestoreDAO
import com.upreality.car.expenses.data.remote.expenses.converters.RemoteExpenseConverter
import com.upreality.car.expenses.data.shared.model.DateConverter
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class ExpensesSyncService @Inject constructor(
    private val timeDataSource: TimeDataSource,
    private val expensesFirestoreDataSource: ExpensesFirestoreDAO,
    private val operationsFirestoreDAO: ExpenseOperationFirestoreDAO,
    private val expensesLocalDataSource: ExpensesLocalDataSource,
    private val expensesInfoLocalDataSource: ExpensesInfoLocalDataSource,
) {

    private val composite = CompositeDisposable()

    fun start() {
        composite.add(createLocalObserver())
        composite.add(updateLocalObserver())
        composite.add(createRemoteObserver())
    }

    private fun createLocalObserver(): Disposable {
        val createdSelector = ExpenseInfoStateFilter(ExpenseInfoSyncState.Created)
        return expensesInfoLocalDataSource.get(createdSelector).flatMapCompletable { createdInfos ->
            Flowable.fromIterable(createdInfos).flatMapCompletable { expenseInfo ->
                expensesLocalDataSource
                    .get(ExpenseIdFilter(expenseInfo.localId)).firstElement()
                    .map(List<ExpenseRoom>::first)
                    .map(RoomExpenseConverter::toExpense)
                    .map(RemoteExpenseConverter::fromExpense)
                    .flatMap(expensesFirestoreDataSource::create)
                    .flatMapCompletable { remoteExpenseId ->
                        timeDataSource.getTime().map(DateConverter::toTimestamp)
                            .flatMapCompletable { timestamp ->
                                val operation = ExpenseOperationFirestore(
                                    remoteExpenseId,
                                    ExpenseOperationFirestoreType.Created,
                                    timestamp
                                )
                                val createOperation = operationsFirestoreDAO.create(operation)
                                val updatedInfo = ExpenseInfo(
                                    expenseInfo.id,
                                    expenseInfo.localId,
                                    DateConverter.fromTimestamp(timestamp),
                                    remoteExpenseId,
                                    ExpenseInfoSyncState.Persists,
                                    expenseInfo.remoteVersion
                                )

                                val updateInfo = expensesInfoLocalDataSource.update(updatedInfo)
                                createOperation.ignoreElement().andThen(updateInfo)
                            }
                    }
            }
        }.subscribe()
    }

    private fun updateLocalObserver(): Disposable {
        val updatedSelector = ExpenseInfoStateFilter(ExpenseInfoSyncState.Updated)
        return expensesInfoLocalDataSource.get(updatedSelector).flatMapCompletable { createdInfos ->
            Flowable.fromIterable(createdInfos).flatMapCompletable { expenseInfo ->
                expensesLocalDataSource
                    .get(ExpenseIdFilter(expenseInfo.localId)).firstElement()
                    .map(List<ExpenseRoom>::first)
                    .map(RoomExpenseConverter::toExpense)
                    .map(RemoteExpenseConverter::fromExpense)
                    .flatMap(expensesFirestoreDataSource::create)
                    .flatMapCompletable { remoteExpenseId ->
                        timeDataSource.getTime().map(DateConverter::toTimestamp)
                            .flatMapCompletable { timestamp ->
                                val operation = ExpenseOperationFirestore(
                                    remoteExpenseId,
                                    ExpenseOperationFirestoreType.Created,
                                    timestamp
                                )
                                val createOperation = operationsFirestoreDAO.create(operation)
                                val updatedInfo = ExpenseInfo(
                                    expenseInfo.id,
                                    expenseInfo.localId,
                                    DateConverter.fromTimestamp(timestamp),
                                    remoteExpenseId,
                                    ExpenseInfoSyncState.Persists,
                                    expenseInfo.remoteVersion
                                )

                                val updateInfo = expensesInfoLocalDataSource.update(updatedInfo)
                                createOperation.ignoreElement().andThen(updateInfo)
                            }
                    }
            }
        }.subscribe()
    }


}
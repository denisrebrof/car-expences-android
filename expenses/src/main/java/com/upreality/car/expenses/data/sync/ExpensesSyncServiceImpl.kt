package com.upreality.car.expenses.data.sync

import com.upreality.car.expenses.data.local.expensesinfo.model.entities.ExpenseInfoSyncState
import com.upreality.car.expenses.data.remote.expenses.converters.RemoteExpenseConverter
import com.upreality.car.expenses.data.shared.model.DateConverter
import com.upreality.car.expenses.data.sync.model.ExpenseLocalSyncModel
import com.upreality.car.expenses.data.sync.model.ExpenseRemoteSyncModel
import com.upreality.car.expenses.data.sync.model.ExpensesLocalSyncFilter
import com.upreality.car.expenses.data.sync.model.ExpensesRemoteSyncFilter
import com.upreality.car.expenses.domain.IExpensesSyncService
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.*
import javax.inject.Inject

class ExpensesSyncServiceImpl @Inject constructor(
    private val localDataSource: IExpensesSyncLocalDataSource,
    private val remoteDataSource: IExpensesSyncRemoteDataSource,
    private val syncTimestampProvider: IExpensesSyncTimestampProvider
) : IExpensesSyncService {

    override fun createSyncLoop(): Disposable {
        val composite = CompositeDisposable()

        getUpdatedRemoteModelsFlow(ExpensesRemoteSyncFilter::Deleted).flatMapCompletable { deletedExpenses ->
            Flowable.fromIterable(deletedExpenses).flatMapCompletable { remoteExpense ->
                val filter = ExpensesLocalSyncFilter.RemoteId(remoteExpense.remoteModel.id)
                localDataSource
                    .get(filter)
                    .firstElement()
                    .map(List<ExpenseLocalSyncModel>::firstOrNull)
                    .flatMapCompletable {
                        val updateTimestampCompletable =
                            updateTimestamp(remoteExpense.timestamp)
                        localDataSource.delete(it).andThen(updateTimestampCompletable)
                    }
            }
        }.subscribe().apply(composite::add)

        getUpdatedRemoteModelsFlow(ExpensesRemoteSyncFilter::Updated).flatMapCompletable { updatedExpenses ->
            Flowable.fromIterable(updatedExpenses).flatMapCompletable { remoteExpense ->
                val filter = ExpensesLocalSyncFilter.RemoteId(remoteExpense.remoteModel.id)
                localDataSource
                    .get(filter)
                    .firstElement()
                    .map(List<ExpenseLocalSyncModel>::firstOrNull)
                    .map { savedModel ->
                        val localModel = RemoteExpenseConverter.toExpense(remoteExpense.remoteModel)
                        savedModel.copy(state = ExpenseInfoSyncState.Persists, expense = localModel)
                    }.flatMapCompletable {
                        val updateTimestampCompletable = updateTimestamp(remoteExpense.timestamp)
                        localDataSource.update(it).andThen(updateTimestampCompletable)
                    }
            }
        }.subscribe().apply(composite::add)

        getUpdatedRemoteModelsFlow(ExpensesRemoteSyncFilter::Created).flatMapCompletable { createdExpenses ->
            Flowable.fromIterable(createdExpenses).flatMapCompletable { remoteExpense ->
                val localModel = RemoteExpenseConverter.toExpense(remoteExpense.remoteModel)
                val localSyncModel = ExpenseLocalSyncModel(localModel, ExpenseInfoSyncState.Created)
                val createLocalInstanceMaybe = localDataSource.create(localSyncModel)
                val updateTimestampCompletable = updateTimestamp(remoteExpense.timestamp)
                createLocalInstanceMaybe.ignoreElement().andThen(updateTimestampCompletable)
            }
        }.subscribe().apply(composite::add)

        return composite
    }

    private fun updateTimestamp(date: Date): Completable {
        val timestamp = DateConverter.toTimestamp(date)
        return syncTimestampProvider.set(timestamp)
    }

    private fun getUpdatedRemoteModelsFlow(
        mapper: (Date) -> ExpensesRemoteSyncFilter
    ): Flowable<List<ExpenseRemoteSyncModel>> {
        return syncTimestampProvider.get()
            .map(DateConverter::fromTimestamp)
            .map(mapper)
            .switchMap(remoteDataSource::get)
    }


}
package com.upreality.car.expenses.data.sync

import android.util.Log
import com.upreality.car.expenses.data.local.expensesinfo.model.entities.ExpenseInfoSyncState
import com.upreality.car.expenses.data.remote.expenseoperations.model.entities.ExpenseOperationFirestoreType
import com.upreality.car.expenses.data.remote.expenses.converters.RemoteExpenseConverter
import com.upreality.car.expenses.data.shared.model.DateConverter
import com.upreality.car.expenses.data.sync.model.ExpenseLocalSyncModel
import com.upreality.car.expenses.data.sync.model.ExpenseRemoteSyncModel
import com.upreality.car.expenses.data.sync.model.ExpensesLocalSyncFilter
import com.upreality.car.expenses.data.sync.model.ExpensesRemoteSyncFilter
import com.upreality.car.expenses.domain.IExpensesSyncService
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import java.util.*
import javax.inject.Inject

class ExpensesSyncServiceImpl @Inject constructor(
    private val localDataSource: IExpensesSyncLocalDataSource,
    private val remoteDataSource: IExpensesSyncRemoteDataSource,
    private val syncTimestampProvider: IExpensesSyncTimestampProvider
) : IExpensesSyncService {

    override fun createSyncLoop(): Disposable {
        return Flowable.combineLatest(
            getUpdatedRemoteExpensesFlow(),
            getUpdatedLocalExpensesFlow(),
            { remoteUpdates, localUpdates -> remoteUpdates to localUpdates }
        ).onBackpressureLatest().flatMapCompletable { (updatedRemote, _) ->
            val syncFromRemote = Flowable.fromIterable(updatedRemote)
                .flatMapCompletable { updatedExpense ->
                    when (updatedExpense.operationType) {
                        ExpenseOperationFirestoreType.Created -> createLocal(updatedExpense)
                        ExpenseOperationFirestoreType.Updated -> updateLocal(updatedExpense)
                        ExpenseOperationFirestoreType.Deleted -> deleteLocal(updatedExpense)
                    }
                }

            val localUpdatesMaybe = getUpdatedLocalExpensesFlow().firstElement()
            val syncFromLocal = localUpdatesMaybe.flatMapCompletable { localUpdatesList ->
                Flowable.fromIterable(localUpdatesList)
                    .flatMapCompletable { updatedExpense ->
                        when (updatedExpense.state) {
                            ExpenseInfoSyncState.Deleted -> deleteRemote(updatedExpense)
                            ExpenseInfoSyncState.Created -> createRemote(updatedExpense)
                            ExpenseInfoSyncState.Updated -> updateRemote(updatedExpense)
                            else -> Completable.complete()
                        }
                    }
            }

            syncFromRemote.andThen(syncFromLocal)
        }.doOnError {
            Log.e("Sync", "Error during sync occurs: $it")
        }.subscribe()
    }

    private fun createRemote(updatedExpense: ExpenseLocalSyncModel): Completable {
        val remoteModel = RemoteExpenseConverter.fromExpense(updatedExpense.expense)
        val remoteSyncModel = ExpenseRemoteSyncModel(
            remoteModel,
            ExpenseOperationFirestoreType.Created
        )
        return remoteDataSource
            .create(remoteSyncModel)
            .flatMapCompletable(this::updateTimestampByRemote)
    }

    private fun updateRemote(updatedExpense: ExpenseLocalSyncModel): Completable {
        val remoteModel = RemoteExpenseConverter.fromExpense(updatedExpense.expense)
        val remoteSyncModel = ExpenseRemoteSyncModel(
            remoteModel,
            ExpenseOperationFirestoreType.Updated
        )
        return remoteDataSource
            .update(remoteSyncModel)
            .map(DateConverter::fromTimestamp)
            .flatMapCompletable(this::updateTimestamp)
    }

    private fun deleteRemote(updatedExpense: ExpenseLocalSyncModel): Completable {
        val remoteModel = RemoteExpenseConverter.fromExpense(updatedExpense.expense)
        val remoteSyncModel = ExpenseRemoteSyncModel(
            remoteModel,
            ExpenseOperationFirestoreType.Deleted
        )
        return remoteDataSource
            .delete(remoteSyncModel)
            .map(DateConverter::fromTimestamp)
            .flatMapCompletable(this::updateTimestamp)
    }

    private fun updateTimestampByRemote(remoteId: String): Completable {
        val filter = ExpensesRemoteSyncFilter.Id(remoteId)
        return remoteDataSource.get(filter)
            .firstElement()
            .map(List<ExpenseRemoteSyncModel>::firstOrNull)
            .map(ExpenseRemoteSyncModel::timestamp)
            .map(DateConverter::fromTimestamp)
            .flatMapCompletable(this::updateTimestamp)
    }

    private fun getUpdatedRemoteExpensesFlow(): Flowable<List<ExpenseRemoteSyncModel>> {
        return syncTimestampProvider.get()
            .map(DateConverter::fromTimestamp)
            .map(ExpensesRemoteSyncFilter::FromTime)
            .switchMap(remoteDataSource::get)
            .onBackpressureLatest()
            .map { it.sortedBy(ExpenseRemoteSyncModel::timestamp) }
    }

    private fun getUpdatedLocalExpensesFlow(): Flowable<List<ExpenseLocalSyncModel>> {
        return localDataSource.get(ExpensesLocalSyncFilter.StateUpdated)
    }

    private fun createLocal(createdRemoteExpense: ExpenseRemoteSyncModel): Completable {
        val timestamp = DateConverter.fromTimestamp(createdRemoteExpense.timestamp)
        val localModel = RemoteExpenseConverter.toExpense(createdRemoteExpense.remoteModel)
        val localSyncModel = ExpenseLocalSyncModel(localModel, ExpenseInfoSyncState.Created)
        val createLocalInstanceMaybe = localDataSource.create(localSyncModel)
        val updateTimestampCompletable = updateTimestamp(timestamp)
        return createLocalInstanceMaybe.ignoreElement().andThen(updateTimestampCompletable)
    }

    private fun updateLocal(updatedRemoteExpense: ExpenseRemoteSyncModel): Completable {
        return syncLocalModel(
            updatedRemoteExpense,
            ExpenseInfoSyncState.Persists,
            localDataSource::update
        )
    }

    private fun deleteLocal(deletedRemoteExpense: ExpenseRemoteSyncModel): Completable {
        return syncLocalModel(
            deletedRemoteExpense,
            ExpenseInfoSyncState.Deleted,
            localDataSource::delete
        )
    }

    private fun syncLocalModel(
        updatedRemoteExpense: ExpenseRemoteSyncModel,
        syncState: ExpenseInfoSyncState,
        syncOperation: (ExpenseLocalSyncModel) -> Completable
    ): Completable {
        val timestamp = DateConverter.fromTimestamp(updatedRemoteExpense.timestamp)
        val localModel = RemoteExpenseConverter.toExpense(updatedRemoteExpense.remoteModel)
        val localSyncModel = ExpenseLocalSyncModel(localModel, syncState)
        val updateLocalInstanceMaybe = syncOperation(localSyncModel)
        val updateTimestampCompletable = updateTimestamp(timestamp)
        return updateLocalInstanceMaybe.andThen(updateTimestampCompletable)
    }

    private fun updateTimestamp(date: Date): Completable {
        val timestamp = DateConverter.toTimestamp(date)
        return syncTimestampProvider.set(timestamp)
    }
}
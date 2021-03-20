package com.upreality.car.expenses.data.sync

import android.util.Log
import com.upreality.car.expenses.data.local.expensesinfo.model.entities.ExpenseInfoSyncState
import com.upreality.car.expenses.data.remote.expenses.converters.RemoteExpenseConverter
import com.upreality.car.expenses.data.sync.model.ExpenseLocalSyncModel
import com.upreality.car.expenses.data.sync.model.ExpenseRemoteSyncOperationModel
import com.upreality.car.expenses.domain.IExpensesSyncService
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class ExpensesSyncServiceImpl @Inject constructor(
    private val localDataSource: IExpensesSyncLocalDataSource,
    private val remoteDataSource: IExpensesSyncRemoteDataSource,
    private val syncTimestampProvider: IExpensesSyncTimestampProvider
) : IExpensesSyncService {

    override fun createSyncLoop(): Disposable {

        return Flowable.combineLatest(
            getUpdatedRemoteExpensesFlow(),
            localDataSource.getUpdates(),
            { remoteUpdates, localUpdates -> remoteUpdates to localUpdates }
        ).onBackpressureLatest().flatMapCompletable { (updatedRemote, updatesLocal) ->
            val syncFromRemote = Flowable
                .fromIterable(updatedRemote)
                .flatMapCompletable(this::syncLocalModel)

            val syncFromLocal = Flowable
                .fromIterable(updatesLocal)
                .filter { it.state != ExpenseInfoSyncState.Persists }
                .flatMapCompletable(this::syncRemoteModel)

            syncFromRemote.andThen(syncFromLocal)
        }.doOnError {
            Log.e("Sync", "Error during sync occurs: $it")
        }.subscribe()
    }

    private fun getUpdatedRemoteExpensesFlow(): Flowable<List<ExpenseRemoteSyncOperationModel>> {
        return syncTimestampProvider.get()
            .switchMap(remoteDataSource::getModified)
            .onBackpressureLatest()
            .map { it.sortedBy(ExpenseRemoteSyncOperationModel::tstamp) }
    }

    private fun syncRemoteModel(updatedLocalExpense: ExpenseLocalSyncModel): Completable {
        val remoteModel = RemoteExpenseConverter.fromExpense(updatedLocalExpense.expense)
        val syncOperation = when (updatedLocalExpense.state) {
            ExpenseInfoSyncState.Created -> remoteDataSource::create
            ExpenseInfoSyncState.Updated -> remoteDataSource::update
            ExpenseInfoSyncState.Deleted -> remoteDataSource::delete
            else -> return Completable.complete() //TODO: review
        }
        return syncOperation(remoteModel).flatMapCompletable(syncTimestampProvider::set)
    }

    private fun syncLocalModel(updateOperation: ExpenseRemoteSyncOperationModel): Completable {
        val updateLocalInstanceMaybe = when (updateOperation) {
            is ExpenseRemoteSyncOperationModel.Create -> localDataSource.create(
                RemoteExpenseConverter.toExpense(updateOperation.remoteModel),
                updateOperation.remoteModel.id
            )
            is ExpenseRemoteSyncOperationModel.Update -> localDataSource.update(
                RemoteExpenseConverter.toExpense(updateOperation.remoteModel)
            )
            is ExpenseRemoteSyncOperationModel.Delete -> localDataSource.delete(updateOperation.remoteModelId)
        }
        val updateTimestampCompletable = syncTimestampProvider.set(updateOperation.tstamp)
        return updateLocalInstanceMaybe.andThen(updateTimestampCompletable)
    }
}
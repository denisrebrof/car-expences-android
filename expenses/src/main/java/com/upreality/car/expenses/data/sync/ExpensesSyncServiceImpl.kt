package com.upreality.car.expenses.data.sync

import android.util.Log
import com.upreality.car.expenses.data.local.expensesinfo.model.entities.ExpenseInfoSyncState
import com.upreality.car.expenses.data.remote.expenseoperations.model.entities.ExpenseRemoteOperationType
import com.upreality.car.expenses.data.remote.expenses.converters.RemoteExpenseConverter
import com.upreality.car.expenses.data.shared.model.DateConverter
import com.upreality.car.expenses.data.sync.model.ExpenseLocalSyncModel
import com.upreality.car.expenses.data.sync.model.ExpenseRemoteSyncFilter
import com.upreality.car.expenses.data.sync.model.ExpenseRemoteSyncModel
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

    private fun getUpdatedRemoteExpensesFlow(): Flowable<List<ExpenseRemoteSyncModel>> {
        return syncTimestampProvider.get()
            .map(DateConverter::fromTimestamp)
            .map(ExpenseRemoteSyncFilter::FromTime)
            .switchMap(remoteDataSource::get)
            .onBackpressureLatest()
            .map { it.sortedBy(ExpenseRemoteSyncModel::timestamp) }
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

    private fun syncLocalModel(updatedRemoteExpense: ExpenseRemoteSyncModel): Completable {
        val localModel = RemoteExpenseConverter.toExpense(updatedRemoteExpense.remoteModel)
        val syncOperation = when (updatedRemoteExpense.operationType) {
            ExpenseRemoteOperationType.Created -> localDataSource::create
            ExpenseRemoteOperationType.Updated -> localDataSource::update
            ExpenseRemoteOperationType.Deleted -> localDataSource::delete
        }
        val updateLocalInstanceMaybe = syncOperation(localModel)
        val updateTimestampCompletable = syncTimestampProvider.set(updatedRemoteExpense.timestamp)
        return updateLocalInstanceMaybe.andThen(updateTimestampCompletable)
    }
}
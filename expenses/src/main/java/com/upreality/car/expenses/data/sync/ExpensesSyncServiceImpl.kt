package com.upreality.car.expenses.data.sync

import android.util.Log
import com.upreality.car.expenses.data.local.expensesinfo.model.entities.ExpenseInfoSyncState
import com.upreality.car.expenses.data.remote.expenses.converters.RemoteExpenseConverter
import com.upreality.car.expenses.data.sync.model.ExpenseLocalSyncModel
import com.upreality.car.expenses.data.sync.model.ExpenseSyncRemoteModel
import com.upreality.car.expenses.domain.IExpensesSyncService
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ExpensesSyncServiceImpl @Inject constructor(
    private val localDataSource: IExpensesSyncLocalDataSource,
    private val remoteDataSource: IExpensesSyncRemoteDataSource,
    private val syncTimestampProvider: IExpensesSyncTimestampProvider
) : IExpensesSyncService {

    private var localToRemoteSyncPossible = false

    override fun createSyncLoop(): Disposable {

        val composite = CompositeDisposable()

        val syncFromRemote = getUpdatedRemoteExpensesFlow()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .concatMapCompletable { updatedRemote ->
                localToRemoteSyncPossible = updatedRemote.isEmpty()
                Flowable
                    .fromIterable(updatedRemote)
                    .concatMapCompletable(this::syncLocalFromRemote)
            }.doOnError {
                Log.e("Error","$it")
            }.subscribe()

        val syncFromLocalAfterRemote = localDataSource.getLastUpdate()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .filter { localToRemoteSyncPossible }
            .concatMapCompletable { lastLocalUpdate ->
                val localUpdate = lastLocalUpdate as? ExpenseLocalSyncModel.Update
                localUpdate?.let(this::syncRemoteFromLocal) ?: Completable.complete()
            }.doOnError {
                Log.e("Error","$it")
            }.subscribe()

        composite.addAll(syncFromRemote, syncFromLocalAfterRemote)

        return composite
    }

    private fun getUpdatedRemoteExpensesFlow(): Flowable<List<ExpenseSyncRemoteModel>> {
        return syncTimestampProvider.get()
            .switchMap(remoteDataSource::getModified)
            .map { it.sortedBy(ExpenseSyncRemoteModel::timestamp) }
    }

    private fun syncLocalFromRemote(syncRemoteModel: ExpenseSyncRemoteModel): Completable {
        val updateLocalInstanceMaybe = when (syncRemoteModel.deleted) {
            true -> localDataSource.delete(syncRemoteModel.remoteModel.id)
            else -> {
                val expense = RemoteExpenseConverter.toExpense(syncRemoteModel.remoteModel)
                localDataSource.createOrUpdate(expense, syncRemoteModel.remoteModel.id)
            }
        }
        val updateTimestampCompletable = syncTimestampProvider.set(syncRemoteModel.timestamp)
        return updateLocalInstanceMaybe.andThen(updateTimestampCompletable)
    }

    private fun syncRemoteFromLocal(updatedLocalExpense: ExpenseLocalSyncModel.Update): Completable {
        val remoteModel = RemoteExpenseConverter.fromExpense(updatedLocalExpense.expense)
        val syncOperation = when (updatedLocalExpense.state) {
            ExpenseInfoSyncState.Created -> remoteDataSource.create(
                remoteModel,
                updatedLocalExpense.expense.id
            )
            ExpenseInfoSyncState.Updated -> remoteDataSource.update(remoteModel)
            ExpenseInfoSyncState.Deleted -> remoteDataSource.delete(updatedLocalExpense.expense.id)
            else -> return Completable.complete() //TODO: review
        }
        return syncOperation.ignoreElement()
    }
}
package com.upreality.car.expenses.data.sync

import android.util.Log
import com.upreality.car.expenses.data.remote.expenses.converters.RemoteExpenseConverter
import com.upreality.car.expenses.data.sync.model.ExpenseSyncRemoteModel
import com.upreality.car.expenses.domain.IExpensesSyncService
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ExpensesSyncServiceImpl @Inject constructor(
    private val localDataSource: IExpensesSyncLocalDataSource,
    private val remoteDataSource: IExpensesSyncRemoteDataSource,
    private val syncTimestampProvider: IExpensesSyncTimestampProvider
) : IExpensesSyncService {

    override fun createSyncLoop(): Completable {
        return getUpdatedRemoteExpensesFlow()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .concatMapCompletable { updatedRemote ->
                Flowable
                    .fromIterable(updatedRemote)
                    .concatMapCompletable(this::syncLocalFromRemote)
            }
    }

    private fun getUpdatedRemoteExpensesFlow(): Flowable<List<ExpenseSyncRemoteModel>> {
        return syncTimestampProvider.get()
            .switchMap(remoteDataSource::getModified)
            .map { it.sortedBy(ExpenseSyncRemoteModel::timestamp) }
    }

    private fun syncLocalFromRemote(syncRemoteModel: ExpenseSyncRemoteModel): Completable {
        val updateLocalInstance = when (syncRemoteModel) {
            is ExpenseSyncRemoteModel.Deleted -> {
                //In some fucking reason this never completes, wtf?
                //now it does not handled, simple subscribe call
                localDataSource.delete(syncRemoteModel.id).onErrorComplete().subscribe()
                Completable.complete()
            }
            is ExpenseSyncRemoteModel.Persisted -> {
                val expense = RemoteExpenseConverter.toExpense(syncRemoteModel.remoteModel)
                localDataSource.createOrUpdate(expense, syncRemoteModel.remoteModel.id)
            }
        }.onErrorComplete()
        val updateTimestampCompletable = syncTimestampProvider.set(syncRemoteModel.timestamp)
        return updateLocalInstance.andThen(updateTimestampCompletable).doOnComplete {
            Log.e("Compl","Compl")
        }
    }
}
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
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ExpensesSyncServiceImpl @Inject constructor(
    private val localDataSource: IExpensesSyncLocalDataSource,
    private val remoteDataSource: IExpensesSyncRemoteDataSource,
    private val syncTimestampProvider: IExpensesSyncTimestampProvider
) : IExpensesSyncService {

    private val triggerProc = BehaviorProcessor.createDefault(Unit)

    override fun createSyncLoop(): Disposable {

        val composite = CompositeDisposable()

        val logLocalChangeDisp = localDataSource.getUpdates().retry().subscribe {
            Log.d("SYNC", "Updated local list")
        }

        val logRemChangeDisp =
            getUpdatedRemoteExpensesFlow()
                .doOnError {
                    Log.e("Sync", "Error during getUpdatedRemoteExpensesFlow occurs: $it")
                }.retry().subscribe {
                    Log.d("SYNC", "Updated REMOTE list")
                }

        val syncFromRemote = getUpdatedRemoteExpensesFlow().subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .onBackpressureLatest().flatMapCompletable { updatedRemote ->
                Flowable
                    .fromIterable(updatedRemote)
                    .flatMapCompletable(this::syncLocalFromRemote)
            }.doOnError {
                Log.e("Sync", "Error during R sync occurs: $it")
            }.retry().subscribe()

        val syncFromLocalAfterRemote = Flowable.combineLatest(
            triggerProc,
            getUpdatedRemoteExpensesFlow(),
            localDataSource.getUpdates(),
            { _, remoteUpdates, localUpdates -> remoteUpdates to localUpdates }
        ).subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .onBackpressureLatest().flatMapCompletable { (updatesRemote, updatesLocal) ->

                val syncFromLocal = Flowable
                    .fromIterable(updatesLocal)
                    .filter { it.state != ExpenseInfoSyncState.Persists }
                    .flatMapCompletable(this::syncRemoteFromLocal)

                if (updatesRemote.isEmpty()) syncFromLocal else Completable.complete()
            }.doOnError {
                Log.e("Sync", "Error during L sync occurs: $it")
            }.retry().subscribe()

        composite.addAll(logLocalChangeDisp, logRemChangeDisp, syncFromRemote, syncFromLocalAfterRemote)

        return composite
    }

    override fun triggerSync() {
        triggerProc.onNext(Unit)
    }

    private fun getUpdatedRemoteExpensesFlow(): Flowable<List<ExpenseSyncRemoteModel>> {
        return syncTimestampProvider.get()
            .doOnNext {
                Log.d("TS", "next: $it")
            }
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

    private fun syncRemoteFromLocal(updatedLocalExpense: ExpenseLocalSyncModel): Completable {
        val remoteModel = RemoteExpenseConverter.fromExpense(updatedLocalExpense.expense)
        val syncOperation = when (updatedLocalExpense.state) {
            ExpenseInfoSyncState.Created -> remoteDataSource.create(
                remoteModel,
                updatedLocalExpense.expense.id
            )
            ExpenseInfoSyncState.Updated -> remoteDataSource.update(remoteModel)
            ExpenseInfoSyncState.Deleted -> remoteDataSource.delete(remoteModel)
            else -> return Completable.complete() //TODO: review
        }
        return syncOperation
            .ignoreElement()
//            .flatMapCompletable { ts ->
//                Log.d("TS", "set: $ts")
//                syncTimestampProvider.set(ts)
//            }
    }
}
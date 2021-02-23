package com.upreality.car.expenses.data

import com.upreality.car.expenses.data.local.expenses.ExpensesLocalDataSource
import com.upreality.car.expenses.data.local.expensesinfo.ExpensesInfoLocalDataSource
import com.upreality.car.expenses.data.local.expensesinfo.model.entities.ExpenseInfo
import com.upreality.car.expenses.data.local.expensesinfo.model.queries.ExpenseInfoIdFilter
import com.upreality.car.expenses.data.remote.ExpensesRemoteDataSource
import com.upreality.car.expenses.data.remote.firestore.model.filters.ExpenseRemoteFilter
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class ExpensesSyncService @Inject constructor(
    private val expensesRemoteDataSource: ExpensesRemoteDataSource,
    private val expensesLocalDataSource: ExpensesLocalDataSource,
    private val expensesInfoLocalDataSource: ExpensesInfoLocalDataSource,
) {

    private val composite = CompositeDisposable()

    fun start() {
        composite.add(createLocalInfoObserver())
        composite.add(createRemoteObserver())

    }

    private fun createLocalInfoObserver(): Disposable {
        expensesRemoteDataSource.get(ExpenseRemoteFilter.Created).flatMapSingle {
            Flowable.fromIterable(it).flatMapMaybe { expense ->
                expensesLocalDataSource.create(expense).flatMap { localId ->
                    val createdExpenseInfo = ExpenseInfo(0L, localId)
                    expensesInfoLocalDataSource.create()
                }
            }.toList()
        }

        expensesRemoteDataSource.get(ExpenseRemoteFilter.Updated).flatMap { updatedRemoteExpenses ->
            Flowable.fromIterable(updatedRemoteExpenses).flatMapCompletable {
                val infoFilter = ExpenseInfoIdFilter(it.id)
                when (expensesInfoLocalDataSource.get())
            }
        }
    }

    private fun createRemoteObserver(): Disposable {

    }

    fun stop() = composite.dispose()

}
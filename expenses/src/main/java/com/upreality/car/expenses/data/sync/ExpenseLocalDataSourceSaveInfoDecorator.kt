package com.upreality.car.expenses.data.sync

import com.upreality.car.common.data.time.TimeDataSource
import com.upreality.car.expenses.data.local.ExpensesLocalDataSource
import com.upreality.car.expenses.data.local.expenses.dao.ExpenseLocalDetailsDao
import com.upreality.car.expenses.data.local.expenses.dao.ExpenseLocalEntitiesDao
import com.upreality.car.expenses.data.local.expenses.model.ExpenseLocal
import com.upreality.car.expenses.data.sync.expensesinfo.ExpensesInfoLocalDataSource
import com.upreality.car.expenses.data.sync.expensesinfo.model.entities.ExpenseInfo
import com.upreality.car.expenses.data.sync.expensesinfo.model.entities.ExpenseInfoSyncState
import com.upreality.car.expenses.data.sync.expensesinfo.model.queries.ExpenseInfoLocalIdFilter
import io.reactivex.Completable
import io.reactivex.Maybe
import javax.inject.Inject

class ExpenseLocalDataSourceSaveInfoDecorator @Inject constructor(
    private val timeDataSource: TimeDataSource,
    entitiesDao: ExpenseLocalEntitiesDao,
    detailsDao: ExpenseLocalDetailsDao,
    private val infoLocalDataSource: ExpensesInfoLocalDataSource
) : ExpensesLocalDataSource(entitiesDao, detailsDao) {

    override fun create(expense: ExpenseLocal): Maybe<Long> {
        return super.create(expense).flatMap { expenseId ->
            timeDataSource.getTime().flatMap { time ->
                val info = ExpenseInfo(0L, expenseId, time)
                infoLocalDataSource.create(info)
            }.map { expenseId }
        }
    }

    override fun update(expense: ExpenseLocal): Completable {
        return timeDataSource.getTime().flatMapCompletable { time ->
            getUpdateInfoMaybe(expense.id).flatMapCompletable { savedInfo ->
                val newState = when (savedInfo.state) {
                    ExpenseInfoSyncState.Persists -> ExpenseInfoSyncState.Updated
                    else -> savedInfo.state
                }
                val updatedInfo = savedInfo.copy(
                    state = newState,
                    timestamp = time
                )
                infoLocalDataSource.update(updatedInfo)
            }
        }.andThen { super.update(expense) }
    }

    override fun delete(expense: ExpenseLocal): Completable {

        return timeDataSource.getTime().flatMapCompletable { time ->
            getUpdateInfoMaybe(expense.id).flatMapCompletable { savedInfo ->
                val persists = savedInfo.state != ExpenseInfoSyncState.Created
                val updatedInfo = savedInfo.copy(
                    state = ExpenseInfoSyncState.Deleted,
                    timestamp = time
                )
                when (persists) {
                    true -> infoLocalDataSource.update(updatedInfo)
                    else -> infoLocalDataSource.delete(savedInfo)
                }
            }
        }.andThen { super.update(expense) }
    }

    private fun getUpdateInfoMaybe(expenseId: Long): Maybe<ExpenseInfo> {
        val filter = ExpenseInfoLocalIdFilter(expenseId)
        return infoLocalDataSource
            .get(filter)
            .firstElement()
            .map(List<ExpenseInfo>::firstOrNull)
    }
}
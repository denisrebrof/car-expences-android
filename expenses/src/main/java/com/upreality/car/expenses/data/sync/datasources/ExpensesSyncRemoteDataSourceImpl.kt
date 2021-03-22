package com.upreality.car.expenses.data.sync.datasources

import com.upreality.car.expenses.data.local.expensesinfo.ExpensesInfoLocalDataSource
import com.upreality.car.expenses.data.local.expensesinfo.model.entities.ExpenseInfo
import com.upreality.car.expenses.data.local.expensesinfo.model.entities.ExpenseInfoSyncState
import com.upreality.car.expenses.data.local.expensesinfo.model.queries.ExpenseInfoLocalIdFilter
import com.upreality.car.expenses.data.remote.ExpensesRemoteDataSource
import com.upreality.car.expenses.data.remote.expenses.model.ExpenseRemote
import com.upreality.car.expenses.data.remote.expenses.model.filters.ExpenseRemoteFilter
import com.upreality.car.expenses.data.remote.expensestate.dao.ExpenseStateRemoteDAO
import com.upreality.car.expenses.data.remote.expensestate.model.ExpenseRemoteState
import com.upreality.car.expenses.data.remote.expensestate.model.ExpenseRemoteStateFilter
import com.upreality.car.expenses.data.shared.model.DateConverter
import com.upreality.car.expenses.data.sync.IExpensesSyncRemoteDataSource
import com.upreality.car.expenses.data.sync.model.ExpenseSyncRemoteModel
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import java.util.*
import javax.inject.Inject

class ExpensesSyncRemoteDataSourceImpl @Inject constructor(
    private val remoteDataSource: ExpensesRemoteDataSource,
    private val statesDAO: ExpenseStateRemoteDAO,
    private val expensesInfoLocalDataSource: ExpensesInfoLocalDataSource
) : IExpensesSyncRemoteDataSource {

    private val dateConverter = DateConverter()

    override fun getModified(fromTime: Long): Flowable<List<ExpenseSyncRemoteModel>> {
        val filter = ExpenseRemoteStateFilter.FromTime(fromTime)
        return statesDAO.get(filter)
            .map { list -> list.sortedBy(ExpenseRemoteState::timestamp) }
            .flatMapMaybe { states ->
                Flowable.fromIterable(states).flatMapMaybe { state ->
                    getExpense(state.remoteId).map { expense ->
                        val date = dateConverter.toTimestamp(state.timestamp ?: Date())
                        ExpenseSyncRemoteModel(expense, date, state.deleted)
                    }
                }.toList().toMaybe()
            }
    }

    override fun create(remoteExpense: ExpenseRemote, localId: Long): Maybe<Long> {
        return remoteDataSource.create(remoteExpense)
            .flatMap { id ->
                val createdStateTimestampMaybe = createState(id)
                setRemoteId(localId, id).andThen(createdStateTimestampMaybe)
            }
    }

    override fun update(expense: ExpenseRemote): Maybe<Long> {
        val updatedState = updateState(expense, false)
        return remoteDataSource.update(expense).andThen(updatedState)
    }

    override fun delete(expense: ExpenseRemote): Maybe<Long> {
        val updatedState = updateState(expense, true)
        return remoteDataSource.delete(expense).andThen(updatedState)
    }

    private fun getExpense(remoteId: String): Maybe<ExpenseRemote> {
        val filter = ExpenseRemoteFilter.Id(remoteId)
        return remoteDataSource
            .get(filter)
            .firstElement()
            .map(List<ExpenseRemote>::firstOrNull)
    }

    private fun updateState(expense: ExpenseRemote, deleted: Boolean): Maybe<Long> {
        val updateState = getState(expense)
            .map { it.copy(deleted = deleted) }
            .flatMapCompletable(statesDAO::update)

        val getUpdatedTimestamp = getState(expense)
            .map(ExpenseRemoteState::timestamp)
            .map(dateConverter::toTimestamp)

        return updateState.andThen(getUpdatedTimestamp)
    }

    private fun getState(expense: ExpenseRemote): Maybe<ExpenseRemoteState> {
        val filter = ExpenseRemoteStateFilter.ByRemoteId(expense.id)
        return statesDAO.get(filter)
            .firstElement()
            .map(List<ExpenseRemoteState>::firstOrNull)
    }

    private fun createState(remoteId: String): Maybe<Long> {
        val state = ExpenseRemoteState(remoteId = remoteId)
        return statesDAO.create(state)
            .map(ExpenseRemoteStateFilter::Id)
            .flatMap {
                statesDAO
                    .get(it)
                    .firstElement()
                    .map(List<ExpenseRemoteState>::firstOrNull)
            }.map(ExpenseRemoteState::timestamp)
            .map(dateConverter::toTimestamp)
    }

    private fun setRemoteId(localId: Long, remoteId: String): Completable {
        val filter = ExpenseInfoLocalIdFilter(localId)
        return expensesInfoLocalDataSource.get(filter)
            .map(List<ExpenseInfo>::firstOrNull)
            .firstElement()
            .map { it.copy(remoteId = remoteId, state = ExpenseInfoSyncState.Persists) }
            .flatMapCompletable(expensesInfoLocalDataSource::update)
    }
}
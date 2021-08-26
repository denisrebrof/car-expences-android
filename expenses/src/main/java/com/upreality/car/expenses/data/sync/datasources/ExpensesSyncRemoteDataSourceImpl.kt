package com.upreality.car.expenses.data.sync.datasources

import com.upreality.car.expenses.data.sync.remote.ExpensesRemoteDAO
import com.upreality.car.expenses.data.sync.remote.expenses.model.ExpenseRemote
import com.upreality.car.expenses.data.sync.remote.expenses.model.filters.ExpenseRemoteFilter
import com.upreality.car.expenses.data.sync.remote.expensestate.dao.ExpenseStateRemoteDAO
import com.upreality.car.expenses.data.sync.remote.expensestate.model.ExpenseRemoteState
import com.upreality.car.expenses.data.sync.remote.expensestate.model.ExpenseRemoteStateFilter
import com.upreality.car.expenses.data.sync.room.expenses.converters.RoomDateConverter
import com.upreality.car.expenses.data.sync.IExpensesSyncRemoteDataSource
import com.upreality.car.expenses.data.sync.model.ExpenseSyncRemoteModel
import io.reactivex.Flowable
import io.reactivex.Maybe
import java.util.*
import javax.inject.Inject

class ExpensesSyncRemoteDataSourceImpl @Inject constructor(
    private val remoteDAO: ExpensesRemoteDAO,
    private val statesDAO: ExpenseStateRemoteDAO
) : IExpensesSyncRemoteDataSource {

    private val dateConverter = RoomDateConverter()

    override fun getModified(fromTime: Long): Flowable<List<ExpenseSyncRemoteModel>> {
        val filter = ExpenseRemoteStateFilter.FromTime(fromTime)
        return statesDAO.get(filter)
            .map { list -> list.sortedBy(ExpenseRemoteState::timestamp) }
            .concatMapMaybe { states ->
                Flowable.fromIterable(states).concatMapMaybe { state ->
                    val date = dateConverter.toTimestamp(state.timestamp ?: Date())
                    when {
                        state.deleted -> Maybe.just(
                            ExpenseSyncRemoteModel.Deleted(state.remoteId, date)
                        )
                        else -> getExpense(state.remoteId).map { expense ->
                            ExpenseSyncRemoteModel.Persisted(expense, date)
                        }
                    }
                }.toList().toMaybe()
            }
    }

    private fun getExpense(remoteId: String): Maybe<ExpenseRemote> {
        val filter = ExpenseRemoteFilter.Id(remoteId)
        return remoteDAO
            .get(filter)
            .firstElement()
            .map(List<ExpenseRemote>::firstOrNull)
    }
}
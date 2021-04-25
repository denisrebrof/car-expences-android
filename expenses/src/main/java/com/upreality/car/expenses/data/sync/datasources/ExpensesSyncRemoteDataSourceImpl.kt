package com.upreality.car.expenses.data.sync.datasources

import com.upreality.car.expenses.data.remote.ExpensesRemoteDAO
import com.upreality.car.expenses.data.remote.expenses.model.ExpenseRemote
import com.upreality.car.expenses.data.remote.expenses.model.filters.ExpenseRemoteFilter
import com.upreality.car.expenses.data.remote.expensestate.dao.ExpenseStateRemoteDAO
import com.upreality.car.expenses.data.remote.expensestate.model.ExpenseRemoteState
import com.upreality.car.expenses.data.remote.expensestate.model.ExpenseRemoteStateFilter
import com.upreality.car.expenses.data.shared.model.DateConverter
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

    private val dateConverter = DateConverter()

    override fun getModified(fromTime: Long): Flowable<List<ExpenseSyncRemoteModel>> {
        val filter = ExpenseRemoteStateFilter.FromTimePersisted(fromTime)
        return statesDAO.get(filter)
            .map { list -> list.sortedBy(ExpenseRemoteState::timestamp) }
            .concatMapMaybe { states ->
                Flowable.fromIterable(states).concatMapMaybe { state ->
                    getExpense(state.remoteId).map { expense ->
                        val date = dateConverter.toTimestamp(state.timestamp ?: Date())
                        ExpenseSyncRemoteModel(expense, date, state.deleted)
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
package com.upreality.car.expenses.data.sync.datasources

import android.util.Log
import com.upreality.car.expenses.data.local.room.expenses.ExpensesLocalDAO
import com.upreality.car.expenses.data.local.room.expenses.converters.RoomExpenseConverter
import com.upreality.car.expenses.data.local.room.expenses.model.ExpenseRoom
import com.upreality.car.expenses.data.local.room.expenses.model.filters.ExpenseIdFilter
import com.upreality.car.expenses.data.local.room.expensesinfo.ExpensesInfoLocalDataSource
import com.upreality.car.expenses.data.local.room.expensesinfo.model.entities.ExpenseInfo
import com.upreality.car.expenses.data.local.room.expensesinfo.model.entities.ExpenseInfoSyncState.*
import com.upreality.car.expenses.data.local.room.expensesinfo.model.queries.*
import com.upreality.car.expenses.data.local.room.expensesinfo.model.queries.ExpenseInfoRemoteIdFilter
import com.upreality.car.expenses.data.sync.IExpensesSyncLocalDataSource
import com.upreality.car.expenses.domain.model.expence.Expense
import io.reactivex.Completable
import io.reactivex.Maybe
import java.util.*
import javax.inject.Inject

class ExpensesSyncLocalDataSourceImpl @Inject constructor(
    private val expensesLocalDataSource: ExpensesLocalDAO,
    private val expensesInfoLocalDataSource: ExpensesInfoLocalDataSource
) : IExpensesSyncLocalDataSource {

    override fun createOrUpdate(expense: Expense, remoteId: String): Completable {
        val infoMaybe = getExpenseInfoByRemoteId(remoteId)
        val updatedExpense = RoomExpenseConverter.fromExpense(expense)

        return infoMaybe.flatMapCompletable { info ->
            updatedExpense.id = info.localId
            expensesLocalDataSource.update(updatedExpense)
        }.onErrorResumeNext {
            val createInfo = { localId: Long ->
                ExpenseInfo(0, localId, remoteId)
                    .let(expensesInfoLocalDataSource::create)
                    .ignoreElement()
            }
            expensesLocalDataSource
                .create(updatedExpense)
                .flatMapCompletable(createInfo)
        }
    }

    override fun delete(remoteId: String): Completable {
        val deletedExpenseInfo = getExpenseInfoByRemoteId(remoteId)
        return deletedExpenseInfo.flatMapCompletable { info ->
            getLocalExpense(info)
                .flatMapCompletable(expensesLocalDataSource::delete)
                .andThen { expensesInfoLocalDataSource.delete(info) }
        }.doOnComplete {
            Log.d("Compl", "")
        }
    }

    private fun getExpenseInfoByRemoteId(expenseId: String): Maybe<ExpenseInfo> {
        val filter = ExpenseInfoRemoteIdFilter(expenseId)
        return expensesInfoLocalDataSource
            .get(filter)
            .firstElement()
            .map(List<ExpenseInfo>::firstOrNull)
    }

    private fun getLocalExpense(info: ExpenseInfo): Maybe<ExpenseRoom> {
        return expensesLocalDataSource
            .get(ExpenseIdFilter(info.localId))
            .firstElement()
            .map(List<ExpenseRoom>::firstOrNull)
    }
}
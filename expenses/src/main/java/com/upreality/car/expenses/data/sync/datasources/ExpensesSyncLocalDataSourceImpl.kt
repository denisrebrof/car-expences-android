package com.upreality.car.expenses.data.sync.datasources

import com.upreality.car.expenses.data.local.expenses.ExpensesLocalDataSource
import com.upreality.car.expenses.data.local.expenses.converters.RoomExpenseConverter
import com.upreality.car.expenses.data.local.expenses.model.ExpenseRoom
import com.upreality.car.expenses.data.local.expenses.model.filters.ExpenseIdFilter
import com.upreality.car.expenses.data.local.expensesinfo.ExpensesInfoLocalDataSource
import com.upreality.car.expenses.data.local.expensesinfo.model.entities.ExpenseInfo
import com.upreality.car.expenses.data.local.expensesinfo.model.entities.ExpenseInfoSyncState.*
import com.upreality.car.expenses.data.local.expensesinfo.model.queries.*
import com.upreality.car.expenses.data.sync.IExpensesSyncLocalDataSource
import com.upreality.car.expenses.data.sync.model.ExpenseLocalSyncModel
import com.upreality.car.expenses.domain.model.expence.Expense
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import javax.inject.Inject

class ExpensesSyncLocalDataSourceImpl @Inject constructor(
    private val expensesLocalDataSource: ExpensesLocalDataSource,
    private val expensesInfoLocalDataSource: ExpensesInfoLocalDataSource
) : IExpensesSyncLocalDataSource {

    override fun getUpdates(): Flowable<List<ExpenseLocalSyncModel>> {
        return expensesInfoLocalDataSource
            .get(ExpenseInfoAllFilter)
            .map { list -> list.filter { it.state != Persists } }
            .flatMapMaybe(this::getSyncModelsMaybe)
    }

    override fun createOrUpdate(expense: Expense, remoteId: String): Completable {
        val infoMaybe = getExpenseInfoByRemoteId(remoteId)
        val updatedExpense = RoomExpenseConverter.fromExpense(expense)

        return infoMaybe.flatMapCompletable { info ->
            expense.id = info.localId
            val updateExpense = expensesLocalDataSource.update(updatedExpense)
            val updatedInfo = info.copy(state = Persists)
            val updateInfo = expensesInfoLocalDataSource.update(updatedInfo)
            updateExpense.andThen(updateInfo)
        }.onErrorResumeNext { //info not found, so create new
            val createInfo = { localId: Long ->
                ExpenseInfo(0, localId, remoteId, Persists)
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
        }
    }

    private fun getSyncModelsMaybe(infos: List<ExpenseInfo>): Maybe<List<ExpenseLocalSyncModel>> {
        return Flowable.fromIterable(infos).flatMapMaybe { modifiedInfo ->
            getLocalExpense(modifiedInfo)
                .map(RoomExpenseConverter::toExpense)
                .map { ExpenseLocalSyncModel(it, modifiedInfo.state) }
        }.toList().toMaybe()
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
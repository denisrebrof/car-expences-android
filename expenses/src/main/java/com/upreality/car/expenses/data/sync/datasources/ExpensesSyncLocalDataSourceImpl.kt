package com.upreality.car.expenses.data.sync.datasources

import com.upreality.car.expenses.data.local.expenses.ExpensesLocalDataSource
import com.upreality.car.expenses.data.local.expenses.converters.RoomExpenseConverter
import com.upreality.car.expenses.data.local.expenses.model.ExpenseRoom
import com.upreality.car.expenses.data.local.expenses.model.filters.ExpenseIdFilter
import com.upreality.car.expenses.data.local.expensesinfo.ExpensesInfoLocalDataSource
import com.upreality.car.expenses.data.local.expensesinfo.model.entities.ExpenseInfo
import com.upreality.car.expenses.data.local.expensesinfo.model.entities.ExpenseInfoSyncState
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

    private fun getSyncModelsMaybe(infos: List<ExpenseInfo>): Maybe<List<ExpenseLocalSyncModel>> {
        return Flowable.fromIterable(infos).flatMapMaybe { modifiedInfo ->
            getLocalExpense(modifiedInfo)
                .map(RoomExpenseConverter::toExpense)
                .map { ExpenseLocalSyncModel(it, modifiedInfo.state) }
        }.toList().toMaybe()
    }

    override fun update(expense: Expense): Completable {
        val updatedExpense = RoomExpenseConverter.fromExpense(expense)
        val updateExpense = expensesLocalDataSource.update(updatedExpense)

        val updateInfo = getExpenseInfo(expense)
            .map { info -> info.copy(state = Persists) }
            .flatMapCompletable(expensesInfoLocalDataSource::update)

        return updateExpense.andThen(updateInfo)
    }

    override fun delete(id: String): Completable {
        val deletedExpenseInfo = expensesInfoLocalDataSource
            .get(ExpenseInfoRemoteIdFilter(id))
            .firstElement()
            .map(List<ExpenseInfo>::firstOrNull)

        return deletedExpenseInfo.flatMapCompletable { info ->
            getLocalExpense(info)
                .flatMapCompletable(expensesLocalDataSource::delete)
                .andThen { expensesInfoLocalDataSource.delete(info) }
        }
    }

    private fun getExpenseInfo(expense: Expense): Maybe<ExpenseInfo> {
        val filter = ExpenseInfoLocalIdFilter(expense.id)
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

    override fun create(expense: Expense, remoteId: String): Completable {
        val createdExpense = RoomExpenseConverter.fromExpense(expense)
        return expensesLocalDataSource.create(createdExpense).flatMapCompletable {
            val createdInfo = ExpenseInfo(0, it, remoteId)
            expensesInfoLocalDataSource.create(createdInfo).ignoreElement()
        }
    }
}
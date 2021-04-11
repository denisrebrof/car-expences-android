package com.upreality.car.expenses.data.sync.datasources

import android.util.Log
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
import java.util.*
import javax.inject.Inject

class ExpensesSyncLocalDataSourceImpl @Inject constructor(
    private val expensesLocalDataSource: ExpensesLocalDataSource,
    private val expensesInfoLocalDataSource: ExpensesInfoLocalDataSource
) : IExpensesSyncLocalDataSource {

    override fun getLastUpdate(): Flowable<ExpenseLocalSyncModel> {

        val listUpdates = expensesInfoLocalDataSource
            .get(ExpenseInfoAllFilter)
            .map { list -> list.filter { it.state != Persists } }

        val emptyUpdates: Flowable<ExpenseLocalSyncModel> = listUpdates
            .filter(List<ExpenseInfo>::isEmpty)
            .map { ExpenseLocalSyncModel.Empty }

        val nonEmptyUpdates: Flowable<ExpenseLocalSyncModel> = listUpdates
            .filter(List<ExpenseInfo>::isNotEmpty)
            .map(List<ExpenseInfo>::last)
            .distinctUntilChanged { prevInfo, info ->
                if (prevInfo.id == info.id) prevInfo.state == info.state else false
            }.concatMapMaybe(this::getSyncModelUpdateMaybe)

        return emptyUpdates.mergeWith(nonEmptyUpdates).distinctUntilChanged { prevModel, model ->
            prevModel is ExpenseLocalSyncModel.Empty && model is ExpenseLocalSyncModel.Empty
        }
    }

    override fun createOrUpdate(expense: Expense, remoteId: String): Completable {
        val infoMaybe = getExpenseInfoByRemoteId(remoteId)
        val updatedExpense = RoomExpenseConverter.fromExpense(expense)

        return infoMaybe.flatMapCompletable { info ->
            updatedExpense.id = info.localId
            val updateExpense = expensesLocalDataSource.update(updatedExpense)
            val updatedInfo = info.copy(state = Persists)
            val updateInfo = expensesInfoLocalDataSource.update(updatedInfo)
            updateExpense.andThen(updateInfo)
        }.onErrorResumeNext {
            Log.e("Error:", "$it")
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
            val updatedInfo = info.copy(state = Persists)
            getLocalExpense(info)
                .flatMapCompletable(expensesLocalDataSource::delete)
                .andThen { expensesInfoLocalDataSource.delete(updatedInfo) }
        }
    }

    private fun getSyncModelUpdateMaybe(modifiedInfo: ExpenseInfo): Maybe<ExpenseLocalSyncModel.Update> {
        return when (modifiedInfo.state) {
            Deleted -> {
                //TODO remove this hack
                val expense = Expense.Fuel(Date(), 0f, 0f, 0f)
                expense.id = modifiedInfo.localId
                Maybe.just(ExpenseLocalSyncModel.Update(expense, Deleted))
            }
            else -> getLocalExpense(modifiedInfo)
                .map(RoomExpenseConverter::toExpense)
                .map { ExpenseLocalSyncModel.Update(it, modifiedInfo.state) }
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
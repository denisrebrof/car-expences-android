package com.upreality.car.expenses.data

import com.upreality.car.common.data.time.TimeDataSource
import com.upreality.car.expenses.data.local.expenses.ExpensesLocalDataSource
import com.upreality.car.expenses.data.local.expenses.converters.RoomExpenseConverter
import com.upreality.car.expenses.data.local.expenses.converters.RoomExpenseFilterConverter
import com.upreality.car.expenses.data.local.expensesinfo.ExpensesInfoLocalDataSource
import com.upreality.car.expenses.data.local.expensesinfo.model.entities.ExpenseInfo
import com.upreality.car.expenses.data.local.expensesinfo.model.entities.ExpenseInfoSyncState
import com.upreality.car.expenses.data.local.expensesinfo.model.queries.ExpenseInfoLocalIdFilter
import com.upreality.car.expenses.domain.IExpensesRepository
import com.upreality.car.expenses.domain.model.ExpenseFilter
import com.upreality.car.expenses.domain.model.expence.Expense
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import javax.inject.Inject

class ExpensesRepositoryImpl @Inject constructor(
    private val timeDataSource: TimeDataSource,
    private val expensesLocalDataSource: ExpensesLocalDataSource,
    private val expensesInfoLocalDataSource: ExpensesInfoLocalDataSource
) : IExpensesRepository {

    companion object {
        const val NEW_INSTANCE_ID = 0L
    }

    override fun create(expense: Expense): Maybe<Long> {
        expense.id = NEW_INSTANCE_ID
        val localModel = RoomExpenseConverter.fromExpense(expense)
        val createLocal = expensesLocalDataSource.create(localModel)
        val timestampMaybe = timeDataSource.getTime()
        return timestampMaybe.flatMap { timestamp ->
            createLocal.flatMap { localExpenseId ->
                val expenseInfo = ExpenseInfo(NEW_INSTANCE_ID, localExpenseId, timestamp)
                expensesInfoLocalDataSource.create(expenseInfo).map { localExpenseId }
            }
        }
    }

    override fun get(filter: ExpenseFilter): Flowable<List<Expense>> {
        val localFilter = RoomExpenseFilterConverter.convert(filter)
        return expensesLocalDataSource.get(localFilter).map { localExpenses ->
            localExpenses.map(RoomExpenseConverter::toExpense)
        }
    }

    override fun update(expense: Expense): Completable {
        val localModel = RoomExpenseConverter.fromExpense(expense)
        val updateLocal = expensesLocalDataSource.update(localModel)
        val expenseInfoSelector = ExpenseInfoLocalIdFilter(expense.id)
        val expenseInfoMaybe = expensesInfoLocalDataSource.get(expenseInfoSelector).firstElement()
        val updateExpenseInfo = expenseInfoMaybe
            .map(List<ExpenseInfo>::firstOrNull)
            .flatMap(this::updateExpenseInfo)
            .flatMapCompletable(expensesInfoLocalDataSource::update)
        return updateLocal.andThen(updateExpenseInfo)
    }

    override fun delete(expense: Expense): Completable {
        val localModel = RoomExpenseConverter.fromExpense(expense)
        val deleteLocal = expensesLocalDataSource.delete(localModel)
        val expenseInfoSelector = ExpenseInfoLocalIdFilter(expense.id)
        val expenseInfoMaybe = expensesInfoLocalDataSource.get(expenseInfoSelector).firstElement()
        val updateExpenseInfo = expenseInfoMaybe
            .map(List<ExpenseInfo>::firstOrNull)
            .flatMap(this::deleteExpenseInfo)
            .flatMapCompletable(expensesInfoLocalDataSource::update)
        return deleteLocal.andThen(updateExpenseInfo)
    }

    private fun updateExpenseInfo(info: ExpenseInfo): Maybe<ExpenseInfo> {
        return updateExpenseInfoState(info, ExpenseInfoSyncState.Updated)
    }

    private fun deleteExpenseInfo(info: ExpenseInfo): Maybe<ExpenseInfo> {
        return updateExpenseInfoState(info, ExpenseInfoSyncState.Deleted)
    }

    private fun updateExpenseInfoState(
        info: ExpenseInfo,
        newState: ExpenseInfoSyncState
    ): Maybe<ExpenseInfo> {
        return timeDataSource.getTime().map { timestamp ->
            ExpenseInfo(
                info.id,
                info.localId,
                timestamp,
                info.remoteId,
                newState,
                info.remoteVersion
            )
        }
    }
}
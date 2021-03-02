package com.upreality.car.expenses.data

import com.upreality.car.common.data.time.TimeDataSource
import com.upreality.car.expenses.data.local.expenses.ExpensesLocalDataSource
import com.upreality.car.expenses.data.local.expensesinfo.ExpensesInfoLocalDataSource
import com.upreality.car.expenses.data.local.expensesinfo.model.entities.ExpenseInfo
import com.upreality.car.expenses.data.local.expensesinfo.model.entities.ExpenseRemoteState
import com.upreality.car.expenses.data.local.expensesinfo.model.queries.ExpenseInfoLocalIdFilter
import com.upreality.car.expenses.data.remote.ExpensesRemoteDataSource
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
    private val expensesRemoteDataSource: ExpensesRemoteDataSource,
    private val expensesInfoLocalDataSource: ExpensesInfoLocalDataSource
) : IExpensesRepository {

    companion object {
        const val NEW_INSTANCE_ID = 0L
    }

    override fun create(expense: Expense): Maybe<Long> {
        expense.id = NEW_INSTANCE_ID
        val createLocal = expensesLocalDataSource.create(expense)
        val timestampMaybe = timeDataSource.getTime()
        return timestampMaybe.flatMap { timestamp ->
            createLocal.flatMap { localExpenseId ->
                val expenseInfo = ExpenseInfo(NEW_INSTANCE_ID, localExpenseId, timestamp)
                expensesInfoLocalDataSource.create(expenseInfo).map { localExpenseId }
            }
        }
    }

    override fun get(filter: ExpenseFilter): Flowable<List<Expense>> {
        return expensesLocalDataSource.get(filter)
    }

    override fun update(expense: Expense): Completable {
        val updateLocal = expensesLocalDataSource.update(expense)
        val expenseInfoSelector = ExpenseInfoLocalIdFilter(expense.id)
        val expenseInfoMaybe = expensesInfoLocalDataSource.get(expenseInfoSelector).firstElement()
        val updateExpenseInfo = expenseInfoMaybe
            .map(List<ExpenseInfo>::firstOrNull)
            .flatMap(this::updateExpenseInfo)
            .flatMapCompletable(expensesInfoLocalDataSource::update)
        return updateLocal.andThen(updateExpenseInfo)
    }

    override fun delete(expense: Expense): Completable {
        val deleteLocal = expensesLocalDataSource.delete(expense)
        val expenseInfoSelector = ExpenseInfoLocalIdFilter(expense.id)
        val expenseInfoMaybe = expensesInfoLocalDataSource.get(expenseInfoSelector).firstElement()
        val updateExpenseInfo = expenseInfoMaybe
            .map(List<ExpenseInfo>::firstOrNull)
            .flatMap(this::deleteExpenseInfo)
            .flatMapCompletable(expensesInfoLocalDataSource::update)
        return deleteLocal.andThen(updateExpenseInfo)
    }

    private fun updateExpenseInfo(info: ExpenseInfo): Maybe<ExpenseInfo> {
        return updateExpenseInfoState(info, ExpenseRemoteState.Updated)
    }

    private fun deleteExpenseInfo(info: ExpenseInfo): Maybe<ExpenseInfo> {
        return updateExpenseInfoState(info, ExpenseRemoteState.Deleted)
    }

    private fun updateExpenseInfoState(
        info: ExpenseInfo,
        newState: ExpenseRemoteState
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
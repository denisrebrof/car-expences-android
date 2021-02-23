package com.upreality.car.expenses.data

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
        return createLocal.flatMap { localExpenseId ->
            val expenseInfo = ExpenseInfo(NEW_INSTANCE_ID, localExpenseId)
            expensesInfoLocalDataSource.create(expenseInfo).map { localExpenseId }
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
            .map(this::updateExpenseInfo)
            .flatMapCompletable(expensesInfoLocalDataSource::update)
        return updateLocal.andThen(updateExpenseInfo)
    }

    private fun updateExpenseInfo(info: ExpenseInfo): ExpenseInfo {
        return ExpenseInfo(
            info.id,
            info.localId,
            info.remoteId,
            ExpenseRemoteState.Updated,
            info.remoteVersion
        )
    }

    override fun delete(expense: Expense): Completable {
        val deleteLocal = expensesLocalDataSource.delete(expense)
        val expenseInfoSelector = ExpenseInfoLocalIdFilter(expense.id)
        val expenseInfoMaybe = expensesInfoLocalDataSource.get(expenseInfoSelector).firstElement()
        val updateExpenseInfo = expenseInfoMaybe
            .map(List<ExpenseInfo>::firstOrNull)
            .map(this::deleteExpenseInfo)
            .flatMapCompletable(expensesInfoLocalDataSource::update)
        return deleteLocal.andThen(updateExpenseInfo)
    }

    private fun deleteExpenseInfo(info: ExpenseInfo): ExpenseInfo {
        return ExpenseInfo(
            info.id,
            info.localId,
            info.remoteId,
            ExpenseRemoteState.Deleted,
            info.remoteVersion
        )
    }
}
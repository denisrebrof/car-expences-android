package com.upreality.car.expenses.data.sync.datasources

import com.upreality.car.common.data.database.IDatabaseFilter
import com.upreality.car.expenses.data.local.expenses.ExpensesLocalDataSource
import com.upreality.car.expenses.data.local.expenses.model.ExpenseRoom
import com.upreality.car.expenses.data.local.expensesinfo.ExpensesInfoLocalDataSource
import com.upreality.car.expenses.data.local.expensesinfo.model.entities.ExpenseInfo
import com.upreality.car.expenses.data.local.expensesinfo.model.entities.ExpenseInfoSyncState
import com.upreality.car.expenses.data.local.expensesinfo.model.queries.ExpenseInfoLocalIdFilter
import com.upreality.car.expenses.data.repository.IExpensesLocalDataSource
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import javax.inject.Inject

class ExpensesLocalDSSyncDecorator @Inject constructor(
    private val localDS: ExpensesLocalDataSource,
    private val infoDS: ExpensesInfoLocalDataSource
) : IExpensesLocalDataSource {

    override fun delete(expense: ExpenseRoom): Completable {
        val updateState = updateInfo(expense, ExpenseInfoSyncState.Deleted)
        return localDS.delete(expense).andThen(updateState)
    }

    override fun update(expense: ExpenseRoom): Completable {
        val updateState = updateInfo(expense, ExpenseInfoSyncState.Updated)
        return localDS.update(expense).andThen(updateState)
    }

    override fun get(filter: IDatabaseFilter): Flowable<List<ExpenseRoom>> {
        return localDS.get(filter)
    }

    override fun create(expense: ExpenseRoom): Maybe<Long> {
        return localDS.create(expense).flatMap { createdExpenseId ->
            val info = ExpenseInfo(0, createdExpenseId, String())
            infoDS.create(info).map { createdExpenseId }
        }
    }

    private fun updateInfo(expense: ExpenseRoom, state: ExpenseInfoSyncState): Completable {
        return infoDS
            .get(ExpenseInfoLocalIdFilter(expense.id))
            .map(List<ExpenseInfo>::firstOrNull)
            .map { it.copy(state = state) }
            .flatMapCompletable(infoDS::update)
    }
}
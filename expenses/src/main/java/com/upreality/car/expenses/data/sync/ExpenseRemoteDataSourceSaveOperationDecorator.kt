package com.upreality.car.expenses.data.sync

import com.upreality.car.common.data.time.TimeDataSource
import com.upreality.car.expenses.data.remote.ExpensesRemoteDataSource
import com.upreality.car.expenses.data.remote.expenses.dao.ExpenseRemoteDetailsDAO
import com.upreality.car.expenses.data.remote.expenses.dao.ExpenseRemoteEntityDAO
import com.upreality.car.expenses.data.remote.expenses.model.ExpenseRemote
import com.upreality.car.expenses.data.sync.expenseoperations.dao.ExpenseOperationRemoteDAO
import com.upreality.car.expenses.data.sync.expenseoperations.model.entities.ExpenseOperationRemote
import com.upreality.car.expenses.data.sync.expenseoperations.model.entities.ExpenseOperationRemoteType
import io.reactivex.Completable
import io.reactivex.Maybe
import java.util.*
import javax.inject.Inject

class ExpenseRemoteDataSourceSaveOperationDecorator @Inject constructor(
    entityDAO: ExpenseRemoteEntityDAO,
    detailsDAO: ExpenseRemoteDetailsDAO,
    private val timeDataSource: TimeDataSource,
    private val operationsDAO: ExpenseOperationRemoteDAO
) : ExpensesRemoteDataSource(entityDAO, detailsDAO) {

    override fun create(expense: ExpenseRemote): Maybe<String> {
        return super.create(expense).flatMap { createdId ->
            createOperation(
                createdId,
                ExpenseOperationRemoteType.Created
            ).andThen(Maybe.just(createdId))
        }
    }

    override fun update(expense: ExpenseRemote): Completable {
        return createOperation(
            expense.id,
            ExpenseOperationRemoteType.Updated
        ).andThen { super.update(expense) }
    }

    override fun delete(expense: ExpenseRemote): Completable {
        return createOperation(
            expense.id,
            ExpenseOperationRemoteType.Deleted
        ).andThen { super.delete(expense) }
    }

    private fun createOperation(expenseId: String, type: ExpenseOperationRemoteType): Completable {
        return timeDataSource.getTime().map(Date::getTime).flatMapCompletable { time ->
            val operation = ExpenseOperationRemote(String(), expenseId, type, time)
            operationsDAO.create(operation).ignoreElement()
        }
    }
}
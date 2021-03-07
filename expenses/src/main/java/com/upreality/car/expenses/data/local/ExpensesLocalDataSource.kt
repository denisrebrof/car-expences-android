package com.upreality.car.expenses.data.local

import androidx.sqlite.db.SimpleSQLiteQuery
import com.upreality.car.common.data.database.IDatabaseFilter
import com.upreality.car.expenses.data.local.expenses.converters.room.RoomExpenseEntitiesConverter
import com.upreality.car.expenses.data.local.expenses.dao.ExpenseLocalDetailsDao
import com.upreality.car.expenses.data.local.expenses.dao.ExpenseLocalEntitiesDao
import com.upreality.car.expenses.data.local.expenses.model.ExpenseLocal
import com.upreality.car.expenses.data.local.expenses.model.entities.ExpenseLocalEntity
import com.upreality.car.expenses.data.local.expenses.model.filters.ExpenseLocalIdFilter
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import javax.inject.Inject

open class ExpensesLocalDataSource @Inject constructor(
    private val expenseEntitiesDao: ExpenseLocalEntitiesDao,
    private val expenseDetailsDao: ExpenseLocalDetailsDao
) {

    open fun create(expense: ExpenseLocal): Maybe<Long> {
        val details = RoomExpenseEntitiesConverter.toExpenseDetails(expense, 0)
        return expenseDetailsDao.insert(details).flatMap { detailsId ->
            val expenseEntity = RoomExpenseEntitiesConverter.toExpenseEntity(expense, detailsId)
            expenseEntitiesDao.insert(expenseEntity)
        }
    }

    open fun get(filter: IDatabaseFilter): Flowable<List<ExpenseLocal>> {
        val query = SimpleSQLiteQuery(filter.getFilterExpression())
        val expenseEntitiesFlow = expenseEntitiesDao.load(query)

        return expenseEntitiesFlow.flatMapSingle { expenseEntities ->
            Flowable.fromIterable(expenseEntities).flatMapMaybe { entity ->
                val detailsMaybe = expenseDetailsDao.get(entity.detailsId, entity.type)
                detailsMaybe.map { RoomExpenseEntitiesConverter.toExpense(entity, it) }
            }.toList()
        }
    }

    open fun update(expense: ExpenseLocal): Completable {
        val savedExpenseMaybe = getSavedExpenseEntity(expense.id)
        return savedExpenseMaybe.flatMapCompletable { entity ->
            val detailsId = entity.detailsId
            val details = RoomExpenseEntitiesConverter.toExpenseDetails(expense, detailsId)
            if (RoomExpenseEntitiesConverter.getExpenseType(expense) == entity.type) {
                expenseDetailsDao.update(details).andThen(
                    RoomExpenseEntitiesConverter.toExpenseEntity(expense, detailsId)
                        .let(expenseEntitiesDao::update)
                )
            } else {
                expenseDetailsDao.get(detailsId, entity.type).flatMapCompletable {
                    expenseDetailsDao.delete(it).andThen(
                        expenseDetailsDao.insert(details).flatMapCompletable {
                            val expenseEntity =
                                RoomExpenseEntitiesConverter.toExpenseEntity(expense, detailsId)
                            expenseEntitiesDao.update(expenseEntity)
                        }
                    )
                }
            }
        }
    }

    open fun delete(expense: ExpenseLocal): Completable {
        return getSavedExpenseEntity(expense.id).flatMapCompletable {
            val detailsId = it.detailsId
            val details = RoomExpenseEntitiesConverter.toExpenseDetails(expense, detailsId)
            expenseDetailsDao.delete(details).andThen(
                expenseEntitiesDao.delete(
                    RoomExpenseEntitiesConverter.toExpenseEntity(
                        expense,
                        detailsId
                    )
                )
            )
        }
    }

    private fun getSavedExpenseEntity(expenseId: Long): Maybe<ExpenseLocalEntity> {
        val idFilter = ExpenseLocalIdFilter(expenseId).getFilterExpression()
        val query = SimpleSQLiteQuery(idFilter)
        return expenseEntitiesDao.load(query).firstElement()
            .map(List<ExpenseLocalEntity>::firstOrNull)
    }
}
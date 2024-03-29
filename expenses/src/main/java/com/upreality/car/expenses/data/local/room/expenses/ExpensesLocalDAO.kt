package com.upreality.car.expenses.data.local.room.expenses

import android.util.Log
import androidx.sqlite.db.SimpleSQLiteQuery
import com.upreality.car.expenses.data.local.room.expenses.converters.RoomExpenseEntitiesConverter
import com.upreality.car.expenses.data.local.room.expenses.converters.RoomExpenseFilterConverter
import com.upreality.car.expenses.data.local.room.expenses.dao.ExpenseDetailsDao
import com.upreality.car.expenses.data.local.room.expenses.dao.ExpensesDao
import com.upreality.car.expenses.data.local.room.expenses.model.ExpenseRoom
import com.upreality.car.expenses.data.local.room.expenses.model.entities.ExpenseEntity
import com.upreality.car.expenses.domain.model.ExpenseFilter
import data.database.IDatabaseFilter
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import javax.inject.Inject

class ExpensesLocalDAO @Inject constructor(
    private val expensesDao: ExpensesDao,
    private val expenseDetailsDao: ExpenseDetailsDao
) {

    fun create(expense: ExpenseRoom): Maybe<Long> {
        val details = RoomExpenseEntitiesConverter.toExpenseDetails(expense, 0)
        return expenseDetailsDao.insert(details).flatMap { detailsId ->
            val expenseEntity = RoomExpenseEntitiesConverter.toExpenseEntity(expense, detailsId)
            expensesDao.insert(expenseEntity)
        }
    }

    fun get(filter: IDatabaseFilter): Flowable<List<ExpenseRoom>> {
        val query = SimpleSQLiteQuery(filter.getFilterExpression())
        val expenseEntitiesFlow = expensesDao.load(query)

        return expenseEntitiesFlow.flatMapSingle { expenseEntities ->
            Flowable.fromIterable(expenseEntities).flatMapMaybe { entity ->
                val detailsMaybe = expenseDetailsDao.get(entity.detailsId, entity.type)
                detailsMaybe.map { RoomExpenseEntitiesConverter.toExpense(entity, it) }
            }.toList()
        }
    }

    fun update(expense: ExpenseRoom): Completable {
        val savedExpenseMaybe = getSavedExpenseEntity(expense.id)
        return savedExpenseMaybe.flatMapCompletable { entity ->
            val detailsId = entity.detailsId
            val details = RoomExpenseEntitiesConverter.toExpenseDetails(expense, detailsId)
            if (RoomExpenseEntitiesConverter.getExpenseType(expense) == entity.type) {
                expenseDetailsDao.update(details).andThen(
                    RoomExpenseEntitiesConverter.toExpenseEntity(expense, detailsId)
                        .let(expensesDao::update)
                )
            } else {
                expenseDetailsDao.get(detailsId, entity.type).flatMapCompletable {
                    expenseDetailsDao.delete(it).andThen(
                        expenseDetailsDao.insert(details).flatMapCompletable {
                            val expenseEntity =
                                RoomExpenseEntitiesConverter.toExpenseEntity(expense, detailsId)
                            expensesDao.update(expenseEntity)
                        }
                    )
                }
            }
        }
    }

    fun delete(expense: ExpenseRoom): Completable {
        return getSavedExpenseEntity(expense.id).flatMapCompletable {
            val detailsId = it.detailsId
            val details = RoomExpenseEntitiesConverter.toExpenseDetails(expense, detailsId)
            expensesDao
                .delete(RoomExpenseEntitiesConverter.toExpenseEntity(expense, detailsId))
                .andThen(
                    expenseDetailsDao.delete(details)
                ).doOnComplete {
                    Log.e("Compl", "")
                }
                .doOnError {
                    Log.e("Error", "")
                }
                .doFinally {
                    Log.e("Fin", "")
                }
        }
    }

    private fun getSavedExpenseEntity(expenseId: Long): Maybe<ExpenseEntity> {
        val idFilter = ExpenseFilter.Id(expenseId).let(::listOf).let {
            RoomExpenseFilterConverter.convert(it)
        }.getFilterExpression()
        val query = SimpleSQLiteQuery(idFilter)
        return expensesDao.load(query).firstElement().map(List<ExpenseEntity>::firstOrNull)
    }
}
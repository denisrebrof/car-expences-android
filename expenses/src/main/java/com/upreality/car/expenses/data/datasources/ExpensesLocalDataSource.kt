package com.upreality.car.expenses.data.datasources

import androidx.sqlite.db.SimpleSQLiteQuery
import com.upreality.car.expenses.data.converters.ExpenseConverter
import com.upreality.car.expenses.data.converters.ExpenseFilterConverter
import com.upreality.car.expenses.data.dao.ExpenseDetailsDao
import com.upreality.car.expenses.data.dao.ExpensesDao
import com.upreality.car.expenses.data.model.entities.ExpenseEntity
import com.upreality.car.expenses.data.model.queries.ExpenseIdFilter
import com.upreality.car.expenses.domain.model.ExpenseFilter
import com.upreality.car.expenses.domain.model.expence.Expense
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import javax.inject.Inject

class ExpensesLocalDataSource @Inject constructor(
    private val expensesDao: ExpensesDao,
    private val expenseDetailsDao: ExpenseDetailsDao
) {
    private val converter = ExpenseConverter()
    private val filterConverter = ExpenseFilterConverter()

    fun create(expense: Expense): Maybe<Long> {
        val details = converter.toExpenseDetails(expense, 0)
        return expenseDetailsDao.insert(details).flatMap { detailsId ->
            val expenseEntity = converter.toExpenseEntity(expense, detailsId)
            expensesDao.insert(expenseEntity)
        }
    }

    fun get(filter: ExpenseFilter): Flowable<List<Expense>> {
        val roomFilter = filterConverter.convert(filter)
        val query = SimpleSQLiteQuery(roomFilter.getFilterExpression())
        val expenseEntitiesFlow = expensesDao.load(query)

        return expenseEntitiesFlow.flatMapSingle { expenseEntities ->
            Flowable.fromIterable(expenseEntities).flatMapMaybe { entity ->
                val detailsMaybe = expenseDetailsDao.get(entity.detailsId, entity.type)
                detailsMaybe.map { converter.toExpense(entity, it) }
            }.toList()
        }
    }

    fun update(expense: Expense): Completable {
        val savedExpenseMaybe = getSavedExpenseEntity(expense.id)
        return savedExpenseMaybe.flatMapCompletable { entity ->
            val detailsId = entity.detailsId
            val details = converter.toExpenseDetails(expense, detailsId)
            if (converter.getExpenseType(expense) == entity.type) {
                expenseDetailsDao.update(details).andThen(
                    converter.toExpenseEntity(expense, detailsId).let {
                        expensesDao.update(it)
                    }
                )
            } else {
                expenseDetailsDao.get(detailsId, entity.type).flatMapCompletable {
                    expenseDetailsDao.delete(it).andThen(
                        expenseDetailsDao.insert(details).flatMapCompletable {
                            val expenseEntity = converter.toExpenseEntity(expense, detailsId)
                            expensesDao.update(expenseEntity)
                        }
                    )
                }
            }
        }
    }

    private fun getSavedExpenseEntity(expenseId: Long): Maybe<ExpenseEntity> {
        val idFilter = ExpenseIdFilter(expenseId).getFilterExpression()
        val query = SimpleSQLiteQuery(idFilter)
        return expensesDao.load(query).firstElement().map { it.firstOrNull() }
    }

    fun delete(expense: Expense): Completable {
        return getSavedExpenseEntity(expense.id).flatMapCompletable {
            val detailsId = it.detailsId
            val details = converter.toExpenseDetails(expense, detailsId)
            expenseDetailsDao.delete(details).andThen(
                expensesDao.delete(converter.toExpenseEntity(expense, detailsId))
            )
        }
    }
}
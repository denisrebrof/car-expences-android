package com.upreality.car.expenses.data.datasources

import android.util.Log
import androidx.sqlite.db.SimpleSQLiteQuery
import com.upreality.car.expenses.data.converters.ExpenseConverter
import com.upreality.car.expenses.data.converters.ExpenseFilterConverter
import com.upreality.car.expenses.data.dao.ExpenseDetailsDao
import com.upreality.car.expenses.data.dao.ExpensesDao
import com.upreality.car.expenses.data.model.entities.ExpenseEntity
import com.upreality.car.expenses.data.model.queries.ExpenseIdFilter
import com.upreality.car.expenses.domain.ExpenseFilter
import com.upreality.car.expenses.domain.model.expence.Expense
import javax.inject.Inject

class ExpensesLocalDataSource @Inject constructor(
    private val expensesDao: ExpensesDao,
    private val expenseDetailsDao: ExpenseDetailsDao
) {

    private val converter = ExpenseConverter()
    private val filterConverter = ExpenseFilterConverter()

    fun create(expense: Expense): Long {
        val details = converter.toExpenseDetails(expense, 0)
        val detailsId = expenseDetailsDao.insert(details)
        val expenseType = converter.getExpenseType(expense)
        return expensesDao.insert(
            ExpenseEntity(
                0,
                expense.date,
                expense.cost,
                expenseType,
                detailsId
            )
        )
    }

    fun get(filter: ExpenseFilter): List<Expense> {
        val roomFilter = filterConverter.convert(filter)
        val query = SimpleSQLiteQuery(roomFilter.getFilterExpression())
        val expenseEntities = expensesDao.load(query)
        return expenseEntities.mapNotNull { expenseEntity ->
            val detailsId = expenseEntity.detailsId
            val details = expenseDetailsDao.get(detailsId, expenseEntity.type)
            details?.let { converter.toExpense(expenseEntity, details) }
        }
    }

    fun update(expense: Expense) {
        val detailsId = getSavedDetailsId(expense.id)
        if (detailsId == null) {
            Log.e("Update Error", "Expense due update does not found in room DB!")
            return
        }

        val details = converter.toExpenseDetails(expense, detailsId)
        expenseDetailsDao.update(details)

        val expenseEntity = converter.toExpenseEntity(expense, detailsId)
        expensesDao.update(expenseEntity)
    }

    private fun getSavedDetailsId(expenseId: Long): Long? {
        val idFilter = ExpenseIdFilter(expenseId).getFilterExpression()
        val query = SimpleSQLiteQuery(idFilter)
        val savedExpense = expensesDao.load(query).firstOrNull()
        return savedExpense?.detailsId
    }

    fun delete(expense: Expense) {
        val detailsId = getSavedDetailsId(expense.id)
        if (detailsId == null) {
            Log.e("Delete Error", "Expense due delete does not found in room DB!")
            return
        }

        val details = converter.toExpenseDetails(expense, detailsId)
        expenseDetailsDao.delete(details)

        val expenseType = converter.getExpenseType(expense)
        expense.apply {
            val expenseEntity = ExpenseEntity(id, date, cost, expenseType, detailsId)
            expensesDao.delete(expenseEntity)
        }
    }
}
package com.upreality.car.expenses.data

import com.upreality.car.expenses.data.datasources.ExpensesLocalDataSource
import com.upreality.car.expenses.domain.ExpenseFilter
import com.upreality.car.expenses.domain.IExpensesRepository
import com.upreality.car.expenses.domain.model.expence.Expense
import javax.inject.Inject

class ExpensesRepository @Inject constructor(
    private val expensesLocalDataSource: ExpensesLocalDataSource
) : IExpensesRepository {

    override fun create(expense: Expense) {

    }

    override fun get(filter: ExpenseFilter): List<Expense> {
        return expensesLocalDataSource
    }

    override fun update(expense: Expense) {

    }

    override fun delete(expense: Expense) {

    }
}
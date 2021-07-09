package com.upreality.car.expenses.domain.usecases

import com.upreality.car.expenses.domain.IExpensesRepository
import com.upreality.car.expenses.domain.model.ExpenseFilter
import com.upreality.car.expenses.domain.model.expence.Expense
import javax.inject.Inject

class ExpensesInteractorImpl @Inject constructor(
    private val repository: IExpensesRepository
) : IExpensesInteractor {

    override fun createExpense(expense: Expense) = repository.create(expense)

    override fun getExpensesFlow(filter: ExpenseFilter) = repository.get(filter)

    override fun deleteExpense(expense: Expense) = repository.delete(expense)

    override fun updateExpense(expense: Expense) = repository.update(expense)

}
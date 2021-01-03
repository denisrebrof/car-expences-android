package com.upreality.car.expenses.domain

import com.upreality.car.expenses.domain.model.expence.Expense

interface IExpensesRepository {
    fun create(expense: Expense)
    fun get(filter: ExpenseFilter): List<Expense>
    fun update(expense: Expense)
    fun delete(expense: Expense)

}

enum class ExpenseFilter {
    All,
    Fines,
    Maintenance,
    Fuel
}
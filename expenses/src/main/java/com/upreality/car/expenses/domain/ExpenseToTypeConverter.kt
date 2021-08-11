package com.upreality.car.expenses.domain

import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.domain.model.expence.Expense
import kotlin.reflect.KClass

object ExpenseToTypeConverter {
    fun toType(expense: Expense): ExpenseType {
        return when (expense) {
            is Expense.Fuel -> ExpenseType.Fuel
            is Expense.Fine -> ExpenseType.Fines
            is Expense.Maintenance -> ExpenseType.Maintenance
        }
    }

    fun toType(clazz: KClass<out Expense>): ExpenseType {
        return when (clazz) {
            Expense.Fuel::class -> ExpenseType.Fuel
            Expense.Fine::class -> ExpenseType.Fines
            Expense.Maintenance::class -> ExpenseType.Maintenance
            else -> ExpenseType.Fuel
        }
    }
}
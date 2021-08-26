package com.upreality.car.expenses.data.shared.converters

import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.domain.model.FinesCategories

object ExpenseTypeConverter {
    fun toId(type: ExpenseType) = when (type) {
        ExpenseType.Fines -> 1
        ExpenseType.Maintenance -> 2
        ExpenseType.Fuel -> 3
    }

    fun fromId(id: Int) = when (id) {
        1 -> ExpenseType.Fines
        2 -> ExpenseType.Maintenance
        3 -> ExpenseType.Fuel
        else -> ExpenseType.Fuel
    }
}
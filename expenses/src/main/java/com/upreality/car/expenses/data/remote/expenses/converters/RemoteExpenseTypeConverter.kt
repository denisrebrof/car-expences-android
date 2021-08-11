package com.upreality.car.expenses.data.remote.expenses.converters

import com.upreality.car.expenses.data.shared.model.ExpenseType

object RemoteExpenseTypeConverter {

    fun toExpenseType(typeId: Int): ExpenseType {
        return when (typeId) {
            1 -> ExpenseType.Fines
            2 -> ExpenseType.Fuel
            else -> ExpenseType.Maintenance
        }
    }

    fun toExpenseTypeId(type: ExpenseType): Int {
        return when (type) {
            ExpenseType.Fines -> 1
            ExpenseType.Fuel -> 2
            ExpenseType.Maintenance -> 3
        }
    }
}
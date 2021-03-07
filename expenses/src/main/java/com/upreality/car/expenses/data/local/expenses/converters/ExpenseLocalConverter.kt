package com.upreality.car.expenses.data.local.expenses.converters

import com.upreality.car.expenses.data.local.expenses.model.ExpenseLocal
import com.upreality.car.expenses.domain.model.expence.Expense

object ExpenseLocalConverter {
    fun toExpense(dataModel: ExpenseLocal): Expense {
        return when (dataModel) {
            is ExpenseLocal.Fine -> Expense.Fine(
                dataModel.date,
                dataModel.cost,
                dataModel.type
            )
            is ExpenseLocal.Fuel -> Expense.Fuel(
                dataModel.date,
                dataModel.cost,
                dataModel.liters,
                dataModel.mileage,
            )
            is ExpenseLocal.Maintenance -> Expense.Maintenance(
                dataModel.date,
                dataModel.cost,
                dataModel.type,
                dataModel.mileage,
            )
        }
    }

    fun fromExpense(domainModel: Expense): ExpenseLocal {
        return when (domainModel) {
            is Expense.Fine -> ExpenseLocal.Fine(
                domainModel.date,
                domainModel.cost,
                domainModel.type
            )
            is Expense.Fuel -> ExpenseLocal.Fuel(
                domainModel.date,
                domainModel.cost,
                domainModel.liters,
                domainModel.mileage,
            )
            is Expense.Maintenance -> ExpenseLocal.Maintenance(
                domainModel.date,
                domainModel.cost,
                domainModel.type,
                domainModel.mileage,
            )
        }
    }
}
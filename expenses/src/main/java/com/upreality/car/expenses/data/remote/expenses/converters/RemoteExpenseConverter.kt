package com.upreality.car.expenses.data.remote.expenses.converters

import com.upreality.car.expenses.data.remote.expenses.model.ExpenseFirestore
import com.upreality.car.expenses.domain.model.expence.Expense

object RemoteExpenseConverter {
    fun toExpense(dataModel: ExpenseFirestore): Expense {
        return when (dataModel) {
            is ExpenseFirestore.Fine -> Expense.Fine(
                dataModel.date,
                dataModel.cost,
                dataModel.type
            )
            is ExpenseFirestore.Fuel -> Expense.Fuel(
                dataModel.date,
                dataModel.cost,
                dataModel.liters,
                dataModel.mileage,
            )
            is ExpenseFirestore.Maintenance -> Expense.Maintenance(
                dataModel.date,
                dataModel.cost,
                dataModel.type,
                dataModel.mileage,
            )
        }
    }

    fun fromExpense(domainModel: Expense): ExpenseFirestore {
        return when (domainModel) {
            is Expense.Fine -> ExpenseFirestore.Fine(
                domainModel.date,
                domainModel.cost,
                domainModel.type
            )
            is Expense.Fuel -> ExpenseFirestore.Fuel(
                domainModel.date,
                domainModel.cost,
                domainModel.liters,
                domainModel.mileage,
            )
            is Expense.Maintenance -> ExpenseFirestore.Maintenance(
                domainModel.date,
                domainModel.cost,
                domainModel.type,
                domainModel.mileage,
            )
        }
    }
}
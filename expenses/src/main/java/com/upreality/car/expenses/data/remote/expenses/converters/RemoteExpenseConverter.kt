package com.upreality.car.expenses.data.remote.expenses.converters

import com.upreality.car.expenses.data.remote.expenses.model.ExpenseRemote
import com.upreality.car.expenses.domain.model.expence.Expense

object RemoteExpenseConverter {
    fun toExpense(dataModel: ExpenseRemote): Expense {
        return when (dataModel) {
            is ExpenseRemote.Fine -> Expense.Fine(
                dataModel.date,
                dataModel.cost,
                dataModel.type
            )
            is ExpenseRemote.Fuel -> Expense.Fuel(
                dataModel.date,
                dataModel.cost,
                dataModel.liters,
                dataModel.mileage,
            )
            is ExpenseRemote.Maintenance -> Expense.Maintenance(
                dataModel.date,
                dataModel.cost,
                dataModel.type,
                dataModel.mileage,
            )
        }
    }

    fun fromExpense(domainModel: Expense, remoteId: String = String()): ExpenseRemote {
        return when (domainModel) {
            is Expense.Fine -> ExpenseRemote.Fine(
                domainModel.date,
                domainModel.cost,
                domainModel.type
            )
            is Expense.Fuel -> ExpenseRemote.Fuel(
                domainModel.date,
                domainModel.cost,
                domainModel.liters,
                domainModel.mileage,
            )
            is Expense.Maintenance -> ExpenseRemote.Maintenance(
                domainModel.date,
                domainModel.cost,
                domainModel.type,
                domainModel.mileage,
            )
        }.also { it.id = remoteId }
    }
}
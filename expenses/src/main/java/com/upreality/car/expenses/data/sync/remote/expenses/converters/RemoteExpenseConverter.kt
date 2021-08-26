package com.upreality.car.expenses.data.sync.remote.expenses.converters

import com.upreality.car.expenses.data.sync.remote.expenses.model.ExpenseRemote
import com.upreality.car.expenses.domain.model.expence.Expense
import data.OptionalValueConverter

object RemoteExpenseConverter {

    private val optionalConverter = OptionalValueConverter(-1f)

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
                optionalConverter.toOptional(dataModel.liters),
                optionalConverter.toOptional(dataModel.mileage),
            )
            is ExpenseRemote.Maintenance -> Expense.Maintenance(
                dataModel.date,
                dataModel.cost,
                dataModel.type,
                optionalConverter.toOptional(dataModel.mileage),
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
                optionalConverter.toValue(domainModel.fuelAmount),
                optionalConverter.toValue(domainModel.mileage),
            )
            is Expense.Maintenance -> ExpenseRemote.Maintenance(
                domainModel.date,
                domainModel.cost,
                domainModel.type,
                optionalConverter.toValue(domainModel.mileage),
            )
        }.also { it.id = remoteId }
    }
}
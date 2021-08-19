package com.upreality.car.expenses.data.local.room.expenses.converters

import com.upreality.car.expenses.data.local.room.expenses.model.ExpenseRoom
import com.upreality.car.expenses.domain.model.expence.Expense
import data.OptionalValueConverter

object RoomExpenseConverter {

    private val optionalConverter = OptionalValueConverter(-1f)

    fun toExpense(dataModel: ExpenseRoom): Expense {
        return when (dataModel) {
            is ExpenseRoom.Fine -> Expense.Fine(
                dataModel.date,
                dataModel.cost,
                dataModel.type
            )
            is ExpenseRoom.Fuel -> Expense.Fuel(
                dataModel.date,
                dataModel.cost,
                optionalConverter.toOptional(dataModel.liters),
                optionalConverter.toOptional(dataModel.mileage),
            )
            is ExpenseRoom.Maintenance -> Expense.Maintenance(
                dataModel.date,
                dataModel.cost,
                dataModel.type,
                optionalConverter.toOptional(dataModel.mileage),
            )
        }.apply { id = dataModel.id }
    }

    fun fromExpense(domainModel: Expense): ExpenseRoom {
        return when (domainModel) {
            is Expense.Fine -> ExpenseRoom.Fine(
                domainModel.date,
                domainModel.cost,
                domainModel.type
            )
            is Expense.Fuel -> ExpenseRoom.Fuel(
                domainModel.date,
                domainModel.cost,
                optionalConverter.toValue(domainModel.fuelAmount),
                optionalConverter.toValue(domainModel.mileage),
            )
            is Expense.Maintenance -> ExpenseRoom.Maintenance(
                domainModel.date,
                domainModel.cost,
                domainModel.type,
                optionalConverter.toValue(domainModel.mileage),
            )
        }.apply { id = domainModel.id }
    }
}
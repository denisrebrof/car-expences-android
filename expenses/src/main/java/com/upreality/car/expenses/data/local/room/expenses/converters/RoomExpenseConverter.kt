package com.upreality.car.expenses.data.local.room.expenses.converters

import com.upreality.car.expenses.data.local.room.expenses.model.ExpenseRoom
import com.upreality.car.expenses.domain.model.expence.Expense

object RoomExpenseConverter {
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
                dataModel.liters,
                dataModel.mileage,
            )
            is ExpenseRoom.Maintenance -> Expense.Maintenance(
                dataModel.date,
                dataModel.cost,
                dataModel.type,
                dataModel.mileage,
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
                domainModel.liters,
                domainModel.mileage,
            )
            is Expense.Maintenance -> ExpenseRoom.Maintenance(
                domainModel.date,
                domainModel.cost,
                domainModel.type,
                domainModel.mileage,
            )
        }.apply { id = domainModel.id }
    }
}
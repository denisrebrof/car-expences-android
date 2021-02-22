package com.upreality.car.expenses.data.local.expenses.converters

import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.data.local.expenses.model.entities.ExpenseDetails
import com.upreality.car.expenses.data.local.expenses.model.entities.ExpenseEntity
import com.upreality.car.expenses.domain.model.expence.Expense

class ExpenseConverter {

    fun getExpenseType(domainModel: Expense): ExpenseType {
        return when (domainModel) {
            is Expense.Fuel -> ExpenseType.Fuel
            is Expense.Fine -> ExpenseType.Fines
            is Expense.Maintenance -> ExpenseType.Maintenance
        }
    }

    fun toExpenseEntity(domainModel: Expense, detailsId: Long): ExpenseEntity {
        return ExpenseEntity(
            domainModel.id,
            domainModel.date,
            domainModel.cost,
            getExpenseType(domainModel),
            detailsId
        )
    }

    fun toExpenseDetails(domainModel: Expense, id: Long): ExpenseDetails {
        return when (getExpenseType(domainModel)) {
            ExpenseType.Fuel -> {
                val fuelExpense = domainModel as Expense.Fuel
                ExpenseDetails.ExpenseFuelDetails(
                    id,
                    fuelExpense.liters,
                    fuelExpense.mileage
                )
            }
            ExpenseType.Fines -> {
                val finesExpense = domainModel as Expense.Fine
                ExpenseDetails.ExpenseFinesDetails(id, finesExpense.type)
            }
            ExpenseType.Maintenance -> {
                val finesExpense = domainModel as Expense.Maintenance
                ExpenseDetails.ExpenseMaintenanceDetails(
                    id,
                    finesExpense.type,
                    finesExpense.mileage
                )
            }
        }
    }

    fun toExpense(entity: ExpenseEntity, expenseDetails: ExpenseDetails): Expense {
        return when (entity.type) {
            ExpenseType.Fines -> {
                val details = expenseDetails as ExpenseDetails.ExpenseFinesDetails
                Expense.Fine(entity.date, entity.cost, details.type)
            }
            ExpenseType.Fuel -> {
                val details = expenseDetails as ExpenseDetails.ExpenseFuelDetails
                Expense.Fuel(entity.date, entity.cost, details.liters, details.mileage)
            }
            ExpenseType.Maintenance -> {
                val details = expenseDetails as ExpenseDetails.ExpenseMaintenanceDetails
                Expense.Maintenance(entity.date, entity.cost, details.type, details.mileage)
            }
        }.apply {
            id = entity.id
        }
    }
}
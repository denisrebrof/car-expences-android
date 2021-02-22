package com.upreality.car.expenses.data.remote.firestore.converters

import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.data.remote.firestore.model.entities.ExpenseDetailsFirestore
import com.upreality.car.expenses.data.remote.firestore.model.entities.ExpenseEntityFirestore
import com.upreality.car.expenses.domain.model.expence.Expense

object RemoteExpenseConverter {

    fun getExpenseType(domainModel: Expense): ExpenseType {
        return when (domainModel) {
            is Expense.Fuel -> ExpenseType.Fuel
            is Expense.Fine -> ExpenseType.Fines
            is Expense.Maintenance -> ExpenseType.Maintenance
        }
    }

    fun toExpenseEntity(domainModel: Expense, detailsId: Long): ExpenseEntityFirestore {
        val date = DateConverter.toTime(domainModel.date)
        val type = getExpenseType(domainModel)
        return ExpenseEntityFirestore(
            String(), // empty
            date,
            domainModel.cost,
            RemoteExpenseTypeConverter.toExpenseTypeId(type),
            detailsId
        )
    }

    fun toExpenseDetails(domainModel: Expense, id: String): ExpenseDetailsFirestore {
        return when (getExpenseType(domainModel)) {
            ExpenseType.Fuel -> {
                val fuelExpense = domainModel as Expense.Fuel
                ExpenseDetailsFirestore.ExpenseFuelDetails(
                    id,
                    fuelExpense.liters,
                    fuelExpense.mileage
                )
            }
            ExpenseType.Fines -> {
                val finesExpense = domainModel as Expense.Fine
                ExpenseDetailsFirestore.ExpenseFinesDetails(id, finesExpense.type)
            }
            ExpenseType.Maintenance -> {
                val finesExpense = domainModel as Expense.Maintenance
                ExpenseDetailsFirestore.ExpenseMaintenanceDetails(
                    id,
                    finesExpense.type,
                    finesExpense.mileage
                )
            }
        }
    }

    fun toExpense(
        entity: ExpenseEntityFirestore,
        expenseDetails: ExpenseDetailsFirestore
    ): Expense {
        val type = RemoteExpenseTypeConverter.toExpenseType(entity.type)
        val date = DateConverter.toDate(entity.date)
        return when (type) {
            ExpenseType.Fines -> {
                val details = expenseDetails as ExpenseDetailsFirestore.ExpenseFinesDetails
                Expense.Fine(date, entity.cost, details.type)
            }
            ExpenseType.Fuel -> {
                val details = expenseDetails as ExpenseDetailsFirestore.ExpenseFuelDetails
                Expense.Fuel(date, entity.cost, details.liters, details.mileage)
            }
            ExpenseType.Maintenance -> {
                val details = expenseDetails as ExpenseDetailsFirestore.ExpenseMaintenanceDetails
                Expense.Maintenance(date, entity.cost, details.type, details.mileage)
            }
        }
        //missed id
    }
}


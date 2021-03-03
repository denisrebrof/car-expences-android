package com.upreality.car.expenses.data.remote.expenses.converters

import com.upreality.car.expenses.data.remote.expenses.model.ExpenseFirestore
import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.data.remote.expenses.model.entities.ExpenseDetailsFirestore
import com.upreality.car.expenses.data.remote.expenses.model.entities.ExpenseEntityFirestore

object RemoteExpenseConverter {

    fun getExpenseType(remoteModel: ExpenseFirestore): ExpenseType {
        return when (remoteModel) {
            is ExpenseFirestore.Fuel -> ExpenseType.Fuel
            is ExpenseFirestore.Fine -> ExpenseType.Fines
            is ExpenseFirestore.Maintenance -> ExpenseType.Maintenance
        }
    }

    fun toExpenseEntity(remoteModel: ExpenseFirestore, detailsId: String): ExpenseEntityFirestore {
        val date = DateConverter.toTime(remoteModel.date)
        val type = getExpenseType(remoteModel)
        return ExpenseEntityFirestore(
            String(), // empty
            date,
            remoteModel.cost,
            RemoteExpenseTypeConverter.toExpenseTypeId(type),
            detailsId
        )
    }

    fun toExpenseDetails(remoteModel: ExpenseFirestore, id: String): ExpenseDetailsFirestore {
        return when (getExpenseType(remoteModel)) {
            ExpenseType.Fuel -> {
                val fuelExpense = remoteModel as ExpenseFirestore.Fuel
                ExpenseDetailsFirestore.ExpenseFuelDetails(
                    id,
                    fuelExpense.liters,
                    fuelExpense.mileage
                )
            }
            ExpenseType.Fines -> {
                val finesExpense = remoteModel as ExpenseFirestore.Fine
                ExpenseDetailsFirestore.ExpenseFinesDetails(id, finesExpense.type)
            }
            ExpenseType.Maintenance -> {
                val finesExpense = remoteModel as ExpenseFirestore.Maintenance
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
    ): ExpenseFirestore {
        val type = RemoteExpenseTypeConverter.toExpenseType(entity.type)
        val date = DateConverter.toDate(entity.date)
        return when (type) {
            ExpenseType.Fines -> {
                val details = expenseDetails as ExpenseDetailsFirestore.ExpenseFinesDetails
                ExpenseFirestore.Fine(date, entity.cost, details.type)
            }
            ExpenseType.Fuel -> {
                val details = expenseDetails as ExpenseDetailsFirestore.ExpenseFuelDetails
                ExpenseFirestore.Fuel(date, entity.cost, details.liters, details.mileage)
            }
            ExpenseType.Maintenance -> {
                val details = expenseDetails as ExpenseDetailsFirestore.ExpenseMaintenanceDetails
                ExpenseFirestore.Maintenance(date, entity.cost, details.type, details.mileage)
            }
        }
        //missed id
    }
}


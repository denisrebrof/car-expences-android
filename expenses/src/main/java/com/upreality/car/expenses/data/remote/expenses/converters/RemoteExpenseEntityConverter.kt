package com.upreality.car.expenses.data.remote.expenses.converters

import com.upreality.car.expenses.data.remote.expenses.model.ExpenseRemote
import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.data.remote.expenses.model.entities.ExpenseDetailsRemote
import com.upreality.car.expenses.data.remote.expenses.model.entities.ExpenseEntityRemote

object RemoteExpenseEntityConverter {

    private fun getExpenseType(remoteModel: ExpenseRemote): ExpenseType {
        return when (remoteModel) {
            is ExpenseRemote.Fuel -> ExpenseType.Fuel
            is ExpenseRemote.Fine -> ExpenseType.Fines
            is ExpenseRemote.Maintenance -> ExpenseType.Maintenance
        }
    }

    fun toExpenseEntity(remoteModel: ExpenseRemote, detailsId: String): ExpenseEntityRemote {
        val date = DateConverter.toTime(remoteModel.date)
        val type = getExpenseType(remoteModel)
        return ExpenseEntityRemote(
            String(), // empty
            date,
            remoteModel.cost,
            RemoteExpenseTypeConverter.toExpenseTypeId(type),
            detailsId
        )
    }

    fun toExpenseDetails(remoteModel: ExpenseRemote, id: String): ExpenseDetailsRemote {
        return when (getExpenseType(remoteModel)) {
            ExpenseType.Fuel -> {
                val fuelExpense = remoteModel as ExpenseRemote.Fuel
                ExpenseDetailsRemote.ExpenseFuelDetails(
                    id,
                    fuelExpense.liters,
                    fuelExpense.mileage
                )
            }
            ExpenseType.Fines -> {
                val finesExpense = remoteModel as ExpenseRemote.Fine
                ExpenseDetailsRemote.ExpenseFinesDetails(id, finesExpense.type)
            }
            ExpenseType.Maintenance -> {
                val finesExpense = remoteModel as ExpenseRemote.Maintenance
                ExpenseDetailsRemote.ExpenseMaintenanceDetails(
                    id,
                    finesExpense.type,
                    finesExpense.mileage
                )
            }
        }
    }

    fun toExpense(
        entity: ExpenseEntityRemote,
        expenseDetails: ExpenseDetailsRemote
    ): ExpenseRemote {
        val type = RemoteExpenseTypeConverter.toExpenseType(entity.type)
        val date = DateConverter.toDate(entity.date)
        return when (type) {
            ExpenseType.Fines -> {
                val details = expenseDetails as ExpenseDetailsRemote.ExpenseFinesDetails
                ExpenseRemote.Fine(date, entity.cost, details.type)
            }
            ExpenseType.Fuel -> {
                val details = expenseDetails as ExpenseDetailsRemote.ExpenseFuelDetails
                ExpenseRemote.Fuel(date, entity.cost, details.liters, details.mileage)
            }
            ExpenseType.Maintenance -> {
                val details = expenseDetails as ExpenseDetailsRemote.ExpenseMaintenanceDetails
                ExpenseRemote.Maintenance(date, entity.cost, details.type, details.mileage)
            }
        }
        //missed id
    }
}


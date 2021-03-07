package com.upreality.car.expenses.data.remote.expenses.converters

import com.upreality.car.expenses.data.remote.expenses.model.ExpenseRemote
import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.data.remote.expenses.model.entities.ExpenseRemoteDetailsEntity
import com.upreality.car.expenses.data.remote.expenses.model.entities.ExpenseRemoteEntity

object RemoteExpenseEntityConverter {

    private fun getExpenseType(remoteModel: ExpenseRemote): ExpenseType {
        return when (remoteModel) {
            is ExpenseRemote.Fuel -> ExpenseType.Fuel
            is ExpenseRemote.Fine -> ExpenseType.Fines
            is ExpenseRemote.Maintenance -> ExpenseType.Maintenance
        }
    }

    fun toExpenseEntity(remoteModel: ExpenseRemote, detailsId: String): ExpenseRemoteEntity {
        val date = DateConverter.toTime(remoteModel.date)
        val type = getExpenseType(remoteModel)
        return ExpenseRemoteEntity(
            String(), // empty
            date,
            remoteModel.cost,
            RemoteExpenseTypeConverter.toExpenseTypeId(type),
            detailsId
        )
    }

    fun toExpenseDetails(remoteModel: ExpenseRemote, id: String): ExpenseRemoteDetailsEntity {
        return when (getExpenseType(remoteModel)) {
            ExpenseType.Fuel -> {
                val fuelExpense = remoteModel as ExpenseRemote.Fuel
                ExpenseRemoteDetailsEntity.ExpenseFuelDetails(
                    id,
                    fuelExpense.liters,
                    fuelExpense.mileage
                )
            }
            ExpenseType.Fines -> {
                val finesExpense = remoteModel as ExpenseRemote.Fine
                ExpenseRemoteDetailsEntity.ExpenseFinesDetails(id, finesExpense.type)
            }
            ExpenseType.Maintenance -> {
                val finesExpense = remoteModel as ExpenseRemote.Maintenance
                ExpenseRemoteDetailsEntity.ExpenseMaintenanceDetails(
                    id,
                    finesExpense.type,
                    finesExpense.mileage
                )
            }
        }
    }

    fun toExpense(
        entity: ExpenseRemoteEntity,
        expenseDetails: ExpenseRemoteDetailsEntity
    ): ExpenseRemote {
        val type = RemoteExpenseTypeConverter.toExpenseType(entity.type)
        val date = DateConverter.toDate(entity.date)
        return when (type) {
            ExpenseType.Fines -> {
                val details = expenseDetails as ExpenseRemoteDetailsEntity.ExpenseFinesDetails
                ExpenseRemote.Fine(date, entity.cost, details.type)
            }
            ExpenseType.Fuel -> {
                val details = expenseDetails as ExpenseRemoteDetailsEntity.ExpenseFuelDetails
                ExpenseRemote.Fuel(date, entity.cost, details.liters, details.mileage)
            }
            ExpenseType.Maintenance -> {
                val details = expenseDetails as ExpenseRemoteDetailsEntity.ExpenseMaintenanceDetails
                ExpenseRemote.Maintenance(date, entity.cost, details.type, details.mileage)
            }
        }
        //missed id
    }
}


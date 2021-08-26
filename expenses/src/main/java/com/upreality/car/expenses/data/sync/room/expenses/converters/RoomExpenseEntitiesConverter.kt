package com.upreality.car.expenses.data.sync.room.expenses.converters

import com.upreality.car.expenses.data.sync.room.expenses.model.ExpenseRoom
import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.data.sync.room.expenses.model.entities.ExpenseDetails
import com.upreality.car.expenses.data.sync.room.expenses.model.entities.ExpenseEntity

object RoomExpenseEntitiesConverter {

    fun getExpenseType(domainModel: ExpenseRoom): ExpenseType {
        return when (domainModel) {
            is ExpenseRoom.Fuel -> ExpenseType.Fuel
            is ExpenseRoom.Fine -> ExpenseType.Fines
            is ExpenseRoom.Maintenance -> ExpenseType.Maintenance
        }
    }

    fun toExpenseEntity(dataModel: ExpenseRoom, detailsId: Long): ExpenseEntity {
        return ExpenseEntity(
            dataModel.id,
            dataModel.date,
            dataModel.cost,
            getExpenseType(dataModel),
            detailsId
        )
    }

    fun toExpenseDetails(dataModel: ExpenseRoom, id: Long): ExpenseDetails {
        return when (getExpenseType(dataModel)) {
            ExpenseType.Fuel -> {
                val fuelExpense = dataModel as ExpenseRoom.Fuel
                ExpenseDetails.ExpenseFuelDetails(
                    id,
                    fuelExpense.liters,
                    fuelExpense.mileage
                )
            }
            ExpenseType.Fines -> {
                val finesExpense = dataModel as ExpenseRoom.Fine
                ExpenseDetails.ExpenseFinesDetails(id, finesExpense.type)
            }
            ExpenseType.Maintenance -> {
                val finesExpense = dataModel as ExpenseRoom.Maintenance
                ExpenseDetails.ExpenseMaintenanceDetails(
                    id,
                    finesExpense.type,
                    finesExpense.mileage
                )
            }
        }
    }

    fun toExpense(entity: ExpenseEntity, expenseDetails: ExpenseDetails): ExpenseRoom {
        return when (entity.type) {
            ExpenseType.Fines -> {
                val details = expenseDetails as ExpenseDetails.ExpenseFinesDetails
                ExpenseRoom.Fine(entity.date, entity.cost, details.type)
            }
            ExpenseType.Fuel -> {
                val details = expenseDetails as ExpenseDetails.ExpenseFuelDetails
                ExpenseRoom.Fuel(entity.date, entity.cost, details.liters, details.mileage)
            }
            ExpenseType.Maintenance -> {
                val details = expenseDetails as ExpenseDetails.ExpenseMaintenanceDetails
                ExpenseRoom.Maintenance(entity.date, entity.cost, details.type, details.mileage)
            }
        }.apply {
            id = entity.id
        }
    }
}
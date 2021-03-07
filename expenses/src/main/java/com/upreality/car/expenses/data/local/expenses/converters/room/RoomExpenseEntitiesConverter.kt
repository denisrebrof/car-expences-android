package com.upreality.car.expenses.data.local.expenses.converters.room

import com.upreality.car.expenses.data.local.expenses.model.ExpenseLocal
import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.data.local.expenses.model.entities.ExpenseLocalDetailsEntity
import com.upreality.car.expenses.data.local.expenses.model.entities.ExpenseLocalEntity

object RoomExpenseEntitiesConverter {

    fun getExpenseType(domainModel: ExpenseLocal): ExpenseType {
        return when (domainModel) {
            is ExpenseLocal.Fuel -> ExpenseType.Fuel
            is ExpenseLocal.Fine -> ExpenseType.Fines
            is ExpenseLocal.Maintenance -> ExpenseType.Maintenance
        }
    }

    fun toExpenseEntity(dataModel: ExpenseLocal, detailsId: Long): ExpenseLocalEntity {
        return ExpenseLocalEntity(
            dataModel.id,
            dataModel.date,
            dataModel.cost,
            getExpenseType(dataModel),
            detailsId
        )
    }

    fun toExpenseDetails(dataModel: ExpenseLocal, id: Long): ExpenseLocalDetailsEntity {
        return when (getExpenseType(dataModel)) {
            ExpenseType.Fuel -> {
                val fuelExpense = dataModel as ExpenseLocal.Fuel
                ExpenseLocalDetailsEntity.ExpenseFuelDetails(
                    id,
                    fuelExpense.liters,
                    fuelExpense.mileage
                )
            }
            ExpenseType.Fines -> {
                val finesExpense = dataModel as ExpenseLocal.Fine
                ExpenseLocalDetailsEntity.ExpenseFinesDetails(id, finesExpense.type)
            }
            ExpenseType.Maintenance -> {
                val finesExpense = dataModel as ExpenseLocal.Maintenance
                ExpenseLocalDetailsEntity.ExpenseMaintenanceDetails(
                    id,
                    finesExpense.type,
                    finesExpense.mileage
                )
            }
        }
    }

    fun toExpense(entity: ExpenseLocalEntity, expenseDetails: ExpenseLocalDetailsEntity): ExpenseLocal {
        return when (entity.type) {
            ExpenseType.Fines -> {
                val details = expenseDetails as ExpenseLocalDetailsEntity.ExpenseFinesDetails
                ExpenseLocal.Fine(entity.date, entity.cost, details.type)
            }
            ExpenseType.Fuel -> {
                val details = expenseDetails as ExpenseLocalDetailsEntity.ExpenseFuelDetails
                ExpenseLocal.Fuel(entity.date, entity.cost, details.liters, details.mileage)
            }
            ExpenseType.Maintenance -> {
                val details = expenseDetails as ExpenseLocalDetailsEntity.ExpenseMaintenanceDetails
                ExpenseLocal.Maintenance(entity.date, entity.cost, details.type, details.mileage)
            }
        }.apply {
            id = entity.id
        }
    }
}
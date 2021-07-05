package com.upreality.car.expenses.data.realm.model

import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.domain.model.expence.Expense
import io.realm.Realm

object ExpenseRealmConverter {
    fun fromDomain(expense: Expense, realm: Realm): ExpenseRealm {
        val dataModel = realm
            .createObject(ExpenseRealm::class.java, expense.id)
            .apply {
                cost = expense.cost
                date = expense.date
            }
        when (expense) {
            is Expense.Fine -> {
                dataModel.type = ExpenseType.Fines
                dataModel.fineType = expense.type
            }
            is Expense.Fuel -> {
                dataModel.type = ExpenseType.Fuel
                dataModel.fuelLiters = expense.liters
                dataModel.mileage = expense.mileage
            }
            is Expense.Maintenance -> {
                dataModel.type = ExpenseType.Maintenance
                dataModel.mileage = expense.mileage
                dataModel.maintenanceType = expense.type
            }
        }
        return dataModel
    }

    fun toDomain(dataModel: ExpenseRealm): Expense {
        return when (dataModel.type) {
            ExpenseType.Fines -> Expense.Fine(
                dataModel.date,
                dataModel.cost,
                dataModel.fineType,
            )
            ExpenseType.Fuel -> Expense.Fuel(
                dataModel.date,
                dataModel.cost,
                dataModel.fuelLiters,
                dataModel.mileage,
            )
            ExpenseType.Maintenance -> Expense.Maintenance(
                dataModel.date,
                dataModel.cost,
                dataModel.maintenanceType,
                dataModel.mileage,
            )
        }.apply { id = dataModel._id }
    }
}
package com.upreality.car.expenses.data.backend.model

import com.upreality.car.expenses.data.shared.converters.DateConverter
import com.upreality.car.expenses.data.shared.converters.ExpenseTypeConverter
import com.upreality.car.expenses.data.shared.converters.FinesCategoriesConverter
import com.upreality.car.expenses.data.shared.converters.MaintenanceTypeConverter
import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.domain.ExpenseToTypeConverter
import com.upreality.car.expenses.domain.model.FinesCategories
import com.upreality.car.expenses.domain.model.MaintenanceType
import com.upreality.car.expenses.domain.model.expence.Expense
import data.OptionalValueConverter
import domain.OptionalValue

object ExpenseBackendConverter {

    private val optionalConverter = OptionalValueConverter(-1f)

    private val defaultFineCategoryId = FinesCategories.Undefined
        .let(FinesCategoriesConverter::toId)

    private val defaultMaintenanceTypeId = MaintenanceType.Undefined
        .let(MaintenanceTypeConverter::toId)

    fun toExpense(model: ExpenseBackendModel): Expense {
        val type = ExpenseTypeConverter.fromId(model.typeId ?: ExpenseType.Fuel.id)
        val date = DateConverter.fromTimestamp(model.time ?: 0L)
        val cost = model.cost ?: 0f
        return when (type) {
            ExpenseType.Fines -> Expense.Fine(
                date = date,
                cost = cost,
                type = FinesCategoriesConverter.fromId(model.fineTypeId ?: defaultFineCategoryId)
            )
            ExpenseType.Fuel -> Expense.Fuel(
                date = date,
                cost = cost,
                fuelAmount = model.fuelAmount?.let(optionalConverter::toOptional)
                    ?: OptionalValue.Undefined,
                mileage = model.mileage?.let(optionalConverter::toOptional)
                    ?: OptionalValue.Undefined,
            )
            ExpenseType.Maintenance -> Expense.Maintenance(
                date = date,
                cost = cost,
                type = MaintenanceTypeConverter
                    .fromId(model.maintenanceTypeId ?: defaultMaintenanceTypeId),
                mileage = model.mileage?.let(optionalConverter::toOptional)
                    ?: OptionalValue.Undefined,
            )
        }.apply { id = model.id ?: 0L }
    }

    fun fromExpense(expense: Expense): ExpenseBackendModel {
        val typeId = ExpenseToTypeConverter.toType(expense).let(ExpenseTypeConverter::toId)
        val time = DateConverter.toTimestamp(expense.date)

        val mileage = (expense as? Expense.Fuel)?.mileage
            ?: (expense as? Expense.Maintenance)?.mileage
        val mileageValue = mileage?.let(optionalConverter::toValue)
            ?: optionalConverter.defaultValue

        val fuelAmount = (expense as? Expense.Fuel)?.fuelAmount
        val fuelAmountValue = fuelAmount?.let(optionalConverter::toValue)
            ?: optionalConverter.defaultValue

        val maintenanceType = (expense as? Expense.Maintenance)?.type
        val maintenanceTypeIdValue = maintenanceType?.let(MaintenanceTypeConverter::toId)
            ?: defaultMaintenanceTypeId

        val fineType = (expense as? Expense.Fine)?.type
        val fineTypeId = fineType?.let(FinesCategoriesConverter::toId) ?: defaultFineCategoryId

        return ExpenseBackendModel(
            expense.id,
            time = time,
            cost = expense.cost,
            typeId = typeId,
            mileage = mileageValue,
            fuelAmount = fuelAmountValue,
            maintenanceTypeId = maintenanceTypeIdValue,
            fineTypeId = fineTypeId
        )
    }
}
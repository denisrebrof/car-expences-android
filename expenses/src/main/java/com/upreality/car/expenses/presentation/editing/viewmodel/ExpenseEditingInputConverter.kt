package com.upreality.car.expenses.presentation.editing.viewmodel

import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.domain.model.FinesCategories
import com.upreality.car.expenses.domain.model.MaintenanceType
import com.upreality.car.expenses.domain.model.expence.Expense
import presentation.ValidationResult
import java.util.*
import javax.inject.Inject

class ExpenseEditingInputConverter @Inject constructor() {
    fun toExpense(
        costState: ValidationResult<*, Float>,
        dateState: ValidationResult<*, Date>,
        typeState: ValidationResult<*, ExpenseType>,
        litersState: ValidationResult<*, Float>,
        mileageState: ValidationResult<*, Float>,
        fineTypeState: ValidationResult<*, FinesCategories>,
        maintenanceTypeState: ValidationResult<*, MaintenanceType>
    ): Result<Expense> = kotlin.runCatching {
        return@runCatching when (typeState.validValueOrNull()) {
            ExpenseType.Fines -> Expense.Fine(
                date = dateState.requireValid(),
                cost = costState.requireValid(),
                type = fineTypeState.requireValid()
            )
            ExpenseType.Fuel -> Expense.Fuel(
                date = dateState.requireValid(),
                cost = costState.requireValid(),
                liters = litersState.requireValid(),
                mileage = mileageState.requireValid(),
            )
            ExpenseType.Maintenance -> Expense.Maintenance(
                date = dateState.requireValid(),
                cost = costState.requireValid(),
                mileage = mileageState.requireValid(),
                type = maintenanceTypeState.requireValid()
            )
            else -> null
        }!!
    }
}
package com.upreality.car.expenses.presentation

import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.domain.model.expence.Expense
import java.security.InvalidParameterException
import java.util.*

object ExpenseEditingInputStateConverter {

    fun toExpense(inputState: ExpenseEditingViewModel.ExpenseEditingViewState): Result<Expense> {
        val exception = InvalidParameterException("Invalid input")
        val failure = Result.failure<Expense>(exception)

        val cost = inputState.costState.validOrNull() ?: return failure
        val type = inputState.typeState.validOrNull() ?: return failure

        val expense = when (type.input) {
            ExpenseType.Fines -> {
                val fineCategory = inputState.fineTypeState.validOrNull() ?: return failure
                Expense.Fine(
                    date = Date(),
                    cost = cost.input?.toFloatOrNull() ?: return failure,
                    type = fineCategory.input ?: return failure
                )
            }
            ExpenseType.Fuel -> {
                val liters = inputState.litersState.validOrNull() ?: return failure
                val mileage = inputState.mileageState.validOrNull() ?: return failure
                Expense.Fuel(
                    date = Date(),
                    cost = cost.input?.toFloatOrNull() ?: return failure,
                    liters = liters.input?.toFloatOrNull() ?: return failure,
                    mileage = mileage.input?.toFloatOrNull() ?: return failure
                )
            }
            else -> null
        } ?: return failure
        return Result.success(expense)
    }

    fun getExpenseType(expense: Expense): ExpenseType {
        return when (expense) {
            is Expense.Fuel -> ExpenseType.Fuel
            is Expense.Fine -> ExpenseType.Fines
            is Expense.Maintenance -> ExpenseType.Maintenance
        }
    }
}
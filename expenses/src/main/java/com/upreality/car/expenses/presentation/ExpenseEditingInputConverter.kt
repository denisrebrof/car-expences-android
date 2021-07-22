package com.upreality.car.expenses.presentation

import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.domain.model.FinesCategories
import com.upreality.car.expenses.domain.model.expence.Expense
import domain.DateTimeInteractor
import presentation.InputState
import java.security.InvalidParameterException
import javax.inject.Inject
import com.upreality.car.expenses.presentation.ExpenseEditingViewModel.ExpenseEditingDateSelectorState as DateSelectorState

class ExpenseEditingInputConverter @Inject constructor(
    private val dateTimeInteractor: DateTimeInteractor
) {

    fun toExpense(
        costState: InputState<String>,
        dateState: InputState<DateSelectorState>,
        typeState: InputState<ExpenseType>,
        litersState: InputState<String>,
        mileageState: InputState<String>,
        fineTypeState: InputState<FinesCategories>,
    ): Result<Expense> {
        val exception = InvalidParameterException("Invalid input")
        val failure = Result.failure<Expense>(exception)

        val cost = costState.validOrNull()?.input?.toFloatOrNull() ?: return failure
        val type = typeState.validOrNull() ?: return failure
        val dateSelectorState = dateState.validOrNull() ?: return failure
        val date = when(dateSelectorState.inp!!){
            DateSelectorState.Today -> dateTimeInteractor.getToday()
            DateSelectorState.Yesterday -> dateTimeInteractor.getYesterday()
            is DateSelectorState.Custom -> (dateSelectorState.inp as DateSelectorState.Custom).date
        }

        val expense = when (type.input) {
            ExpenseType.Fines -> {
                val fineCategory = fineTypeState.validOrNull() ?: return failure
                Expense.Fine(
                    date = date,
                    cost = cost,
                    type = fineCategory.input ?: return failure
                )
            }
            ExpenseType.Fuel -> {
                val liters = litersState.validOrNull() ?: return failure
                val mileage = mileageState.validOrNull() ?: return failure
                Expense.Fuel(
                    date = date,
                    cost = cost,
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
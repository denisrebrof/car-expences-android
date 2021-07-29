package com.upreality.car.expenses.presentation.fitering

import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.presentation.fitering.ExpenseFilteringKeys.DateRange
import presentation.InputForm
import presentation.ValidationResult
import java.util.*

sealed class ExpenseFilteringKeys<in ValueType : Any, in OutType : Any> :
    InputForm.FieldKey<ValueType, OutType>() {
    object DateRange : ExpenseFilteringKeys<ExpenseFilteringDateRange, DateRange>()
    object Type : ExpenseFilteringKeys<ExpenseType, ExpenseType>()
}

data class ExpenseFilteringDateRange(
    val from: DateState = DateState.Undefined,
    val to: DateState = DateState.Undefined
) {
    sealed class DateState {
        object Undefined : DateState()
        data class Defined(val date: Date) : DateState()
    }
}

data class ExpenseEditingViewState(
    val dateRangeState: ValidationResult<ExpenseFilteringDateRange, DateRange>,
    val typeState: ValidationResult<ExpenseType, ExpenseType>,
)

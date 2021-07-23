package com.upreality.car.expenses.presentation.editing.viewmodel

import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.domain.model.FinesCategories
import presentation.InpForm
import presentation.ValidationResult
import java.util.*
import com.upreality.car.expenses.presentation.editing.viewmodel.ExpenseEditingDateInputValue as DateInputValue

sealed class ExpenseEditingKeys<in ValueType : Any, in OutType : Any> :
    InpForm.FieldKey<ValueType, OutType>() {
    object Cost : ExpenseEditingKeys<String, Float>()
    object SpendDate : ExpenseEditingKeys<DateInputValue, Date>()
    object Type : ExpenseEditingKeys<ExpenseType, ExpenseType>()
    object Liters : ExpenseEditingKeys<String, Float>()
    object Mileage : ExpenseEditingKeys<String, Float>()
    object FineType : ExpenseEditingKeys<FinesCategories, FinesCategories>()
}

sealed class ExpenseEditingAction {
    object Finish : ExpenseEditingAction()
    data class ShowDatePicker(
        val year: Int,
        val month: Int,
        val day: Int
    ) : ExpenseEditingAction()
}

sealed class ExpenseEditingIntent {
    object Close : ExpenseEditingIntent()
    object Submit : ExpenseEditingIntent()
    object Delete : ExpenseEditingIntent()
    data class SelectDate(val type: ExpenseEditingDateSelectionType) : ExpenseEditingIntent()
}

enum class ExpenseEditingDateSelectionType {
    Today,
    Yesterday,
    Custom
}


data class ExpenseEditingViewState(
    val isValid: Boolean,
    val newExpenseCreation: Boolean,
    val costState: ValidationResult<String, Float>,
    val dateState: ValidationResult<DateInputValue, Date>,
    val typeState: ValidationResult<ExpenseType, ExpenseType>,
    val litersState: ValidationResult<String, Float>,
    val mileageState: ValidationResult<String, Float>,
    val fineTypeState: ValidationResult<FinesCategories, FinesCategories>,
)

sealed class ExpenseEditingDateInputValue {
    object Today : DateInputValue()
    object Yesterday : DateInputValue()
    data class Custom(val date: Date) : DateInputValue()
}
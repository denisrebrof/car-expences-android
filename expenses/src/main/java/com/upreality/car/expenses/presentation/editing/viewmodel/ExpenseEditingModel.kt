package com.upreality.car.expenses.presentation.editing.viewmodel

import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.domain.model.FinesCategories
import presentation.InputForm
import presentation.InputState
import java.util.*
import kotlin.reflect.KClass

sealed class ExpenseEditingKeys<out ValueType : Any>(
    id: Int,
    type: KClass<ValueType>
) : InputForm.FieldKeys<ValueType>(id) {
    object Cost : ExpenseEditingKeys<String>(0, String::class)
    object SpendDate : ExpenseEditingKeys<ExpenseEditingDateInputValue>(
        1,
        ExpenseEditingDateInputValue::class
    )

    object Type : ExpenseEditingKeys<ExpenseType>(2, ExpenseType::class)
    object Liters : ExpenseEditingKeys<String>(3, String::class)
    object Mileage : ExpenseEditingKeys<String>(4, String::class)
    object FineType : ExpenseEditingKeys<FinesCategories>(5, FinesCategories::class)
}

sealed class ExpenseEditingAction {
    object Finish : ExpenseEditingAction()
    data class ShowDatePicker(
        val year: Int,
        val month: Int,
        val day: Int
    ) : ExpenseEditingAction()
//        data class SetupExpense(
//            val costState: String,
//            val typeState: ExpenseType,
//            val litersState: String,
//            val mileageState: String,
//            val fineTypeState: FinesCategories,
//        ) : ExpenseEditingAction()
}

sealed class ExpenseEditingIntent {
    data class FillForm<ValueType : Any>(
        val key: ExpenseEditingKeys<ValueType>,
        val value: ValueType,
        val type: KClass<ValueType>
    ) : ExpenseEditingIntent()

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
    val costState: InputState<String>,
    val dateState: InputState<ExpenseEditingDateInputValue>,
    val typeState: InputState<ExpenseType>,
    val litersState: InputState<String>,
    val mileageState: InputState<String>,
    val fineTypeState: InputState<FinesCategories>,
)

sealed class ExpenseEditingDateInputValue {
    object Today : ExpenseEditingDateInputValue()
    object Yesterday : ExpenseEditingDateInputValue()
    data class Custom(val date: Date) : ExpenseEditingDateInputValue()
}
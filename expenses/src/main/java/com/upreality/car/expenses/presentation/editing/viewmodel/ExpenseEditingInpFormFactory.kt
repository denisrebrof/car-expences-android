package com.upreality.car.expenses.presentation.editing.viewmodel

import com.upreality.car.expenses.presentation.editing.viewmodel.ExpenseEditingKeys.*
import domain.DateTimeInteractor
import presentation.InpForm
import presentation.ValidationResult
import java.util.*
import javax.inject.Inject
import com.upreality.car.expenses.presentation.editing.viewmodel.ExpenseEditingDateInputValue as DateInputValue

class ExpenseEditingInpFormFactory @Inject constructor(
    private val dateTimeInteractor: DateTimeInteractor
) {

    fun create(): InpForm {
        val numValidator = this::validatePositiveFloatInput
        val dateValidator = this::validateSpendDate
        return InpForm().apply {
            createField(Cost, numValidator)
            createField(SpendDate, dateValidator)
            createField(Type, InpForm.Companion::validateNotNull)
            createField(Liters, numValidator)
            createField(Mileage, numValidator)
            createField(FineType, InpForm.Companion::validateNotNull)
        }
    }

    private fun validateSpendDate(
        value: DateInputValue?
    ): ValidationResult<DateInputValue, Date> {
        if (value == null)
            return ValidationResult.Empty

        val date = when (value) {
            DateInputValue.Today -> dateTimeInteractor.getToday()
            DateInputValue.Yesterday -> dateTimeInteractor.getYesterday()
            is DateInputValue.Custom -> value.date
        }

        return ValidationResult.Valid(value, date)
    }

    private fun validatePositiveFloatInput(value: String?): ValidationResult<String, Float> {
        if (value.isNullOrEmpty()) return ValidationResult.Empty
        val floatInput = value.toFloatOrNull()
            ?: return ValidationResult.Invalid(value, "Invalid value")

        if (floatInput < 0f)
            return ValidationResult.Invalid(value, "Value less than zero")

        return ValidationResult.Valid(value, floatInput)
    }
}
package com.upreality.car.expenses.presentation.editing.viewmodel

import com.upreality.car.expenses.presentation.editing.viewmodel.ExpenseEditingKeys.*
import domain.DateTimeInteractor
import domain.OptionalValue
import presentation.InputForm
import presentation.ValidationResult
import java.util.*
import javax.inject.Inject
import com.upreality.car.expenses.presentation.editing.viewmodel.ExpenseEditingDateInputValue as DateInputValue

class ExpenseEditingInpFormFactory @Inject constructor(
    private val dateTimeInteractor: DateTimeInteractor
) {

    fun create(): InputForm {
        val numValidator = this::validatePositiveFloatInput
        val optionalValidator = this::validateOptionalPositiveFloatInput
        val dateValidator = this::validateSpendDate
        return InputForm().apply {
            createField(Cost, numValidator)
            createField(SpendDate, dateValidator)
            createField(Type, InputForm.Companion::validateNotNull)
            createField(Liters, optionalValidator, "")
            createField(Mileage, optionalValidator, "")
            createField(FineType, InputForm.Companion::validateNotNull)
            createField(Maintenance, InputForm.Companion::validateNotNull)
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

    private fun validateOptionalPositiveFloatInput(
        value: String?
    ): ValidationResult<String, OptionalValue<Float>> {
        if (value == null)
            return ValidationResult.Empty

        if (value.isEmpty() || value.isBlank())
            return ValidationResult.Valid(value, OptionalValue.Undefined)

        val floatInput = value.toFloatOrNull()
            ?: return ValidationResult.Invalid(value, "Invalid value")

        if (floatInput < 0f)
            return ValidationResult.Invalid(value, "Value less than zero")

        return ValidationResult.Valid(value, OptionalValue.Defined(floatInput))
    }
}
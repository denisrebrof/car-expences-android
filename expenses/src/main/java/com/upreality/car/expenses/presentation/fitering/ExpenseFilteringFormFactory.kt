package com.upreality.car.expenses.presentation.fitering

import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.domain.model.DateRange
import domain.DateTimeInteractor
import presentation.InputForm
import presentation.ValidationResult
import javax.inject.Inject

class ExpenseFilteringFormFactory @Inject constructor(
    private val dateTimeInteractor: DateTimeInteractor,
) {

    private var defaultRange: DateRange? = null
    private var defaultTypes: List<ExpenseType>? = ExpenseType.values().toList()

    fun setDefaultTypes(types: List<ExpenseType>): ExpenseFilteringFormFactory {
        this.defaultTypes = types
        return this
    }

    fun setDefaultRange(range: DateRange): ExpenseFilteringFormFactory {
        this.defaultRange = range
        return this
    }

    fun create() = InputForm().apply {
        val mask = defaultTypes?.let(::ExpenseFilteringTypeMask)
        createField(ExpenseFilteringKeys.Type, this@ExpenseFilteringFormFactory::validateSpendDate, mask)
        createField(ExpenseFilteringKeys.Range, InputForm.Companion::validateNotNull, defaultRange)
    }

    private fun validateSpendDate(
        value: ExpenseFilteringTypeMask?
    ): ValidationResult<ExpenseFilteringTypeMask, Set<ExpenseType>> {
        if (value == null)
            return ValidationResult.Empty

        val types = value.getFilteredTypes()
        return ValidationResult.Valid(value, types)
    }
}
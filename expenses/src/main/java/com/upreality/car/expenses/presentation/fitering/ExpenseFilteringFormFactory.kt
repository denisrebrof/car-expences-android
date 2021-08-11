package com.upreality.car.expenses.presentation.fitering

import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.domain.model.DateRange
import com.upreality.car.expenses.presentation.editing.viewmodel.ExpenseEditingDateInputValue
import com.upreality.car.expenses.presentation.fitering.ExpenseFilteringKeys.Range
import com.upreality.car.expenses.presentation.fitering.ExpenseFilteringKeys.Type
import domain.DateTimeInteractor
import presentation.InputForm
import presentation.ValidationResult
import java.util.*
import javax.inject.Inject

class ExpenseFilteringFormFactory @Inject constructor(
    private val dateTimeInteractor: DateTimeInteractor,
) {

    private var defaultSelection: DateRangeSelection? = null
    private var defaultTypes: List<ExpenseType>? = ExpenseType.values().toList()

    fun setDefaultTypes(types: List<ExpenseType>): ExpenseFilteringFormFactory {
        this.defaultTypes = types
        return this
    }

    fun setDefaultRange(selection: DateRangeSelection): ExpenseFilteringFormFactory {
        this.defaultSelection = selection
        return this
    }

    fun create() = InputForm().apply {
        val mask = defaultTypes?.let(::ExpenseFilteringTypeMask)
        createField(Type, this@ExpenseFilteringFormFactory::validateType, mask)
        createField(Range, this@ExpenseFilteringFormFactory::validateDateRange, defaultSelection)
    }

    private fun validateDateRange(
        value: DateRangeSelection?
    ): ValidationResult<DateRangeSelection, DateRange> {
        if (value == null)
            return ValidationResult.Empty

        val startDate = when (value) {
            DateRangeSelection.AllTime -> Date(0)
            DateRangeSelection.Month -> dateTimeInteractor.getTimeAgo(Calendar.MONTH, 1)
            DateRangeSelection.Season -> dateTimeInteractor.getTimeAgo(Calendar.MONTH, 3)
            DateRangeSelection.Week -> dateTimeInteractor.getTimeAgo(Calendar.WEEK_OF_MONTH, 1)
            DateRangeSelection.Year -> dateTimeInteractor.getTimeAgo(Calendar.YEAR, 1)
            is DateRangeSelection.CustomRange -> null
        }

        val dateRange = when (value) {
            is DateRangeSelection.CustomRange -> value.range
            else -> DateRange(startDate!!, dateTimeInteractor.getToday())
        }

        return ValidationResult.Valid(value, dateRange)
    }

    private fun validateType(
        value: ExpenseFilteringTypeMask?
    ): ValidationResult<ExpenseFilteringTypeMask, Set<ExpenseType>> {
        if (value == null)
            return ValidationResult.Empty

        val types = value.getFilteredTypes()
        return ValidationResult.Valid(value, types)
    }
}
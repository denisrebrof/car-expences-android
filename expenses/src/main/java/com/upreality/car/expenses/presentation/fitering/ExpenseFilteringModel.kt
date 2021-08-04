package com.upreality.car.expenses.presentation.fitering

import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.domain.model.DateRange
import com.upreality.car.expenses.domain.model.ExpenseFilter
import presentation.InputForm
import presentation.ValidationResult

sealed class ExpenseFilteringKeys<in ValueType : Any, in OutType : Any> :
    InputForm.FieldKey<ValueType, OutType>() {
    object Range : ExpenseFilteringKeys<DateRangeSelection, DateRange>()
    object Type : ExpenseFilteringKeys<ExpenseFilteringTypeMask, Set<ExpenseType>>()
}

class ExpenseFilteringTypeMask(
    availableTypes: List<ExpenseType> = listOf()
) {
    private val typesMap = mutableMapOf<ExpenseType, Boolean>()

    init {
        availableTypes.forEach { typesMap[it] = true }
    }

    fun getFilteredTypes() = typesMap.filter { (_, available) ->
        available
    }.keys

    fun setType(type: ExpenseType, available: Boolean) {
        typesMap[type] = available
    }
}

data class ExpenseFilteringViewState(
    val dateRangeState: ValidationResult<DateRangeSelection, DateRange>,
    val typeState: ValidationResult<ExpenseFilteringTypeMask, Set<ExpenseType>>,
)

sealed class ExpenseFilteringIntent {
    data class SetTypeFilter(
        val type: ExpenseType,
        val available: Boolean
    ) : ExpenseFilteringIntent()

    object ShowDateRange : ExpenseFilteringIntent()
    data class ApplyDateRange(val selection: DateRangeSelection) : ExpenseFilteringIntent()
    object DropFilters : ExpenseFilteringIntent()
}

sealed class ExpenseFilteringAction {
    data class ApplyFilters(val filters: List<ExpenseFilter>): ExpenseFilteringAction()
    data class ShowRangePicker(
        val fromTime: Long,
        val toTime: Long
    ) : ExpenseFilteringAction()
}

sealed class DateRangeSelection {
    data class CustomRange(val range: DateRange) : DateRangeSelection()
    object AllTime : DateRangeSelection()
    object Week : DateRangeSelection()
    object Month : DateRangeSelection()
    object Season : DateRangeSelection()
    object Year : DateRangeSelection()
}



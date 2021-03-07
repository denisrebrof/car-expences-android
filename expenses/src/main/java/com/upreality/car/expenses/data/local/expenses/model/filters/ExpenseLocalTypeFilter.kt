package com.upreality.car.expenses.data.local.expenses.model.filters

import com.upreality.car.expenses.data.local.expenses.converters.ExpenseTypeConverter
import com.upreality.car.expenses.data.shared.model.ExpenseType

class ExpenseLocalTypeFilter(type: ExpenseType) : ExpenseLocalColumnFilter("type") {

    private val converter = ExpenseTypeConverter()

    override val filter = converter.toId(type).toString()

}
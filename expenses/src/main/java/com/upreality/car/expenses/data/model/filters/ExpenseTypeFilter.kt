package com.upreality.car.expenses.data.model.filters

import com.upreality.car.expenses.data.converters.ExpenseTypeConverter
import com.upreality.car.expenses.data.model.ExpenseType

class ExpenseTypeFilter(type: ExpenseType) : ExpenseColumnFilter("type") {

    private val converter = ExpenseTypeConverter()

    override val filter = converter.toId(type).toString()

}
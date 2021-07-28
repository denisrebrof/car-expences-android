package com.upreality.car.expenses.data.local.room.expenses.model.filters

import com.upreality.car.common.data.database.IDatabaseFilter
import java.util.*

class ExpenseDateFilter(val from: Date, val to: Date): IDatabaseFilter {
    override fun getFilterExpression(): String {
        return "SELECT * FROM expenses WHERE date BETWEEN ${from.time} AND ${to.time} ORDER BY date"
    }
}
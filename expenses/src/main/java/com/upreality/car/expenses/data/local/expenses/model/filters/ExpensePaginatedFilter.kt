package com.upreality.car.expenses.data.local.expenses.model.filters

import com.upreality.car.common.data.database.IDatabaseFilter

abstract class ExpensePaginatedFilter(private val cursor: Long, private val length: Int) :
    IDatabaseFilter {

    abstract val orderColumn: String
    abstract val sortAscending: Boolean

    override fun getFilterExpression(): String {
        val compareOperator = if(sortAscending) '>' else '<'
        return "SELECT * FROM expenses WHERE $orderColumn$compareOperator$cursor ORDER BY orderColumn LIMIT $length"
    }
}
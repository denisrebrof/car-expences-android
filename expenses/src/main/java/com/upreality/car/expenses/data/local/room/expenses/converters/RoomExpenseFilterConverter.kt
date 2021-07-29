package com.upreality.car.expenses.data.local.room.expenses.converters

import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.domain.model.ExpenseFilter
import data.database.IDatabaseFilter
import domain.RequestPagingState

object RoomExpenseFilterConverter {

    private fun convert(filter: ExpenseFilter): String? {
        val getTypeId: (ExpenseType) -> Int = { type -> ExpenseTypeConverter().toId(type) }
        return when (filter) {
            ExpenseFilter.All -> null
            ExpenseFilter.Fines -> "type LIKE ${getTypeId(ExpenseType.Fines)}"
            ExpenseFilter.Maintenance -> "type LIKE ${getTypeId(ExpenseType.Maintenance)}"
            ExpenseFilter.Fuel -> "type LIKE ${getTypeId(ExpenseType.Fuel)}"
            is ExpenseFilter.Id -> "id LIKE ${filter.id}"
            is ExpenseFilter.DateRange -> "date BETWEEN ${filter.from.time} AND ${filter.to.time}"
        }
    }

    fun convert(
        filters: List<ExpenseFilter>,
        pagingState: RequestPagingState = RequestPagingState.Undefined
    ): IDatabaseFilter {
        var baseQuery = "SELECT * FROM expenses"

        filters.mapNotNull(this::convert).forEach {
            baseQuery += " WHERE ${it};"
        }

        if (pagingState is RequestPagingState.Paged) {
            baseQuery += " LIMIT ${pagingState.pageSize}" + " OFFSET ${pagingState.cursor}"
        }

        return object : IDatabaseFilter {
            override fun getFilterExpression() = baseQuery
        }
    }

}
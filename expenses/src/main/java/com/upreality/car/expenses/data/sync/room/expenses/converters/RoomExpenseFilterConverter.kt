package com.upreality.car.expenses.data.sync.room.expenses.converters

import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.domain.ExpenseToTypeConverter
import com.upreality.car.expenses.domain.model.ExpenseFilter
import com.upreality.car.expenses.domain.model.expence.Expense
import data.database.IDatabaseFilter
import domain.RequestPagingState
import kotlin.reflect.KClass

object RoomExpenseFilterConverter {

    private fun convert(filter: ExpenseFilter): String? {
        val getTypeId: (ExpenseType) -> Int = { type -> RoomExpenseTypeConverter().toId(type) }
        return when (filter) {
            ExpenseFilter.All -> null
            is ExpenseFilter.Id -> "id LIKE ${filter.id}"
            is ExpenseFilter.DateRange -> "date BETWEEN ${filter.from.time} AND ${filter.to.time}"
            is ExpenseFilter.Type -> "type IN ${getTypesRange(filter.types)}"
        }
    }

    private fun getTypesRange(types: List<KClass<out Expense>>): String {
        val expenseTypes = types.map(ExpenseToTypeConverter::toType)
        val builder = StringBuilder()
        builder.append('(')
        expenseTypes.forEachIndexed { index, expenseType ->
            RoomExpenseTypeConverter().toId(expenseType).let(builder::append)
            if (index != expenseTypes.size - 1)
                builder.append(',')
        }
        builder.append(')')
        return builder.toString()
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
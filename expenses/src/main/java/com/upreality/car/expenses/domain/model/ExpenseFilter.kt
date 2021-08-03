package com.upreality.car.expenses.domain.model

import com.upreality.car.expenses.domain.model.expence.Expense
import java.util.*
import kotlin.reflect.KClass

sealed class ExpenseFilter {
    object All : ExpenseFilter()
    data class Id(val id: Long) : ExpenseFilter()
    data class Type(val types: List<KClass<out Expense>>) : ExpenseFilter()
    data class DateRange(val from: Date, val to: Date) : ExpenseFilter()
}
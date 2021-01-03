package com.upreality.car.expenses.data.model.filters

interface IExpenseFilter {
    fun getFilterExpression(): String
}
package com.upreality.car.expenses.data.model.queries

interface IExpenseFilter {
    fun getFilterExpression(): String
}
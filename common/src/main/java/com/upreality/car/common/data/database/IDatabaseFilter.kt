package com.upreality.car.common.data.database

interface IDatabaseFilter {
    fun getFilterExpression(): String
}
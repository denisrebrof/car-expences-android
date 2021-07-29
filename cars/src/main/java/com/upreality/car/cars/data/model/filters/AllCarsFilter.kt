package com.upreality.car.cars.data.model.filters

import data.database.IDatabaseFilter

object AllCarsFilter : IDatabaseFilter {
    override fun getFilterExpression(): String {
        return "SELECT * FROM cars"
    }
}
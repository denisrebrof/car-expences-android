package com.upreality.car.cars.data.model.filters

import com.upreality.common.data.IDatabaseFilter

object AllCarsFilter : IDatabaseFilter {
    override fun getFilterExpression(): String {
        return "SELECT * FROM cars"
    }
}
package com.upreality.car.cars.data.model.filters

import com.upreality.car.common.data.IDatabaseFilter

class SingleCarFilter(private val carId: Long) : IDatabaseFilter {
    override fun getFilterExpression(): String {
        return "SELECT * FROM cars WHERE id LIKE $carId"
    }
}
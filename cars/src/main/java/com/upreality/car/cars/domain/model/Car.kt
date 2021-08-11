package com.upreality.car.cars.domain.model

import com.upreality.car.brending.domain.model.CarMark

data class Car(
    val id: Long,
    val name: String,
    val mileage: Int,
    val mark: CarMark
)
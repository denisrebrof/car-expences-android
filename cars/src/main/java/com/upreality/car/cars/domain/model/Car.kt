package com.upreality.car.cars.domain.model

data class Car(
    var id: Long = 0L,
    var name: String
) {

    companion object{
        const val DEFAULT_CAR_ID = -10L
    }

    fun getDefaultCar(): Car {
        return Car(
            id = DEFAULT_CAR_ID,
            name = "Default Car"
        )
    }
}

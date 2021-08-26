package com.upreality.car.expenses.data.shared.converters

import com.upreality.car.expenses.domain.model.FinesCategories

object FinesCategoriesConverter {

    fun fromId(typeId: Int): FinesCategories {
        return when (typeId) {
            -1 -> FinesCategories.Undefined
            1 -> FinesCategories.SpeedLimit
            2 -> FinesCategories.Parking
            3 -> FinesCategories.RoadMarking
            else -> FinesCategories.Other
        }
    }

    fun toId(type: FinesCategories): Int {
        return when (type) {
            FinesCategories.Undefined -> -1
            FinesCategories.Other -> 0
            FinesCategories.SpeedLimit -> 1
            FinesCategories.Parking -> 2
            FinesCategories.RoadMarking -> 3
        }
    }
}
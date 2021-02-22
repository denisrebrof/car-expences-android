package com.upreality.car.expenses.data.remote.firestore.converters

import com.upreality.car.expenses.domain.model.FinesCategories

object RemoteFinesCategoriesConverter {

    fun toFinesCategories(typeId: Int): FinesCategories {
        return when (typeId) {
            1 -> FinesCategories.SpeedLimit
            2 -> FinesCategories.Parking
            3 -> FinesCategories.RoadMarking
            else -> FinesCategories.Other
        }
    }

    fun toFinesCategoriesId(type: FinesCategories): Int {
        return when (type) {
            FinesCategories.Other -> 0
            FinesCategories.SpeedLimit -> 1
            FinesCategories.Parking -> 2
            FinesCategories.RoadMarking -> 3
        }
    }
}
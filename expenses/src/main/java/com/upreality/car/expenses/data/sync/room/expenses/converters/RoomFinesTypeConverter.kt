package com.upreality.car.expenses.data.sync.room.expenses.converters

import androidx.room.TypeConverter
import com.upreality.car.expenses.domain.model.FinesCategories

class RoomFinesTypeConverter {
    @TypeConverter
    fun toId(type: FinesCategories) = when (type) {
        FinesCategories.Parking -> 1
        FinesCategories.RoadMarking -> 2
        FinesCategories.SpeedLimit -> 3
        FinesCategories.Other -> 0
        FinesCategories.Undefined -> -1
    }

    @TypeConverter
    fun fromId(id: Int) = when (id) {
        -1 -> FinesCategories.Undefined
        1 -> FinesCategories.Parking
        2 -> FinesCategories.RoadMarking
        3 -> FinesCategories.SpeedLimit
        else -> FinesCategories.Other
    }
}
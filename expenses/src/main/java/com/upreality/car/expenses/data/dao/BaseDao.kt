package com.upreality.car.expenses.data.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update

interface BaseDao<T> {

    @Insert
    fun insert(obj: T): Long

    @Update
    fun update(obj: T)

    @Delete
    fun delete(obj: T)
}
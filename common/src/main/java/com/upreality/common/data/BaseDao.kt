package com.upreality.common.data

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import io.reactivex.Completable
import io.reactivex.Maybe

interface BaseDao<T> {

    @Insert
    fun insert(obj: T): Maybe<Long>

    @Update
    fun update(obj: T): Completable

    @Delete
    fun delete(obj: T): Completable
}
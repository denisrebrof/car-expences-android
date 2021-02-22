package com.upreality.car.cars.data.dao

import androidx.room.Dao
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.upreality.car.cars.data.model.entities.CarEntity
import com.upreality.car.common.data.RoomBaseDao
import io.reactivex.Flowable

@Dao
interface CarEntitiesDao: RoomBaseDao<CarEntity> {
    @RawQuery(observedEntities = [CarEntity::class])
    fun load(query: SupportSQLiteQuery): Flowable<List<CarEntity>>
}
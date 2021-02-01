package com.upreality.car.cars.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.upreality.car.cars.data.model.entities.CarEntity
import com.upreality.car.expenses.data.model.entities.ExpenseDetails
import com.upreality.car.expenses.data.model.entities.ExpenseEntity
import com.upreality.common.data.BaseDao
import io.reactivex.Flowable
import io.reactivex.Maybe

@Dao
interface CarEntitiesDao: BaseDao<CarEntity> {
    @RawQuery(observedEntities = [CarEntity::class])
    fun load(query: SupportSQLiteQuery): Flowable<List<CarEntity>>
}
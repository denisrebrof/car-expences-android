package com.upreality.car.expenses.data.local.expenses.dao

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.upreality.car.expenses.data.local.expenses.model.entities.ExpenseLocalEntity
import com.upreality.car.common.data.database.RoomBaseDao
import io.reactivex.Flowable

@Dao
interface ExpenseLocalEntitiesDao: RoomBaseDao<ExpenseLocalEntity> {
    @RawQuery(observedEntities = [ExpenseLocalEntity::class])
    fun load(query: SupportSQLiteQuery): Flowable<List<ExpenseLocalEntity>>
}
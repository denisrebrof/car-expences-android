package com.upreality.car.expenses.data.local.expenses.dao

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.upreality.car.expenses.data.local.expenses.model.entities.ExpenseEntity
import com.upreality.car.common.data.database.RoomBaseDao
import io.reactivex.Flowable

@Dao
interface ExpensesDao: RoomBaseDao<ExpenseEntity> {
    @RawQuery(observedEntities = [ExpenseEntity::class])
    fun load(query: SupportSQLiteQuery): Flowable<List<ExpenseEntity>>
}
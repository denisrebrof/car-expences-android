package com.upreality.car.expenses.data.local.room.expensesinfo.dao

import androidx.room.Dao
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.upreality.car.expenses.data.local.room.expensesinfo.model.entities.ExpenseInfo
import data.database.RoomBaseDao
import io.reactivex.Flowable

@Dao
interface ExpenseInfoDAO : RoomBaseDao<ExpenseInfo> {
    @RawQuery(observedEntities = [ExpenseInfo::class])
    fun load(query: SupportSQLiteQuery): Flowable<List<ExpenseInfo>>
}
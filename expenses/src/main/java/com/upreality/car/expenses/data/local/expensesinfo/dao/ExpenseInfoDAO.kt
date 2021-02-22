package com.upreality.car.expenses.data.local.expensesinfo.dao

import androidx.room.Dao
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.upreality.car.expenses.data.local.expensesinfo.model.entities.ExpenseInfo
import com.upreality.car.common.data.RoomBaseDao
import io.reactivex.Flowable

@Dao
interface ExpenseInfoDAO : RoomBaseDao<ExpenseInfo> {
    @RawQuery(observedEntities = [ExpenseInfo::class])
    fun load(query: SupportSQLiteQuery): Flowable<List<ExpenseInfo>>
}
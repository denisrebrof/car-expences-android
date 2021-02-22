package com.upreality.car.expenses.data.local.expensesinfo.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.upreality.car.expenses.data.local.expensesinfo.entities.ExpenseInfo
import com.upreality.car.common.data.RoomBaseDao
import io.reactivex.Flowable
import io.reactivex.Maybe

@Dao
interface ExpenseInfoDAO : RoomBaseDao<ExpenseInfo> {
    @RawQuery(observedEntities = [ExpenseInfo::class])
    fun load(query: SupportSQLiteQuery): Flowable<List<ExpenseInfo>>

    @Query("SELECT * FROM expense_info WHERE id = :id")
    fun get(id: Long): Maybe<ExpenseInfo>
}
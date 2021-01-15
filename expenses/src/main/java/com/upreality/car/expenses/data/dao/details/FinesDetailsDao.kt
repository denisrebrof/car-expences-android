package com.upreality.car.expenses.data.dao.details

import androidx.room.Dao
import androidx.room.Query
import com.upreality.car.expenses.data.dao.BaseDao
import com.upreality.car.expenses.data.model.entities.ExpenseDetails
import io.reactivex.Flowable
import io.reactivex.Maybe

@Dao
interface FinesDetailsDao : BaseDao<ExpenseDetails.ExpenseFinesDetails> {
    @Query("SELECT * FROM fines_details WHERE id = :id")
    fun get(id: Long): Maybe<ExpenseDetails.ExpenseFinesDetails>
}
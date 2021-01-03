package com.upreality.car.expenses.data.dao.details

import androidx.room.Dao
import com.upreality.car.expenses.data.dao.BaseDao
import com.upreality.car.expenses.data.model.entities.ExpenseDetails

@Dao
interface FinesDetailsDao : BaseDao<ExpenseDetails.ExpenseFinesDetails> {
    fun get(id: Long): ExpenseDetails.ExpenseFinesDetails?
}
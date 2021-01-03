package com.upreality.car.expenses.data.dao

import androidx.room.Dao
import com.upreality.car.expenses.data.model.entities.ExpenseDetails

@Dao
abstract class FinesDetailsDao : BaseDao<ExpenseDetails.ExpenseFinesDetails> {
    abstract fun get(id: Long): ExpenseDetails.ExpenseFinesDetails?
}
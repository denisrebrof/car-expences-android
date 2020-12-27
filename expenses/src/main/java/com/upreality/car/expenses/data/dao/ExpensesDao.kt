package com.upreality.car.expenses.data.dao

import androidx.room.*
import com.upreality.car.expenses.data.converters.DateConverter
import com.upreality.car.expenses.data.converters.ExpenseTypeConverter
import com.upreality.car.expenses.data.converters.FinesTypeConverter
import com.upreality.car.expenses.data.converters.MaintenanceTypeConverter
import com.upreality.car.expenses.data.model.roomentities.Expense
import com.upreality.car.expenses.data.model.roomentities.expencedetails.MaintenanceType
import io.reactivex.Flowable
import org.intellij.lang.annotations.Language

@Dao
interface ExpensesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(expense: Expense): Long

    @Update
    fun update(expense: Expense)

    @Delete
    fun delete(expense: Expense)

    @Query("SELECT * FROM expenses")
    fun load(): Array<Expense>
//
//    @Language("RoomSql")
//    @Query("SELECT * FROM expenses")
//    fun load(type: MaintenanceType = MaintenanceType.NotDefined)

//    @Query("SELECT * FROM expenses WHERE id = :exp_id")
//    suspend fun getById(exp_id: Int): Flowable<Expense>
}
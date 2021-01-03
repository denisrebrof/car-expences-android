package com.upreality.car.expenses.data.dao

import androidx.room.*
import com.upreality.car.expenses.data.model.entities.ExpenseEntity
import org.intellij.lang.annotations.Language

@Dao
interface ExpensesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(expense: ExpenseEntity): Long

    @Update
    fun update(expense: ExpenseEntity)

    @Delete
    fun delete(expense: ExpenseEntity)

    @Language("RoomSql")
    @Query("SELECT * FROM expenses")
    fun load(): Array<ExpenseEntity>

    @Language("RoomSql")
    @Query("SELECT * FROM expenses :filterExpression")
    fun load(filterExpression: String): Array<ExpenseEntity>
}
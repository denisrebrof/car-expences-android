package com.upreality.car.expenses.data.dao

import androidx.room.*
import com.upreality.car.expenses.data.model.entities.ExpenseEntity
import org.intellij.lang.annotations.Language

@Dao
abstract class ExpensesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(expense: ExpenseEntity): Long

    @Update
    abstract fun update(expense: ExpenseEntity)

    @Delete
    abstract fun delete(expense: ExpenseEntity)

    @Language("RoomSql")
    @Query("SELECT * FROM expenses")
    abstract fun load(): Array<ExpenseEntity>

    @Language("RoomSql")
    @Query("SELECT * FROM expenses :filterExpression")
    abstract fun load(filterExpression: String): Array<ExpenseEntity>
}
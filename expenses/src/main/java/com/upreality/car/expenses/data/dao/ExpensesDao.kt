package com.upreality.car.expenses.data.dao

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.upreality.car.expenses.data.model.entities.ExpenseEntity
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import org.intellij.lang.annotations.Language

@Dao
interface ExpensesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(expense: ExpenseEntity): Maybe<Long>

    @Update
    fun update(expense: ExpenseEntity): Completable

    @Delete
    fun delete(expense: ExpenseEntity): Completable

    @RawQuery(observedEntities = [ExpenseEntity::class])
    fun load(query: SupportSQLiteQuery): Flowable<List<ExpenseEntity>>
}
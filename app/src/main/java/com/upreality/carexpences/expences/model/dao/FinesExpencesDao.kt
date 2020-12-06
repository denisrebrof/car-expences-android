package com.upreality.carexpences.expences.model.dao

import androidx.room.*
import com.upreality.carexpences.expences.model.data.entities.FinesExpence
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface FinesExpencesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(vararg expenses: FinesExpence) : Completable

    @Delete
    suspend fun delete(vararg expenses: FinesExpence) : Completable

    @Query("SELECT * FROM fines_expences")
    suspend fun loadAll(): Flowable<Array<FinesExpence>>

    @Query("SELECT * FROM fines_expences WHERE id = :exp_id")
    suspend fun getById(exp_id : Int): Flowable<FinesExpence>
}
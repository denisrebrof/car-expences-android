package com.upreality.carexpences.expences.model.datasources

import androidx.room.*
import com.upreality.carexpences.expences.model.data.entities.FuelExpence
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface FuelExpencesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(vararg expenses: FuelExpence): Completable

    @Delete
    suspend fun delete(vararg expenses: FuelExpence): Completable

    @Query("SELECT * FROM fuel_expences")
    suspend fun loadAll(): Flowable<Array<FuelExpence>>

    @Query("SELECT * FROM fuel_expences WHERE id = :exp_id")
    suspend fun getById(exp_id: Int): Flowable<FuelExpence>
}
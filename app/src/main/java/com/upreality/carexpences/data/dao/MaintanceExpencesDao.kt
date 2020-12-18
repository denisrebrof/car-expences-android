package com.upreality.carexpences.data.dao

import androidx.room.*
import com.upreality.carexpences.domain.entities.MaintanceExpence
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface MaintanceExpencesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(vararg expenses: MaintanceExpence): Completable

    @Delete
    suspend fun delete(vararg expenses: MaintanceExpence): Completable

    @Query("SELECT * FROM maintence_expences")
    suspend fun loadAll(): Flowable<Array<MaintanceExpence>>

    @Query("SELECT * FROM maintence_expences WHERE id = :exp_id")
    suspend fun getById(exp_id: Int): Flowable<MaintanceExpence>
}
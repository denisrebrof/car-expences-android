package com.upreality.carexpences.expences.data.dao

import androidx.room.*
import com.upreality.carexpences.expences.data.model.roomentities.Expence
import com.upreality.carexpences.expences.data.model.roomentities.expencedetails.MaintanceType
import io.reactivex.Flowable
import org.intellij.lang.annotations.Language

@Dao
interface ExpencesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(expense: Expence) : Long

    @Delete
    suspend fun delete(expense: Expence)

    @Query("SELECT * FROM exp")
    suspend fun load(): Flowable<Array<Expence>>

    @Language("RoomSql")
    @Query("SELECT * FROM expences  ")
    suspend fun load(type: MaintanceType = MaintanceType.NotDefined)

    @Query("SELECT * FROM expences WHERE id = :exp_id")
    suspend fun getById(exp_id : Int): Flowable<Expence>
}
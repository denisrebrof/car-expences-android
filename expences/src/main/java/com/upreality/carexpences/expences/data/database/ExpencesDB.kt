package com.upreality.carexpences.expences.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.upreality.carexpences.expences.data.dao.ExpencesDao
import com.upreality.carexpences.expences.data.model.roomentities.Expence
import com.upreality.carexpences.expences.data.model.roomentities.expencedetails.ExpenceFinesDetails
import com.upreality.carexpences.expences.data.model.roomentities.expencedetails.ExpenceFuelDetails
import com.upreality.carexpences.expences.data.model.roomentities.expencedetails.ExpenceMaintanceDetails

@Database(
    entities = [Expence::class, ExpenceMaintanceDetails::class, ExpenceFinesDetails::class, ExpenceFuelDetails::class],
    version = 1
)
abstract class ExpencesDB : RoomDatabase() {
    abstract fun getExpencesDAO(): ExpencesDao
}
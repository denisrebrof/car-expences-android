package com.upreality.carexpences.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.upreality.carexpences.data.dao.FinesExpencesDao
import com.upreality.carexpences.data.dao.FuelExpencesDao
import com.upreality.carexpences.data.dao.MaintanceExpencesDao
import com.upreality.carexpences.domain.entities.FinesExpence
import com.upreality.carexpences.domain.entities.FuelExpence
import com.upreality.carexpences.domain.entities.MaintanceExpence

@Database(entities = [FinesExpence::class, FuelExpence::class, MaintanceExpence::class], version = 1)
abstract class ExpencesDB : RoomDatabase() {
    abstract fun getFuelExpencesDAO() : FuelExpencesDao
    abstract fun getFinesExpencesDAO() : FinesExpencesDao
    abstract fun getMaintanceExpensesDAO() : MaintanceExpencesDao
}
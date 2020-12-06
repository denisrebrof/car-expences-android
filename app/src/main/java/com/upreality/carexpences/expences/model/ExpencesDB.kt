package com.upreality.carexpences.expences.model

import androidx.room.Database
import androidx.room.RoomDatabase
import com.upreality.carexpences.expences.model.dao.FinesExpencesDao
import com.upreality.carexpences.expences.model.dao.FuelExpencesDao
import com.upreality.carexpences.expences.model.dao.MaintanceExpencesDao
import com.upreality.carexpences.expences.model.data.entities.FinesExpence
import com.upreality.carexpences.expences.model.data.entities.FuelExpence
import com.upreality.carexpences.expences.model.data.entities.MaintanceExpence

@Database(entities = [FinesExpence::class, FuelExpence::class, MaintanceExpence::class], version = 1)
abstract class ExpencesDB : RoomDatabase() {
    abstract fun getFuelExpencesDAO() : FuelExpencesDao
    abstract fun getFinesExpencesDAO() : FinesExpencesDao
    abstract fun getMaintanceExpensesDAO() : MaintanceExpencesDao
}
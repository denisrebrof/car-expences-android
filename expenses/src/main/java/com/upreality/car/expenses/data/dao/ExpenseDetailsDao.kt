package com.upreality.car.expenses.data.dao

import com.upreality.car.expenses.data.model.ExpenseType
import com.upreality.car.expenses.data.model.entities.ExpenseDetails
import javax.inject.Inject

class ExpenseDetailsDao @Inject constructor(
    private val finesDetailsDao: FinesDetailsDao,
    private val fuelDetailsDao: FuelDetailsDao,
    private val maintenanceDetailsDao: MaintenanceDetailsDao
) {

    fun insert(details: ExpenseDetails): Long {
        return when (details) {
            is ExpenseDetails.ExpenseMaintenanceDetails -> maintenanceDetailsDao.insert(details)
            is ExpenseDetails.ExpenseFuelDetails -> fuelDetailsDao.insert(details)
            is ExpenseDetails.ExpenseFinesDetails -> finesDetailsDao.insert(details)
        }
    }

    fun update(details: ExpenseDetails) {
        when (details) {
            is ExpenseDetails.ExpenseMaintenanceDetails -> maintenanceDetailsDao.update(details)
            is ExpenseDetails.ExpenseFuelDetails -> fuelDetailsDao.update(details)
            is ExpenseDetails.ExpenseFinesDetails -> finesDetailsDao.update(details)
        }
    }

    fun get(detailsId: Long, type: ExpenseType): ExpenseDetails? {
        return when (type) {
            ExpenseType.Fines -> finesDetailsDao.get(detailsId)
            ExpenseType.Fuel -> fuelDetailsDao.get(detailsId)
            ExpenseType.Maintenance -> maintenanceDetailsDao.get(detailsId)
        }
    }

    fun delete(details: ExpenseDetails) {
        when (details) {
            is ExpenseDetails.ExpenseMaintenanceDetails -> maintenanceDetailsDao.delete(details)
            is ExpenseDetails.ExpenseFuelDetails -> fuelDetailsDao.delete(details)
            is ExpenseDetails.ExpenseFinesDetails -> finesDetailsDao.delete(details)
        }
    }
}
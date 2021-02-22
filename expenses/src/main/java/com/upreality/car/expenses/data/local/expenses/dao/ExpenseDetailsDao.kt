package com.upreality.car.expenses.data.local.expenses.dao

import com.upreality.car.expenses.data.local.expenses.dao.details.FinesDetailsDao
import com.upreality.car.expenses.data.local.expenses.dao.details.FuelDetailsDao
import com.upreality.car.expenses.data.local.expenses.dao.details.MaintenanceDetailsDao
import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.data.local.expenses.model.entities.ExpenseDetails
import io.reactivex.Completable
import io.reactivex.Maybe
import javax.inject.Inject

class ExpenseDetailsDao @Inject constructor(
    private val finesDetailsDao: FinesDetailsDao,
    private val fuelDetailsDao: FuelDetailsDao,
    private val maintenanceDetailsDao: MaintenanceDetailsDao
) {

    fun insert(details: ExpenseDetails): Maybe<Long> {
        return when (details) {
            is ExpenseDetails.ExpenseMaintenanceDetails -> maintenanceDetailsDao.insert(details)
            is ExpenseDetails.ExpenseFuelDetails -> fuelDetailsDao.insert(details)
            is ExpenseDetails.ExpenseFinesDetails -> finesDetailsDao.insert(details)
        }
    }

    fun update(details: ExpenseDetails): Completable {
        return when (details) {
            is ExpenseDetails.ExpenseMaintenanceDetails -> maintenanceDetailsDao.update(details)
            is ExpenseDetails.ExpenseFuelDetails -> fuelDetailsDao.update(details)
            is ExpenseDetails.ExpenseFinesDetails -> finesDetailsDao.update(details)
        }
    }

    fun get(detailsId: Long, type: ExpenseType): Maybe<out ExpenseDetails> {
        return when (type) {
            ExpenseType.Fines -> finesDetailsDao.get(detailsId)
            ExpenseType.Fuel -> fuelDetailsDao.get(detailsId)
            ExpenseType.Maintenance -> maintenanceDetailsDao.get(detailsId)
        }
    }

    fun delete(details: ExpenseDetails): Completable {
        return when (details) {
            is ExpenseDetails.ExpenseMaintenanceDetails -> maintenanceDetailsDao.delete(details)
            is ExpenseDetails.ExpenseFuelDetails -> fuelDetailsDao.delete(details)
            is ExpenseDetails.ExpenseFinesDetails -> finesDetailsDao.delete(details)
        }
    }
}
package com.upreality.car.expenses.data.local.expenses.dao

import com.upreality.car.expenses.data.local.expenses.dao.details.FinesDetailsDao
import com.upreality.car.expenses.data.local.expenses.dao.details.FuelDetailsDao
import com.upreality.car.expenses.data.local.expenses.dao.details.MaintenanceDetailsDao
import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.data.local.expenses.model.entities.ExpenseLocalDetailsEntity
import io.reactivex.Completable
import io.reactivex.Maybe
import javax.inject.Inject

class ExpenseLocalDetailsDao @Inject constructor(
    private val finesDetailsDao: FinesDetailsDao,
    private val fuelDetailsDao: FuelDetailsDao,
    private val maintenanceDetailsDao: MaintenanceDetailsDao
) {

    fun insert(details: ExpenseLocalDetailsEntity): Maybe<Long> {
        return when (details) {
            is ExpenseLocalDetailsEntity.ExpenseMaintenanceDetails -> maintenanceDetailsDao.insert(details)
            is ExpenseLocalDetailsEntity.ExpenseFuelDetails -> fuelDetailsDao.insert(details)
            is ExpenseLocalDetailsEntity.ExpenseFinesDetails -> finesDetailsDao.insert(details)
        }
    }

    fun update(details: ExpenseLocalDetailsEntity): Completable {
        return when (details) {
            is ExpenseLocalDetailsEntity.ExpenseMaintenanceDetails -> maintenanceDetailsDao.update(details)
            is ExpenseLocalDetailsEntity.ExpenseFuelDetails -> fuelDetailsDao.update(details)
            is ExpenseLocalDetailsEntity.ExpenseFinesDetails -> finesDetailsDao.update(details)
        }
    }

    fun get(detailsId: Long, type: ExpenseType): Maybe<out ExpenseLocalDetailsEntity> {
        return when (type) {
            ExpenseType.Fines -> finesDetailsDao.get(detailsId)
            ExpenseType.Fuel -> fuelDetailsDao.get(detailsId)
            ExpenseType.Maintenance -> maintenanceDetailsDao.get(detailsId)
        }
    }

    fun delete(details: ExpenseLocalDetailsEntity): Completable {
        return when (details) {
            is ExpenseLocalDetailsEntity.ExpenseMaintenanceDetails -> maintenanceDetailsDao.delete(details)
            is ExpenseLocalDetailsEntity.ExpenseFuelDetails -> fuelDetailsDao.delete(details)
            is ExpenseLocalDetailsEntity.ExpenseFinesDetails -> finesDetailsDao.delete(details)
        }
    }
}
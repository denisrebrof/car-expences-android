package com.upreality.car.expenses.data.realm.model

import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.domain.model.FinesCategories
import com.upreality.car.expenses.domain.model.MaintenanceType
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class ExpenseRealm : RealmObject() {
    @PrimaryKey
    var id: Long = 0L
    var date: Date = Date()
    var cost: Float = 0f

    //Type
    var typeId = ExpenseType.Fuel.id
    var type: ExpenseType
        get() = ExpenseRealmTypeConverter.fromId(typeId)
        set(value) {
            typeId = ExpenseRealmTypeConverter.toId(value)
        }

    //Shared
    var mileage: Float = 0f //Fuel + Mileage

    //Fuel
    var fuelLiters: Float = 0f

    //Maintenance
    private var maintenanceTypeId = MaintenanceType.Other.let(MaintenanceTypeConverter::toId)
    var maintenanceType: MaintenanceType
        get() = MaintenanceTypeConverter.fromId(maintenanceTypeId)
        set(value) {
            typeId = MaintenanceTypeConverter.toId(value)
        }

    //Fines
    private var fineTypeId = FinesCategories.Other.let(FineTypeConverter::toId)
    var fineType: FinesCategories
        get() = FineTypeConverter.fromId(fineTypeId)
        set(value) {
            typeId = FineTypeConverter.toId(value)
        }
}
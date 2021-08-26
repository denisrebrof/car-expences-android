package com.upreality.car.expenses.data.realm.model

import com.upreality.car.expenses.data.shared.converters.ExpenseTypeConverter
import com.upreality.car.expenses.data.shared.converters.FinesCategoriesConverter
import com.upreality.car.expenses.data.shared.converters.MaintenanceTypeConverter
import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.domain.model.FinesCategories
import com.upreality.car.expenses.domain.model.MaintenanceType
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import java.util.*

open class ExpenseRealm : RealmObject() {
    @PrimaryKey
    var _id: Long = 0L

    @Required
    var date: Date = Date()
    var cost: Float = 0f

    //Type
    var typeId = ExpenseType.Fuel.id
    var type: ExpenseType
        get() = ExpenseTypeConverter.fromId(typeId)
        set(value) {
            typeId = ExpenseTypeConverter.toId(value)
        }

    //Shared
    var mileage: Float = 0f //Fuel + Mileage

    //Fuel
    var fuelAmount: Float = 0f

    //Maintenance
    private var maintenanceTypeId = MaintenanceType.Other.let(MaintenanceTypeConverter::toId)
    var maintenanceType: MaintenanceType
        get() = MaintenanceTypeConverter.fromId(maintenanceTypeId)
        set(value) {
            maintenanceTypeId = MaintenanceTypeConverter.toId(value)
        }

    //Fines
    private var fineTypeId = FinesCategories.Other.let(FinesCategoriesConverter::toId)
    var fineType: FinesCategories
        get() = FinesCategoriesConverter.fromId(fineTypeId)
        set(value) {
            fineTypeId = FinesCategoriesConverter.toId(value)
        }
}
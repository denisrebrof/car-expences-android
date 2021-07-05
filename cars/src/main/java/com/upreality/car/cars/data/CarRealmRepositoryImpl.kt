package com.upreality.car.cars.data

import com.upreality.car.cars.data.datasoures.CarsLocalDataSource
import com.upreality.car.cars.data.model.entities.CarRealmConverter
import com.upreality.car.cars.domain.ICarsRepository
import com.upreality.car.cars.domain.model.Car
import com.upreality.car.common.data.SyncedRealmProvider
import io.reactivex.Completable
import io.reactivex.Flowable
import java.util.*
import javax.inject.Inject

class CarRealmRepositoryImpl @Inject constructor(
    private val dataSource: CarsLocalDataSource,
    private val realmProvider: SyncedRealmProvider
) : ICarsRepository {

    override fun getCars(): Flowable<List<Car>> {
        return Flowable.just(listOf())
    }

    override fun getCar(carId: Long): Flowable<Car> {
        return Flowable.empty()
    }

    override fun create(car: Car): Completable {
        val carWithId = car.copy(id = UUID.randomUUID().mostSignificantBits)
        return Completable.fromAction {
            val realm = realmProvider.getRealmInstance()
            realm.beginTransaction()
            val dataModel = CarRealmConverter.fromDomain(carWithId, realm)
            realm.copyToRealmOrUpdate(dataModel)
            realm.commitTransaction()
            realm.close()
        }
    }

    override fun updateCar(car: Car): Completable {
        return Completable.fromAction {
            val realm = realmProvider.getRealmInstance()
            realm.beginTransaction()
            val dataModel = CarRealmConverter.fromDomain(car, realm)
            realm.copyToRealmOrUpdate(dataModel)
            realm.commitTransaction()
            realm.close()
        }
    }

    override fun deleteCar(car: Car): Completable {
        return Completable.fromAction {
            val realm = realmProvider.getRealmInstance()
//            val car: RealmResults<CarRealm> = realm.where(CarRealm::class.java).equalTo(Car.EMAIL_KEY, email)
//                    .findAll()
//            car.clear()
            realm.close()
        }
    }

}
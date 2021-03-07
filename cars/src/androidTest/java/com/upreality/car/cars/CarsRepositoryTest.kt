package com.upreality.car.cars

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.upreality.car.brending.data.CarMarksRepoStub
import com.upreality.car.cars.data.CarsRepositoryImpl
import com.upreality.car.cars.data.datasoures.CarsLocalDataSource
import com.upreality.car.cars.domain.ICarsRepository
import com.upreality.car.cars.domain.model.Car
import com.upreality.car.expenses.data.local.expenses.model.filters.ExpenseLocalIdFilter
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CarsRepositoryTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    //it's just for Junit to execute tasks synchronously

    @get:Rule
    val databaseRule = CarsDatabaseRule()

    private lateinit var carsRepository: ICarsRepository
    private lateinit var marksRepository: CarMarksRepoStub

    @Before
    fun setUp() {
        val carEntitiesDao = databaseRule.db.getCarsDAO()
        marksRepository =  CarMarksRepoStub()
        val carsLocalDataSource = CarsLocalDataSource(carEntitiesDao, marksRepository)
        carsRepository = CarsRepositoryImpl(carsLocalDataSource)
    }

    @Test
    @Throws(Exception::class)
    fun writeAndReadCarTest() {
        val carId = 1L
        val carName = "My Awesome Car"
        val mark = marksRepository.getMark(0)
        val car = Car(carId, carName, 100, mark)

        val insertedElementMaybe = carsRepository.create(car)
            .subscribeOn(Schedulers.trampoline())
            .observeOn(Schedulers.trampoline())
            .flatMap {
                val idFilter = ExpenseLocalIdFilter(it).getFilterExpression()
                carsRepository.getCar(carId).firstElement()
            }

        val testObserver = insertedElementMaybe.test()
        testObserver.assertNoErrors()
        testObserver.assertComplete()
        testObserver.assertValue { carsDataEquals(it, car) }

        testObserver.dispose()
    }

    private fun carsDataEquals(f: Car, s: Car): Boolean {
        return f.mark == s.mark &&
                f.mileage == s.mileage &&
                f.name == s.name
    }

    @Test
    @Throws(Exception::class)
    fun updateCarTest() {
        val mark = marksRepository.getMark(0)
        val car = Car(0L, "My Awesome Car", 100, mark)
        val id = carsRepository.create(car).blockingGet()

        val carUpdated = Car(id, "My Awesome Car Updated", 200, mark)

        carsRepository.updateCar(carUpdated).blockingAwait()
        val readExpenseResult = carsRepository.getCar(id).blockingFirst()
        assert(readExpenseResult != null)
        assert(carsDataEquals(readExpenseResult!!, carUpdated))
    }
}
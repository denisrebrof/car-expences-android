package com.upreality.car.expenses.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.upreality.car.expenses.data.local.expenses.dao.ExpenseLocalEntitiesDao
import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.data.local.expenses.model.entities.ExpenseLocalEntity
import com.upreality.car.expenses.data.local.expenses.model.filters.ExpenseLocalIdFilter
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class ExpenseEntitiesDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    //it's just for Junit to execute tasks synchronously

    @get:Rule
    val databaseRule = ExpensesDatabaseTestRule()

    private lateinit var expenseEntitiesDao: ExpenseLocalEntitiesDao

    @Before
    fun setUp() {
        expenseEntitiesDao = databaseRule.db.getExpensesDAO()
    }

    @Test
    @Throws(Exception::class)
    fun writeAndReadExpenseEntityTest() {
        val expense = ExpenseLocalEntity(0, Date(), 100F, ExpenseType.Fuel, 200)

        val insertedElementMaybe = expenseEntitiesDao.insert(expense)
            .subscribeOn(Schedulers.trampoline())
            .observeOn(Schedulers.trampoline())
            .flatMap {
                val idFilter = ExpenseLocalIdFilter(it).getFilterExpression()
                expenseEntitiesDao.load(SimpleSQLiteQuery(idFilter)).firstElement()
            }.map {
                it.firstOrNull()
            }

        val testObserver = insertedElementMaybe.test()
        testObserver.assertNoErrors()
        testObserver.assertComplete()
        testObserver.assertValue { expenseEntityDataEquals(it, expense) }

        testObserver.dispose()
    }

    private fun expenseEntityDataEquals(f: ExpenseLocalEntity, s: ExpenseLocalEntity): Boolean {
        return f.date == s.date &&
                f.cost == s.cost &&
                f.type == s.type &&
                f.detailsId == s.detailsId
    }

    @Test
    @Throws(Exception::class)
    fun updateExpenseEntityTest() {
        val expense = ExpenseLocalEntity(0, Date(), 100F, ExpenseType.Fuel, 200)
        val id = expenseEntitiesDao.insert(expense).blockingGet()
        val expenseUpdated = ExpenseLocalEntity(id, Date(), 200F, ExpenseType.Maintenance, 300)
        expenseEntitiesDao.update(expenseUpdated).blockingAwait()
        val idFilter = ExpenseLocalIdFilter(id).getFilterExpression()
        val readExpenseResult = expenseEntitiesDao.load(SimpleSQLiteQuery(idFilter)).blockingFirst()
        val res = readExpenseResult.firstOrNull()
        assert(res != null)
        assert(expenseEntityDataEquals(res!!, expenseUpdated))
    }
}
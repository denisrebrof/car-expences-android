package com.upreality.car.expenses.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.upreality.car.expenses.data.local.expenses.dao.ExpensesDao
import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.data.local.expenses.model.entities.ExpenseEntity
import com.upreality.car.expenses.data.local.expenses.model.queries.ExpenseIdFilter
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class ExpensesDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    //it's just for Junit to execute tasks synchronously

    @get:Rule
    val databaseRule = ExpensesDatabaseTestRule()

    private lateinit var expensesDao: ExpensesDao

    @Before
    fun setUp() {
        expensesDao = databaseRule.db.getExpensesDAO()
    }

    @Test
    @Throws(Exception::class)
    fun writeAndReadExpenseEntityTest() {
        val expense = ExpenseEntity(0, Date(), 100F, ExpenseType.Fuel, 200)

        val insertedElementMaybe = expensesDao.insert(expense)
            .subscribeOn(Schedulers.trampoline())
            .observeOn(Schedulers.trampoline())
            .flatMap {
                val idFilter = ExpenseIdFilter(it).getFilterExpression()
                expensesDao.load(SimpleSQLiteQuery(idFilter)).firstElement()
            }.map {
                it.firstOrNull()
            }

        val testObserver = insertedElementMaybe.test()
        testObserver.assertNoErrors()
        testObserver.assertComplete()
        testObserver.assertValue { expenseEntityDataEquals(it, expense) }

        testObserver.dispose()
    }

    private fun expenseEntityDataEquals(f: ExpenseEntity, s: ExpenseEntity): Boolean {
        return f.date == s.date &&
                f.cost == s.cost &&
                f.type == s.type &&
                f.detailsId == s.detailsId
    }

    @Test
    @Throws(Exception::class)
    fun updateExpenseEntityTest() {
        val expense = ExpenseEntity(0, Date(), 100F, ExpenseType.Fuel, 200)
        val id = expensesDao.insert(expense).blockingGet()
        val expenseUpdated = ExpenseEntity(id, Date(), 200F, ExpenseType.Maintenance, 300)
        expensesDao.update(expenseUpdated).blockingAwait()
        val idFilter = ExpenseIdFilter(id).getFilterExpression()
        val readExpenseResult = expensesDao.load(SimpleSQLiteQuery(idFilter)).blockingFirst()
        val res = readExpenseResult.firstOrNull()
        assert(res != null)
        assert(expenseEntityDataEquals(res!!, expenseUpdated))
    }
}
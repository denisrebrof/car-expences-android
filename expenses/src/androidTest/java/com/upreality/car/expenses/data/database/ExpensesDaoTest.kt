package com.upreality.car.expenses.data.database

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.upreality.car.expenses.data.dao.ExpensesDao
import com.upreality.car.expenses.data.model.ExpenseType
import com.upreality.car.expenses.data.model.entities.ExpenseEntity
import com.upreality.car.expenses.data.model.queries.ExpenseIdFilter
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*

//TODO: rework using Rules
@RunWith(AndroidJUnit4::class)
class ExpensesDaoTest {
    private lateinit var expensesDao: ExpensesDao
    private lateinit var db: ExpensesDB

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, ExpensesDB::class.java
        ).build()
        expensesDao = db.getExpensesDAO()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeAndReadExpenseEntityTest() {
        val expense = ExpenseEntity(0, Date(), 100F, ExpenseType.Fuel, 200)
        val id = expensesDao.insert(expense)
        val idFilter = ExpenseIdFilter(id).getFilterExpression()
        val readExpenseResult = expensesDao.load(SimpleSQLiteQuery(idFilter))
        val res = readExpenseResult.firstOrNull()
        assert(res != null)
        assert(expenseEntityDataEquals(res!!, expense))
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
        val id = expensesDao.insert(expense)
        val expenseUpdated = ExpenseEntity(id, Date(), 200F, ExpenseType.Maintenance, 300)
        expensesDao.update(expenseUpdated)
        val idFilter = ExpenseIdFilter(id).getFilterExpression()
        val readExpenseResult = expensesDao.load(SimpleSQLiteQuery(idFilter))
        val res = readExpenseResult.firstOrNull()
        assert(res != null)
        assert(expenseEntityDataEquals(res!!, expenseUpdated))
    }
}
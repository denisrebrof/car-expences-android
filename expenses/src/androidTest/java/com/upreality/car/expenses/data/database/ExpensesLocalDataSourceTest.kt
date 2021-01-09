package com.upreality.car.expenses.data.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.upreality.car.expenses.data.dao.ExpenseDetailsDao
import com.upreality.car.expenses.data.datasources.ExpensesLocalDataSource
import com.upreality.car.expenses.data.model.queries.ExpenseIdFilter
import com.upreality.car.expenses.domain.ExpenseFilter
import com.upreality.car.expenses.domain.model.expence.Expense
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*

//TODO: rework using Rules
@RunWith(AndroidJUnit4::class)
class ExpensesLocalDataSourceTest {

    private lateinit var localDataSource: ExpensesLocalDataSource
    private lateinit var db: ExpensesDB

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, ExpensesDB::class.java
        ).build()
        val expensesDao = db.getExpensesDAO()
        val expenseDetailsDao = ExpenseDetailsDao(
            db.getFinesDAO(),
            db.getFuelDAO(),
            db.getMaintenanceDAO()
        )
        localDataSource = ExpensesLocalDataSource(expensesDao, expenseDetailsDao)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeAndReadExpenseTest() {
        val expense = Expense.Fuel(Date(), 100F, 500F, 1000F)
        val id = localDataSource.create(expense)
        expense.id = id
        val filter = ExpenseIdFilter(id)
        val requestResult = localDataSource.get(ExpenseFilter.All).firstOrNull { it.id == id }
        assert(expensesEquals(expense, requestResult!!))
    }

    private fun expensesEquals(f: Expense, s: Expense): Boolean {
        return f.date == s.date &&
                f.cost == s.cost &&
                f::class == s::class &&
                when (f) {
                    is Expense.Fuel ->
                        f.liters == (s as Expense.Fuel).liters &&
                                f.mileage == s.mileage
                    is Expense.Fine ->
                        f.type == (s as Expense.Fine).type
                    is Expense.Maintenance ->
                        f.mileage == (s as Expense.Maintenance).mileage
                                && f.type == s.type
                }
    }

    @Test
    @Throws(Exception::class)
    fun updateExpenseTest() {
        val expense = Expense.Fuel(Date(), 100F, 500F, 1000F)
        val id = localDataSource.create(expense)
        expense.id = id
        val updatedExpense = Expense.Fuel(Date(), 300F, 600F, 1090F)
        localDataSource.update(updatedExpense)
        val filter = ExpenseFilter.Fuel
        val loadedExpense = localDataSource.get(filter).firstOrNull { it.id == id }
        assert(expensesEquals(updatedExpense, loadedExpense!!))
    }
}
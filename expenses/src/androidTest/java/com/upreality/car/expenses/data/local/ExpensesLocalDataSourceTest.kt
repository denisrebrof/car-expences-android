package com.upreality.car.expenses.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.upreality.car.expenses.data.local.expenses.dao.ExpenseDetailsDao
import com.upreality.car.expenses.domain.model.ExpenseFilter
import com.upreality.car.expenses.domain.model.expence.Expense
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class ExpensesLocalDataSourceTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    //it's just for Junit to execute tasks synchronously

    @get:Rule
    val databaseRule = ExpensesDatabaseTestRule()

    private lateinit var localDataSource: ExpensesLocalDataSource

    @Before
    fun setUp() {
        databaseRule.db.apply {
            val expensesDao = getExpensesDAO()
            val expenseDetailsDao = ExpenseDetailsDao(
                getFinesDAO(),
                getFuelDAO(),
                getMaintenanceDAO()
            )
            localDataSource = ExpensesLocalDataSource(expensesDao, expenseDetailsDao)
        }
    }

    @Test
    @Throws(Exception::class)
    fun writeAndReadExpenseTest() {
        val expense = Expense.Fuel(Date(), 100F, 500F, 1000F)
        val id = localDataSource.create(expense).blockingGet()
        expense.id = id
        val requestResult = loadExpense(ExpenseFilter.All).firstOrNull { it.id == id }
        assert(expensesEquals(expense, requestResult!!))
    }

    private fun expensesEquals(f: Expense, s: Expense): Boolean {
        return f.date == s.date && f.cost == s.cost && f::class == s::class &&
                when (f) {
                    is Expense.Fuel -> f.liters == (s as Expense.Fuel).liters && f.mileage == s.mileage
                    is Expense.Fine -> f.type == (s as Expense.Fine).type
                    is Expense.Maintenance -> f.mileage == (s as Expense.Maintenance).mileage && f.type == s.type
                }
    }

    @Test
    @Throws(Exception::class)
    fun updateExpenseTest() {
        val expense = Expense.Fuel(Date(), 100F, 500F, 1000F)
        val savedExpenseId = localDataSource.create(expense).blockingGet()
        expense.id = savedExpenseId

        val updatedExpense = Expense.Fuel(Date(), 300F, 600F, 1090F)
        updatedExpense.id = savedExpenseId
        localDataSource.update(updatedExpense).blockingAwait()

        val filter = ExpenseFilter.Fuel
        val loadedExpense = loadExpense(filter).firstOrNull { it.id == savedExpenseId }
        assert(expensesEquals(updatedExpense, loadedExpense!!))
    }

    private fun loadExpense(filter: ExpenseFilter): List<Expense> {
        return localDataSource
            .get(filter)
            .blockingFirst()
    }
}
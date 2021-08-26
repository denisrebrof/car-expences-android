package com.upreality.car.expenses.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.upreality.car.expenses.data.sync.room.expenses.ExpensesLocalDAO
import com.upreality.car.expenses.data.sync.room.expenses.converters.RoomExpenseFilterConverter
import com.upreality.car.expenses.data.sync.room.expenses.dao.ExpenseDetailsDao
import com.upreality.car.expenses.data.sync.room.expenses.model.ExpenseRoom
import com.upreality.car.expenses.domain.model.ExpenseFilter
import domain.RequestPagingState
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class ExpensesLocalDAOTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    //it's just for Junit to execute tasks synchronously

    @get:Rule
    val databaseRule = ExpensesDatabaseTestRule()

    private lateinit var localDAO: ExpensesLocalDAO

    @Before
    fun setUp() {
        databaseRule.db.apply {
            val expensesDao = getExpensesDAO()
            val expenseDetailsDao = ExpenseDetailsDao(
                getFinesDAO(),
                getFuelDAO(),
                getMaintenanceDAO()
            )
            localDAO = ExpensesLocalDAO(expensesDao, expenseDetailsDao)
        }
    }

    @Test
    @Throws(Exception::class)
    fun writeAndReadExpenseTest() {
        val expense = ExpenseRoom.Fuel(Date(), 100F, 500F, 1000F)
        val id = localDAO.create(expense).blockingGet()
        expense.id = id
        val requestResult = loadExpense().firstOrNull { it.id == id }
        assert(expensesEquals(expense, requestResult!!))
    }

    private fun expensesEquals(f: ExpenseRoom, s: ExpenseRoom): Boolean {
        return f.date == s.date && f.cost == s.cost && f::class == s::class &&
                when (f) {
                    is ExpenseRoom.Fuel -> f.liters == (s as ExpenseRoom.Fuel).liters && f.mileage == s.mileage
                    is ExpenseRoom.Fine -> f.type == (s as ExpenseRoom.Fine).type
                    is ExpenseRoom.Maintenance -> f.mileage == (s as ExpenseRoom.Maintenance).mileage && f.type == s.type
                }
    }

    @Test
    @Throws(Exception::class)
    fun updateExpenseTest() {
        val expense = ExpenseRoom.Fuel(Date(), 100F, 500F, 1000F)
        val savedExpenseId = localDAO.create(expense).blockingGet()
        expense.id = savedExpenseId

        val updatedExpense = ExpenseRoom.Fuel(Date(), 300F, 600F, 1090F)
        updatedExpense.id = savedExpenseId
        localDAO.update(updatedExpense).blockingAwait()

        val loadedExpense = loadExpense().firstOrNull { it.id == savedExpenseId }
        assert(expensesEquals(updatedExpense, loadedExpense!!))
    }

    private fun loadExpense(): List<ExpenseRoom> {
        val filters = ExpenseFilter.All.let(::listOf)
        val pagingState = RequestPagingState.Undefined
        val localFilter = RoomExpenseFilterConverter.convert(filters, pagingState)
        return localDAO
            .get(localFilter)
            .blockingFirst()
    }
}
package com.upreality.car.expenses.data.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class ExpensesDatabaseTestRule : TestRule {

    lateinit var db: ExpensesDB

    override fun apply(base: Statement, description: Description) = DatabaseStatement(base)

    inner class DatabaseStatement(private val base: Statement) : Statement() {
        @Throws(Throwable::class)
        override fun evaluate() {
            createDb()
            try {
                base.evaluate()
            } finally {
                db.close()
            }
        }

        private fun createDb() {
            val context = ApplicationProvider.getApplicationContext<Context>()
            db = Room.inMemoryDatabaseBuilder(
                context, ExpensesDB::class.java
            ).build()
        }
    }
}
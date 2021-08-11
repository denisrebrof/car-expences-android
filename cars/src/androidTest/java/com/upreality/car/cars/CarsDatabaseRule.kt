package com.upreality.car.cars

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.upreality.car.cars.data.database.CarsDB
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class CarsDatabaseRule : TestRule {

    lateinit var db: CarsDB

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
                context, CarsDB::class.java
            ).build()
        }
    }
}
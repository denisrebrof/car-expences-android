package com.upreality.car.expenses.data.database

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FailingDebugTest {
    @Test
    @Throws(Exception::class)
    fun fail(){
        assert(false)
    }
}
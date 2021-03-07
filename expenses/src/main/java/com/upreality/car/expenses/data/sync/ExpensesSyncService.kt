package com.upreality.car.expenses.data.sync

import com.upreality.car.common.data.time.TimeDataSource
import com.upreality.car.expenses.data.sync.expensesinfo.ExpensesInfoLocalDataSource
import javax.inject.Inject

class ExpensesSyncService @Inject constructor(
    private val infoDS: ExpensesInfoLocalDataSource,
    private val timeDataSource: TimeDataSource
)
package com.upreality.car.common.data.time

import java.util.*

class TimeLocalDAO {
    fun getTime(): Long = Calendar.getInstance().time.time
}
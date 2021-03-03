package com.upreality.car.common.data.time

import java.util.*
import javax.inject.Inject

class TimeLocalDAO @Inject constructor() {
    fun getTime(): Long = Calendar.getInstance().time.time
}
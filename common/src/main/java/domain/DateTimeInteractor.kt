package domain

import android.text.format.DateUtils
import java.util.*
import javax.inject.Inject

class DateTimeInteractor @Inject constructor() {

    fun isToday(date: Date) = DateUtils.isToday(date.time)

    fun isYesterday(date: Date) = DateUtils.isToday(date.time + DateUtils.DAY_IN_MILLIS)

    fun getToday(): Date = Calendar.getInstance().time

    fun getYesterday(): Date = getDaysAgo(1)

    fun getDaysAgo(days: Int): Date = Calendar.getInstance().apply {
        add(Calendar.DATE, -days)
    }.time

    fun getTimeAgo()
}
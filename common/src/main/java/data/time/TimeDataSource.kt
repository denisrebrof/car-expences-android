package data.time

import io.reactivex.Maybe
import java.util.*
import javax.inject.Inject

class TimeDataSource @Inject constructor(
    private val localDAO: TimeLocalDAO,
    private val timeRemoteDAO: TimeRemoteDAO
) {
    //TODO: implement correct time check
    fun getTime(): Maybe<Date> {
        return timeRemoteDAO.getTime().map(::Date)
    }
}
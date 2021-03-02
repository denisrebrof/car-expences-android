package com.upreality.car.common.data.time

import com.google.firebase.Timestamp
import io.reactivex.Maybe

class TimeRemoteDAO {
    fun getTime(): Maybe<Long>{
        return Maybe.just(Timestamp.now().toDate().time)
    }
}
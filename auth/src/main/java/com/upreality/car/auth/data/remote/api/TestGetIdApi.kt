package com.upreality.car.auth.data.remote.api

import com.upreality.car.auth.data.remote.model.TestGetIdResponse
import io.reactivex.Maybe
import retrofit2.http.GET

interface TestGetIdApi {
    @GET("/expenses")
    fun getId(): Maybe<TestGetIdResponse>
}
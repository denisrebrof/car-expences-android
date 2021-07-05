package com.upreality.car.auth.data.remote.api

import com.upreality.car.auth.data.remote.model.AccountResponse
import io.reactivex.Maybe
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthAPI {
    @FormUrlEncoded
    @POST("/accounts/google-sign-in")
    fun googleSignIn(@Field("token") token: String): Maybe<AccountResponse>
}
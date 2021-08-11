package com.upreality.car.auth.data.remote.api

import com.upreality.car.auth.data.remote.model.TokenResponse
import io.reactivex.Maybe
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface TokenRefreshApi {
    @FormUrlEncoded
    @POST("accounts/refresh-token")
    fun refreshAccessToken(
        @Field("refreshToken") refreshToken: String?
    ): Maybe<TokenResponse>
}
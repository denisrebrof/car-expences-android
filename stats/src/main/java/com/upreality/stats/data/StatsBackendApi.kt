package com.upreality.stats.data

import io.reactivex.Flowable
import retrofit2.http.Body
import retrofit2.http.POST

interface StatsBackendApi {
    @POST("/stats/rate-per-mile")
    fun getRatePerMile(@Body params: StatsBackendRequest): Flowable<Float>

    @POST("/stats/rate-per-liter")
    fun getRatePerLiter(@Body params: StatsBackendRequest): Flowable<Float>

    @POST("/stats/types-rate-map")
    fun getTypesRateMap(@Body params: StatsBackendRequest): Flowable<StatsTypeRateResponse>

    @POST("/stats/rate")
    fun getRate(@Body params: StatsBackendRequest): Flowable<Float>
}
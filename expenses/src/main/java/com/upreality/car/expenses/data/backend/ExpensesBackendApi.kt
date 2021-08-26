package com.upreality.car.expenses.data.backend

import com.upreality.car.expenses.data.backend.model.ExpenseBackendModel
import com.upreality.car.expenses.data.backend.model.ExpensesBackendRequest
import io.reactivex.Completable
import io.reactivex.Flowable
import retrofit2.http.*

interface ExpensesBackendApi {
    @FormUrlEncoded
    @POST("/expenses")
    fun create(@Field("expense") expense: ExpenseBackendModel): Completable

    @FormUrlEncoded
    @PUT("/expenses")
    fun update(@Field("expense") expense: ExpenseBackendModel): Completable

    @FormUrlEncoded
    @DELETE
    fun delete(@Field("id") id: Long): Completable

    @FormUrlEncoded
    @GET
    fun get(@Field("params") params: ExpensesBackendRequest): Flowable<List<ExpenseBackendModel>>
}
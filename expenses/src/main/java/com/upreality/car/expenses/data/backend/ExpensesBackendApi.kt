package com.upreality.car.expenses.data.backend

import com.upreality.car.expenses.data.backend.model.ExpenseBackendModel
import com.upreality.car.expenses.data.backend.model.ExpensesBackendRequest
import io.reactivex.Completable
import io.reactivex.Flowable
import retrofit2.http.*

interface ExpensesBackendApi {
    @POST("/expenses")
    fun create(@Body expense: ExpenseBackendModel): Completable

    @PUT("/expenses")
    fun update(@Body expense: ExpenseBackendModel): Completable

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "expenses",hasBody = true)
    fun delete(@Field("id") id: Long): Completable

    @POST("/expenses/get")
    fun get(@Body params: ExpensesBackendRequest): Flowable<List<ExpenseBackendModel>>
}
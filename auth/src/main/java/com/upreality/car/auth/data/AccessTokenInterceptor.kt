package com.upreality.car.auth.data

import com.upreality.car.auth.data.local.TokenDAO
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

class AccessTokenInterceptor @Inject constructor(
    private val tokenDao: TokenDAO
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenDao.get(TokenDAO.TokenType.ACCESS)
        val newRequest: Request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        return chain.proceed(newRequest)
    }
}
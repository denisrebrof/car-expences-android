package com.upreality.car.auth.data.remote

import com.upreality.car.auth.data.local.TokenDAO
import com.upreality.car.auth.data.remote.api.TokenRefreshApi
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private var tokenDAO: TokenDAO
) : Authenticator {

    private lateinit var tokenApi: TokenRefreshApi

    fun setTokenApi(api: TokenRefreshApi) {
        tokenApi = api
    }

    override fun authenticate(route: Route?, response: Response): Request? {

        val storedRefreshToken = tokenDAO.get(TokenDAO.TokenType.REFRESH)
        val storedAccessToken = tokenDAO.get(TokenDAO.TokenType.ACCESS)
        val refreshResponse = kotlin.runCatching {
            tokenApi.refreshAccessToken(storedRefreshToken).blockingGet()
        }.getOrNull()

        return refreshResponse?.let { tokenResponse ->
            tokenResponse.access_token?.let { tokenDAO.set(it, TokenDAO.TokenType.ACCESS) }
            tokenResponse.refresh_token?.let { tokenDAO.set(it, TokenDAO.TokenType.REFRESH) }

            response.request().newBuilder()
                .header("Authorization", "Bearer ${tokenResponse.access_token}")
                .build()
        }
    }
}
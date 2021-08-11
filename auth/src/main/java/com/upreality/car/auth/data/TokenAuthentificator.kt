package com.upreality.car.auth.data

import com.upreality.car.auth.data.local.TokenDAO
import com.upreality.car.auth.data.remote.api.TokenRefreshApi
import com.upreality.car.auth.domain.AuthState
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val authLocalDataSource: IAuthLocalDataSource,
    private var tokenDAO: TokenDAO
) : Authenticator {

    private lateinit var tokenApi: TokenRefreshApi

    fun setTokenApi(api: TokenRefreshApi) {
        tokenApi = api
    }

    override fun authenticate(route: Route?, response: Response): Request? {
        val storedRefreshToken = tokenDAO.get(TokenDAO.TokenType.REFRESH)
        val storedAccessToken = tokenDAO.get(TokenDAO.TokenType.ACCESS)
        val refreshResponseResult = kotlin.runCatching {
            tokenApi.refreshAccessToken(storedRefreshToken).blockingGet()
        }
        val refreshResponse = refreshResponseResult.getOrNull()
        refreshResponse ?: authLocalDataSource.setAuthState(AuthState.Unauthorized)
        return refreshResponse?.let { tokenResponse ->
            tokenResponse.access_token?.let { tokenDAO.set(it, TokenDAO.TokenType.ACCESS) }
            tokenResponse.refresh_token?.let { tokenDAO.set(it, TokenDAO.TokenType.REFRESH) }

            response.request().newBuilder()
                .header("Authorization", "Bearer ${tokenResponse.access_token}")
                .build()
        }
    }
}
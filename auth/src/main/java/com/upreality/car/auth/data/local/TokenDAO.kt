package com.upreality.car.auth.data.local

import android.content.Context
import javax.inject.Inject

class TokenDAO @Inject constructor(
    prefsContext: Context
) {

    companion object {
        const val PREFS_KEY = "AuthToken"
        const val ACCESS_PREF_KEY = "Access"
        const val REFRESH_PREF_KEY = "Refresh"
    }

    private val prefs = prefsContext.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)

    fun get(type: TokenType): String? {
        val key = getPrefKey(type)
        return prefs.getString(key, null)
    }

    fun set(value: String, type: TokenType) {
        val key = getPrefKey(type)
        prefs.edit().putString(key, value).apply()
    }

    private fun getPrefKey(type: TokenType): String{
        return when(type){
            TokenType.ACCESS -> ACCESS_PREF_KEY
            TokenType.REFRESH -> REFRESH_PREF_KEY
        }
    }

    enum class TokenType {
        ACCESS,
        REFRESH
    }

}


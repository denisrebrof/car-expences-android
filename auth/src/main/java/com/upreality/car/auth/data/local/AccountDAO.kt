package com.upreality.car.auth.data.local

import android.content.Context
import javax.inject.Inject

class AccountDAO @Inject constructor(
    prefsContext: Context
) {
    companion object {
        const val PREFS_KEY = "Account"
        const val FIRST_NAME_PREF_KEY = "FirstName"
        const val LAST_NAME_PREF_KEY = "LastName"
    }

    private val prefs = prefsContext.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)

    fun get(field: AccountField): String? {
        val key = getPrefKey(field)
        return prefs.getString(key, null)
    }

    fun set(value: String, field: AccountField) {
        val key = getPrefKey(field)
        prefs.edit().putString(key, value).apply()
    }

    private fun getPrefKey(field: AccountField): String {
        return when (field) {
            AccountField.FIRST_NAME -> FIRST_NAME_PREF_KEY
            AccountField.LAST_NAME -> LAST_NAME_PREF_KEY
        }
    }

    enum class AccountField {
        FIRST_NAME,
        LAST_NAME
    }
}
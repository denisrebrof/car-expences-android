package com.upreality.car.auth.data.local

import android.content.Context
import com.upreality.car.auth.domain.AuthType
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.processors.BehaviorProcessor
import javax.inject.Inject

class LastAuthStateDAOImpl @Inject constructor(
    prefsContext: Context
) : ILastAuthStateDAO {

    companion object {
        const val PREFS_KEY = "Auth"
        const val LAST_STATE_PREF_KEY = "LastAuthState"
    }

    private val prefs = prefsContext.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
    private val initialAuthType = prefs.getInt(LAST_STATE_PREF_KEY, AuthType.UNDEFINED.id)
    private val prefSubject = BehaviorProcessor.createDefault(initialAuthType)

    override fun get(): Flowable<AuthType> {
        if (!prefs.contains(LAST_STATE_PREF_KEY)) {
            prefs.edit().putInt(LAST_STATE_PREF_KEY, AuthType.UNDEFINED.id).apply()
        }
        return prefSubject.map(this::getAuthType)
    }

    override fun set(authType: AuthType): Completable {
        if (prefs.getInt(LAST_STATE_PREF_KEY, AuthType.UNDEFINED.id) != authType.id) {
            prefs.edit().putInt(LAST_STATE_PREF_KEY, authType.id).apply()
            prefSubject.onNext(authType.id)
        }
        return Completable.complete()
    }

    private fun getAuthType(typeId: Int): AuthType {
        return AuthType.values().firstOrNull { it.id == typeId } ?: AuthType.UNDEFINED
    }
}


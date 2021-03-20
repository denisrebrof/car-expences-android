package com.upreality.car.expenses.data.sync.datasources

import android.content.Context
import android.content.SharedPreferences
import com.upreality.car.expenses.data.sync.IExpensesSyncTimestampProvider
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.processors.BehaviorProcessor
import javax.inject.Inject

class ExpensesSyncTimestampProvider @Inject constructor(
    prefsContext: Context
) : IExpensesSyncTimestampProvider {

    companion object {
        const val PREFS_KEY = "TS"
        const val TS_PREF_KEY = "Timestamp"
    }

    private val prefs: SharedPreferences =
        prefsContext.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
    private val prefSubject = BehaviorProcessor.createDefault(prefs.getLong(TS_PREF_KEY, 0L))

    private val prefChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, _ ->
            prefSubject.onNext(sharedPreferences.getLong(TS_PREF_KEY, 0L))
        }


    override fun get(): Flowable<Long> {
        if (!prefs.contains(TS_PREF_KEY)) {
            prefs.edit().putLong(TS_PREF_KEY, 0L).apply()
        }
        return prefSubject
    }

    override fun set(timestamp: Long): Completable {
        prefs.edit().putLong(TS_PREF_KEY, timestamp).apply()
        prefSubject.onNext(timestamp)
        return Completable.complete()
    }
}
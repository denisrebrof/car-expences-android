package domain

import android.util.Log
import io.reactivex.*
import io.reactivex.disposables.Disposable


inline fun <reified T> Maybe<T>.subscribeWithLogError(): Disposable {
    return doOnError { handleThrowable(it) }
        .onErrorComplete()
        .subscribe()
}

inline fun <reified T> Maybe<T>.subscribeWithLogError(noinline consumer: (T) -> Unit): Disposable {
    return doOnError {
        handleThrowable(it)
    }
        .onErrorComplete()
        .subscribe(consumer)
}

inline fun <reified T> Single<T>.subscribeWithLogError(noinline consumer: (T) -> Unit): Disposable {
    return subscribe(consumer) {
        handleThrowable(it)
    }
}

inline fun <reified T> Flowable<T>.subscribeWithLogError(noinline consumer: (T) -> Unit): Disposable {
    return subscribe(consumer) {
        handleThrowable(it)
    }
}

inline fun <reified T> Flowable<T>.subscribeWithLogError(): Disposable {
    return subscribe({}) {
        handleThrowable(it)
    }
}

inline fun <reified T> Observable<T>.subscribeWithLogError(noinline consumer: (T) -> Unit = {}): Disposable {
    return subscribe(consumer) {
        handleThrowable(it)
    }
}

fun Completable.subscribeWithLogError(): Disposable {
    return onErrorComplete {
        handleThrowable(it)
        false
    }.subscribe()
}

fun handleThrowable(th: Throwable) {
//    if (BuildConfig.DEBUG) throw th else CrashCollector.logException(th)
    Log.e("RxError", th.toString())
}


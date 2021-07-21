package presentation

import domain.handleThrowable
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

object RxLifecycleExtentions {
    fun <ValueType : Any> Flowable<ValueType>.subscribeDefault(consumer: (ValueType) -> Unit): Disposable {
        return this.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(consumer) {
                handleThrowable(it)
            }
    }
}
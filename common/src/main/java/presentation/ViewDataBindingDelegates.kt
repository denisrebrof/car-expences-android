package presentation

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import kotlin.reflect.KProperty

class BindActivity<in R : Activity, out T : ViewDataBinding>(
    @LayoutRes private val layoutRes: Int
) {
    private var value: T? = null

    operator fun getValue(thisRef: R, property: KProperty<*>): T {
        if (value == null) {
            value = DataBindingUtil.setContentView(thisRef, layoutRes)
        }
        return value!!
    }
}

class BindFragment<in R : Fragment, out T : ViewDataBinding>(
    @LayoutRes private val layoutRes: Int
) {
    private val clearBindingHandler by lazy(LazyThreadSafetyMode.NONE) { Handler(Looper.getMainLooper()) }
    private var value: T? = null

    operator fun getValue(thisRef: R, property: KProperty<*>): T {
        if (value == null) {
            value = DataBindingUtil.inflate(
                thisRef.layoutInflater, layoutRes,
                thisRef.view?.rootView as ViewGroup?, false
            )
            thisRef.viewLifecycleOwnerLiveData.observe(thisRef) { viewLifecycleOwner ->
                viewLifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
                    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                    fun onDestroy() {
                        // Lifecycle listeners are called before onDestroyView in a Fragment.
                        // However, we want views to be able to use bindings in onDestroyView
                        // to do cleanup so we clear the reference one frame later.
                        clearBindingHandler.post {
                            value = null
                        }
                    }
                })
            }
        }
        return value!!
    }
}
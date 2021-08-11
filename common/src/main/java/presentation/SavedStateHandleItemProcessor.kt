package presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.BehaviorProcessor
import kotlin.reflect.KProperty

class SavedStateHandleItemProcessor<
        in R : ViewModel,
        out T : BehaviorProcessor<VALUE_TYPE>,
        VALUE_TYPE : Any,
        > constructor(
    private val savedStateHandle: SavedStateHandle,
    private val compositeDisposable: CompositeDisposable,
    private val defaultValue: VALUE_TYPE? = null,
) {
    private var value: T? = null

    @Suppress("UNCHECKED_CAST")
    operator fun getValue(thisRef: R, property: KProperty<*>): T {
        if (value == null) {
            val savedValue = savedStateHandle.get<VALUE_TYPE>(property.name)
            val defaultProcessorValue = savedValue ?: defaultValue
            val processor = defaultProcessorValue?.let { savedValue ->
                BehaviorProcessor.createDefault(savedValue)
            } ?: BehaviorProcessor.create()
            processor.subscribe { item ->
                savedStateHandle.set(property.name, item)
            }.let(compositeDisposable::add)
            value = processor as T
        }
        return value!!
    }
}

class SavedStateItemDelegate<in R : ViewModel, T : Any?> constructor(
    private val savedStateHandle: SavedStateHandle,
    private val default: T? = null,
) {
    operator fun getValue(thisRef: R, property: KProperty<*>): T? {
        return savedStateHandle.get<T>(property.name) ?: default
    }

    operator fun setValue(thisRef: R, property: KProperty<*>, value: T) {
        savedStateHandle.set<T>(property.name, value)
    }
}

class SavedStateRequireItemDelegate<in R : ViewModel, T : Any> constructor(
    private val savedStateHandle: SavedStateHandle,
    private val default: T
) {
    operator fun getValue(thisRef: R, property: KProperty<*>): T {
        return savedStateHandle.get<T>(property.name) ?: default
    }

    operator fun setValue(thisRef: R, property: KProperty<*>, value: T) {
        savedStateHandle.set<T>(property.name, value)
    }
}
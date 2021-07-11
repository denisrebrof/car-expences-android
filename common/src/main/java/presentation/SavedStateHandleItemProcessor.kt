package presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import io.reactivex.processors.BehaviorProcessor
import kotlin.reflect.KProperty

class SavedStateHandleItemProcessor<
        in R : ViewModel,
        out T : BehaviorProcessor<VALUE_TYPE>,
        VALUE_TYPE : Any,
        > constructor(
    private val savedStateHandle: SavedStateHandle,
    private val handleValueKey: String? = null,
    private val defaultValue: VALUE_TYPE? = null,
) {
    private var value: T? = null

    @Suppress("UNCHECKED_CAST")
    operator fun getValue(thisRef: R, property: KProperty<*>): T {
        if (value == null) {
            val key = handleValueKey ?: property.name
            val defaultProcessorValue = savedStateHandle.get<VALUE_TYPE>(key) ?: defaultValue
            val processor = defaultProcessorValue?.let { savedValue ->
                BehaviorProcessor.createDefault(savedValue)
            } ?: BehaviorProcessor.create()
            value = processor.doOnNext { flowValue ->
                savedStateHandle.set(key, flowValue)
            } as T
        }
        return value!!
    }
}

class SavedStateItemNullable<in R : ViewModel, T : Any?> constructor(
    private val savedStateHandle: SavedStateHandle,
    private val handleValueKey: String,
) {
    operator fun getValue(thisRef: R, property: KProperty<*>): T? {
        return savedStateHandle.get<T>(handleValueKey)
    }

    operator fun setValue(thisRef: R, property: KProperty<*>, value: T?) {
        savedStateHandle.set<T>(handleValueKey, value)
    }
}

class SavedStateItem<in R : ViewModel, T : Any> constructor(
    private val savedStateHandle: SavedStateHandle,
    private val handleValueKey: String,
    private val default: T,
) {
    operator fun getValue(thisRef: R, property: KProperty<*>): T {
        return savedStateHandle.get<T>(handleValueKey) ?: default
    }

    operator fun setValue(thisRef: R, property: KProperty<*>, value: T) {
        savedStateHandle.set<T>(handleValueKey, value)
    }
}
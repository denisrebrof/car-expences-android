package presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.processors.FlowableProcessor
import io.sellmair.disposer.disposeBy
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import kotlin.reflect.KProperty

class SavedStateHandleItemProcessor<
        in R : ViewModel,
        out T : FlowableProcessor<VALUE_TYPE>,
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
            processor.getValues()
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
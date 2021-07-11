package presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import io.reactivex.Flowable
import io.reactivex.processors.BehaviorProcessor
import kotlin.reflect.KProperty

class SavedStateItemProcessorWrapper<VALUE_TYPE>(
    private val savedStateHandle: SavedStateHandle,
    private val key: String,
    defaultValue: VALUE_TYPE? = null
) {

    private var initialValue = savedStateHandle.get<VALUE_TYPE>(key) ?: defaultValue

    private val processor = initialValue
        ?.let { value -> BehaviorProcessor.createDefault(value) }
        ?: BehaviorProcessor.create()

    val flow: Flowable<VALUE_TYPE> by lazy(this::processor)

    fun getValue(): VALUE_TYPE? = processor.value

    fun setValue(value: VALUE_TYPE) {
        savedStateHandle.set(key, value)
        processor.onNext(value)
    }
}

class SavedStateItemProcessorWrapperDelegate<in R : ViewModel, VALUE_TYPE, out T : SavedStateItemProcessorWrapper<VALUE_TYPE>> constructor(
    private val savedStateHandle: SavedStateHandle,
    private val defaultValue: VALUE_TYPE? = null
) {
    val value: T? = null

    @Suppress("UNCHECKED_CAST")
    operator fun getValue(
        thisRef: R,
        property: KProperty<*>
    ): T {
        return value ?: SavedStateItemProcessorWrapper(
            savedStateHandle,
            property.name,
            defaultValue
        ) as T
    }
}


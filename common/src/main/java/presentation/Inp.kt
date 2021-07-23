package presentation

import io.reactivex.Flowable
import io.reactivex.processors.BehaviorProcessor
import kotlin.reflect.KClass

sealed class InpState<out INPUT_TYPE : Any, out OUT_TYPE : Any>(val input: INPUT_TYPE? = null) {
    object Empty : InpState<Nothing, Nothing>()

    data class Invalid<out INPUT_TYPE : Any>(
        val reason: String? = null,
        val inp: INPUT_TYPE?
    ) : InpState<INPUT_TYPE, Nothing>(inp)

    data class Valid<out INPUT_TYPE : Any, out OUT_TYPE : Any>(
        val inp: INPUT_TYPE?,
        val out: OUT_TYPE
    ) : InpState<INPUT_TYPE, OUT_TYPE>(inp)

    fun validOrNull(): Valid<INPUT_TYPE, OUT_TYPE>? {
        return this as? Valid
    }
}

sealed class TypedInpState<out INPUT_TYPE : Any> : InpState<INPUT_TYPE, INPUT_TYPE>()

class InpForm {

    private var fieldsSet = hashSetOf<FieldMapEntry<*, *>>()

    fun getStateMapFlow(): Flowable<FormStateMap> {
        val keysToStatesFlows = fieldsSet.map { entry ->
            entry.field.getInputStateFlow().map { entry.key to it }
        }
        return Flowable.combineLatest(keysToStatesFlows) {
            val keysToStates = (it as? Array<Pair<FieldKey<*, *>, InpState<*, *>>>) ?: emptyArray()
            val stateToKeysMap = mapOf(*keysToStates)
            object : FormStateMap {
                override fun <ValueType : Any, OutType : Any> getFieldState(
                    key: FieldKey<ValueType, OutType>
                ): FormStateMap.FieldStateRequestResult<ValueType, OutType> {
                    if (!stateToKeysMap.containsKey(key))
                        return FormStateMap.FieldStateRequestResult.FieldNotFound()

                    val inputState = stateToKeysMap[key] as InpState<ValueType, OutType>
                    return FormStateMap.FieldStateRequestResult.Success(inputState)
                }
            }
        }
    }

    fun <ValueType : Any, OutType : Any> submitValue(
        key: FieldKey<ValueType, OutType>,
        value: ValueType
    ): SubmitFieldValueResult {
        val field = fieldsSet.firstOrNull { entry -> entry.key == key }?.field
        val typedField = (field as? FormField<ValueType, OutType>)
            ?: return SubmitFieldValueResult.FieldNotFound
        typedField.submitInput(value)
        return SubmitFieldValueResult.Success
    }

    enum class SubmitFieldValueResult {
        Success,
        FieldNotFound
    }

    interface FormStateMap {
        fun <ValueType : Any, OutType : Any> getFieldState(
            key: FieldKey<ValueType, OutType>
        ): FieldStateRequestResult<ValueType, OutType>

        sealed class FieldStateRequestResult<out ValueType : Any, out OutType : Any> {
            data class Success<out ValueType : Any, out OutType : Any>(
                val state: InpState<ValueType, OutType>
            ) : FieldStateRequestResult<ValueType, OutType>()

            class FieldNotFound<out ValueType : Any, out OutType : Any> :
                FieldStateRequestResult<ValueType, OutType>()
        }
    }

    abstract class FieldKey<ValueType : Any, out OutType : Any>

    private class FieldMapEntry<ValueType : Any, OutType : Any>(
        val key: FieldKey<ValueType, OutType>,
        val field: FormField<ValueType, OutType>
    ) {
        override fun hashCode() = key.hashCode()
    }
}

class FormField<ValueType : Any, OutType : Any>(
    private val validator: (ValueType) -> InpState<ValueType, OutType>,
    private var value: ValueType? = null,
    private val type: KClass<ValueType>
) {
    private val defaultInputState = value?.let(validator) ?: InpState.Empty
    private val inputState = BehaviorProcessor.createDefault(defaultInputState)

    fun submitInput(input: ValueType) {
        if (this.value == input)
            return
        this.value = input
        validator(input).let(inputState::onNext)
    }

    fun getInputStateFlow(): Flowable<InpState<ValueType, OutType>> {
        return inputState
    }
}
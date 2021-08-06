package presentation

import io.reactivex.Flowable
import io.reactivex.processors.BehaviorProcessor

sealed class ValidationResult<out INPUT_TYPE : Any?, out OUT_TYPE : Any>(val input: INPUT_TYPE? = null) {
    object Empty : ValidationResult<Nothing, Nothing>()

    data class Invalid<out INPUT_TYPE : Any>(
        val inp: INPUT_TYPE?,
        val reason: String? = null
    ) : ValidationResult<INPUT_TYPE, Nothing>(inp)

    data class Valid<out INPUT_TYPE : Any?, out OUT_TYPE : Any>(
        val inp: INPUT_TYPE,
        val out: OUT_TYPE
    ) : ValidationResult<INPUT_TYPE, OUT_TYPE>(inp)

    fun validValueOrNull(): OUT_TYPE? {
        return (this as? Valid)?.out
    }

    fun requireValid() = validValueOrNull()!!
}

class InputForm {

    companion object {
        fun <ValueType : Any> validateNotNull(
            value: ValueType?
        ): ValidationResult<ValueType, ValueType> {
            if (value == null)
                return ValidationResult.Empty
            return ValidationResult.Valid(value, value)
        }
    }

    private var fieldsSet = hashSetOf<FieldMapEntry<*, *>>()

    fun <ValueType : Any, OutType : Any> createField(
        key: FieldKey<ValueType, OutType>,
        validator: (ValueType?) -> ValidationResult<ValueType, OutType>,
        defaultValue: ValueType? = null
    ) = FieldMapEntry(
        key,
        FormField(validator, defaultValue)
    ).let(fieldsSet::add)

    @Suppress("UNCHECKED_CAST")
    fun getStateMapFlow(): Flowable<FormStateMap> {
        val keysToStatesFlows = fieldsSet.map { entry ->
            entry.field.getInputStateFlow().map { entry.key to it }
        }
        return Flowable.combineLatest(keysToStatesFlows) {
            val keysToStates = it.map { pair ->
                pair as Pair<FieldKey<*, *>, ValidationResult<*, *>>
            }.toTypedArray()
            val stateToKeysMap = mapOf(*keysToStates)
            object : FormStateMap {
                override fun <ValueType : Any, OutType : Any> getFieldState(
                    key: FieldKey<ValueType, OutType>
                ): FormStateMap.FieldStateRequestResult<ValueType, OutType> {
                    if (!stateToKeysMap.containsKey(key))
                        return FormStateMap.FieldStateRequestResult.FieldNotFound()

                    val inputState = stateToKeysMap[key] as ValidationResult<ValueType, OutType>
                    return FormStateMap.FieldStateRequestResult.Success(inputState)
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <ValueType : Any, OutType : Any> submit(
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
                val state: ValidationResult<ValueType, OutType>
            ) : FieldStateRequestResult<ValueType, OutType>()

            class FieldNotFound<out ValueType : Any, out OutType : Any> :
                FieldStateRequestResult<ValueType, OutType>()

            fun getOrNull() = (this as? Success)?.state
        }
    }

    abstract class FieldKey<in ValueType : Any, in OutType : Any>

    private class FieldMapEntry<ValueType : Any, OutType : Any>(
        val key: FieldKey<ValueType, OutType>,
        val field: FormField<ValueType, OutType>
    ) {
        override fun hashCode() = key.hashCode()
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as FieldMapEntry<*, *>

            if (key != other.key) return false
            if (field != other.field) return false

            return true
        }
    }
}

internal class FormField<ValueType : Any?, OutType : Any>(
    private val validator: (ValueType) -> ValidationResult<ValueType, OutType>,
    private var value: ValueType? = null
) {
    private val defaultInputState = value?.let(validator) ?: ValidationResult.Empty
    private val inputState = BehaviorProcessor.createDefault(defaultInputState)

    fun submitInput(input: ValueType) {
        if (this.value == input)
            return
        this.value = input
        validator(input).let(inputState::onNext)
    }

    fun getInputStateFlow(): Flowable<ValidationResult<ValueType, OutType>> {
        return inputState
    }
}
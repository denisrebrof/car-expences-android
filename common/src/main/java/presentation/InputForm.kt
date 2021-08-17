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
        fun <VALUE_TYPE : Any> validateNotNull(
            value: VALUE_TYPE?
        ): ValidationResult<VALUE_TYPE, VALUE_TYPE> {
            if (value == null)
                return ValidationResult.Empty
            return ValidationResult.Valid(value, value)
        }
    }

    private var fieldsSet = hashSetOf<FieldMapEntry<*, *>>()

    fun <VALUE_TYPE : Any, OUT_TYPE : Any> createField(
        key: FieldKey<VALUE_TYPE, OUT_TYPE>,
        validator: (VALUE_TYPE?) -> ValidationResult<VALUE_TYPE, OUT_TYPE>,
        defaultValue: VALUE_TYPE? = null
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
                override fun <VALUE_TYPE : Any, OUT_TYPE : Any> getFieldState(
                    key: FieldKey<VALUE_TYPE, OUT_TYPE>
                ): FormStateMap.FieldStateRequestResult<VALUE_TYPE, OUT_TYPE> {
                    if (!stateToKeysMap.containsKey(key))
                        return FormStateMap.FieldStateRequestResult.FieldNotFound()

                    val inputState = stateToKeysMap[key] as ValidationResult<VALUE_TYPE, OUT_TYPE>
                    return FormStateMap.FieldStateRequestResult.Success(inputState)
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <VALUE_TYPE : Any, OUT_TYPE : Any> submit(
        key: FieldKey<VALUE_TYPE, OUT_TYPE>,
        value: VALUE_TYPE
    ): SubmitFieldValueResult {
        val field = fieldsSet.firstOrNull { entry -> entry.key == key }?.field
        val typedField = (field as? FormField<VALUE_TYPE, OUT_TYPE>)
            ?: return SubmitFieldValueResult.FieldNotFound
        typedField.submitInput(value)
        return SubmitFieldValueResult.Success
    }

    enum class SubmitFieldValueResult {
        Success,
        FieldNotFound
    }

    interface FormStateMap {
        fun <VALUE_TYPE : Any, OUT_TYPE : Any> getFieldState(
            key: FieldKey<VALUE_TYPE, OUT_TYPE>
        ): FieldStateRequestResult<VALUE_TYPE, OUT_TYPE>

        sealed class FieldStateRequestResult<out VALUE_TYPE : Any, out OUT_TYPE : Any> {
            data class Success<out VALUE_TYPE : Any, out OUT_TYPE : Any>(
                val state: ValidationResult<VALUE_TYPE, OUT_TYPE>
            ) : FieldStateRequestResult<VALUE_TYPE, OUT_TYPE>()

            class FieldNotFound<out VALUE_TYPE : Any, out OUT_TYPE : Any> :
                FieldStateRequestResult<VALUE_TYPE, OUT_TYPE>()

            fun getOrNull() = (this as? Success)?.state
        }
    }

    abstract class FieldKey<in VALUE_TYPE : Any, in OUT_TYPE : Any>

    private class FieldMapEntry<VALUE_TYPE : Any, OUT_TYPE : Any>(
        val key: FieldKey<VALUE_TYPE, OUT_TYPE>,
        val field: FormField<VALUE_TYPE, OUT_TYPE>
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

internal class FormField<VALUE_TYPE : Any?, OUT_TYPE : Any>(
    private val validator: (VALUE_TYPE) -> ValidationResult<VALUE_TYPE, OUT_TYPE>,
    private var value: VALUE_TYPE? = null
) {
    private val defaultInputState = value?.let(validator) ?: ValidationResult.Empty
    private val inputState = BehaviorProcessor.createDefault(defaultInputState)

    fun submitInput(input: VALUE_TYPE) {
        if (this.value == input)
            return
        this.value = input
        validator(input).let(inputState::onNext)
    }

    fun getInputStateFlow(): Flowable<ValidationResult<VALUE_TYPE, OUT_TYPE>> {
        return inputState
    }
}
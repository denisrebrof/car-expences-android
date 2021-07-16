package presentation

import io.reactivex.Flowable
import io.reactivex.processors.BehaviorProcessor

sealed class InputState<out T : Any> {
    object Empty : InputState<Nothing>()
    data class Invalid(val reason: String? = null) : InputState<Nothing>()
    data class Valid<out T : Any>(val input: T?) : InputState<T>()

    fun validOrNull(): Valid<T>? {
        return this as? Valid
    }
}

//class InputForm<FieldType : Any, FieldKey>(
//    private val fieldsMap: Map<FieldKey, FormField>
//) {
//    fun getFormValidFlow() {
//
//        fieldsMap.values
//            .map(FormField<FieldType>::getInputState)
//            .let(Flowable::combineLatest) {
//
//            }
//    }
//
//    abstract class FormValidator {
//        abstract fun validate(Map<FieldKey, FormField<FieldType>>)
//    }
//}

class FormField<ValueType : Any>(
    private val validator: Validator<ValueType> = NotNullValidator(),
    defaultValue: ValueType? = null
) {
    private val defaultInputState = validator.validate(defaultValue)
    private val inputState = BehaviorProcessor.createDefault(defaultInputState)

    fun getInputState(): Flowable<InputState<ValueType>> = inputState

    fun submitValue(value: ValueType) {
        validator.validate(value).let(inputState::onNext)
    }

    abstract class Validator<ValueType : Any> {
        fun validate(value: ValueType?): InputState<ValueType> = when {
            isEmpty(value) -> InputState.Empty
            isValid(value) -> InputState.Valid(value)
            else -> InputState.Invalid()
        }

        protected abstract fun isEmpty(value: ValueType?): Boolean
        protected abstract fun isValid(value: ValueType?): Boolean
    }

    class NullableValidator<ValueType : Any> : Validator<ValueType>() {
        override fun isEmpty(value: ValueType?) = value != null
        override fun isValid(value: ValueType?) = true
    }

    class NotNullValidator<ValueType : Any> : Validator<ValueType>() {
        override fun isEmpty(value: ValueType?) = true
        override fun isValid(value: ValueType?) = value != null
    }
}
package presentation

import android.os.Build
import androidx.annotation.RequiresApi
import io.reactivex.Flowable
import io.reactivex.processors.BehaviorProcessor
import presentation.InputForm.IFormField.RequestInputStateResult.Success
import kotlin.reflect.KClass
import kotlin.reflect.cast

sealed class InputState<out T : Any> {
    object Empty : InputState<Nothing>()
    data class Invalid<out T : Any>(val reason: String? = null, val input: T?) : InputState<T>()
    data class Valid<out T : Any>(val input: T?) : InputState<T>()

    fun validOrNull(): Valid<T>? {
        return this as? Valid
    }
}

@RequiresApi(Build.VERSION_CODES.N)
fun <ValueType : Any, OutType : Any, Key : InputForm.FieldKeys<ValueType>> InputForm<Key, OutType>.submitValue(
    value: ValueType,
    key: Key
): InputForm.SubmitFieldValueResult {
    return this.submitValue(key, value)
}

@RequiresApi(Build.VERSION_CODES.N)
class InputForm<Key : InputForm.FieldKey, OutType : Any>(
    fields: Map<Key, IFormField>
) {

    init {
        fields.forEach { (key, value) -> fieldsMap[key] = value }
    }

    private val fieldsMap = mutableMapOf<Key, IFormField>()

    internal fun <ValueType : Any> submitValue(
        key: Key,
        value: ValueType
    ): SubmitFieldValueResult {
        return fieldsMap.get(key.fieldId)
            ?.submitInput(value)
            ?.let(SubmitFieldValueResult::Success)
            ?: SubmitFieldValueResult.FieldNotFound
    }

    interface FieldKey {
        val fieldId: Int
    }

    abstract class FieldKeys<ValueType : Any>(val id: Int) : FieldKey {
        override val fieldId: Int = id
        internal fun <OutType : Any> submit(
            value: ValueType,
            form: InputForm<FieldKeys<ValueType>, OutType>
        ): SubmitFieldValueResult {
            return form.submitValue(this, value)
        }
    }

    sealed class SubmitFieldValueResult {
        data class Success(val result: IFormField.SubmitInputResult) : SubmitFieldValueResult()
        object FieldNotFound : SubmitFieldValueResult()
    }

    interface IFormField {

        fun <RequestValueType : Any> requestInputState(
            type: KClass<RequestValueType>
        ): RequestInputStateResult<RequestValueType>

        fun <SubmitValueType : Any> submitInput(
            input: SubmitValueType
        ): SubmitInputResult

        sealed class RequestInputStateResult<out ValueType : Any> {
            data class Success<ValueType : Any>(val value: Flowable<InputState<ValueType>>) :
                RequestInputStateResult<ValueType>()

            data class InvalidType<ValueType : Any>(val inputType: KClass<ValueType>) :
                RequestInputStateResult<Nothing>()
        }

        enum class SubmitInputResult {
            Success,
            InvalidType
        }
    }

    sealed class FormValue<OutType> {
        data class Valid<OutType>(val value: OutType) : FormValue<OutType>()
        object Invalid : FormValue<Nothing>()
        object Empty : FormValue<Nothing>()
    }

}

class ValidatedFormField<ValueType : Any>(
    private val validator: Validator<ValueType> = NotNullValidator(),
    private var value: ValueType? = null,
    private val type: KClass<ValueType>
) : InputForm.IFormField {
    private val defaultInputState = validator.validate(value)
    private val inputState = BehaviorProcessor.createDefault(defaultInputState)

    private fun submitValue(value: ValueType) {
        this.value = value
        validator.validate(value).let(inputState::onNext)
    }

    override fun <RequestValueType : Any> requestInputState(type: KClass<RequestValueType>): InputForm.IFormField.RequestInputStateResult<RequestValueType> {
        if (!type.isInstance(value)) {
            val valueClass = if (value != null) value!!::class else Nothing::class
            return InputForm.IFormField.RequestInputStateResult.InvalidType(valueClass)
        }

        return inputState.map { inputState ->
            when (inputState) {
                InputState.Empty -> InputState.Empty
                is InputState.Invalid -> InputState.Invalid(
                    inputState.reason,
                    type.cast(inputState)
                )
                is InputState.Valid -> InputState.Valid(
                    type.cast(inputState)
                )
            }
        }.let { Success(it) }
    }

    override fun <SubmitValueType : Any> submitInput(
        input: SubmitValueType
    ): InputForm.IFormField.SubmitInputResult {
        if (!type.isInstance(value)) {
            return InputForm.IFormField.SubmitInputResult.InvalidType
        }

        submitValue(type.cast(input))
        return InputForm.IFormField.SubmitInputResult.Success
    }

    abstract class Validator<ValueType : Any> {

        fun validate(value: ValueType?): InputState<ValueType> = when {
            isEmpty(value) -> InputState.Empty
            isValid(value) -> InputState.Valid(value)
            else -> InputState.Invalid(input = value)
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
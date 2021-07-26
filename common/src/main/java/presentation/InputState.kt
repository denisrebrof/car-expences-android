package presentation

import android.os.Build
import androidx.annotation.RequiresApi
import io.reactivex.Flowable
import io.reactivex.processors.BehaviorProcessor
import presentation.InputForm.IFormField.RequestInputStateResult.Success
import kotlin.reflect.KClass
import kotlin.reflect.cast

sealed class InputState<out T : Any>(val input: T? = null) {
    object Empty : InputState<Nothing>()
    data class Invalid<out T : Any>(val reason: String? = null, val inp: T?) : InputState<T>(inp)
    data class Valid<out T : Any>(val inp: T?) : InputState<T>(inp)

    fun validOrNull(): Valid<T>? {
        return this as? Valid
    }
}

@RequiresApi(Build.VERSION_CODES.N)
fun <ValueType : Any> InputForm.submit(
    value: ValueType,
    key: InputForm.FieldKeys<ValueType>
): InputForm.SubmitFieldValueResult {
    return this.submitValue(key, value)
}

@RequiresApi(Build.VERSION_CODES.N)
fun <ValueType : Any> InputForm.getFieldStateFlow(
    key: InputForm.FieldKeys<ValueType>,
    type: KClass<ValueType>
): InputForm.RequestFieldInputStateResult<ValueType> {
    return this.getFieldInputStateFlow(key, type)
}

fun <ValueType : Any> InputForm.FieldKeys<ValueType>.createFieldPair(type: KClass<ValueType>) =
    this to ValidatedFormField(type = type)

@RequiresApi(Build.VERSION_CODES.N)
class InputForm(vararg fields: Pair<FieldKey, IFormField>) {

    private val fieldsMap = mutableMapOf<FieldKey, IFormField>().apply {
        fields.forEach { (key, value) -> put(key, value) }
    }

    internal fun <ValueType : Any> submitValue(
        key: FieldKey,
        value: ValueType
    ): SubmitFieldValueResult {
        return fieldsMap.get(key = key)
            ?.submitInput(value)
            ?.let(SubmitFieldValueResult::Success)
            ?: SubmitFieldValueResult.FieldNotFound
    }

    internal fun <ValueType : Any> getFieldInputStateFlow(
        key: FieldKey,
        type: KClass<ValueType>
    ): RequestFieldInputStateResult<ValueType> {
        val field = fieldsMap.get(key = key) ?: return RequestFieldInputStateResult.FieldNotFound
        return field.requestInputState(type).let { formRequestResult ->
            when (formRequestResult) {
                is IFormField.RequestInputStateResult.InvalidType -> RequestFieldInputStateResult.InvalidType(
                    formRequestResult.inputType
                )
                is Success -> RequestFieldInputStateResult.Success(
                    formRequestResult.value
                )
            }
        }
    }

    interface FieldKey {
        val fieldId: Int
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

            data class InvalidType(val inputType: KClass<out Any>) :
                RequestInputStateResult<Nothing>()
        }

        enum class SubmitInputResult {
            Success,
            InvalidType
        }
    }

    abstract class FieldKeys<out ValueType : Any>(val id: Int) : FieldKey {
        override val fieldId: Int = id
    }

    sealed class SubmitFieldValueResult {
        data class Success(val result: IFormField.SubmitInputResult) : SubmitFieldValueResult()
        object FieldNotFound : SubmitFieldValueResult()
    }

    sealed class RequestFieldInputStateResult<out ValueType : Any> {
        data class Success<ValueType : Any>(val result: Flowable<InputState<ValueType>>) :
            RequestFieldInputStateResult<ValueType>()

        data class InvalidType(val inputType: KClass<out Any>) :
            RequestFieldInputStateResult<Nothing>()

        object FieldNotFound : RequestFieldInputStateResult<Nothing>()
    }
}

class ValidatedFormField<in ValueType : Any>(
    private val validator: Validator<ValueType> = NullableValidator(),
    private var value: ValueType? = null,
    private val type: KClass<ValueType>
) : InputForm.IFormField {
    private val defaultInputState = validator.validate(value)
    private val inputState = BehaviorProcessor.createDefault(defaultInputState)

    private fun submitValue(value: ValueType) {
        if (this.value == value)
            return
        this.value = value
        validator.validate(value).let(inputState::onNext)
    }

    override fun <RequestValueType : Any> requestInputState(type: KClass<RequestValueType>): InputForm.IFormField.RequestInputStateResult<RequestValueType> {
        if (this.type != type) {
            val valueClass = if (value != null) value!!::class else Nothing::class
            return InputForm.IFormField.RequestInputStateResult.InvalidType(valueClass)
        }

        return inputState.map { inputState ->
            when (inputState) {
                InputState.Empty -> InputState.Empty
                is InputState.Invalid -> InputState.Invalid(
                    inputState.reason,
                    inputState.input?.let(type::cast)
                )
                is InputState.Valid -> InputState.Valid(
                    inputState.input?.let(type::cast)
                )
            }
        }.let { Success(it) }
    }

    override fun <SubmitValueType : Any> submitInput(
        input: SubmitValueType
    ): InputForm.IFormField.SubmitInputResult {
        if (!type.isInstance(input)) {
            return InputForm.IFormField.SubmitInputResult.InvalidType
        }

        submitValue(type.cast(input))
        return InputForm.IFormField.SubmitInputResult.Success
    }

    abstract class Validator<ValueType : Any> {

        fun validate(value: ValueType?): InputState<ValueType> = when {
            isEmpty(value) -> InputState.Empty
            isValid(value) -> InputState.Valid(value)
            else -> InputState.Invalid(inp = value)
        }

        protected abstract fun isEmpty(value: ValueType?): Boolean
        protected abstract fun isValid(value: ValueType?): Boolean
    }

    class NullableValidator<ValueType : Any> : Validator<ValueType>() {
        override fun isEmpty(value: ValueType?) = value == null
        override fun isValid(value: ValueType?) = true
    }

    class NotNullValidator<ValueType : Any> : Validator<ValueType>() {
        override fun isEmpty(value: ValueType?) = true
        override fun isValid(value: ValueType?) = value != null
    }
}
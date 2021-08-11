package presentation

import android.os.Build
import androidx.annotation.RequiresApi
import io.reactivex.Flowable
import io.reactivex.processors.BehaviorProcessor
import presentation.InputFormLegacy.IFormField.RequestInputStateResult.Success
import kotlin.reflect.KClass
import kotlin.reflect.cast

sealed class InputStateLegacy<out T : Any>(val input: T? = null) {
    object Empty : InputStateLegacy<Nothing>()
    data class Invalid<out T : Any>(val reason: String? = null, val inp: T?) : InputStateLegacy<T>(inp)
    data class Valid<out T : Any>(val inp: T?) : InputStateLegacy<T>(inp)

    fun validOrNull(): Valid<T>? {
        return this as? Valid
    }
}

@RequiresApi(Build.VERSION_CODES.N)
fun <ValueType : Any> InputFormLegacy.submit(
    value: ValueType,
    key: InputFormLegacy.FieldKeys<ValueType>
): InputFormLegacy.SubmitFieldValueResult {
    return this.submitValue(key, value)
}

@RequiresApi(Build.VERSION_CODES.N)
fun <ValueType : Any> InputFormLegacy.getFieldStateFlow(
    key: InputFormLegacy.FieldKeys<ValueType>,
    type: KClass<ValueType>
): InputFormLegacy.RequestFieldInputStateResult<ValueType> {
    return this.getFieldInputStateFlow(key, type)
}

fun <ValueType : Any> InputFormLegacy.FieldKeys<ValueType>.createFieldPair(type: KClass<ValueType>) =
    this to ValidatedFormFieldLegacy(type = type)

@RequiresApi(Build.VERSION_CODES.N)
class InputFormLegacy(vararg fields: Pair<FieldKey, IFormField>) {

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
            data class Success<ValueType : Any>(val value: Flowable<InputStateLegacy<ValueType>>) :
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
        data class Success<ValueType : Any>(val result: Flowable<InputStateLegacy<ValueType>>) :
            RequestFieldInputStateResult<ValueType>()

        data class InvalidType(val inputType: KClass<out Any>) :
            RequestFieldInputStateResult<Nothing>()

        object FieldNotFound : RequestFieldInputStateResult<Nothing>()
    }
}

class ValidatedFormFieldLegacy<in ValueType : Any>(
    private val validator: ValidatorLegacy<ValueType> = NullableValidator(),
    private var value: ValueType? = null,
    private val type: KClass<ValueType>
) : InputFormLegacy.IFormField {
    private val defaultInputState = validator.validate(value)
    private val inputState = BehaviorProcessor.createDefault(defaultInputState)

    private fun submitValue(value: ValueType) {
        if (this.value == value)
            return
        this.value = value
        validator.validate(value).let(inputState::onNext)
    }

    override fun <RequestValueType : Any> requestInputState(type: KClass<RequestValueType>): InputFormLegacy.IFormField.RequestInputStateResult<RequestValueType> {
        if (this.type != type) {
            val valueClass = if (value != null) value!!::class else Nothing::class
            return InputFormLegacy.IFormField.RequestInputStateResult.InvalidType(valueClass)
        }

        return inputState.map { inputState ->
            when (inputState) {
                InputStateLegacy.Empty -> InputStateLegacy.Empty
                is InputStateLegacy.Invalid -> InputStateLegacy.Invalid(
                    inputState.reason,
                    inputState.input?.let(type::cast)
                )
                is InputStateLegacy.Valid -> InputStateLegacy.Valid(
                    inputState.input?.let(type::cast)
                )
            }
        }.let { Success(it) }
    }

    override fun <SubmitValueType : Any> submitInput(
        input: SubmitValueType
    ): InputFormLegacy.IFormField.SubmitInputResult {
        if (!type.isInstance(input)) {
            return InputFormLegacy.IFormField.SubmitInputResult.InvalidType
        }

        submitValue(type.cast(input))
        return InputFormLegacy.IFormField.SubmitInputResult.Success
    }

    abstract class ValidatorLegacy<ValueType : Any> {

        fun validate(value: ValueType?): InputStateLegacy<ValueType> = when {
            isEmpty(value) -> InputStateLegacy.Empty
            isValid(value) -> InputStateLegacy.Valid(value)
            else -> InputStateLegacy.Invalid(inp = value)
        }

        protected abstract fun isEmpty(value: ValueType?): Boolean
        protected abstract fun isValid(value: ValueType?): Boolean
    }

    class NullableValidator<ValueType : Any> : ValidatorLegacy<ValueType>() {
        override fun isEmpty(value: ValueType?) = value == null
        override fun isValid(value: ValueType?) = true
    }

    class NotNullValidator<ValueType : Any> : ValidatorLegacy<ValueType>() {
        override fun isEmpty(value: ValueType?) = true
        override fun isValid(value: ValueType?) = value != null
    }
}
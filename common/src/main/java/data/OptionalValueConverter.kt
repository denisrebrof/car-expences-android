package data

import domain.OptionalValue

class OptionalValueConverter<T> (val defaultValue: T) {

    fun toValue(optional: OptionalValue<T>): T {
        return when (optional) {
            is OptionalValue.Defined -> optional.value
            OptionalValue.Undefined -> defaultValue
        }
    }

    fun toOptional(value: T): OptionalValue<T> {
        return when (value) {
            defaultValue -> OptionalValue.Undefined
            else -> OptionalValue.Defined(value)
        }
    }
}
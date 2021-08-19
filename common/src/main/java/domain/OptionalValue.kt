package domain

sealed class OptionalValue<out T> {
    object Undefined : OptionalValue<Nothing>()
    data class Defined<T>(val value: T) : OptionalValue<T>()
}
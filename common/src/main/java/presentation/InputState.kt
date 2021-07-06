package presentation

sealed class InputState<out T : Any> {
    object Empty : InputState<Nothing>()
    data class Invalid(val reason: String? = null) : InputState<Nothing>()
    data class Valid<out T : Any>(val input: T) : InputState<T>()
}
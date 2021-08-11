package domain

sealed class RequestPagingState {
    object Undefined : RequestPagingState()
    data class Paged(val cursor: Long, val pageSize: Int) : RequestPagingState()
}
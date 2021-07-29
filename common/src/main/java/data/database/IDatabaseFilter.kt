package data.database

interface IDatabaseFilter {
    fun getFilterExpression(): String
}
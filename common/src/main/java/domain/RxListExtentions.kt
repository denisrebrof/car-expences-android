package domain

import io.reactivex.Flowable
import io.reactivex.Maybe

object RxListExtentions {

    fun <InitialType, ResultType> Flowable<List<InitialType>>.mapList(mapper: (InitialType) -> ResultType): Flowable<List<ResultType>> {
        return this.map { list -> list.map(mapper) }
    }

    fun <InitialType> Flowable<List<InitialType>>.anyMapList(selector: (InitialType) -> Boolean): Flowable<Boolean> {
        return this.map { list -> list.any(selector) }
    }

    fun <ItemType> Flowable<List<ItemType>>.filterList(selector: (ItemType) -> Boolean): Flowable<List<ItemType>> {
        return this.map { list -> list.filter(selector) }
    }

    fun <InitialType, ResultType> Maybe<List<InitialType>>.mapList(mapper: (InitialType) -> ResultType): Maybe<List<ResultType>> {
        return this.map { list -> list.map(mapper) }
    }

    fun <InitialType> Maybe<List<InitialType>>.anyMapList(selector: (InitialType) -> Boolean): Maybe<Boolean> {
        return this.map { list -> list.any(selector) }
    }

    fun <ItemType> Maybe<List<ItemType>>.filterList(selector: (ItemType) -> Boolean): Maybe<List<ItemType>> {
        return this.map { list -> list.filter(selector) }
    }
}
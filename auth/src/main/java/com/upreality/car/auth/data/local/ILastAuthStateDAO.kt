package com.upreality.car.auth.data.local

import com.upreality.car.auth.domain.AuthType
import io.reactivex.Completable
import io.reactivex.Flowable

interface ILastAuthStateDAO {
    fun get(): Flowable<AuthType>
    fun set(authType: AuthType): Completable
}
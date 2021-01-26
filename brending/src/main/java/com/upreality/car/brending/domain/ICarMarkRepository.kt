package com.upreality.car.brending.domain

import com.upreality.car.brending.domain.model.CarMark

interface ICarMarkRepository {
    fun getMark(markId: Long): CarMark
}
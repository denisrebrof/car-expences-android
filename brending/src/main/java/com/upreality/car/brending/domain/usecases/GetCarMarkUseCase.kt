package com.upreality.car.brending.domain.model

import com.upreality.car.brending.domain.ICarMarkRepository
import javax.inject.Inject

class GetCarMarkUseCase @Inject constructor(
    val repository: ICarMarkRepository
) {
    fun getMark(markId: Long ): CarMark{
        return repository.getMark(markId)
    }
}
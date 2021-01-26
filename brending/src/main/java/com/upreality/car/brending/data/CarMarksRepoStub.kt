package com.upreality.car.brending.data

import com.upreality.car.brending.R
import com.upreality.car.brending.domain.ICarMarkRepository
import com.upreality.car.brending.domain.model.CarBrend
import com.upreality.car.brending.domain.model.CarMark
import javax.inject.Inject

class CarMarksRepoStub @Inject constructor() : ICarMarkRepository {
    override fun getMark(markId: Long): CarMark {
        val stubBrend = CarBrend("DefBrend", R.color.design_default_color_secondary)
        val stubMark = CarMark(
            "Def Mark",
            stubBrend
        )
        return stubMark
    }
}
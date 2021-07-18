package com.upreality.car.expenses.presentation

import android.os.Parcel
import android.os.Parcelable
import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.domain.model.FinesCategories
import presentation.ValidatedFormField

class ExpenseEditingInputForm(
    private val expenseType: ExpenseType?,
    private val cost: Float,
    private val liters: Float,
    private val mileage: Float,
    private val fineType: FinesCategories?,
) : Parcelable {

    val expenseTypeField = ValidatedFormField(value = expenseType)
    val costField = ValidatedFormField(value = cost)
    val litersField = ValidatedFormField(value = liters)
    val mileageField = ValidatedFormField(value = mileage)
    val fineTypeField = ValidatedFormField(value = fineType)

    constructor(parcel: Parcel) : this(
        castToInput(parcel.readInt()) { typeId ->
            ExpenseType.values().firstOrNull { it.id == typeId }
        },
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readFloat(),
        castToInput(parcel.readInt()) { typeId ->
            FinesCategories.values().firstOrNull { it.id == typeId }
        }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(expenseTypeField.value?.id ?: EMPTY_INPUT_ID)
        parcel.writeFloat(costField.value!!)
        parcel.writeFloat(litersField.value!!)
        parcel.writeFloat(mileageField.value!!)
        parcel.writeInt(fineTypeField.value?.id ?: EMPTY_INPUT_ID)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ExpenseEditingInputForm> {
        override fun createFromParcel(parcel: Parcel): ExpenseEditingInputForm {
            return ExpenseEditingInputForm(parcel)
        }

        override fun newArray(size: Int): Array<ExpenseEditingInputForm?> {
            return arrayOfNulls(size)
        }

        private const val EMPTY_INPUT_ID = -1

        private fun <InputType> castToInput(hash: Int?, cast: (Int) -> InputType): InputType? {
            return when (hash) {
                null -> null
                EMPTY_INPUT_ID -> null
                else -> cast(hash)
            }
        }
    }
}
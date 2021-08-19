package com.upreality.car.expenses.presentation.list

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import com.upreality.car.expenses.R
import com.upreality.car.expenses.domain.model.expence.Expense
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class ExpensesListAdapterExpenseTypeDataProviderImpl @Inject constructor(
    @ActivityContext
    private val context: Context
) : ExpensesListAdapter.ExpenseTypeDataProvider {
    override fun getIcon(expense: Expense): Drawable? {
        return when (expense) {
            is Expense.Fine -> R.drawable.ic_fine
            is Expense.Fuel -> R.drawable.ic_fuel_half_filled
            is Expense.Maintenance -> R.drawable.ic_maintanace
        }.runCatching {
            //Throws not found exception
            ResourcesCompat.getDrawable(context.resources, this, context.theme)
        }.getOrNull()
    }

    override fun getTypeLabel(expense: Expense): String? {
        //TODO: implement with res and localization
        return expense::class.java.simpleName
    }

    override fun getDetails(expense: Expense): String {
        return when (expense) {
            is Expense.Fine -> expense.type.toString()
            //TODO: rewrite with custom val and text
            is Expense.Fuel -> expense.fuelAmount.toString() + " Liters"
            is Expense.Maintenance -> expense.type.toString()
        }
    }
}
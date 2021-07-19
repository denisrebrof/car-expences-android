package com.upreality.car.expenses.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.domain.model.FinesCategories
import com.upreality.car.expenses.domain.model.expence.Expense
import com.upreality.car.expenses.domain.usecases.IExpensesInteractor
import com.upreality.car.expenses.presentation.ExpenseEditingViewModel.ExpenseEditingKeys.*
import io.reactivex.Flowable
import io.reactivex.Maybe
import presentation.*
import presentation.InputForm.RequestFieldInputStateResult.Success
import java.util.*
import javax.inject.Inject
import kotlin.reflect.KClass

class ExpenseEditingViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val expensesInteractor: IExpensesInteractor,
) : ViewModel() {

    private val selectedExpenseId = handle.get<Long>(ExpenseEditingActivity.EXPENSE_ID)
    private val expenseMaybe = selectedExpenseId?.let(expensesInteractor::getExpenseMaybe)

    @RequiresApi(Build.VERSION_CODES.N)
    private val form = InputForm(
        Cost to Cost.createField(),
        Type to Type.createField(),
        Liters to Liters.createField(),
        Mileage to Mileage.createField(),
        FineType to FineType.createField(),
    )

    @RequiresApi(Build.VERSION_CODES.N)
    fun getViewState(): Flowable<ExpenseEditingViewState> {
        val expenseMaybe = expenseMaybe ?: Maybe.just(Expense.Fuel(Date(), 0f, 0f, 0f))
        return expenseMaybe.doOnSuccess { expense ->
            form.submit(expense.cost, Cost)
            form.submit(expense.let(ExpenseEditingInputStateConverter::getExpenseType), Type)
            when (expense) {
                is Expense.Fine -> {
                    form.submit(expense.type, FineType)
                }
                is Expense.Fuel -> {
                    form.submit(expense.liters, Liters)
                    form.submit(expense.mileage, Mileage)
                }
                is Expense.Maintenance -> {
                    form.submit(expense.mileage, Mileage)
                }
            }
        }.flatMapPublisher {
            val inputStateFlows = arrayOf(
                form.getStateFlow(Cost, Float::class) as Success,
                form.getStateFlow(Type, ExpenseType::class) as Success,
                form.getStateFlow(Liters, Float::class) as Success,
                form.getStateFlow(Mileage, Float::class) as Success,
                form.getStateFlow(FineType, FinesCategories::class) as Success,
            ).map { it.result }
            return@flatMapPublisher Flowable.combineLatest(inputStateFlows) { inputStates ->
                val costState = inputStates[0] as InputState<Float>
                val typeState = inputStates[1] as InputState<ExpenseType>
                val litersState = inputStates[2] as InputState<Float>
                val mileageState = inputStates[3] as InputState<Float>
                val fineTypeState = inputStates[4] as InputState<FinesCategories>
                return@combineLatest ExpenseEditingViewState(
                    isValid = inputStates.all { it is InputState.Valid<*> },
                    newExpenseCreation = selectedExpenseId != null,
                    costState = costState,
                    typeState = typeState,
                    litersState = litersState,
                    mileageState = mileageState,
                    fineTypeState = fineTypeState
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun execute(intent: ExpenseEditingIntent) {
        when(intent){
            is ExpenseEditingIntent.FillForm<*> -> form.submit(intent.value, intent.key)
        }
    }

    sealed class ExpenseEditingKeys<ValueType : Any>(id: Int, type: KClass<ValueType>) :
        InputForm.FieldKeys<ValueType>(id, type) {
        object Cost : ExpenseEditingKeys<Float>(0, Float::class)
        object Type : ExpenseEditingKeys<ExpenseType>(1, ExpenseType::class)
        object Liters : ExpenseEditingKeys<Float>(2, Float::class)
        object Mileage : ExpenseEditingKeys<Float>(3, Float::class)
        object FineType : ExpenseEditingKeys<FinesCategories>(4, FinesCategories::class)
    }

    sealed class ExpenseEditingIntent {
        data class FillForm<ValueType : Any>(
            val key: ExpenseEditingKeys<ValueType>,
            val value: ValueType
        ) : ExpenseEditingIntent()
    }

    data class ExpenseEditingViewState(
        val isValid: Boolean,
        val newExpenseCreation: Boolean,
        val costState: InputState<Float>,
        val typeState: InputState<ExpenseType>,
        val litersState: InputState<Float>,
        val mileageState: InputState<Float>,
        val fineTypeState: InputState<FinesCategories>,
    )
}
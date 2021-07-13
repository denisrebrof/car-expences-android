package com.upreality.car.expenses.presentation

import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.upreality.car.expenses.R
import dagger.hilt.android.AndroidEntryPoint
import com.upreality.car.expenses.databinding.FragmentExpenseEditingUndefinedBinding as ViewBinding

@AndroidEntryPoint
class ExpenseEditingTypeUndefinedFragment : Fragment(R.layout.fragment_expense_editing_undefined) {

    private val binding: ViewBinding by viewBinding(ViewBinding::bind)
}
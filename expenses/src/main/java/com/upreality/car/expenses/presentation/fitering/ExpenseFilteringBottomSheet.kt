package com.upreality.car.expenses.presentation.fitering

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.upreality.car.expenses.R
import dagger.hilt.android.AndroidEntryPoint
import com.upreality.car.expenses.databinding.BottonSheetExpenseFilteringBinding as ViewBinding

@AndroidEntryPoint
class ExpenseFilteringBottomSheet : BottomSheetDialogFragment() {

    private val viewModel: ExpenseFilteringViewModel by activityViewModels()
    private lateinit var binding: ViewBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ViewBinding.inflate(inflater, container, false).root

    override fun onStart() {
        super.onStart()
        view
    }
}
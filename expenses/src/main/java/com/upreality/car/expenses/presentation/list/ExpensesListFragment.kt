package com.upreality.car.expenses.presentation.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy
import by.kirich1409.viewbindingdelegate.viewBinding
import com.upreality.car.expenses.R
import com.upreality.car.expenses.presentation.editing.ExpenseEditingNavigator
import com.upreality.car.expenses.presentation.fitering.ExpenseFilteringAction
import com.upreality.car.expenses.presentation.fitering.ExpenseFilteringBottomSheet
import com.upreality.car.expenses.presentation.fitering.ExpenseFilteringViewModel
import dagger.hilt.android.AndroidEntryPoint
import domain.subscribeWithLogError
import io.sellmair.disposer.disposeBy
import io.sellmair.disposer.disposers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import presentation.RxLifecycleExtentions.subscribeDefault
import javax.inject.Inject
import com.upreality.car.expenses.databinding.FragmentExpensesListBinding as ViewBinding

@AndroidEntryPoint
class ExpensesListFragment : Fragment(R.layout.fragment_expenses_list) {

    private val viewModel: ExpensesListFragmentViewModel by viewModels()
    private val filteringViewModel: ExpenseFilteringViewModel by activityViewModels()

    private val binding: ViewBinding by viewBinding(ViewBinding::bind)

    private lateinit var adapter: ExpensesListAdapter
    private lateinit var layoutManager: LinearLayoutManager

    @Inject
    lateinit var editingNavigator: ExpenseEditingNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //TODO: implement with di
        val provider = requireContext().let(::ExpensesListAdapterExpenseTypeDataProviderImpl)
        adapter = ExpensesListAdapter(provider) { clickedExpense ->
            editingNavigator.openEditing(requireContext(), clickedExpense.id)
        }
        adapter.stateRestorationPolicy = StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutManager = LinearLayoutManager(context).also(binding.list::setLayoutManager)
        binding.list.adapter = adapter
        getItemTouchHelper().attachToRecyclerView(binding.list)
        binding.filterButton.setOnClickListener(this::showFilters)
    }

    private fun showFilters(source: View) {
        ExpenseFilteringBottomSheet().show(parentFragmentManager, "")
    }

    private fun getItemTouchHelper(): ItemTouchHelper {
        return ExpenseListItemSwipeCallback { position ->
            val target = adapter.getItemByPosition(position) ?: return@ExpenseListItemSwipeCallback
            target.let(viewModel::deleteExpense)
                .subscribeWithLogError()
                .disposeBy(lifecycle.disposers.onDestroy)
        }.let(::ItemTouchHelper)
    }

    @ExperimentalCoroutinesApi
    override fun onStart() {
        super.onStart()

        viewModel.getExpensesFlow().subscribeDefault {
            adapter.submitData(lifecycle, it)
            binding.list.scheduleLayoutAnimation()
        }.disposeBy(lifecycle.disposers.onStop)

        viewModel.getRefreshFlow().subscribeDefault {
//            layoutManager.scrollToPosition(0)
        }.disposeBy(lifecycle.disposers.onStop)

        filteringViewModel.getActionsFlow()
            .ofType(ExpenseFilteringAction.ApplyFilters::class.java)
            .map(ExpenseFilteringAction.ApplyFilters::filters)
            .subscribeDefault(viewModel::setFilters)
            .disposeBy(lifecycle.disposers.onStop)

        binding.expensesListFab.setOnClickListener {
            activity?.let(editingNavigator::openCreation)
        }

        binding.expensesListRefresh.setOnClickListener {
            adapter.refresh()
        }
    }
}
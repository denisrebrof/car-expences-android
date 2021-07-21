package com.upreality.car.expenses.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy
import com.upreality.car.expenses.databinding.FragmentExpensesListBinding
import dagger.hilt.android.AndroidEntryPoint
import domain.subscribeWithLogError
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.sellmair.disposer.disposeBy
import io.sellmair.disposer.disposers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@AndroidEntryPoint
class ExpensesListFragment : Fragment() {

    private val viewModel: ExpensesListFragmentViewModel by viewModels()
    private var binding: FragmentExpensesListBinding? = null
    private val requireBinding: FragmentExpensesListBinding
        get() = binding!!

    lateinit var adapter: ExpensesListAdapter
    lateinit var layoutManager: LinearLayoutManager

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

    @ExperimentalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExpensesListBinding.inflate(inflater, container, false)
        layoutManager = LinearLayoutManager(context).also(requireBinding.list::setLayoutManager)
        requireBinding.list.adapter = adapter
        getItemTouchHelper().attachToRecyclerView(requireBinding.list)

        viewModel
            .getExpensesFlow()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWithLogError {
                adapter.submitData(lifecycle, it)
                requireBinding.list.scheduleLayoutAnimation()
            }.disposeBy(lifecycle.disposers.onDestroy)
        viewModel.getRefreshFlow()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWithLogError {
//            layoutManager.scrollToPosition(0)
            }.disposeBy(lifecycle.disposers.onDestroy)
        return requireBinding.root
    }

    private fun getItemTouchHelper(): ItemTouchHelper {
        return ExpenseListItemSwipeCallback { position ->
            val target = adapter.getItemByPosition(position) ?: return@ExpenseListItemSwipeCallback
            target.let(viewModel::deleteExpense)
                .subscribeWithLogError()
                .disposeBy(lifecycle.disposers.onDestroy)
        }.let(::ItemTouchHelper)
    }

    override fun onStart() {
        super.onStart()
        requireBinding.expensesListFab.setOnClickListener {
            activity?.let(editingNavigator::openCreation)
        }

        requireBinding.expensesListRefresh.setOnClickListener {
            adapter.refresh()
        }
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}
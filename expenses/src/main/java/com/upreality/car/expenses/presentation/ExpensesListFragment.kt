package com.upreality.car.expenses.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy
import com.upreality.car.expenses.databinding.FragmentExpensesListBinding
import dagger.hilt.android.AndroidEntryPoint
import domain.subscribeWithLogError
import io.reactivex.schedulers.Schedulers
import io.sellmair.disposer.disposeBy
import io.sellmair.disposer.disposers
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class ExpensesListFragment : Fragment() {

    private val viewModel: ExpensesListFragmentViewModel by viewModels()
    private var binding: FragmentExpensesListBinding? = null
    private val requireBinding: FragmentExpensesListBinding
        get() = binding!!

    lateinit var adapter: ExpensesListAdapter
    lateinit var layoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //TODO: implement with di
        adapter = requireContext()
            .let(::ExpensesListAdapterExpenseTypeDataProviderImpl)
            .let(::ExpensesListAdapter)
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
        viewModel.getExpensesFlow().subscribeWithLogError {
//
            adapter.submitData(lifecycle, it)
            requireBinding.list.scheduleLayoutAnimation()
        }.disposeBy(lifecycle.disposers.onDestroy)
        viewModel.getRefreshFlow().subscribeWithLogError{
//            layoutManager.scrollToPosition(0)
            requireBinding.list.scheduleLayoutAnimation()
        }.disposeBy(lifecycle.disposers.onDestroy)
        return requireBinding.root

//        conversationAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
//            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
//                if (positionStart == 0) {
//                    manager.scrollToPosition(0)
//                }
//            }
//        })
    }

    override fun onStart() {
        super.onStart()
        requireBinding.expensesListFab.setOnClickListener {
            viewModel.createDebugExpense()
                .subscribeOn(Schedulers.io())
                .subscribe({}) {
                    Log.e("Create Error", it.toString())
                }.disposeBy(lifecycle.disposers.onStop)
        }
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}
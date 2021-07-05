package com.upreality.car.expenses.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
        val provider = requireContext().let(::ExpensesListAdapterExpenseTypeDataProviderImpl)
        adapter = ExpensesListAdapter(provider) { clickedExpense ->
            viewModel.deleteExpense(clickedExpense).subscribe({}) { error ->
                Log.e("Error", "Delete expense error: $error")
            }.disposeBy(lifecycle.disposers.onDestroy)
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
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("","Created")
                }) {
                    Log.e("Create Error", it.toString())
                }.disposeBy(lifecycle.disposers.onStop)
        }

        requireBinding.expensesListRefresh.setOnClickListener {
            viewModel.refresh()
        }

        viewModel.getExpensesCountFlow()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("","Get $it exp")
            }) {
                Log.e("Get All Error", it.toString())
            }.disposeBy(lifecycle.disposers.onStop)
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}
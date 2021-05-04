package com.upreality.car.expenses.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.upreality.car.expenses.R
import com.upreality.car.expenses.databinding.FragmentExpensesListBinding
import com.upreality.car.expenses.presentation.dummy.DummyContent

class ExpensesFragment : Fragment() {

    private var binding: FragmentExpensesListBinding? = null
    private val requireBinding: FragmentExpensesListBinding
        get() = binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExpensesListBinding.inflate(inflater, container, false)
        requireBinding.list.layoutManager = LinearLayoutManager(context)
        requireBinding.list.adapter = ExpensesAdapter(DummyContent.ITEMS)
        return requireBinding.root
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}
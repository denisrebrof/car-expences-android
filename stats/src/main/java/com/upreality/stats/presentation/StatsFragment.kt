package com.upreality.stats.presentation

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.upreality.stats.R

class StatsFragment : Fragment() {

    companion object {
        fun newInstance() = StatsFragment()
    }

    private lateinit var viewModel: StatsFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(StatsFragmentViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
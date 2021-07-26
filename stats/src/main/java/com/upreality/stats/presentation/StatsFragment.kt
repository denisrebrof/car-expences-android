package com.upreality.stats.presentation

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.upreality.stats.R
import dagger.hilt.android.AndroidEntryPoint
import com.upreality.stats.databinding.FragmentStatsBinding as ViewBinding

@AndroidEntryPoint
class StatsFragment : Fragment(R.layout.fragment_stats) {

    private val viewModel: StatsFragmentViewModel by viewModels()
    private val binding: ViewBinding by viewBinding(ViewBinding::bind)


}
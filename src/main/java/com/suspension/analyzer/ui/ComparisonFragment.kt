package com.suspension.analyzer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.suspension.analyzer.databinding.FragmentComparisonBinding
import com.suspension.analyzer.viewmodel.ComparisonViewModel
import com.suspension.analyzer.data.TestResult
import com.suspension.analyzer.ui.adapter.TestResultAdapter

class ComparisonFragment : Fragment() {
    private var _binding: FragmentComparisonBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ComparisonViewModel by viewModels()
    private lateinit var adapter: TestResultAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentComparisonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = TestResultAdapter(
            onEdit = {},
            onDelete = {},
            onSelect = { test, selected -> viewModel.toggleSelection(test, selected) }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        viewModel.testResults.observe(viewLifecycleOwner) { adapter.submitList(it) }
        viewModel.comparisonData.observe(viewLifecycleOwner) { updateComparison(it) }
        viewModel.loadTests()
    }

    private fun updateComparison(data: ComparisonViewModel.ComparisonData?) {
        if (data == null) return
        binding.comparisonTable.text = data.tableText
        binding.comparisonChart.setData(data.chartData)
        binding.comparisonChart.invalidate()
        binding.relativeScore.text = data.relativeScoreText
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
